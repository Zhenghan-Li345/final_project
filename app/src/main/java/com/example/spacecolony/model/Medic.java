package com.example.spacecolony.model;

/**
 * Medic specialization.
 * Default stats: baseSkill=7, resilience=2, maxEnergy=18.
 */
public class Medic extends CrewMember {

    private static final int BASE_SKILL = 7;
    private static final int RESILIENCE = 2;
    private static final int MAX_ENERGY = 18;
    private static final int HEAL_AMOUNT = 2;

    /**
     * Creates a new Medic crew member.
     *
     * @param id   unique identifier assigned by Storage
     * @param name the medic's name
     */
    public Medic(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.MEDIC);
    }

    /**
     * Medics perform a standard attack on normal turns.
     */
    @Override
    public int act() {
        return getEffectiveSkill();
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
