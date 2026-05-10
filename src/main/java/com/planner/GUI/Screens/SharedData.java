package com.planner.GUI.Screens;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.List;

public class SharedData {

    public static int totalStudents = 0;

    public static Label totalStudentsLabel =
            new Label("0");

    public static List<String> subjects =
            new ArrayList<>();

    public static ObservableList<String> prioritizedSubjects =
            FXCollections.observableArrayList();

}