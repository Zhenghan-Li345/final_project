package com.example.spacecolony.manager;

import android.content.Context;

import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Threat;
import com.example.spacecolony.storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class MissionControl {

    public interface MissionUpdateListener {
        void onMissionUpdate(CrewMember crewA, CrewMember crewB, Threat threat, String logLine, CombatState combatState);
    }

    public enum CombatState {
        CREW_A_TURN,
        CREW_B_TURN,
        THREAT_TURN,
        MISSION_ENDED
    }

    public enum PlayerAction {
        ATTACK,
        DEFEND,
        SPECIAL_ABILITY
    }

    private static final String[] THREAT_TYPES = {
            "Alien Beast",
            "System Failure",
            "Radiation Storm"
    };

    private static final int EXPERIENCE_REWARD = 1;
    private static int completedMissionCount = 0;

    private final Context appContext;
    private final CrewMember crewA;
    private final CrewMember crewB;
    private final Threat threat;
    private final List<String> missionLog = new ArrayList<>();

    private MissionUpdateListener listener;
    private CombatState combatState = CombatState.CREW_A_TURN;
    private boolean missionStarted = false;
    private boolean missionEnded = false;
    private boolean victory = false;
    private int round = 1;
    private CrewMember pendingThreatTarget;
    private boolean pendingThreatTargetDefended;

    public MissionControl(Context context, CrewMember crewA, CrewMember crewB) {
        this.appContext = context == null ? null : context.getApplicationContext();
        this.crewA = crewA;
        this.crewB = crewB;
        this.threat = createThreat(completedMissionCount);
    }

    private static Threat createThreat(int completedMissionCount) {
        String type = THREAT_TYPES[completedMissionCount % THREAT_TYPES.length];
        String name = type + " " + (completedMissionCount + 1);
        int skill = 4 + completedMissionCount;
        int resilience = 2 + completedMissionCount / 2;
        int maxEnergy = 20 + completedMissionCount * 2;
        return new Threat(name, type, skill, resilience, maxEnergy);
    }

    public void startMission(MissionUpdateListener listener) {
        if (missionStarted) {
            return;
        }

        this.listener = listener;
        missionStarted = true;
        missionEnded = false;
        victory = false;
        round = 1;
        combatState = CombatState.CREW_A_TURN;
        pendingThreatTarget = null;
        pendingThreatTargetDefended = false;
        missionLog.clear();

        crewA.setLocation(CrewLocation.MISSION_CONTROL);
        crewB.setLocation(CrewLocation.MISSION_CONTROL);
        crewA.setCombatContext(threat, crewB);
        crewB.setCombatContext(threat, crewA);
        Storage.saveData(appContext);

        appendLog("=== MISSION STARTED ===");
        appendLog("Threat: " + threat.toString());
        appendLog("Crew A: " + crewA.toString());
        appendLog("Crew B: " + crewB.toString());
        appendLog("");
        appendLog("--- Round " + round + " ---");
        appendLog("Turn: " + getCurrentCrew().getName());
    }

    public void performAction(PlayerAction action) {
        if (!missionStarted || missionEnded || action == null || !isPlayerTurn()) {
            return;
        }

        CrewMember actingCrew = getCurrentCrew();
        if (actingCrew == null || !actingCrew.isAlive()) {
            moveToNextPlayableState(actingCrew);
            return;
        }

        switch (action) {
            case ATTACK:
                executeAttack(actingCrew, false);
                break;
            case DEFEND:
                executeDefend(actingCrew);
                break;
            case SPECIAL_ABILITY:
                executeAttack(actingCrew, true);
                break;
        }

        if (!missionEnded) {
            combatState = CombatState.THREAT_TURN;
            appendLog("Turn: Enemy");
        }
    }

    public void performThreatTurn() {
        if (!missionStarted || missionEnded || combatState != CombatState.THREAT_TURN) {
            return;
        }

        CrewMember target = pendingThreatTarget;
        boolean defended = pendingThreatTargetDefended;
        pendingThreatTarget = null;
        pendingThreatTargetDefended = false;

        executeThreatAttack(target, defended);

        if (!missionEnded) {
            moveToNextPlayableState(target);
        }
    }

    private void executeAttack(CrewMember actingCrew, boolean useSpecialAbility) {
        CrewMember ally = actingCrew == crewA ? crewB : crewA;
        int allyEnergyBefore = ally != null && ally.isAlive() ? ally.getEnergy() : -1;
        int threatEnergyBefore = threat.getEnergy();

        int power = useSpecialAbility ? actingCrew.useSpecialAbility() : actingCrew.act();
        if (power > 0) {
            threat.defend(power);
        }

        int allyEnergyAfter = ally != null && ally.isAlive() ? ally.getEnergy() : -1;
        int damageToThreat = threatEnergyBefore - threat.getEnergy();

        if (useSpecialAbility) {
            if (power > 0) {
                appendLog(actingCrew.getName() + " uses " + actingCrew.getSpecialAbilityName()
                        + " for " + damageToThreat + " damage. Threat energy: "
                        + threat.getEnergy() + "/" + threat.getMaxEnergy());
            } else {
                appendLog(actingCrew.getName() + " uses " + actingCrew.getSpecialAbilityName() + ".");
            }
        } else {
            appendLog(actingCrew.getName() + " attacks threat for " + damageToThreat
                    + " damage. Threat energy: " + threat.getEnergy()
                    + "/" + threat.getMaxEnergy());
        }

        appendSupportLog(actingCrew, ally, allyEnergyBefore, allyEnergyAfter);

        if (!threat.isAlive()) {
            appendLog("The threat has been neutralized!");
            endMission(true);
            return;
        }

        pendingThreatTarget = actingCrew;
        pendingThreatTargetDefended = false;
    }

    private void executeDefend(CrewMember actingCrew) {
        appendLog(actingCrew.getName() + " takes a defensive stance.");
        pendingThreatTarget = actingCrew;
        pendingThreatTargetDefended = true;
    }

    private void executeThreatAttack(CrewMember target, boolean defended) {
        if (missionEnded || !threat.isAlive() || target == null || !target.isAlive()) {
            return;
        }

        int threatPower = threat.act();
        int incomingPower = defended ? Math.max(0, threatPower - 2) : threatPower;
        int energyBefore = target.getEnergy();
        target.defend(incomingPower);
        int damageTaken = energyBefore - target.getEnergy();

        appendLog("Threat attacks " + target.getName()
                + " for " + damageTaken + " damage. "
                + target.getName() + " energy: " + target.getEnergy()
                + "/" + target.getMaxEnergy());

        if (!target.isAlive()) {
            appendLog(target.getName() + " has died and is removed from the crew.");
            if (target == crewA && crewB.isAlive()) {
                crewB.recordMission(false);
            } else if (target == crewB && crewA.isAlive()) {
                crewA.recordMission(false);
            }
            Storage.removeCrewMember(target.getId());
            Storage.saveData(appContext);

            if (!crewA.isAlive() && !crewB.isAlive()) {
                endMission(false);
            }
        }
    }

    private void moveToNextPlayableState(CrewMember actingCrew) {
        if (missionEnded) {
            return;
        }

        if (actingCrew == crewA) {
            if (crewB.isAlive()) {
                combatState = CombatState.CREW_B_TURN;
                appendLog("Turn: " + crewB.getName());
            } else {
                startNextRound();
            }
            return;
        }

        startNextRound();
    }

    private void startNextRound() {
        if (missionEnded) {
            return;
        }

        if (!crewA.isAlive() && !crewB.isAlive()) {
            endMission(false);
            return;
        }

        round++;
        combatState = crewA.isAlive() ? CombatState.CREW_A_TURN : CombatState.CREW_B_TURN;
        appendLog("");
        appendLog("--- Round " + round + " ---");
        CrewMember currentCrew = getCurrentCrew();
        if (currentCrew != null) {
            appendLog("Turn: " + currentCrew.getName());
        }
    }

    private void endMission(boolean didWin) {
        if (missionEnded) {
            return;
        }

        missionEnded = true;
        combatState = CombatState.MISSION_ENDED;
        victory = didWin;
        pendingThreatTarget = null;
        pendingThreatTargetDefended = false;

        appendLog("");
        appendLog("=== MISSION ENDED ===");

        if (didWin) {
            completedMissionCount++;
            appendLog("Result: VICTORY");
            if (crewA.isAlive()) {
                crewA.recordMission(true);
                crewA.gainExperience(EXPERIENCE_REWARD);
                crewA.setLocation(CrewLocation.MISSION_CONTROL);
                appendLog(crewA.getName() + " gained " + EXPERIENCE_REWARD + " experience.");
            }
            if (crewB.isAlive()) {
                crewB.recordMission(true);
                crewB.gainExperience(EXPERIENCE_REWARD);
                crewB.setLocation(CrewLocation.MISSION_CONTROL);
                appendLog(crewB.getName() + " gained " + EXPERIENCE_REWARD + " experience.");
            }
        } else {
            if (crewA.isAlive()) {
                crewA.recordMission(false);
            }
            if (crewB.isAlive()) {
                crewB.recordMission(false);
            }
            appendLog("Mission failed. All crew members lost.");
        }

        crewA.clearCombatContext();
        crewB.clearCombatContext();
        Storage.saveData(appContext);
    }

    private void appendSupportLog(CrewMember actingMember, CrewMember ally,
                                  int allyEnergyBefore, int allyEnergyAfter) {
        if (ally != null && allyEnergyBefore >= 0 && allyEnergyAfter > allyEnergyBefore) {
            appendLog(actingMember.getName() + " restored "
                    + (allyEnergyAfter - allyEnergyBefore) + " energy to " + ally.getName() + ".");
        }
    }

    private void appendLog(String line) {
        missionLog.add(line);
        if (listener != null) {
            listener.onMissionUpdate(crewA, crewB, threat, line, combatState);
        }
    }

    private boolean isPlayerTurn() {
        return combatState == CombatState.CREW_A_TURN || combatState == CombatState.CREW_B_TURN;
    }

    private CrewMember getCurrentCrew() {
        switch (combatState) {
            case CREW_A_TURN:
                return crewA;
            case CREW_B_TURN:
                return crewB;
            case THREAT_TURN:
            case MISSION_ENDED:
            default:
                return null;
        }
    }

    public List<String> getMissionLog() {
        return missionLog;
    }

    public boolean isVictory() {
        return victory;
    }

    public boolean isMissionEnded() {
        return missionEnded;
    }

    public Threat getThreat() {
        return threat;
    }

    public CombatState getCombatState() {
        return combatState;
    }

    public CrewMember getCrewA() {
        return crewA;
    }

    public CrewMember getCrewB() {
        return crewB;
    }

    public static int getCompletedMissionCount() {
        return completedMissionCount;
    }

    public void cleanupReferences() {
        pendingThreatTarget = null;
        pendingThreatTargetDefended = false;
        crewA.clearCombatContext();
        crewB.clearCombatContext();
    }
}
