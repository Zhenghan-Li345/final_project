package com.example.spacecolony.model;

public abstract class CrewMember {

    private int id;
    private String name;
    private int baseSkill;
    private int resilience;
    private int experience;
    private int energy;
    private int maxEnergy;
    private Specialization specialization;
    private CrewLocation location;
    private Threat currentThreat;
    private CrewMember ally;
    private int missionsCompleted;
    private int victories;
    private int trainingSessions;

    public CrewMember(int id, String name, int baseSkill, int resilience,
                      int maxEnergy, Specialization specialization) {
        this.id = id;
        this.name = name;
        this.baseSkill = baseSkill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.specialization = specialization;
        this.experience = 0;
        this.energy = maxEnergy;
        this.location = CrewLocation.QUARTERS;
        this.missionsCompleted = 0;
        this.victories = 0;
        this.trainingSessions = 0;
    }

    public int getEffectiveSkill() {
        return baseSkill + experience;
    }

    protected int getMissionRandomBonus() {
        return (int) (Math.random() * 3);
    }

    protected int getMissionPower() {
        return getEffectiveSkill() + getMissionRandomBonus();
    }

    public int act() {
        return getMissionPower();
    }

    public int useSpecialAbility() {
        return act();
    }

    public String getSpecialAbilityName() {
        return "Special Ability";
    }

    /** Applies incoming damage after defense. */
    public void defend(int incomingPower) {
        int damage = Math.max(0, incomingPower - resilience);
        applyDamage(damage);
    }

    public void gainExperience(int amount) {
        if (amount > 0) {
            experience += amount;
        }
    }

    public void restoreEnergy() {
        energy = maxEnergy;
    }

    public boolean isAlive() {
        return energy > 0;
    }

    public void setCombatContext(Threat currentThreat, CrewMember ally) {
        this.currentThreat = currentThreat;
        this.ally = ally;
    }

    public void clearCombatContext() {
        this.currentThreat = null;
        this.ally = null;
    }

    /** Restores state loaded from persistence. */
    public void applyLoadedState(int experience, int energy, CrewLocation location,
                                 int missionsCompleted, int victories, int trainingSessions) {
        this.experience = Math.max(0, experience);
        this.energy = Math.max(0, Math.min(energy, maxEnergy));
        this.location = location == null ? CrewLocation.QUARTERS : location;
        this.missionsCompleted = Math.max(0, missionsCompleted);
        this.victories = Math.max(0, victories);
        this.trainingSessions = Math.max(0, trainingSessions);
        clearCombatContext();
    }

    protected Threat getCurrentThreat() {
        return currentThreat;
    }

    protected CrewMember getAlly() {
        return ally;
    }

    protected void applyDamage(int damage) {
        energy -= damage;
        if (energy < 0) {
            energy = 0;
        }
    }

    protected void restoreAllyEnergy(int amount) {
        if (ally != null && ally.isAlive() && amount > 0) {
            ally.receiveHealing(amount);
        }
    }

    protected void receiveHealing(int amount) {
        if (amount <= 0 || !isAlive()) {
            return;
        }
        energy = Math.min(maxEnergy, energy + amount);
    }

    public void recordMission(boolean didWin) {
        missionsCompleted++;
        if (didWin) {
            victories++;
        }
    }

    public void recordTrainingSession() {
        trainingSessions++;
    }

    public String getLocationLabel() {
        switch (location) {
            case QUARTERS:
                return "Quarters";
            case SIMULATOR:
                return "Simulator";
            case MISSION_CONTROL:
                return "Mission Control";
            default:
                return "Quarters";
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBaseSkill() {
        return baseSkill;
    }

    public int getResilience() {
        return resilience;
    }

    public int getExperience() {
        return experience;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public Specialization getSpecialization() {
        return specialization;
    }

    public CrewLocation getLocation() {
        return location;
    }

    public int getMissionsCompleted() {
        return missionsCompleted;
    }

    public int getVictories() {
        return victories;
    }

    public int getTrainingSessions() {
        return trainingSessions;
    }

    public void setLocation(CrewLocation location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return name + " [" + specialization + "] "
                + "Skill: " + getEffectiveSkill()
                + " | Energy: " + energy + "/" + maxEnergy;
    }
}
