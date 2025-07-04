package com.matey.disciteomnesapp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.matey.disciteomnesapp.R;
import com.matey.disciteomnesapp.models.Task;

import java.util.List;

/**
 * Adapter for displaying tasks inside a RecyclerView.
 *
 * Each task item includes:
 * - Title and description
 * - Group name (passed externally)
 * - Assigned user
 * - Due date
 * - Completion checkbox
 *
 * ✅ Firebase completion status updating was implemented manually.
 * ⚠️ Null/empty string formatting and clean checkbox listener reset logic were enhanced with AI assistance.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final String groupId;
    private final String groupName;

    /**
     * Constructor to initialize task list and associated group info.
     */
    public TaskAdapter(List<Task> taskList, String groupId, String groupName) {
        this.taskList = taskList;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout for individual task items
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        // Set basic text fields
        holder.titleText.setText(task.title);
        holder.descriptionText.setText(task.description);
        holder.groupTextView.setText("Group: " + groupName);

        // Display assignee name if available
        holder.assigneeTextView.setText(task.assignedToName != null && !task.assignedToName.isEmpty()
                ? "Assigned to: " + task.assignedToName
                : "Assigned to: (none)");

        // Display due date if available
        holder.dueDateTextView.setText(task.dueDate != null && !task.dueDate.isEmpty()
                ? "Due: " + task.dueDate
                : "Due: not set");

        // Reset and handle checkbox to prevent unintended reuse behavior
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.completed);

        // Update task completion status in Firebase when changed
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.completed = isChecked;
            FirebaseDatabase.getInstance()
                    .getReference("tasks")
                    .child(task.id)
                    .child("completed")
                    .setValue(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * ViewHolder class for accessing and binding task views.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descriptionText, groupTextView, assigneeTextView, dueDateTextView;
        CheckBox checkBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitleText);
            descriptionText = itemView.findViewById(R.id.taskDescriptionText);
            groupTextView = itemView.findViewById(R.id.taskGroupText);
            assigneeTextView = itemView.findViewById(R.id.taskAssigneeText);
            dueDateTextView = itemView.findViewById(R.id.taskDueDateText);
            checkBox = itemView.findViewById(R.id.taskCheckBox);
        }
    }
}
