package com.matey.disciteomnesapp.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.Group;
import com.matey.disciteomnesapp.ui.adapters.GroupAdapter;
import com.matey.disciteomnesapp.utils.GroupService;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupListActivity displays a list of all available study groups,
 * with the option to filter the list by groups the user has joined.
 *
 * ✅ Firebase & RecyclerView logic implemented manually.
 * ⚠️ Comments and structural explanations assisted by AI.
 */
public class GroupListActivity extends AppCompatActivity {

    private RecyclerView groupRecyclerView;
    private GroupAdapter groupAdapter;
    private List<Group> allGroups;

    private GroupService groupService;

    private MaterialButton filterToggleButton;
    private FloatingActionButton addGroupButton;

    private boolean showingMyGroups = false;
    private String currentUserId;

    /**
     * Initializes the group list screen and loads Firebase data.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        groupRecyclerView = findViewById(R.id.groupRecyclerView);
        filterToggleButton = findViewById(R.id.filterToggleButton);
        addGroupButton = findViewById(R.id.addGroupButton);

        // Set up RecyclerView with empty adapter initially
        allGroups = new ArrayList<>();
        groupAdapter = new GroupAdapter(new ArrayList<>());
        groupRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupRecyclerView.setAdapter(groupAdapter);

        groupService = new GroupService();

        // 🔁 Load and observe group data from Firebase
        loadGroups();

        // 🔘 Toggle between all groups and user's groups
        filterToggleButton.setOnClickListener(v -> {
            showingMyGroups = !showingMyGroups;
            filterToggleButton.setText(showingMyGroups ? "Show All Groups" : "Show My Groups");
            updateFilteredList();
        });

        // ➕ Button to create a new group
        addGroupButton.setOnClickListener(v -> showCreateGroupDialog());
    }

    /**
     * Fetches group data from Firebase and refreshes UI.
     */
    private void loadGroups() {
        groupService.getGroupsRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allGroups.clear();
                for (DataSnapshot groupSnapshot : snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    if (group != null) {
                        allGroups.add(group);
                    }
                }
                updateFilteredList();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(GroupListActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Applies the filter (if enabled) and updates the RecyclerView.
     */
    private void updateFilteredList() {
        List<Group> filtered = new ArrayList<>();
        for (Group group : allGroups) {
            if (!showingMyGroups || (group.members != null && group.members.contains(currentUserId))) {
                filtered.add(group);
            }
        }
        groupAdapter = new GroupAdapter(filtered);
        groupRecyclerView.setAdapter(groupAdapter);
    }

    /**
     * Opens a dialog for group creation using CreateGroupDialog.
     */
    private void showCreateGroupDialog() {
        CreateGroupDialog dialog = new CreateGroupDialog();
        dialog.show(getSupportFragmentManager(), "CreateGroupDialog");
    }
}
