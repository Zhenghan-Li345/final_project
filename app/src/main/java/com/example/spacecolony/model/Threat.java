package com.example.spacecolony.model;

/**
 * Represents a threat that crew members face during a cooperative mission.
 * Threats are managed and created by MissionControl.
 */
public class Threat {

    private String name;
    private String type;
    private int skill;        // attack power used in act()
    private int resilience;   // damage reduction used in defend()
    private int energy;       // current health
    private int maxEnergy;    // starting health

    /**
     * Constructs a Threat with the given parameters.
     *
     * @param name       threat name displayed to the player
     * @param type       threat type used by specialization logic
     * @param skill      attack power
     * @param resilience damage reduction
     * @param maxEnergy  starting and maximum energy (health)
     */
    public Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.type = type;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    // -------------------------------------------------------------------------
    // Core game methods
    // -------------------------------------------------------------------------

    /**
     * The threat acts against a target.
     * Returns the threat's skill as its attack power.
     *
     * @return attack power
     */
    public int act() {
        return skill;
    }

    /**
     * The threat defends against an incoming attack.
     * Damage taken = max(0, incomingPower - resilience).
     *
     * @param incomingPower the attacker's power
     */
    public void defend(int incomingPower) {
        int damage = Math.max(0, incomingPower - resilience);
        energy -= damage;
        if (energy < 0) {
            energy = 0;
        }
    }

    /**
     * Returns true if the threat is still alive (energy > 0).
     *
     * @return true if alive
     */
    public boolean isAlive() {
        return energy > 0;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSkill() {
        return skill;
    }

    public int getResilience() {
        return resilience;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    /**
     * Returns a readable summary of this threat.
     */
    @Override
    public String toString() {
        return name + " [" + type + "] | Skill: " + skill
                + " | Resilience: " + resilience
                + " | Energy: " + energy + "/" + maxEnergy;
    }
}
