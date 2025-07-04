package com.matey.disciteomnesapp.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.matey.disciteomnesapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DialogFragment that allows a user to create a new study group.
 * Users can enter a group name and description, and the group
 * will be saved to Firebase with the current user as a member.
 *
 * ⚠️ Portions of this file (structure, commenting) were generated using AI assistance.
 * ✅ The logic, error handling, and Firebase integration were manually implemented and verified.
 */
public class CreateGroupDialog extends DialogFragment {

    private TextInputEditText nameInput, descriptionInput;

    /**
     * Creates the dialog UI for entering group details.
     *
     * @param savedInstanceState Not used.
     * @return A configured AlertDialog instance.
     */
    @SuppressLint("WrongViewCast")
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_create_group, null);
        nameInput = view.findViewById(R.id.groupNameInput);
        descriptionInput = view.findViewById(R.id.groupDescriptionInput);

        return new AlertDialog.Builder(requireActivity())
                .setTitle("Create New Group")
                .setView(view)
                .setPositiveButton("Create", (dialog, which) -> createGroup())
                .setNegativeButton("Cancel", null)
                .create();
    }

    /**
     * Handles the logic for creating a new group:
     * - Validates the group name.
     * - Generates a unique Firebase ID.
     * - Adds the current user to the member list.
     * - Stores the group in Firebase.
     */
    private void createGroup() {
        String name = nameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        // Ensure the group name is not empty
        if (TextUtils.isEmpty(name)) {
            if (isAdded()) {
                Toast.makeText(requireContext(), "Group name is required", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Generate unique ID and get current user ID
        String groupId = FirebaseDatabase.getInstance().getReference("groups").push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (groupId == null || userId == null) {
            Toast.makeText(requireContext(), "Unexpected error occurred", Toast.LENGTH_SHORT).show();
            return;
        }

        // Add current user to the group's member list
        List<String> members = new ArrayList<>();
        members.add(userId);

        // Create the group data object
        Map<String, Object> groupData = new HashMap<>();
        groupData.put("id", groupId);
        groupData.put("name", name);
        groupData.put("description", description);
        groupData.put("members", members);

        // Save the group to Firebase
        FirebaseDatabase.getInstance().getReference("groups").child(groupId)
                .setValue(groupData)
                .addOnSuccessListener(unused -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Group created", Toast.LENGTH_SHORT).show();
                    }
                    dismiss(); // Close the dialog
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Failed to create group", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
