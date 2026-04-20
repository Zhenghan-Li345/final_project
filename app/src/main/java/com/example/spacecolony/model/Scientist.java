package com.example.spacecolony.model;

/**
 * Scientist specialization.
 * Default stats: baseSkill=8, resilience=1, maxEnergy=17.
 */
public class Scientist extends CrewMember {

    private static final int BASE_SKILL   = 8;
    private static final int RESILIENCE   = 1;
    private static final int MAX_ENERGY   = 17;

    /**
     * Creates a new Scientist crew member.
     *
     * @param id   unique identifier assigned by Storage
     * @param name the scientist's name
     */
    public Scientist(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.SCIENTIST);
    }

    /**
     * Scientists apply analytical thinking in missions.
     * Their act() uses the default effective skill.
     */
    @Override
    public int act() {
        return getEffectiveSkill();
    }
}

