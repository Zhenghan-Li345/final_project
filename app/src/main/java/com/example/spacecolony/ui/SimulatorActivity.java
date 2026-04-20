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

public class SimulatorActivity extends AppCompatActivity {

    private static final int TRAIN_EXP = 1;

    private CrewMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulator);

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

    private void refreshList() {
        List<CrewMember> updated = Storage.getCrewMembersByLocation(CrewLocation.SIMULATOR);
        adapter.updateList(updated);
    }

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
        refreshList();
    }

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
