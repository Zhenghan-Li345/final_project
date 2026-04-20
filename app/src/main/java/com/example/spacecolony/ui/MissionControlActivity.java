package com.example.spacecolony.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.manager.MissionControl;
import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Specialization;
import com.example.spacecolony.model.Threat;
import com.example.spacecolony.storage.Storage;
import com.example.spacecolony.ui.adapter.CrewMemberAdapter;

import java.util.List;

/**
 * Mission Control screen with player-controlled turn-based combat.
 */
public class MissionControlActivity extends AppCompatActivity {

    private static final long THREAT_TURN_DELAY_MS = 350L;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private CrewMemberAdapter adapter;
    private MissionControl activeMission;
    private ImageView ivCrewA;
    private ImageView ivCrewB;
    private ImageView ivThreat;
    private ProgressBar progressCrewA;
    private ProgressBar progressCrewB;
    private ProgressBar progressThreat;
    private TextView tvCrewAName;
    private TextView tvCrewBName;
    private TextView tvThreatName;
    private TextView tvMissionLog;
    private TextView tvTurnStatus;
    private ScrollView scrollMissionLog;
    private Button btnLaunch;
    private Button btnToQuarters;
    private Button btnAttack;
    private Button btnDefend;
    private Button btnSpecialAbility;
    private boolean missionResultShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_control);

        ivCrewA = findViewById(R.id.iv_crew_a);
        ivCrewB = findViewById(R.id.iv_crew_b);
        ivThreat = findViewById(R.id.iv_threat);
        progressCrewA = findViewById(R.id.progress_crew_a);
        progressCrewB = findViewById(R.id.progress_crew_b);
        progressThreat = findViewById(R.id.progress_threat);
        tvCrewAName = findViewById(R.id.tv_crew_a_name);
        tvCrewBName = findViewById(R.id.tv_crew_b_name);
        tvThreatName = findViewById(R.id.tv_threat_name);
        tvMissionLog = findViewById(R.id.tv_mission_log);
        tvTurnStatus = findViewById(R.id.tv_turn_status);
        scrollMissionLog = findViewById(R.id.scroll_mission_log);

        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter();
        recyclerView.setAdapter(adapter);

        btnLaunch = findViewById(R.id.btn_launch_mission);
        btnToQuarters = findViewById(R.id.btn_move_to_quarters);
        btnAttack = findViewById(R.id.btn_attack);
        btnDefend = findViewById(R.id.btn_defend);
        btnSpecialAbility = findViewById(R.id.btn_special_ability);

        btnLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMission();
            }
        });

        btnToQuarters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToQuarters();
            }
        });

        btnAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPlayerAction(MissionControl.PlayerAction.ATTACK);
            }
        });

        btnDefend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPlayerAction(MissionControl.PlayerAction.DEFEND);
            }
        });

        btnSpecialAbility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performPlayerAction(MissionControl.PlayerAction.SPECIAL_ABILITY);
            }
        });

        resetVisuals();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    @Override
    protected void onPause() {
        if (activeMission != null
                && !activeMission.isMissionEnded()
                && activeMission.getCombatState() == MissionControl.CombatState.THREAT_TURN) {
            activeMission.performThreatTurn();
            Storage.saveData(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (activeMission != null) {
            activeMission.cleanupReferences();
        }
    }

    private void refreshList() {
        List<CrewMember> updated = Storage.getCrewMembersByLocation(CrewLocation.MISSION_CONTROL);
        adapter.updateList(updated);
    }

    private void returnToQuarters() {
        if (activeMission != null && !activeMission.isMissionEnded()) {
            Toast.makeText(this, R.string.msg_mission_already_running, Toast.LENGTH_SHORT).show();
            return;
        }

        List<CrewMember> selected = adapter.getSelectedCrewMembers();
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.msg_none_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember member : selected) {
            Storage.moveCrewMember(member.getId(), CrewLocation.QUARTERS);
        }
        Storage.saveData(this);
        refreshList();
        resetVisuals();
    }

    private void launchMission() {
        if (activeMission != null && !activeMission.isMissionEnded()) {
            Toast.makeText(this, R.string.msg_mission_already_running, Toast.LENGTH_SHORT).show();
            return;
        }

        List<CrewMember> selected = adapter.getSelectedCrewMembers();
        if (selected.size() != 2) {
            Toast.makeText(this, R.string.msg_select_two, Toast.LENGTH_SHORT).show();
            return;
        }

        CrewMember crewA = selected.get(0);
        CrewMember crewB = selected.get(1);

        activeMission = new MissionControl(this, crewA, crewB);
        missionResultShown = false;
        tvMissionLog.setText("");
        activeMission.startMission(new MissionControl.MissionUpdateListener() {
            @Override
            public void onMissionUpdate(CrewMember updatedCrewA, CrewMember updatedCrewB,
                                        Threat updatedThreat, String logLine,
                                        MissionControl.CombatState combatState) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appendMissionLog(logLine);
                        updateVisuals(updatedCrewA, updatedCrewB, updatedThreat);
                        updateTurnUi(combatState);
                        if (activeMission != null
                                && combatState == MissionControl.CombatState.THREAT_TURN
                                && !activeMission.isMissionEnded()) {
                            scheduleThreatTurn();
                        }
                        if (activeMission != null && activeMission.isMissionEnded() && !missionResultShown) {
                            missionResultShown = true;
                            showMissionResultDialog();
                        }
                    }
                });
            }
        });

        updateVisuals(crewA, crewB, activeMission.getThreat());
        updateTurnUi(activeMission.getCombatState());
        refreshList();
    }

    private void performPlayerAction(MissionControl.PlayerAction action) {
        if (activeMission == null || activeMission.isMissionEnded()) {
            return;
        }
        setControlsEnabled(false);
        activeMission.performAction(action);
    }

    private void scheduleThreatTurn() {
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activeMission != null && !activeMission.isMissionEnded()) {
                    activeMission.performThreatTurn();
                }
            }
        }, THREAT_TURN_DELAY_MS);
    }

    private void showMissionResultDialog() {
        if (activeMission == null) {
            return;
        }

        StringBuilder log = new StringBuilder();
        for (String line : activeMission.getMissionLog()) {
            log.append(line).append("\n");
        }

        setControlsEnabled(false);
        btnLaunch.setEnabled(true);
        btnToQuarters.setEnabled(true);

        new AlertDialog.Builder(this)
                .setTitle(activeMission.isVictory()
                        ? getString(R.string.title_mission_victory)
                        : getString(R.string.title_mission_defeat))
                .setMessage(log.toString())
                .setPositiveButton(R.string.btn_ok, (dialog, which) -> refreshList())
                .setCancelable(false)
                .show();
    }

    private void appendMissionLog(String line) {
        String existing = tvMissionLog.getText().toString();
        if (existing.isEmpty() || existing.equals(getString(R.string.msg_mission_log_idle))) {
            tvMissionLog.setText(line);
        } else {
            tvMissionLog.append("\n" + line);
        }
        scrollMissionLog.post(new Runnable() {
            @Override
            public void run() {
                scrollMissionLog.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void resetVisuals() {
        handler.removeCallbacksAndMessages(null);
        activeMission = null;
        missionResultShown = false;
        tvCrewAName.setText(getString(R.string.label_crew_a));
        tvCrewBName.setText(getString(R.string.label_crew_b));
        tvThreatName.setText(getString(R.string.label_threat));
        tvTurnStatus.setText(getString(R.string.msg_turn_idle));
        progressCrewA.setProgress(0);
        progressCrewB.setProgress(0);
        progressThreat.setProgress(0);
        ivCrewA.setImageResource(R.drawable.ic_pilot);
        ivCrewB.setImageResource(R.drawable.ic_engineer);
        ivThreat.setImageResource(R.drawable.ic_threat_alien);
        tvMissionLog.setText(getString(R.string.msg_mission_log_idle));
        btnLaunch.setEnabled(true);
        btnToQuarters.setEnabled(true);
        setControlsEnabled(false);
    }

    private void updateTurnUi(MissionControl.CombatState combatState) {
        if (activeMission == null || combatState == MissionControl.CombatState.MISSION_ENDED
                || activeMission.isMissionEnded()) {
            tvTurnStatus.setText(getString(R.string.msg_turn_ended));
            setControlsEnabled(false);
            btnLaunch.setEnabled(true);
            btnToQuarters.setEnabled(true);
            return;
        }

        btnLaunch.setEnabled(false);
        btnToQuarters.setEnabled(false);

        if (combatState == MissionControl.CombatState.THREAT_TURN) {
            tvTurnStatus.setText(getString(R.string.msg_turn_enemy));
            setControlsEnabled(false);
            return;
        }

        CrewMember currentCrew = combatState == MissionControl.CombatState.CREW_A_TURN
                ? activeMission.getCrewA()
                : activeMission.getCrewB();

        if (currentCrew == null || !currentCrew.isAlive()) {
            setControlsEnabled(false);
            return;
        }

        tvTurnStatus.setText(getString(R.string.msg_turn_format, currentCrew.getName()));
        setControlsEnabled(true);
    }

    private void setControlsEnabled(boolean enabled) {
        btnAttack.setEnabled(enabled);
        btnDefend.setEnabled(enabled);
        btnSpecialAbility.setEnabled(enabled);
    }

    private void updateVisuals(CrewMember crewA, CrewMember crewB, Threat threat) {
        if (crewA != null) {
            tvCrewAName.setText(crewA.getName());
            ivCrewA.setImageResource(getCrewImageRes(crewA.getSpecialization()));
            progressCrewA.setMax(crewA.getMaxEnergy());
            progressCrewA.setProgress(crewA.getEnergy());
        }

        if (crewB != null) {
            tvCrewBName.setText(crewB.getName());
            ivCrewB.setImageResource(getCrewImageRes(crewB.getSpecialization()));
            progressCrewB.setMax(crewB.getMaxEnergy());
            progressCrewB.setProgress(crewB.getEnergy());
        }

        if (threat != null) {
            tvThreatName.setText(threat.getName());
            ivThreat.setImageResource(getThreatImageRes(threat));
            progressThreat.setMax(threat.getMaxEnergy());
            progressThreat.setProgress(threat.getEnergy());
        }
    }

    private int getCrewImageRes(Specialization specialization) {
        if (specialization == null) {
            return R.drawable.ic_pilot;
        }

        switch (specialization) {
            case PILOT:
                return R.drawable.ic_pilot;
            case ENGINEER:
                return R.drawable.ic_engineer;
            case MEDIC:
                return R.drawable.ic_medic;
            case SCIENTIST:
                return R.drawable.ic_scientist;
            case SOLDIER:
                return R.drawable.ic_soldier;
            default:
                return R.drawable.ic_pilot;
        }
    }

    private int getThreatImageRes(Threat threat) {
        if (threat == null || threat.getType() == null) {
            return R.drawable.ic_threat_alien;
        }

        switch (threat.getType()) {
            case "System Failure":
                return R.drawable.ic_threat_system_failure;
            case "Radiation Storm":
                return R.drawable.ic_threat_radiation;
            case "Alien Beast":
            default:
                return R.drawable.ic_threat_alien;
        }
    }
}
