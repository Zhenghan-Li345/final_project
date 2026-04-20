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

public class Storage {

    private static final String TAG = "Storage";
    private static final String DATA_FILE_NAME = "crew_data.json";
    private static final Gson GSON = new Gson();

    private static final HashMap<Integer, CrewMember> crewMap = new HashMap<>();
    private static int nextId = 1;

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
        int missionsCompleted;
        int victories;
        int trainingSessions;
    }

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
            record.missionsCompleted = member.getMissionsCompleted();
            record.victories = member.getVictories();
            record.trainingSessions = member.getTrainingSessions();
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
                member.applyLoadedState(
                        record.experience,
                        record.energy,
                        record.location,
                        record.missionsCompleted,
                        record.victories,
                        record.trainingSessions);
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

    public static int generateId() {
        return nextId++;
    }

    // -------------------------------------------------------------------------
    // Add / remove
    // -------------------------------------------------------------------------

    public static void addCrewMember(CrewMember member) {
        crewMap.put(member.getId(), member);
    }

    public static void removeCrewMember(int id) {
        crewMap.remove(id);
    }

    // -------------------------------------------------------------------------
    // Retrieval
    // -------------------------------------------------------------------------

    public static CrewMember getCrewMemberById(int id) {
        return crewMap.get(id);
    }

    public static List<CrewMember> getAllCrewMembers() {
        return new ArrayList<>(crewMap.values());
    }

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

    public static void clear() {
        crewMap.clear();
        nextId = 1;
    }
}
