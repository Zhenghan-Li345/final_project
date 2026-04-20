error id: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/ui/adapter/CrewMemberAdapter.java:com/example/spacecolony/ui/adapter/CrewMemberAdapter#ViewHolder#getBindingAdapterPosition#
file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/ui/adapter/CrewMemberAdapter.java
empty definition using pc, found symbol in pc: com/example/spacecolony/ui/adapter/CrewMemberAdapter#ViewHolder#getBindingAdapterPosition#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 3856
uri: file:///E:/java/project/SpaceColony/app/src/main/java/com/example/spacecolony/ui/adapter/CrewMemberAdapter.java
text:
```scala
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

/**
 * RecyclerView adapter for displaying a list of CrewMember objects.
 * Supports multi-selection via CheckBox on each row.
 * Reused across QuartersActivity, SimulatorActivity, and MissionControlActivity.
 *
 * Each row shows:
 *   - crew member name
 *   - specialization
 *   - effective skill, experience, energy/maxEnergy, current location
 *   - a CheckBox for selection
 *
 * Selected crew member IDs are tracked internally.
 * Call getSelectedCrewMembers() to retrieve the selected items.
 * Call clearSelection() to deselect all.
 * Call updateList() to refresh the data.
 */
public class CrewMemberAdapter extends RecyclerView.Adapter<CrewMemberAdapter.ViewHolder> {

    // The current list of crew members to display
    private final List<CrewMember> crewList = new ArrayList<>();

    // Set of IDs of currently selected crew members
    private final Set<Integer> selectedIds = new HashSet<>();

    // -------------------------------------------------------------------------
    // Public data methods
    // -------------------------------------------------------------------------

    /**
     * Replaces the current list with a new one and refreshes the view.
     * Clears any existing selection.
     *
     * @param newList updated list of crew members
     */
    public void updateList(List<CrewMember> newList) {
        crewList.clear();
        crewList.addAll(newList);
        selectedIds.clear();
        notifyDataSetChanged();
    }

    /**
     * Returns all crew members that are currently selected (checked).
     *
     * @return list of selected CrewMember objects
     */
    public List<CrewMember> getSelectedCrewMembers() {
        List<CrewMember> selected = new ArrayList<>();
        for (CrewMember member : crewList) {
            if (selectedIds.contains(member.getId())) {
                selected.add(member);
            }
        }
        return selected;
    }

    /**
     * Clears all selections and refreshes the view.
     */
    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    // -------------------------------------------------------------------------
    // RecyclerView.Adapter overrides
    // -------------------------------------------------------------------------

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

        // Toggle selection when the row is tapped
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer id = member.getId();
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id);
                } else {
                    selectedIds.add(id);
                }
                int pos = holder.@@getBindingAdapterPosition();
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

    // -------------------------------------------------------------------------
    // ViewHolder
    // -------------------------------------------------------------------------

    /**
     * Holds references to all views inside one crew member list item.
     */
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

        /**
         * Binds one CrewMember's data to this row's views.
         *
         * @param member     the crew member to display
         * @param isSelected whether this item is currently selected
         */
        public void bind(CrewMember member, boolean isSelected) {
            tvName.setText(member.getName());
            tvSpecialization.setText(member.getSpecialization().toString());
            tvStats.setText(
                    "Skill: " + member.getEffectiveSkill()
                    + "  |  EXP: " + member.getExperience()
                    + "  |  Energy: " + member.getEnergy() + "/" + member.getMaxEnergy()
            );
            tvLocation.setText("Location: " + member.getLocation().toString());

            // Sync checkbox with selection state.
            // Temporarily remove listener to prevent recursive callbacks.
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(isSelected);
        }
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: com/example/spacecolony/ui/adapter/CrewMemberAdapter#ViewHolder#getBindingAdapterPosition#