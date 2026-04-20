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
 * Displays crew members currently in the SIMULATOR.
 * The user checks one or more crew members, then taps a button to:
 *   - Train them (+1 EXP each)
 *   - Move them back to QUARTERS (energy restored automatically)
 */
public class SimulatorActivity extends AppCompatActivity {

    // Experience gained per training action
    private static final int TRAIN_EXP = 1;

    private CrewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

        // Set up RecyclerView with multi-select adapter
        RecyclerView recyclerView = findViewById(R.id.rv_crew);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CrewMemberAdapter();
        recyclerView.setAdapter(adapter);

        Button btnTrain      = findViewById(R.id.btn_train);
        Button btnToQuarters = findViewById(R.id.btn_move_to_quarters);

        btnTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trainSelected();
            }
        });

        btnToQuarters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToQuarters();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    /** Reloads crew in SIMULATOR from Storage and notifies the adapter. */
    private void refreshList() {
        List<CrewMember> updated = Storage.getCrewMembersByLocation(CrewLocation.SIMULATOR);
        adapter.updateList(updated);
    }

    /**
     * Trains all selected crew members (+1 EXP each).
     * Shows a toast if no crew member is selected.
     */
    private void trainSelected() {
        List<CrewMember> selected = adapter.getSelectedCrewMembers();
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.msg_none_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember member : selected) {
            member.gainExperience(TRAIN_EXP);
            member.recordTrainingSession();
        }
        Storage.saveData(this);
        // Refresh to show updated EXP values; selection is cleared by updateList
        refreshList();
    }

    /**
     * Moves all selected crew members back to QUARTERS.
     * Storage.moveCrewMember restores energy automatically.
     * Shows a toast if no crew member is selected.
     */
    private void moveToQuarters() {
        List<CrewMember> selected = adapter.getSelectedCrewMembers();
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.msg_none_selected, Toast.LENGTH_SHORT).show();
            return;
        }
        for (CrewMember member : selected) {
            Storage.moveCrewMember(member.getId(), CrewLocation.QUARTERS);
        }
        Storage.saveData(this);
        refreshList();
    }
}
