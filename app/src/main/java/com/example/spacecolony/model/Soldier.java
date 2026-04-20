package com.example.spacecolony.model;

public class Soldier extends CrewMember {

    private static final int BASE_SKILL = 9;
    private static final int RESILIENCE = 0;
    private static final int MAX_ENERGY = 16;
    private static final int BURST_DAMAGE = 2;

    public Soldier(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.SOLDIER);
    }

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
