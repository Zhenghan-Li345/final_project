package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.storage.Storage;
import com.example.spacecolony.ui.adapter.CrewMemberAdapter;

import java.util.List;

/**
 * Displays crew members currently in QUARTERS.
 * The user checks one or more crew members, then taps a button to move them
 * to the Simulator or Mission Control.
 * Energy is restored automatically when crew members arrive in Quarters
 * (handled by Storage.moveCrewMember).
 */
public class QuartersActivity extends AppCompatActivity {

    private CrewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

        // Set up RecyclerView with multi-select adapter (no click listener needed)
        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter();
        recyclerView.setAdapter(adapter);

        Button btnToSimulator = findViewById(R.id.btn_move_to_simulator);
        Button btnToMissionControl = findViewById(R.id.btn_move_to_mission_control);

        btnToSimulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSelected(CrewLocation.SIMULATOR);
            }
        });

        btnToMissionControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveSelected(CrewLocation.MISSION_CONTROL);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    /** Reloads crew in QUARTERS from Storage and notifies the adapter. */
    private void refreshList() {
        List<CrewMember> updated = Storage.getCrewMembersByLocation(CrewLocation.QUARTERS);
        adapter.updateList(updated);
    }

    /**
     * Moves all selected crew members to the given location.
     * Shows a toast if no crew member is selected.
     *
     * @param destination the target location
     */
    private void moveSelected(CrewLocation destination) {
        List<CrewMember> selected = adapter.getSelectedCrewMembers();
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.msg_none_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember member : selected) {
            Storage.moveCrewMember(member.getId(), destination);
        }
        Storage.saveData(this);
        refreshList();
    }
}
