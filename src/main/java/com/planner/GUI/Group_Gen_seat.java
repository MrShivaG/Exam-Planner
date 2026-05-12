package com.planner.GUI;

import com.planner.Database.ArrangementsDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class Group_Gen_seat {

    public static ScrollPane showGroup(
            String groupName,
            ExamConfig config
    ) {

        VBox mainContainer = new VBox(30);

        Button printAll = new Button("Print All");

        printAll.setOnAction(e ->

        {

            String html =
                    generateGroupHtml(
                            groupName,
                            config
                    );

            Gen_seat.openHtmlInBrowser(html);
        });

        try {

            Connection con =
                    ArrangementsDB.connection();

            PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT arrangement_name FROM arrangements_group WHERE group_name = ?"
                    );

            ps.setString(1, groupName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String arrangementName =
                        rs.getString("arrangement_name");

                List<String> tables =
                        fetchArrangementTables(arrangementName);

                ScrollPane pane =
                        Gen_seat.showTablesScreen(
                                tables,
                                config
                        );

                mainContainer.getChildren().add(printAll);

                mainContainer.getChildren().add(pane);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        ScrollPane scrollPane =
                new ScrollPane(mainContainer);

        scrollPane.setFitToWidth(true);

        return scrollPane;
    }

    private static List<String> fetchArrangementTables(
            String arrangementName
    ) {

        List<String> tables =
                new ArrayList<>();

        try {

            Connection con =
                    ArrangementsDB.connection();

            PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT table_name FROM arrangementdb WHERE arrangement_name = ?"
                    );

            ps.setString(1, arrangementName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                tables.add(
                        rs.getString("table_name")
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return tables;
    }

    private static String generateGroupHtml(
            String groupName,
            ExamConfig config
    ) {

        StringBuilder fullHtml =
                new StringBuilder();

        fullHtml.append("""
                    <html>
                
                    <head>
                
                    <style>
                
                    body{
                        background:#808080;
                        font-family:'Times New Roman';
                    }
                
                    .page-break{
                        page-break-after:always;
                    }
                
                    </style>
                
                    </head>
                
                    <body>
                """);

        try {

            Connection con =
                    ArrangementsDB.connection();

            PreparedStatement ps =
                    con.prepareStatement(
                            "SELECT arrangement_name FROM arrangements_group WHERE group_name = ?"
                    );

            ps.setString(1, groupName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                String arrangementName =
                        rs.getString("arrangement_name");

                List<String> tables =
                        fetchArrangementTables(
                                arrangementName
                        );

                for (String table : tables) {

                    List<List<String>> data =
                            ArrangementsDB
                                    .fetcharrData(table);

                    String html =
                            Gen_seat.generateHtml(
                                    table,
                                    data,
                                    config,
                                    null
                            );

                    fullHtml.append(html);

                    fullHtml.append("""
                                <div class='page-break'></div>
                            """);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        fullHtml.append("""
                    </body>
                    </html>
                """);

        return fullHtml.toString();

    }


}