error id: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/storage/Storage.java:java/util/HashMap#
file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/storage/Storage.java
empty definition using pc, found symbol in pc: java/util/HashMap#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 649
uri: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/storage/Storage.java
text:
```scala
package com.example.spacecolony.storage;

import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple static storage class that holds all crew members for the entire app.
 * Uses a HashMap<Integer, CrewMember> keyed by unique crew member ID.
 * All Activities access crew data through this class.
 */
public class Storage {

    // The single shared map of all crew members, keyed by their unique ID.
    private static final HashMap<Integer, CrewMember> crewMap = new HashMap@@<>();

    // Auto-incrementing ID counter for new crew members.
    private static int nextId = 1;

    // Private constructor — this class should never be instantiated.
    private Storage() {}

    // -------------------------------------------------------------------------
    // ID management
    // -------------------------------------------------------------------------

    /**
     * Generates and returns the next unique crew member ID.
     *
     * @return a new unique ID
     */
    public static int generateId() {
        return nextId++;
    }

    // -------------------------------------------------------------------------
    // Add / remove
    // -------------------------------------------------------------------------

    /**
     * Adds a crew member to storage.
     * The crew member must already have an ID assigned via generateId().
     *
     * @param member the crew member to add
     */
    public static void addCrewMember(CrewMember member) {
        crewMap.put(member.getId(), member);
    }

    /**
     * Removes a crew member from storage by ID.
     * Called when a crew member dies.
     *
     * @param id the ID of the crew member to remove
     */
    public static void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    // -------------------------------------------------------------------------
    // Retrieval
    // -------------------------------------------------------------------------

    /**
     * Returns a crew member by ID, or null if not found.
     *
     * @param id the crew member's unique ID
     * @return the CrewMember, or null
     */
    public static CrewMember getCrewMemberById(int id) {
        return crewMap.get(id);
    }

    /**
     * Returns a list of all crew members in storage, regardless of location.
     *
     * @return list of all crew members
     */
    public static List<CrewMember> getAllCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

    /**
     * Returns a list of all crew members currently at the given location.
     *
     * @param location the location to filter by
     * @return list of crew members at that location (may be empty)
     */
    public static List<CrewMember> getCrewMembersByLocation(CrewLocation location) {
        List<CrewMember> result = new ArrayList<>();
        for (Map.Entry<Integer, CrewMember> entry : crewMap.entrySet()) {
            if (entry.getValue().getLocation() == location) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    // -------------------------------------------------------------------------
    // Move
    // -------------------------------------------------------------------------

    /**
     * Moves a crew member to a new location.
     * If the new location is QUARTERS, the crew member's energy is restored.
     *
     * @param id          the ID of the crew member to move
     * @param newLocation the destination location
     */
    public static void moveCrewMember(int id, CrewLocation newLocation) {
        CrewMember member = crewMap.get(id);
        if (member == null) {
            return;
        }
        member.setLocation(newLocation);
        if (newLocation == CrewLocation.QUARTERS) {
            member.restoreEnergy();
        }
    }

    // -------------------------------------------------------------------------
    // Reset
    // -------------------------------------------------------------------------

    /**
     * Clears all crew members from storage and resets the ID counter.
     * Useful for starting a new game session.
     */
    public static void clear() {
        crewMap.clear();
        nextId = 1;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/util/HashMap#