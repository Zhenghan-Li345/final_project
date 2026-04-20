error id: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/model/Scientist.java:_empty_/Specialization#
file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/model/Scientist.java
empty definition using pc, found symbol in pc: _empty_/Specialization#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 614
uri: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/model/Scientist.java
text:
```scala
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
        super(id, name, BASE_SKILL, RESILIENCE, MAX_ENERGY, Spe@@cialization.SCIENTIST);
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


```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/Specialization#