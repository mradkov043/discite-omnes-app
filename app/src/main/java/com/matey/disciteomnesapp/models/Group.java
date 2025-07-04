package com.matey.disciteomnesapp.models;

import java.util.List;

public class Group {
    public String id;
    public String name;
    public String description;
    public List<String> members;

    public Group() {

    }

    public Group(String id, String name, String description, List<String> members) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
    }
}
