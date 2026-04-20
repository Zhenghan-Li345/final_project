package com.example.spacecolony.model;

public class Threat {

    private String name;
    private String type;
    private int skill;
    private int resilience;
    private int energy;
    private int maxEnergy;

    public Threat(String name, String type, int skill, int resilience, int maxEnergy) {
        this.name = name;
        this.type = type;
        this.skill = skill;
        this.resilience = resilience;
        this.maxEnergy = maxEnergy;
        this.energy = maxEnergy;
    }

    public int act() {
        return skill;
    }

    /** Applies incoming damage after resilience. */
    public void defend(int incomingPower) {
        int damage = Math.max(0, incomingPower - resilience);
        energy -= damage;
        if (energy < 0) {
            energy = 0;
        }
    }

    public boolean isAlive() {
        return energy > 0;
    }

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

    @Override
    public String toString() {
        return name + " [" + type + "] | Skill: " + skill
                + " | Resilience: " + resilience
                + " | Energy: " + energy + "/" + maxEnergy;
    }
}
