package com.example.spacecolony.model;

/**
 * Pilot specialization.
 * Default stats: baseSkill=5, resilience=4, maxEnergy=20.
 */
public class Pilot extends CrewMember {

    private static final int BASE_SKILL = 5;
    private static final int RESILIENCE = 4;
    private static final int MAX_ENERGY = 20;

    /**
     * Creates a new Pilot crew member.
     *
     * @param id   unique identifier assigned by Storage
     * @param name the pilot's name
     */
    public Pilot(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.PILOT);
    }

    /**
     * Pilots rely on agility and reduce one more point of final damage.
     */
    @Override
    public void defend(int incomingPower) {
        int damage = Math.max(0, incomingPower - getResilience() - 1);
        applyDamage(damage);
    }
}
