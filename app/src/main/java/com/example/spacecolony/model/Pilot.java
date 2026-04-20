package com.example.spacecolony.model;

public class Pilot extends CrewMember {

    private static final int BASE_SKILL = 5;
    private static final int RESILIENCE = 4;
    private static final int MAX_ENERGY = 20;

    public Pilot(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.PILOT);
    }

    /** Pilots reduce one extra point of damage. */
    @Override
    public void defend(int incomingPower) {
        int damage = Math.max(0, incomingPower - getResilience() - 1);
        applyDamage(damage);
    }
}
