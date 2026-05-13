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
import java.util.List;

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


            WebView sheet = createExamSheet(tableName, data, config, teachers);

            main.getChildren().add(sheet);

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

                    String html = generateHtml(
                            tableName,
                            data,
                            config,
                            teachers
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
            List<List<String>> data,
            ExamConfig config,
            List<Teacher> teachers
    ) {

        String html = generateHtml(
                tableName,
                data,
                config,
                teachers
        );

        WebView webView = new WebView();

        webView.setPrefWidth(794);
        webView.setPrefHeight(1123);

        webView.setMinWidth(794);
        webView.setMinHeight(1123);

        webView.setMaxWidth(794);
        webView.setMaxHeight(1123);
        webView.setZoom(0.92);

        WebEngine engine = webView.getEngine();

        engine.loadContent(html);

        return webView;
    }

    static void openHtmlInBrowser(
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

    static String generateHtml(
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

        String seatingTable = grid.toString();

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

        String html = """
                <!DOCTYPE html>
                
                <style>
                
                *{
                    box-sizing:border-box;
                }
                
                @page{
                    size:A4 portrait;
                    margin:8mm;
                }
                
                body{
                
                    margin:0;
                    padding:0;
                
                    background:#808080;
                
                    font-family:'Times New Roman';
                }
                
                .page{
                
                       width:190mm;
                
                       min-height:277mm;
                
                       margin:0 auto;
                
                       padding:6mm;
                
                       box-sizing:border-box;
                
                       background:white;
                
                       overflow:hidden;
                   }
                 
                .page:not(:last-child){
                    page-break-after:always;
                }

                .content{
                
                     flex:1;
                }
                 
                .header{
                       text-align:center;
                       margin-bottom:6px;
                   }
                
                   .header h2{
                       margin:0;
                       font-size:18px;
                       font-weight:bold;
                   }
                
                   .header h3{
                       margin:2px 0;
                       font-size:14px;
                   }
                
                   .header p{
                       margin:0;
                       font-size:11px;
                   }
                
                h1,h2,h3,h4,h5{
                
                    margin:2px;
                
                    padding:0;
                }
                
                .room-row{
                
                      display:flex;
                
                      justify-content:space-between;
                
                      margin-top:8px;
                
                      font-size:12px;
                
                      font-weight:bold;
                  }
                
                  .info-section{
                
                      margin-top:8px;
                
                      font-size:11px;
                
                      line-height:1.3;
                  }
                
                  .time{
                
                      margin-top:4px;
                
                      font-size:12px;
                
                      font-weight:bold;
                  }
                
                table, tr, td {
                    page-break-inside: avoid !important;
                }
                
                table{
                    width:100%;
                    border-collapse:collapse;
               
                    table-layout:fixed;
                    page-break-inside:auto;
                }
                
                tr{
                    page-break-inside:avoid;
                }
                
              
                
                .seat-table{
                
                      width:100%;
                
                      border-collapse:collapse;
                
                      table-layout:fixed;
                }
                
                .seat-table td{
                
                      border:1px solid black;
                
                      text-align:center;
                
                      padding:1px;
                
                      font-size:8px;
                
                      font-weight:bold;
                
                      white-space:nowrap;
                }
                  
                  
                .summary-table{
                
                     width:100%;
                
                     border-collapse:collapse;
                
                     margin-top:12px;
                 }
                
                 .summary-table th,
                 .summary-table td{
                
                     border:1px solid black;
                
                     padding:4px;
                
                     text-align:center;
                
                     font-size:10px;
                 }
                 
                 .teacher-table{
                
                     width:100%;
                
                     border-collapse:collapse;
                
                     margin-top:10px;
                
                     table-layout:fixed;
                 }
                
                 .teacher-table td,
                 .teacher-table th{
                
                     border:1px solid black;
                
                     padding:4px;
                
                     font-size:10px;
                
                     word-break:break-word;
                 }
                 
                .inv-table td{
                    height:40px;
                }
                
                .footer{
                
                            margin-top:10px;
                
                            display:flex;
                
                            justify-content:space-between;
                
                            font-size:11px;
                
                            font-style:italic;
                        }
                    
                .footer-section{
                
                    position:absolute;
                
                    bottom:20px;
                
                    left:18px;
                
                    right:18px;
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
                Date : """ + DateUtil.formatForUI(config.getDate()) + """
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
              
              
                """.formatted(config.getSession());

        return html;

    }
};













