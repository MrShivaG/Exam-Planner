package com.planner.GUI;

import com.planner.Database.ArrangementsDB;
import com.planner.GUI.Screens.TeacherAssign;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.List;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

            WebView sheet = createExamSheet(tableName, data, config, teachers);

            main.getChildren().add(sheet);
        }

        printBtn.setOnAction(e -> {

            PrinterJob job = PrinterJob.createPrinterJob();
            if(job == null){
                Notification.message("!No Printer Found!");
                return;
            }
            job.getJobSettings().setPageLayout(
                    job.getPrinter().createPageLayout(
                            Paper.A4,
                            PageOrientation.PORTRAIT,
                            Printer.MarginType.DEFAULT
                    )
            );

            if (job != null && job.showPrintDialog(main.getScene().getWindow())) {

                boolean success = true;

                for (Node node : main.getChildren()) {

                    // skip top bar (buttons)
                    if (node instanceof HBox) continue;

                    if (!job.printPage(node)) {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    job.endJob();
                    Notification.message("Printed Successfully");
                }
            }
        });

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);

        return scroll;
    }


    private static WebView createExamSheet(
            String tableName,
            List<List<String>> data,
            ExamConfig config,
            List<Teacher> teachers
    ) {

        String roomNo =
                tableName.substring(
                        tableName.lastIndexOf("_") + 1
                );

        StringBuilder grid = new StringBuilder();

        grid.append("<table class='seat-table'>");

        for (List<String> row : data) {

            grid.append("<tr>");

            for (String value : row) {

                if (value == null ||
                        value.equalsIgnoreCase("null")) {

                    value = "";
                }

                grid.append("<td>");
                grid.append(value);
                grid.append("</td>");
            }

            grid.append("</tr>");
        }

        grid.append("</table>");

        String teacher1 = "";
        String teacher2 = "";

        if (teachers != null) {

            if (teachers.size() > 0) {
                teacher1 = teachers.get(0).getName();
            }

            if (teachers.size() > 1) {
                teacher2 = teachers.get(1).getName();
            }
        }

        String seatingTable = grid.toString();

        String html = """
<!DOCTYPE html>
<html>
<head>

<style>

body{
    font-family:'Times New Roman';
    background:white;
    padding:20px;
}

.page{
    width:900px;
    margin:auto;
    color:black;
}

.header{
    text-align:center;
    margin-bottom:10px;
}

.header h2{
    margin:0;
    font-size:28px;
    font-weight:bold;
}

.header h3{
    margin:5px 0;
    font-size:22px;
}

.header p{
    margin:2px 0;
    font-size:18px;
}

.room-row{
    display:flex;
    justify-content:space-between;
    margin-top:20px;
    font-size:20px;
    font-weight:bold;
}

.info-section{
    margin-top:20px;
    font-size:18px;
    line-height:1.8;
}

.time{
    margin-top:10px;
    font-size:20px;
    font-weight:bold;
}

table{
    width:100%;
    border-collapse:collapse;
    margin-top:20px;
}

td, th{
    border:1px solid black;
    padding:10px;
    text-align:center;
    font-size:18px;
}

.summary-table td,
.summary-table th{
    padding:8px;
}

.inv-table td{
    height:40px;
}

.footer{
    margin-top:40px;
    display:flex;
    justify-content:space-between;
    font-size:20px;
    font-style:italic;
}

.bold{
    font-weight:bold;
}

</style>

</head>

<body>

<div class='page'>

<div class='header'>

<h2>
SAGAR INSTITUTE OF SCIENCE,
TECHNOLOGY & RESEARCH BHOPAL
</h2>

<h3>
B.Tech %s Examination
(Seating Plan)
</h3>

<p>
""" + config.getSession() + """
</p>

</div>

<div class='room-row'>

<div>
Room No : """ + roomNo + """
</div>

<div>
Date : """ + config.getDate() + """
</div>

</div>

<div class='info-section'>

<div>
Branch : CS
&nbsp;&nbsp;&nbsp;&nbsp;
Subject : Data Analytics
</div>

<div>
Branch : CE
&nbsp;&nbsp;&nbsp;&nbsp;
Subject : CP&M
</div>

<div class='time'>

Time :
""" + config.getExamTime() + """

</div>

</div>

""" + seatingTable + """

<table class='summary-table'>

<tr>
<th>Paper Code</th>
<th>From</th>
<th>To</th>
<th>Total</th>
</tr>

<tr>
<td>CS101(A)</td>
<td>0537CS241051</td>
<td>0537CS241061</td>
<td>10</td>
</tr>

<tr>
<td>CE101(B)</td>
<td>0537CE241009</td>
<td>0537CE241011</td>
<td>2</td>
</tr>

<tr>
<th colspan='3'>
Total
</th>

<th>
52
</th>

</tr>

</table>

<table>

<tr>

<th>S.No.</th>

<th>
No. of Present Student
</th>

<th>
No. of Absent Student
</th>

<th>Total</th>

</tr>

<tr>
<td>1</td>
<td></td>
<td></td>
<td></td>
</tr>

</table>

<table class='inv-table'>

<tr>

<th>S.No</th>

<th>
Name of Invigilators
</th>

<th>Designation</th>

<th>Branch</th>

<th>Signature</th>

</tr>

<tr>

<td>1</td>

<td>
""" + teacher1 + """
</td>

<td></td>

<td></td>

<td></td>

</tr>

<tr>

<td>2</td>

<td>
""" + teacher2 + """
</td>

<td></td>

<td></td>

<td></td>

</tr>

</table>

<div class='footer'>

<div>
(Exam Supdt.)
</div>

<div>
(Observer)
</div>

</div>

</div>

</body>
</html>
""".formatted(config.getSession());

        WebView webView = new WebView();

        webView.setPrefWidth(850);
        webView.setPrefHeight(1200);

        WebEngine engine = webView.getEngine();

        engine.loadContent(html);

        return webView;
    }


};













