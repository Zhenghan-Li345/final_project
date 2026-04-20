package com.example.spacecolony.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import com.example.spacecolony.model.CrewMember;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CrewMemberAdapter extends RecyclerView.Adapter<CrewMemberAdapter.ViewHolder> {

    // The current list of crew members to display
    private final List<CrewMember> crewList = new ArrayList<>();

    // Set of IDs of currently selected crew members
    private final Set<Integer> selectedIds = new HashSet<>();

    // -------------------------------------------------------------------------
    // Public data methods
    // -------------------------------------------------------------------------

    public void updateList(List<CrewMember> newList) {
        crewList.clear();
        crewList.addAll(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public List<CrewMember> getSelectedCrewMembers() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember member : crewList) {
            if (selectedIds.contains(member.getId())) {
                selected.add(member);
            }
        }
        return selected;
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_crew_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember member = crewList.get(position);
        boolean isSelected = selectedIds.contains(member.getId());
        holder.bind(member, isSelected);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = member.getId();
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id);
                } else {
                    selectedIds.add(id);
                }
                int pos = holder.getBindingAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(pos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return crewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvName;
        private final TextView tvSpecialization;
        private final TextView tvStats;
        private final TextView tvLocation;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName           = itemView.findViewById(R.id.tv_crew_name);
            tvSpecialization = itemView.findViewById(R.id.tv_crew_specialization);
            tvStats          = itemView.findViewById(R.id.tv_crew_stats);
            tvLocation       = itemView.findViewById(R.id.tv_crew_location);
            checkBox         = itemView.findViewById(R.id.cb_crew_select);
        }

        public void bind(CrewMember member, boolean isSelected) {
            tvName.setText(member.getName());
            tvSpecialization.setText(member.getSpecialization().toString());
            tvStats.setText(
                    itemView.getContext().getString(
                            R.string.crew_stats_format,
                            member.getEffectiveSkill(),
                            member.getExperience(),
                            member.getEnergy(),
                            member.getMaxEnergy())
                            + " | Missions: " + member.getMissionsCompleted()
                            + " | Wins: " + member.getVictories()
                            + " | Training: " + member.getTrainingSessions()
            );
            tvLocation.setText(itemView.getContext().getString(
                    R.string.crew_location_format,
                    member.getLocationLabel()));

            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isSelected);
        }
    }
}
