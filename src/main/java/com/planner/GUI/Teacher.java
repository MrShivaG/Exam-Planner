package com.planner.GUI;

public class Teacher {
    private String name;
    private String gender;

    public Teacher(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }

    public String getName() { return name; }
    public String getGender() { return gender; }

    @Override
    public String toString() {
        return name;
    }
}