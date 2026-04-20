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

public class QuartersActivity extends AppCompatActivity {

    private CrewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quarters);

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

    private void refreshList() {
        List<CrewMember> updated = Storage.getCrewMembersByLocation(CrewLocation.QUARTERS);
        adapter.updateList(updated);
    }

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
