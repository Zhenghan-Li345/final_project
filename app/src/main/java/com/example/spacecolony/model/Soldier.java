package com.example.spacecolony.model;

/**
 * Soldier specialization.
 * Default stats: baseSkill=9, resilience=0, maxEnergy=16.
 */
public class Soldier extends CrewMember {

    private static final int BASE_SKILL = 9;
    private static final int RESILIENCE = 0;
    private static final int MAX_ENERGY = 16;
    private static final int BURST_DAMAGE = 2;

    /**
     * Creates a new Soldier crew member.
     *
     * @param id   unique identifier assigned by Storage
     * @param name the soldier's name
     */
    public Soldier(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.SOLDIER);
    }

    /**
     * Soldiers use the standard attack formula on normal turns.
     */
    @Override
    public int act() {
        return getMissionPower();
    }

    @Override
    public int useSpecialAbility() {
        return getMissionPower() + (BURST_DAMAGE * 2);
    }

    @Override
    public String getSpecialAbilityName() {
        return "Double Burst";
    }
}
