package com.matey.disciteomnesapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.matey.disciteomnesapp.models.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupService {
    private final DatabaseReference groupsRef;
    private final String currentUserId;

    public GroupService() {
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void createGroup(String name, String description, Runnable onSuccess, Runnable onFailure) {
        String groupId = groupsRef.push().getKey();
        if (groupId == null) return;

        List<String> members = new ArrayList<>();
        members.add(currentUserId);

        Group newGroup = new Group(groupId, name, description, members);
        groupsRef.child(groupId).setValue(newGroup)
                .addOnSuccessListener(unused -> onSuccess.run())
                .addOnFailureListener(e -> onFailure.run());
    }

    public DatabaseReference getGroupsRef() {
        return groupsRef;
    }
}
