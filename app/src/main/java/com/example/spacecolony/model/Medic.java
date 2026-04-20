package com.example.spacecolony.model;

public class Medic extends CrewMember {

    private static final int BASE_SKILL = 7;
    private static final int RESILIENCE = 2;
    private static final int MAX_ENERGY = 18;
    private static final int HEAL_AMOUNT = 2;

    public Medic(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.MEDIC);
    }

    @Override
    public int act() {
        return getMissionPower();
    }

    @Override
    public int useSpecialAbility() {
        restoreAllyEnergy(HEAL_AMOUNT * 2);
        return 0;
}

    @Override
    public String getSpecialAbilityName() {
        return "Medical Aid";
    }
}
