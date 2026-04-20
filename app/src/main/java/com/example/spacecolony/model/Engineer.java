package com.example.spacecolony.model;

public class Engineer extends CrewMember {

    private static final int BASE_SKILL = 6;
    private static final int RESILIENCE = 3;
    private static final int MAX_ENERGY = 19;

    public Engineer(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.ENGINEER);
    }

    /** Engineers deal double power against System Failure threats. */
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
