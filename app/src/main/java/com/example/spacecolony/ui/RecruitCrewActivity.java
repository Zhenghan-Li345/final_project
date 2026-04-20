package com.example.spacecolony.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;
import com.example.spacecolony.model.Engineer;
import com.example.spacecolony.model.Medic;
import com.example.spacecolony.model.Pilot;
import com.example.spacecolony.model.Scientist;
import com.example.spacecolony.model.Soldier;
import com.example.spacecolony.model.Specialization;
import com.example.spacecolony.storage.Storage;

public class RecruitCrewActivity extends AppCompatActivity {

    private EditText etName;
    private Spinner spinnerSpec;

    private static final Specialization[] SPEC_ORDER = {
            Specialization.PILOT,
            Specialization.ENGINEER,
            Specialization.MEDIC,
            Specialization.SCIENTIST,
            Specialization.SOLDIER
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit_crew);

        etName      = findViewById(R.id.et_name);
        spinnerSpec = findViewById(R.id.spinner_specialization);

        String[] specNames = new String[SPEC_ORDER.length];
        for (int i = 0; i < SPEC_ORDER.length; i++) {
            specNames[i] = SPEC_ORDER[i].toString();
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, specNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpec.setAdapter(spinnerAdapter);

        Button btnConfirm = findViewById(R.id.btn_confirm_recruit);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recruitCrew();
            }
        });
    }

    private void recruitCrew() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.msg_name_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        Specialization spec = SPEC_ORDER[spinnerSpec.getSelectedItemPosition()];
        int id = Storage.generateId();
        CrewMember newMember = createCrewMember(id, name, spec);
        Storage.addCrewMember(newMember);
        Storage.saveData(this);

        Toast.makeText(this,
                getString(R.string.msg_recruited_as, newMember.getName(), spec),
                Toast.LENGTH_SHORT).show();

        finish();
    }

    private CrewMember createCrewMember(int id, String name, Specialization spec) {
        switch (spec) {
            case PILOT:     return new Pilot(id, name);
            case ENGINEER:  return new Engineer(id, name);
            case MEDIC:     return new Medic(id, name);
            case SCIENTIST: return new Scientist(id, name);
            case SOLDIER:   return new Soldier(id, name);
            default:        return new Pilot(id, name);
        }
    }
}
