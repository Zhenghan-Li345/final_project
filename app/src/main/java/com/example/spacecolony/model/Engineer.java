package com.example.spacecolony.model;

/**
 * Engineer specialization.
 * Default stats: baseSkill=6, resilience=3, maxEnergy=19.
 */
public class Engineer extends CrewMember {

    private static final int BASE_SKILL = 6;
    private static final int RESILIENCE = 3;
    private static final int MAX_ENERGY = 19;

    /**
     * Creates a new Engineer crew member.
     *
     * @param id   unique identifier assigned by Storage
     * @param name the engineer's name
     */
    public Engineer(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.ENGINEER);
    }

    /**
     * Engineers are especially effective against system failures.
     */
    @Override
    public int act() {
        Threat currentThreat = getCurrentThreat();
        int power = getEffectiveSkill();
        if (currentThreat != null && "System Failure".equals(currentThreat.getType())) {
            return power * 2;
        }
        return power;
    }
}
