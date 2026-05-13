package com.planner.GUI;

import javafx.print.*;
import javafx.scene.web.WebView;

import com.planner.Database.ArrangementsDB;
import com.planner.GUI.Screens.TeacherAssign;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.web.WebEngine;


public class Gen_seat {

    public static ScrollPane showTablesScreen(
            List<String> tableNames,
            ExamConfig config
    ){

        VBox main = new VBox(30);
        main.setPadding(new Insets(20));
        main.setStyle("-fx-background-color: #F8F9FA;");

        Button printBtn = new Button("Print");
        printBtn.getStyleClass().add("primary-btn");

        HBox topBar = new HBox(printBtn);
        topBar.setAlignment(Pos.CENTER_RIGHT);

        main.getChildren().add( topBar);

        for (String tableName : tableNames) {

            List<List<String>> data = ArrangementsDB.fetcharrData(tableName);

            if (data == null || data.isEmpty()) continue;

            int roomNo = Integer.parseInt(
                    tableName.substring(tableName.lastIndexOf("_") + 1)
            );

            List<Teacher> teachers = TeacherAssign.getRoomTeachers().get(roomNo);

//
//            WebView sheet = createExamSheet(tableName, enrolls, config, teachers, rowsRoom, rangeRaw);
//
//            main.getChildren().add(sheet);

        }



            printBtn.setOnAction(e -> {

                StringBuilder fullHtml = new StringBuilder();

                fullHtml.append("""
<html>

<head>

<style>

body{
    font-family:'Times New Roman';
   
    margin:0;
  
      background:#808080;
                        
   padding:20px;
}



</style>

</head>

<body>

""");

                for (String tableName : tableNames) {

                    List<List<String>> data =
                            ArrangementsDB.fetcharrData(tableName);

                    if (data == null || data.isEmpty()) {
                        continue;
                    }

                    int roomNo = Integer.parseInt(
                            tableName.substring(
                                    tableName.lastIndexOf("_") + 1
                            )
                    );

                    List<Teacher> teachers =
                            TeacherAssign
                                    .getRoomTeachers()
                                    .get(roomNo);

                    List<String> enrolls = data.stream()
                            .map(r -> r.size() > 1 ? r.get(1) : "")
                            .collect(Collectors.toList());

                    int rowsRoom = 6;

                    List<List<String>> rangeRaw = new ArrayList<>();

                    String html = generateHtml(
                            String.valueOf(roomNo),
                            tableName,
                            enrolls,
                            config,
                            teachers,
                            rowsRoom,
                            rangeRaw
                    );
                    fullHtml.append(html);

                    fullHtml.append("""
        <div class='page-break'></div>
    """);
                }

                fullHtml.append("""

</body>
</html>
""");

                openHtmlInBrowser(fullHtml.toString());

            });

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private static WebView createExamSheet(
            String tableName,
            List<String> enrolls,        // ✅ updated
            ExamConfig config,
            List<Teacher> teachers,
            int rowsRoom,                // ✅ updated
            List<List<String>> rangeRaw  // ✅ updated
    ) {
        String roomNo = tableName.substring(tableName.lastIndexOf("_") + 1);

        String html = generateHtml(
                roomNo,
                tableName,
                enrolls,
                config,
                teachers,
                rowsRoom,
                rangeRaw
        );

        WebView webView = new WebView();
        webView.setPrefWidth(794);
        webView.setPrefHeight(1123);
        webView.setMinWidth(794);
        webView.setMinHeight(1123);
        webView.setMaxWidth(794);
        webView.setMaxHeight(1123);
        webView.setZoom(0.92);
        webView.getEngine().loadContent(html);

        return webView;
    }

    public static VBox showSingleTable(

            String tableName,
            ExamConfig config

    ) {

        VBox main =
                new VBox();

        List<List<String>> data =
                ArrangementsDB.fetcharrData(
                        tableName
                );

        if (data == null ||
                data.isEmpty()) {

            return main;
        }

//        WebView sheet = createExamSheet(tableName, enrolls, config, teachers, rowsRoom, rangeRaw);
//
//        main.getChildren()
//                .add(sheet);

        return main;
    }

    public static void openHtmlInBrowser(
            String html
    ) {

            try {

                Path path = Files.createTempFile(
                        "exam_sheet",
                        ".html"
                );

                Files.writeString(path, html);

                Desktop.getDesktop().browse(
                        path.toUri()
                );

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    public static String generateHtml(
            String roomNo,
            String tableName,
            List<String> enrolls,
            ExamConfig config,
            List<Teacher> teachers,
            int rowsRoom,
            List<List<String>> rangeRaw
    ) {

        System.out.println("=== DEBUG ===");
        System.out.println("TableName: " + tableName);
        System.out.println("Date: " + config.getDate());
        System.out.println("Session: " + config.getSession());
        System.out.println("Semester: " + config.getSemester());
        System.out.println("ExamTime: " + config.getExamTime());
        //System.out.println("Data rows: " + data.size());
        System.out.println("Teachers: " + teachers);
        System.out.println("=============");

        int totalStudents = enrolls.size();
        int cols = (rowsRoom > 0) ? (int) Math.ceil((double) totalStudents / rowsRoom) : 6;

        StringBuilder grid = new StringBuilder();
        grid.append("<table class='seat-table'>");

        int idx = 0;
        for (int r = 0; r < rowsRoom; r++) {
            grid.append("<tr>");
            for (int c = 0; c < cols; c++) {
                if (idx < enrolls.size()) {
                    String enroll = enrolls.get(idx++);
                    if (enroll == null || enroll.equalsIgnoreCase("null")) enroll = "";
                    boolean isCS = enroll.toUpperCase().contains("CS");
                    if (isCS) {
                        grid.append("<td><i><b>").append(enroll).append("</b></i></td>");
                    } else {
                        grid.append("<td>").append(enroll).append("</td>");
                    }
                } else {
                    grid.append("<td></td>");
                }
            }
            grid.append("</tr>");
        }
        grid.append("</table>");

        String seatingTable = grid.toString();

        String teacher1 = "";
        String teacher2 = "";
        if (teachers != null) {
            if (teachers.size() > 0) teacher1 = teachers.get(0).getName();
            if (teachers.size() > 1) teacher2 = teachers.get(1).getName();
        }

        // Time — null ho toh blank
        String examTime = config.getExamTime();
        if (examTime == null || examTime.equalsIgnoreCase("null")) {
            examTime = "";
        }

        String semester = config.getSemester();
        if (semester == null) semester = "";

        StringBuilder rangeHtml = new StringBuilder();
        rangeHtml.append("""
    <table class='summary-table'>
        <tr>
            <th>Paper Code</th>
            <th>Roll No. From</th>
            <th>Roll No. To</th>
            <th>Total</th>
        </tr>
    """);

        if (rangeRaw != null) {
            for (List<String> rd : rangeRaw) {
                rangeHtml.append("<tr>")
                        .append("<td>").append(rd.size() > 1 ? rd.get(1) : "").append("</td>")  // PaperCode
                        .append("<td>").append(rd.size() > 2 ? rd.get(2) : "").append("</td>")  // RangeFrom
                        .append("<td>").append(rd.size() > 3 ? rd.get(3) : "").append("</td>")  // RangeTo
                        .append("<td>").append(rd.size() > 4 ? rd.get(4) : "").append("</td>")  // Total
                        .append("</tr>");
            }
        }
        rangeHtml.append("</table>");

        String html = """
        <!DOCTYPE html>
        <html>
        <head>
        <style>
        * { box-sizing: border-box; }
        @page { size: A4 portrait; margin: 8mm; }
        body { margin:0; padding:0; background:#808080; font-family:'Times New Roman',Times,serif; }
        .page { width:190mm; min-height:277mm; margin:0 auto; padding:6mm 8mm; box-sizing:border-box; background:white; overflow:hidden; }
        .page:not(:last-child) { page-break-after:always; }
        .header { text-align:center; margin-bottom:4px; }
        .header h2 { margin:0; font-size:15px; font-weight:bold; font-style:italic; text-transform:uppercase; }
        .header h3 { margin:2px 0 0; font-size:13px; font-weight:bold; }
        .room-date-row { display:flex; justify-content:space-between; align-items:flex-start; margin-top:6px; font-size:12px; }
        .room-name { font-style:italic; font-size:13px; text-align:left; text-decoration:underline; }
        .date-right { font-style:italic; font-size:11px; text-align:right; }
        .semester-line { text-align:center; font-size:11px; margin-top:2px; }
        .info-section { margin-top:6px; font-size:11px; line-height:1.5; }
        .time-line { margin-top:3px; font-size:11px; font-style:italic; }
        table { width:100%; border-collapse:collapse; table-layout:fixed; }
        tr { page-break-inside:avoid; }
        .seat-table { width:100%; border-collapse:collapse; table-layout:fixed; margin-top:8px; }
        .seat-table td { border:1px solid black; text-align:center; padding:2px 1px; font-size:8px; white-space:nowrap; overflow:hidden; }
        .summary-table { width:100%; border-collapse:collapse; margin-top:10px; font-size:10px; }
        .summary-table th, .summary-table td { border:1px solid black; padding:3px 4px; text-align:center; }
        .present-table { width:100%; border-collapse:collapse; margin-top:8px; font-size:10px; }
        .present-table th, .present-table td { border:1px solid black; padding:3px 4px; text-align:center; }
        .inv-table { width:100%; border-collapse:collapse; margin-top:8px; font-size:10px; }
        .inv-table th, .inv-table td { border:1px solid black; padding:4px; text-align:center; }
        .inv-table td { height:36px; }
        .footer { margin-top:10px; display:flex; justify-content:space-between; font-size:11px; font-style:italic; }
        </style>
        </head>
        <body>
        <div class='page'>
        <div class='header'>
            <h2>SAGAR INSTITUTE OF SCIENCE, TECHNOLOGY &amp; RESEARCH BHOPAL (SISTec-R)</h2>
            <h3>B. Tech\s\s
        """ + semester + """
        \s\sSEM EXAMINATION (Seating Plan)</h3>
        </div>
        <div class='semester-line'>
        """ + semester + """
        </div>
        <div class='room-date-row'>
            <div class='room-name'>Room No:\s
        """ + roomNo + """
        </div>
            <div class='date-right'>Date:\s
        """ + config.getDate() + """
        </div>
        </div>
        <div class='info-section'>
            <div>Branch: CE &nbsp;&nbsp; Sub Code/Subject Name: - CE503 (B)/CP&amp;M</div>
            <div>Branch: CS &nbsp;&nbsp; Sub Code/Subject Name: - CS503 (A)/Data Analytics</div>
            <div class='time-line'>Time:\s
        """ + examTime + """
        </div>
        </div>
        """ + seatingTable + rangeHtml.toString() + """
                <table class='summary-table'>
                             <tr>
                                 <th>Paper Code</th>
                                 <th colspan='2'>Roll No.</th>
                                 <th>Total</th>
                             </tr>
                             <tr>
                                 <td></td>
                                 <th>From</th>
                                 <th>To</th>
                                 <td></td>
                             </tr>
                         </table>
                         
        <table class='present-table'>
            <tr>
                <th>S.No.</th>
                <th>No. of Present Student</th>
                <th>No. of Absent Student</th>
                <th>Total</th>
            </tr>
            <tr>
                <td style='height:28px'></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>
        </table>
        <table class='inv-table'>
            <tr>
                <th>S.No</th>
                <th>Name of Invigilators</th>
                <th>Designation</th>
                <th>Branch</th>
                <th>Signature</th>
            </tr>
            <tr>
                <td>1</td>
                <td>
        """ + teacher1 + """
                </td>
                <td></td><td></td><td></td>
            </tr>
            <tr>
                <td>2</td>
                <td>
        """ + teacher2 + """
                </td>
                <td></td><td></td><td></td>
            </tr>
        </table>
        <div class='footer'>
            <div>(Exam Supdt.)</div>
            <div>(Observer)</div>
        </div>
        </div>
        </body>
        </html>
        """;

        return html;
    }
};













