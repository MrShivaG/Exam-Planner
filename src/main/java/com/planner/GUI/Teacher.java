package com.planner.GUI;

public class Teacher {

    private int id;
    private String name;
    private String gender;
    private String branch;
    private String phone;
    private String email;

    // Full constructor
    public Teacher(
            int id,
            String name,
            String gender,
            String branch,
            String phone,
            String email
    ) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.branch = branch;
        this.phone = phone;
        this.email = email;
    }

    // Short constructor (TeacherDB ke liye)
    public Teacher(String name, String gender) {
        this.name = name;
        this.gender = gender;
    }

    public int getId()      { return id; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getBranch() { return branch; }
    public String getPhone()  { return phone; }
    public String getEmail()  { return email; }
}