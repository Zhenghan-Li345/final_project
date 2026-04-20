package com.example.spacecolony.storage;

import android.content.Context;
import android.util.Log;

import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

    private static final String TAG = "Storage";
    private static final String DATA_FILE_NAME = "crew_data.json";
    private static final Gson GSON = new Gson();

    // The single shared map of all crew members, keyed by their unique ID.
    private static final HashMap<Integer, CrewMember> crewMap = new HashMap<>();

    // Auto-incrementing ID counter for new crew members.
    private static int nextId = 1;

    // Private constructor — this class should never be instantiated.
    private Storage() {}

    private static class StorageSnapshot {
        int nextId;
        List<CrewRecord> crewMembers = new ArrayList<>();
    }

    private static class CrewRecord {
        String crewType;
        int id;
        String name;
        int experience;
        int energy;
        CrewLocation location;
    }

    // -------------------------------------------------------------------------
    // Persistence
    // -------------------------------------------------------------------------

    public static void saveData(Context context) {
        if (context == null) {
            return;
        }

        StorageSnapshot snapshot = new StorageSnapshot();
        snapshot.nextId = nextId;

        for (CrewMember member : crewMap.values()) {
            CrewRecord record = new CrewRecord();
            record.crewType = member.getClass().getSimpleName();
            record.id = member.getId();
            record.name = member.getName();
            record.experience = member.getExperience();
            record.energy = member.getEnergy();
            record.location = member.getLocation();
            snapshot.crewMembers.add(record);
        }

        File file = new File(context.getFilesDir(), DATA_FILE_NAME);
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(GSON.toJson(snapshot));
        } catch (IOException ignored) {
        }
    }

    public static void loadData(Context context) {
        if (context == null) {
            return;
        }

        File file = new File(context.getFilesDir(), DATA_FILE_NAME);
        if (!file.exists()) {
            crewMap.clear();
            nextId = 1;
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            StorageSnapshot snapshot = GSON.fromJson(reader, StorageSnapshot.class);
            crewMap.clear();
            nextId = 1;

            if (snapshot == null) {
                Log.e(TAG, "Loaded snapshot is null. Resetting storage.");
                return;
            }

            if (snapshot.crewMembers == null) {
                Log.e(TAG, "Loaded crew list is null. Resetting storage.");
                return;
            }

            for (CrewRecord record : snapshot.crewMembers) {
                CrewMember member = createCrewMember(record);
                if (member == null) {
                    continue;
                }
                member.applyLoadedState(record.experience, record.energy, record.location);
                crewMap.put(member.getId(), member);
                nextId = Math.max(nextId, member.getId() + 1);
            }

            if (snapshot.nextId > 0) {
                nextId = Math.max(nextId, snapshot.nextId);
            }
        } catch (JsonSyntaxException | IOException exception) {
            Log.e(TAG, "Failed to load crew data. Resetting storage.", exception);
            crewMap.clear();
            nextId = 1;
        }
    }

    private static CrewMember createCrewMember(CrewRecord record) {
        if (record == null || record.crewType == null) {
            return null;
        }

        switch (record.crewType) {
            case "Pilot":
                return new Pilot(record.id, record.name);
            case "Engineer":
                return new Engineer(record.id, record.name);
            case "Medic":
                return new Medic(record.id, record.name);
            case "Scientist":
                return new Scientist(record.id, record.name);
            case "Soldier":
                return new Soldier(record.id, record.name);
            default:
                return null;
        }
    }

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
