package com.planner.GUI;

import java.time.LocalDate;

public class ExamConfig {
    private String collegeName;
    private String examTime;
    private String session;
    private String subject;
    private LocalDate date;

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getExamTime() { return examTime; }
    public void setExamTime(String examTime) { this.examTime = examTime; }

    public String getSession() { return session; }
    public void setSession(String session) { this.session = session; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}