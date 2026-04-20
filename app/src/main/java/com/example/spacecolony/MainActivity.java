package com.example.spacecolony;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolony.model.CrewLocation;
import com.example.spacecolony.storage.Storage;
import com.example.spacecolony.ui.MissionControlActivity;
import com.example.spacecolony.ui.QuartersActivity;
import com.example.spacecolony.ui.RecruitCrewActivity;
import com.example.spacecolony.ui.SimulatorActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvCountQuarters;
    private TextView tvCountSimulator;
    private TextView tvCountMissionControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Storage.loadData(this);
        setContentView(R.layout.activity_main);

        tvCountQuarters      = findViewById(R.id.tv_count_quarters);
        tvCountSimulator     = findViewById(R.id.tv_count_simulator);
        tvCountMissionControl = findViewById(R.id.tv_count_mission_control);

        Button btnRecruit = findViewById(R.id.btn_recruit);
        Button btnQuarters = findViewById(R.id.btn_quarters);
        Button btnSimulator = findViewById(R.id.btn_simulator);
        Button btnMissionControl = findViewById(R.id.btn_mission_control);

        btnRecruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RecruitCrewActivity.class));
            }
        });

        btnQuarters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, QuartersActivity.class));
            }
        });

        btnSimulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SimulatorActivity.class));
            }
        });

        btnMissionControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MissionControlActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCounts();
    }

    private void refreshCounts() {
        int quarters = Storage.getCrewMembersByLocation(CrewLocation.QUARTERS).size();
        int simulator = Storage.getCrewMembersByLocation(CrewLocation.SIMULATOR).size();
        int missionControl = Storage.getCrewMembersByLocation(CrewLocation.MISSION_CONTROL).size();

        tvCountQuarters.setText(getString(R.string.count_quarters, quarters));
        tvCountSimulator.setText(getString(R.string.count_simulator, simulator));
        tvCountMissionControl.setText(getString(R.string.count_mission_control, missionControl));
    }
}
