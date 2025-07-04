package com.matey.disciteomnesapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.Group;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a list of groups with Join/Leave functionality.
 *
 * This adapter enables users to:
 * - See group names and descriptions
 * - Join a group if not already a member
 * - Leave a group if already a member
 *
 * ✅ Firebase data manipulation was implemented manually.
 * ⚠️ Button state logic structure and adapter refresh logic were improved with AI assistance.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private final List<Group> groupList;
    private final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public GroupAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each group item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);

        // Set name and description
        holder.nameText.setText(group.name);
        holder.descriptionText.setText(group.description);

        // Ensure the member list is initialized
        if (group.members == null) {
            group.members = new ArrayList<>();
        }

        // Determine membership state
        boolean isMember = group.members.contains(currentUserId);
        holder.joinButton.setText(isMember ? "Leave" : "Join");
        holder.joinButton.setEnabled(true);

        // Handle Join/Leave logic
        holder.joinButton.setOnClickListener(v -> {
            if (isMember) {
                // ❌ Leave group
                group.members.remove(currentUserId);
                FirebaseDatabase.getInstance().getReference("groups")
                        .child(group.id)
                        .child("members")
                        .setValue(group.members)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(holder.itemView.getContext(), "Left group", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(holder.getAdapterPosition());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error leaving group", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // ✅ Join group
                group.members.add(currentUserId);
                FirebaseDatabase.getInstance().getReference("groups")
                        .child(group.id)
                        .child("members")
                        .setValue(group.members)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(holder.itemView.getContext(), "Joined group", Toast.LENGTH_SHORT).show();
                            notifyItemChanged(holder.getAdapterPosition());
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(holder.itemView.getContext(), "Error joining group", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    /**
     * ViewHolder for each group item, holding the views to be updated.
     */
    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descriptionText;
        Button joinButton;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.groupNameText);
            descriptionText = itemView.findViewById(R.id.groupDescriptionText);
            joinButton = itemView.findViewById(R.id.joinGroupButton);
        }
    }
}
