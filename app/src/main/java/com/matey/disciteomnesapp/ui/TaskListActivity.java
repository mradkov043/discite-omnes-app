package com.matey.disciteomnesapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.Task;
import com.matey.disciteomnesapp.models.User;
import com.matey.disciteomnesapp.ui.adapters.TaskAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TaskListActivity allows users to:
 * 1. Select one of their groups.
 * 2. View all tasks for that group.
 * 3. Create new tasks (with due date and assignee).
 *
 * ✅ Firebase integration is manually implemented.
 * ⚠️ Layout logic and list filtering structure were improved with AI-assisted suggestions.
 */
public class TaskListActivity extends AppCompatActivity {

    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList = new ArrayList<>();

    private String groupId;
    private String groupName;
    private Map<String, String> memberIdToNameMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        // Initialize RecyclerView
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Floating action button to create tasks
        FloatingActionButton addTaskFab = findViewById(R.id.addTaskFab);
        addTaskFab.setOnClickListener(v -> showCreateTaskDialog());

        // Prompt user to select one of their joined groups
        showGroupSelectionDialog();
    }

    /**
     * Displays a dialog to select a group the user belongs to.
     * Once selected, tasks are loaded and displayed.
     */
    private void showGroupSelectionDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_group, null);
        Spinner groupSpinner = dialogView.findViewById(R.id.groupSelector);

        List<String> groupNamesList = new ArrayList<>();
        List<String> groupIds = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groupNamesList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapter);

        // Load user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please re-login.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Fetch groups from Firebase that include the current user
        FirebaseDatabase.getInstance().getReference("groups")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot groupSnap : snapshot.getChildren()) {
                            for (DataSnapshot member : groupSnap.child("members").getChildren()) {
                                String value = member.getValue(String.class);
                                if (userId.equals(value)) {
                                    String name = groupSnap.child("name").getValue(String.class);
                                    String id = groupSnap.getKey();
                                    if (id != null && name != null) {
                                        groupIds.add(id);
                                        groupNamesList.add(name);
                                    }
                                    break;
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(TaskListActivity.this, "Failed to load groups", Toast.LENGTH_SHORT).show();
                    }
                });

        // Show dialog
        new AlertDialog.Builder(this)
                .setTitle("Select Group")
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("Continue", (dialog, which) -> {
                    int selectedIndex = groupSpinner.getSelectedItemPosition();
                    if (selectedIndex >= 0 && selectedIndex < groupIds.size()) {
                        groupId = groupIds.get(selectedIndex);
                        groupName = groupNamesList.get(selectedIndex);

                        taskAdapter = new TaskAdapter(taskList, groupId, groupName);
                        taskRecyclerView.setAdapter(taskAdapter);
                        loadTasks();
                    } else {
                        Toast.makeText(this, "Please select a valid group", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    /**
     * Loads tasks for the selected group from Firebase.
     */
    private void loadTasks() {
        FirebaseDatabase.getInstance().getReference("tasks")
                .orderByChild("groupId").equalTo(groupId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        taskList.clear();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Task task = snap.getValue(Task.class);
                            if (task != null) taskList.add(task);
                        }
                        taskAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(TaskListActivity.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Shows a dialog for creating a new task.
     * Allows setting: title, description, due date, and assigning to a group member.
     */
    private void showCreateTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_task, null);
        EditText titleInput = dialogView.findViewById(R.id.taskTitleInput);
        EditText descriptionInput = dialogView.findViewById(R.id.taskDescriptionInput);
        EditText dueDateInput = dialogView.findViewById(R.id.dueDateInput);
        Spinner memberSpinner = dialogView.findViewById(R.id.memberSpinner);

        ArrayAdapter<String> memberAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>());
        memberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(memberAdapter);

        // Load group members and populate dropdown with their names
        FirebaseDatabase.getInstance().getReference("groups").child(groupId).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot membersSnap) {
                        List<String> userIds = new ArrayList<>();
                        for (DataSnapshot member : membersSnap.getChildren()) {
                            String userId = member.getValue(String.class);
                            userIds.add(userId);
                        }

                        FirebaseDatabase.getInstance().getReference("users")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot usersSnap) {
                                        for (DataSnapshot userSnap : usersSnap.getChildren()) {
                                            User user = userSnap.getValue(User.class);
                                            if (user != null && userIds.contains(user.id)) {
                                                memberIdToNameMap.put(user.name, user.id);
                                                memberAdapter.add(user.name);
                                            }
                                        }
                                        memberAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError error) {
                                        Toast.makeText(TaskListActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(TaskListActivity.this, "Failed to load group members", Toast.LENGTH_SHORT).show();
                    }
                });

        // Show dialog for task creation
        new AlertDialog.Builder(this)
                .setTitle("Create Task")
                .setView(dialogView)
                .setPositiveButton("Create", (dialog, which) -> {
                    String title = titleInput.getText().toString().trim();
                    String description = descriptionInput.getText().toString().trim();
                    String dueDate = dueDateInput.getText().toString().trim();
                    String assignedToName = (String) memberSpinner.getSelectedItem();
                    String assignedToId = memberIdToNameMap.get(assignedToName);

                    if (TextUtils.isEmpty(title)) {
                        Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Construct Task object and save to DB
                    String taskId = FirebaseDatabase.getInstance().getReference("tasks").push().getKey();
                    Task task = new Task(taskId, groupId, title, description, false, assignedToId, assignedToName, dueDate);

                    FirebaseDatabase.getInstance().getReference("tasks")
                            .child(taskId).setValue(task)
                            .addOnSuccessListener(unused -> Toast.makeText(this, "Task created", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to create task", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
