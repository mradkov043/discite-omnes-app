package com.matey.disciteomnesapp.models;

public class Task {
    public String id;
    public String groupId;
    public String title;
    public String description;
    public boolean completed;
    public String assignedTo;
    public String assignedToName;
    public String dueDate; //(format: "YYYY-MM-DD")

    public Task() {

    }

    public Task(String id, String groupId, String title, String description, boolean completed, String assignedTo) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.assignedTo = assignedTo;
    }

    public Task(String id, String groupId, String title, String description, boolean completed, String assignedTo, String assignedToName, String dueDate) {
        this.id = id;
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.assignedTo = assignedTo;
        this.assignedToName = assignedToName;
        this.dueDate = dueDate;
    }
}
