package com.example.spacecolony.model;

public class Scientist extends CrewMember {

    private static final int BASE_SKILL   = 8;
    private static final int RESILIENCE   = 1;
    private static final int MAX_ENERGY   = 17;

    public Scientist(int id, String name) {
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Specialization.SCIENTIST);
    }

    @Override
    public int act() {
        return getMissionPower();
    }
}

