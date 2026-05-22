package com.planner.GUI;

import com.planner.Database.DB_Methods;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
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
            StringBuilder pagesHtml = new StringBuilder();

            for (String tableName : tableNames) {
                List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
                if (data == null || data.isEmpty()) {
                    continue;
                }

                int roomNo = Integer.parseInt(
                        tableName.substring(
                                tableName.lastIndexOf("_") + 1
                        )
                );

                List<Teacher> teachers = TeacherAssign.getRoomTeachers().get(roomNo);

                List<String> enrolls = data.stream()
                        .map(r -> r.size() > 1 ? r.get(1) : "")
                        .collect(Collectors.toList());

                int rowsRoom = 6;
                List<List<String>> rangeRaw = new ArrayList<>();

                String pageHtml = generateRoomPage(
                        String.valueOf(roomNo),
                        tableName,
                        enrolls,
                        config,
                        teachers,
                        rowsRoom,
                        rangeRaw
                );
                pagesHtml.append(pageHtml);
            }

            String fullHtml = wrapInHtmlDoc(pagesHtml.toString());
            openHtmlInBrowser(fullHtml);
        });

        ScrollPane scroll = new ScrollPane(main);
        scroll.setFitToWidth(true);

        return scroll;
    }

    private static WebView createExamSheet(
            String tableName,
            List<String> enrolls,
            ExamConfig config,
            List<Teacher> teachers,
            int rowsRoom,
            List<List<String>> rangeRaw
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
        String pageHtml = generateRoomPage(roomNo, tableName, enrolls, config, teachers, rowsRoom, rangeRaw);
        return wrapInHtmlDoc(pageHtml);
    }

    public static String generateRoomPage(
            String roomNo,
            String tableName,
            List<String> enrolls,
            ExamConfig config,
            List<Teacher> teachers,
            int rowsRoom,
            List<List<String>> rangeRaw
    ) {
        int cols = 5;
        int rows = 9;
        boolean dimsFound = false;

        String dateVal = null;
        String sessionVal = null;
        try {
            DB_Methods db = new DB_Methods();
            String[] dateSession = db.fetchDateAndSession(tableName);
            if (dateSession != null) {
                dateVal = dateSession[0];
                sessionVal = dateSession[1];
            }
        } catch (Exception e) {
            System.out.println("Error fetching date and session: " + e.getMessage());
        }

        if (dateVal == null || dateVal.isEmpty() || dateVal.equalsIgnoreCase("null")) {
            if (config.getDate() != null) {
                dateVal = DateUtil.formatForUI(config.getDate());
            } else {
                dateVal = "";
            }
        } else {
            try {
                dateVal = DateUtil.formatForUI(DateUtil.parse(dateVal));
            } catch (Exception e) {
                // Keep raw database format if parsing fails
            }
        }

        if (sessionVal == null || sessionVal.isEmpty() || sessionVal.equalsIgnoreCase("null")) {
            sessionVal = config.getSession() != null ? config.getSession() : "";
        }

        try {
            DB_Methods db = new DB_Methods();
            int roomNoInt = -1;
            try {
                String digitsOnly = roomNo.replaceAll("\\D+", "");
                if (!digitsOnly.isEmpty()) {
                    roomNoInt = Integer.parseInt(digitsOnly);
                }
            } catch (NumberFormatException ignored) {}

            if (roomNoInt != -1) {
                int[] dims = db.fetchRowColumn(roomNoInt);
                if (dims != null && dims[0] > 0 && dims[1] > 0) {
                    rows = dims[0];
                    cols = dims[1];
                    dimsFound = true;
                }
            }

            if (!dimsFound) {
                String groupName = config.getArrangementName();
                if (groupName != null && !groupName.isEmpty()) {
                    List<String[]> groupTables = db.fetch_group_tables(groupName);
                    for (String[] row : groupTables) {
                        if (row[0].trim().equalsIgnoreCase(tableName.trim())) {
                            int capacity = Integer.parseInt(row[3].trim());
                            cols = Integer.parseInt(row[7].trim());
                            rows = capacity / cols;
                            dimsFound = true;
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching room dimensions for table " + tableName + ": " + e.getMessage());
        }

        // Fetch actual data to get unique subject codes
        List<List<String>> data = ArrangementsDB.fetcharrData(tableName);
        List<String> enrollList = new ArrayList<>();
        Map<String, String> branchToSubjectInfo = new LinkedHashMap<>();

        if (data != null) {
            for (List<String> row : data) {
                if (row.size() > 1) {
                    String rawEnroll = row.get(1);
                    String cleanedEnroll = (rawEnroll != null) ? rawEnroll.replaceAll("<[^>]*>", "").trim() : "";
                    enrollList.add(cleanedEnroll);
                }
                if (row.size() > 2) {
                    String enroll = row.size() > 1 ? row.get(1) : "";
                    String subCode = row.get(2);
                    if (subCode != null && !subCode.equalsIgnoreCase("null") && !subCode.isEmpty() &&
                            enroll != null && !enroll.equalsIgnoreCase("null") && !enroll.isEmpty()) {

                        enroll = enroll.replaceAll("<[^>]*>", "").trim();
                        subCode = subCode.replaceAll("<[^>]*>", "").trim();

                        String branch = extractBranch(subCode);
                        if (!branchToSubjectInfo.containsKey(branch)) {
                            String subName = fetchSubjectName(subCode);
                            String info = subCode + (subName.isEmpty() ? "" : "/" + subName);
                            branchToSubjectInfo.put(branch, info);
                        }
                    }
                }
            }
        }
        if (enrollList.isEmpty()) {
            enrollList = enrolls;
        }

        if (branchToSubjectInfo.isEmpty()) {
            branchToSubjectInfo.put("CE", "CE503 (B)/CP&M");
            branchToSubjectInfo.put("CS", "CS503 (A)/Data Analytics");
        }

        // Determine the primary branch for alternate seating highlighting
        String primaryBranch = "";
        if (data != null) {
            for (List<String> row : data) {
                if (row.size() > 1) {
                    String enroll = row.get(1);
                    if (enroll != null && !enroll.equalsIgnoreCase("null")) {
                        enroll = enroll.replaceAll("<[^>]*>", "").trim();
                        if (!enroll.isEmpty()) {
                            primaryBranch = getBranchFromEnrollment(enroll);
                            if (!primaryBranch.isEmpty()) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (primaryBranch.isEmpty() && enrollList != null) {
            for (String enroll : enrollList) {
                if (enroll != null && !enroll.equalsIgnoreCase("null")) {
                    enroll = enroll.replaceAll("<[^>]*>", "").trim();
                    if (!enroll.isEmpty()) {
                        primaryBranch = getBranchFromEnrollment(enroll);
                        if (!primaryBranch.isEmpty()) {
                            break;
                        }
                    }
                }
            }
        }

        // Calculate max enrollment length dynamically
        int maxLen = 12;
        if (data != null) {
            for (List<String> row : data) {
                if (row.size() > 1) {
                    String enroll = row.get(1);
                    if (enroll != null && !enroll.equalsIgnoreCase("null")) {
                        String clean = enroll.replaceAll("<[^>]*>", "").trim();
                        if (clean.length() > maxLen) {
                            maxLen = clean.length();
                        }
                    }
                }
            }
        }
        if (enrollList != null) {
            for (String enroll : enrollList) {
                if (enroll != null && !enroll.equalsIgnoreCase("null")) {
                    String clean = enroll.replaceAll("<[^>]*>", "").trim();
                    if (clean.length() > maxLen) {
                        maxLen = clean.length();
                    }
                }
            }
        }

        // Determine dynamic left/right padding and inner width based on column count
        int paddingSide;
        double innerGridWidthPx;
        if (cols <= 3) {
            paddingSide = 22;
            innerGridWidthPx = 552.0;
        } else if (cols == 4) {
            paddingSide = 18;
            innerGridWidthPx = 582.0;
        } else if (cols == 5) {
            paddingSide = 12;
            innerGridWidthPx = 627.0;
        } else {
            paddingSide = 8;
            innerGridWidthPx = 657.0;
        }

        double cellPaddingAndBorders = 4.0; // padding: 2px 1px (2px) + borders (2px)
        double usableCellWidth = (innerGridWidthPx / cols) - cellPaddingAndBorders;
        double charWidthFactor = 0.66; // conservative average width of Arial bold/italic uppercase char relative to font-size
        double calculatedFontSize = usableCellWidth / (maxLen * charWidthFactor);

        int gridFontSize = (int) Math.floor(calculatedFontSize);
        // Clamp to a sensible range: min 4px, max based on column count (or max 19px)
        int maxAllowedFontSize = (cols <= 3) ? 19 : ((cols == 4) ? 17 : ((cols == 5) ? 14 : 12));
        gridFontSize = Math.max(4, Math.min(maxAllowedFontSize, gridFontSize));

        // Seating Grid (Column-Major indexing)
        StringBuilder grid = new StringBuilder();
        grid.append("<div class='table-container' style='flex: ").append(rows).append(";'>");
        grid.append("<table class='seat-table'>");
        for (int r = 0; r < rows; r++) {
            grid.append("<tr>");
            for (int c = 0; c < cols; c++) {
                int idx = c * rows + r;
                if (data != null && idx < data.size()) {
                    List<String> row = data.get(idx);
                    String enroll = row.size() > 1 ? row.get(1) : "";
                    if (enroll == null || enroll.equalsIgnoreCase("null")) enroll = "";

                    enroll = enroll.replaceAll("<[^>]*>", "").trim();

                    if (enroll.isEmpty()) {
                        grid.append("<td spellcheck='false' style='font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'></td>");
                    } else {
                        boolean highlightCell = false;
                        if (!primaryBranch.isEmpty()) {
                            String branch = getBranchFromEnrollment(enroll);
                            highlightCell = !branch.isEmpty() && !branch.equalsIgnoreCase(primaryBranch);
                        } else {
                            highlightCell = ((r + c) % 2 == 1);
                        }

                        if (highlightCell) {
                            grid.append("<td spellcheck='false' style='font-style: italic; font-weight: bold; font-family: Arial, sans-serif; font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'>").append(enroll).append("</td>");
                        } else {
                            grid.append("<td spellcheck='false' style='font-weight: normal; font-family: Arial, sans-serif; font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'>").append(enroll).append("</td>");
                        }
                    }
                } else if (idx < enrollList.size()) {
                    String enroll = enrollList.get(idx);
                    if (enroll == null || enroll.equalsIgnoreCase("null")) enroll = "";
                    enroll = enroll.replaceAll("<[^>]*>", "").trim();
                    if (enroll.isEmpty()) {
                        grid.append("<td spellcheck='false' style='font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'></td>");
                    } else {
                        boolean highlightCell = false;
                        if (!primaryBranch.isEmpty()) {
                            String branch = getBranchFromEnrollment(enroll);
                            highlightCell = !branch.isEmpty() && !branch.equalsIgnoreCase(primaryBranch);
                        } else {
                            highlightCell = ((r + c) % 2 == 1);
                        }

                        if (highlightCell) {
                            grid.append("<td spellcheck='false' style='font-style: italic; font-weight: bold; font-family: Arial, sans-serif; font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'>").append(enroll).append("</td>");
                        } else {
                            grid.append("<td spellcheck='false' style='font-weight: normal; font-family: Arial, sans-serif; font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'>").append(enroll).append("</td>");
                        }
                    }
                } else {
                    grid.append("<td spellcheck='false' style='font-size: ").append(gridFontSize).append("px; text-decoration: none !important;'></td>");
                }
            }
            grid.append("</tr>");
        }
        grid.append("</table>");
        grid.append("</div>");

        // Fetch Range Table from DB if rangeRaw is empty/null
        if (rangeRaw == null || rangeRaw.isEmpty()) {
            try {
                String rangeTable = tableName.trim() + "_Range";
                List<List<String>> fetched = ArrangementsDB.fetcharrData(rangeTable);
                if (fetched != null) {
                    rangeRaw = fetched;
                }
            } catch (Exception e) {
                System.out.println("Error fetching range raw data for table " + tableName + "_Range: " + e.getMessage());
            }
        }

        // Group Range Table by PaperCode (rowspan)
        Map<String, List<List<String>>> groupedRanges = new LinkedHashMap<>();
        if (rangeRaw != null) {
            for (List<String> rd : rangeRaw) {
                if (rd.size() > 1) {
                    String paperCode = rd.get(1);
                    if (paperCode == null || paperCode.equalsIgnoreCase("null") || paperCode.trim().isEmpty()) {
                        continue;
                    }
                    groupedRanges.computeIfAbsent(paperCode.trim().replaceAll("<[^>]*>", ""), k -> new ArrayList<>()).add(rd);
                }
            }
        }

        int summaryRowsCount = 0;
        if (!groupedRanges.isEmpty()) {
            for (Map.Entry<String, List<List<String>>> entry : groupedRanges.entrySet()) {
                summaryRowsCount += entry.getValue().size();
            }
        } else {
            summaryRowsCount = 1;
        }
        int rangeFlex = 2 + summaryRowsCount + 1; // 2 header rows + body rows + 1 total row

        StringBuilder rangeHtml = new StringBuilder();
        rangeHtml.append("<div class='table-container' style='flex: ").append(rangeFlex).append(";'>");
        rangeHtml.append("<table class='summary-table'>");
        rangeHtml.append("<thead>");
        rangeHtml.append("<tr>");
        rangeHtml.append("<th rowspan='2' style='width: 25%;'>Paper Code</th>");
        rangeHtml.append("<th colspan='2'>Roll No.</th>");
        rangeHtml.append("<th rowspan='2' style='width: 15%;'>Total</th>");
        rangeHtml.append("</tr>");
        rangeHtml.append("<tr>");
        rangeHtml.append("<th style='width: 30%;'>From</th>");
        rangeHtml.append("<th style='width: 30%;'>To</th>");
        rangeHtml.append("</tr>");
        rangeHtml.append("</thead>");
        rangeHtml.append("<tbody>");

        int overallTotal = 0;
        if (!groupedRanges.isEmpty()) {
            for (Map.Entry<String, List<List<String>>> entry : groupedRanges.entrySet()) {
                String paperCode = entry.getKey();
                List<List<String>> rowsList = entry.getValue();
                int rowspan = rowsList.size();

                for (int i = 0; i < rowspan; i++) {
                    List<String> rd = rowsList.get(i);
                    rangeHtml.append("<tr>");
                    if (i == 0) {
                        rangeHtml.append("<td rowspan='").append(rowspan).append("' spellcheck='false'>").append(paperCode).append("</td>");
                    }

                    String from = rd.size() > 2 ? rd.get(2) : "";
                    String to = rd.size() > 3 ? rd.get(3) : "";
                    String totalStr = rd.size() > 4 ? rd.get(4) : "";

                    if (from == null || from.equalsIgnoreCase("null")) from = "";
                    if (to == null || to.equalsIgnoreCase("null")) to = "";
                    if (totalStr == null || totalStr.equalsIgnoreCase("null")) totalStr = "";

                    from = from.replaceAll("<[^>]*>", "").trim();
                    to = to.replaceAll("<[^>]*>", "").trim();
                    totalStr = totalStr.replaceAll("<[^>]*>", "").trim();

                    try {
                        if (!totalStr.isEmpty()) {
                            overallTotal += Integer.parseInt(totalStr);
                        }
                    } catch (NumberFormatException ignored) {}

                    boolean highlightRange = false;
                    if (!primaryBranch.isEmpty()) {
                        String rangeBranch = getBranchFromEnrollment(from);
                        if (rangeBranch.isEmpty() && !to.isEmpty()) {
                            rangeBranch = getBranchFromEnrollment(to);
                        }
                        highlightRange = !rangeBranch.isEmpty() && !rangeBranch.equalsIgnoreCase(primaryBranch);
                    }

                    String fromContent = from;
                    String toContent = to;
                    if (highlightRange && !from.isEmpty()) {
                        fromContent = "<span style='font-style: italic; font-weight: bold; text-decoration: none !important;'>" + from + "</span>";
                    }
                    if (highlightRange && !to.isEmpty()) {
                        toContent = "<span style='font-style: italic; font-weight: bold; text-decoration: none !important;'>" + to + "</span>";
                    }

                    rangeHtml.append("<td spellcheck='false'>").append(fromContent).append("</td>");
                    rangeHtml.append("<td spellcheck='false'>").append(toContent).append("</td>");
                    rangeHtml.append("<td spellcheck='false'>").append(totalStr).append("</td>");
                    rangeHtml.append("</tr>");
                }
            }
        } else {
            // Placeholder rows if empty
            rangeHtml.append("<tr><td>&nbsp;</td><td></td><td></td><td></td></tr>");
        }

        // Add overall total row
        rangeHtml.append("<tr>");
        rangeHtml.append("<td colspan='3' style='text-align:right; font-weight:bold; padding-right:15px;'>Total</td>");
        rangeHtml.append("<td style='font-weight:bold;'>").append(overallTotal).append("</td>");
        rangeHtml.append("</tr>");
        rangeHtml.append("</tbody>");
        rangeHtml.append("</table>");
        rangeHtml.append("</div>");

        // Invigilators info
        String teacher1 = "";
        String teacher2 = "";
        if (teachers != null) {
            if (teachers.size() > 0 && teachers.get(0) != null) teacher1 = cleanHtml(teachers.get(0).getName());
            if (teachers.size() > 1 && teachers.get(1) != null) teacher2 = cleanHtml(teachers.get(1).getName());
        }

        String examTime = config.getExamTime();
        if (examTime == null || examTime.equalsIgnoreCase("null")) {
            examTime = "";
        }
        examTime = cleanHtml(examTime);

        String semester = config.getSemester();
        if (semester == null) semester = "";
        semester = cleanHtml(semester);

        // Build info section lines using a borderless table
        StringBuilder infoRows = new StringBuilder();
        for (Map.Entry<String, String> entry : branchToSubjectInfo.entrySet()) {
            infoRows.append("<tr>")
                    .append("<td style='width: 120px; font-weight: bold;'>Branch: ").append(cleanHtml(entry.getKey())).append("</td>")
                    .append("<td style='font-weight: bold;'>Sub Code/Subject Name: - ").append(cleanHtml(entry.getValue())).append("</td>")
                    .append("</tr>\n");
        }

        String roomDisplayName = cleanHtml(getRoomDisplayName(roomNo));
        dateVal = cleanHtml(dateVal);
        sessionVal = cleanHtml(sessionVal).replace("_", "-");

        return """
        <div class='page' style='padding: 10mm {paddingSide}mm;'>
            <div class='header'>
                <h2>SAGAR INSTITUTE OF SCIENCE, TECHNOLOGY &amp; RESEARCH BHOPAL (SISTec-R)</h2>
                <h3>B. Tech. {semester} SEM EXAMINATION (Seating Plan)</h3>
                <div class='session-year'>{session}</div>
                <div class='room-container'>
                    <div class='room-line'>Room No: {roomDisplayName}</div>
                    <div class='date-right'>Date: {date}</div>
                </div>
            </div>
            
            <table class='info-table'>
                {infoRows}
                <tr>
                    <td colspan='2' class='time-line'>Time: {examTime}</td>
                </tr>
            </table>
            
            {grid}
            
            {rangeHtml}
            
            <div class='table-container' style='flex: 3;'>
                <table class='present-table'>
                    <thead>
                        <tr>
                            <th style='width: 10%;'>S.No.</th>
                            <th style='width: 40%;'>No. of Present Student</th>
                            <th style='width: 40%;'>No. of Absent Student</th>
                            <th style='width: 10%;'>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class='table-container' style='flex: 3;'>
                <table class='inv-table'>
                    <thead>
                        <tr>
                            <th style='width: 10%;'>S.No</th>
                            <th style='width: 40%;'>Name of Invigilators</th>
                            <th style='width: 15%;'>Designation</th>
                            <th style='width: 15%;'>Branch</th>
                            <th style='width: 20%;'>Signature</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td>1</td>
                            <td style='text-align: center; vertical-align: middle;'>{teacher1}</td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                        <tr>
                            <td>2</td>
                            <td style='text-align: center; vertical-align: middle;'>{teacher2}</td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <div class='footer'>
                <div>(Exam Supdt.)</div>
                <div>(Observer)</div>
            </div>
        </div>
        """
                .replace("{semester}", semester)
                .replace("{session}", sessionVal)
                .replace("{roomDisplayName}", roomDisplayName)
                .replace("{date}", dateVal)
                .replace("{infoRows}", infoRows.toString())
                .replace("{examTime}", examTime)
                .replace("{grid}", grid.toString())
                .replace("{rangeHtml}", rangeHtml.toString())
                .replace("{teacher1}", teacher1)
                .replace("{teacher2}", teacher2)
                .replace("{paddingSide}", String.valueOf(paddingSide));
    }

    public static String wrapInHtmlDoc(String bodyContent) {
        return """
        <!DOCTYPE html>
        <html spellcheck='false'>
        <head>
        <meta charset='utf-8'>
        <title>Exam Seating Plan</title>
        <style>
        * {
            box-sizing: border-box;
            text-decoration: none !important;
        }
        th, td, span, div, p, h1, h2, h3, h4, a, u, b, i {
            text-decoration: none !important;
        }
        th, td {
            background-color: white !important;
            background: white !important;
        }
        ::spelling-error, *::spelling-error {
            text-decoration: none !important;
            background-color: transparent !important;
            background: transparent !important;
            color: inherit !important;
        }
        ::grammar-error, *::grammar-error {
            text-decoration: none !important;
            background-color: transparent !important;
            background: transparent !important;
            color: inherit !important;
        }
        @page {
            size: A4 portrait;
            margin: 18mm 10mm;
        }
        html, body {
            margin: 0;
            padding: 0;
            width: 100%;
            height: 100%;
            background: #f3f4f6;
            font-family: 'Times New Roman', Times, serif;
            -webkit-print-color-adjust: exact;
            print-color-adjust: exact;
        }
        .page {
            width: 190mm;
            height: 261mm;
            margin: 18mm auto;
            background: white;
            box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06);
            display: flex;
            flex-direction: column;
            justify-content: flex-start;
            page-break-after: always;
            page-break-inside: avoid;
        }
        
        .header {
            position: relative;
            text-align: center;
            margin-bottom: 8px;
            flex: 0 0 auto;
        }
        .header h2 {
            margin: 0;
            font-size: 24px;
            font-weight: bold;
            text-transform: uppercase;
        }
        .header h3 {
            margin: 4px 0 0;
            font-size: 21px;
            font-weight: bold;
        }
        .session-year {
            margin: 2px 0 0;
            font-size: 19px;
            font-weight: bold;
        }
        .room-container {
            display: flex;
            justify-content: center;
            position: relative;
            margin-top: 6px;
        }
        .room-line {
            font-size: 22px;
            font-weight: bold;
            font-style: italic;
            text-align: center;
        }
        .date-right {
            position: absolute;
            right: 0;
            bottom: 0;
            font-size: 18px;
            font-style: italic;
            font-weight: normal;
        }
        
        .info-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 12px;
            flex: 0 0 auto;
        }
        .info-table td {
            border: none !important;
            padding: 3px 0;
            font-size: 17.5px;
            font-weight: bold;
            text-align: left;
            background-color: transparent !important;
            background: transparent !important;
        }
        .time-line {
            font-style: italic;
            font-weight: bold;
            padding-top: 4px !important;
        }
        
        .table-container {
            display: block;
            margin-bottom: 12px;
            min-height: 0;
        }
        .table-container table {
            height: 100%;
            width: 100%;
        }
        
        .seat-table {
            width: 100%;
            border-collapse: collapse;
            table-layout: fixed;
        }
        .seat-table td {
            border: 1px solid black;
            text-align: center;
            padding: 2px 1px;
            font-size: 19px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: clip;
        }
        
        .summary-table {
            width: 100%;
            border-collapse: collapse;
        }
        .summary-table th, .summary-table td {
            border: 1px solid black;
            padding: 4px 10px;
            text-align: center;
            font-size: 18px;
        }
        .summary-table th {
            font-weight: bold;
        }
        
        .present-table {
            width: 100%;
            border-collapse: collapse;
        }
        .present-table th, .present-table td {
            border: 1px solid black;
            padding: 4px 10px;
            text-align: center;
            font-size: 18px;
        }
        .present-table th {
            font-weight: bold;
        }
        
        .inv-table {
            width: 100%;
            border-collapse: collapse;
        }
        .inv-table th, .inv-table td {
            border: 1px solid black;
            padding: 4px 10px;
            text-align: center;
            font-size: 18px;
            vertical-align: middle;
        }
        .inv-table th {
            font-weight: bold;
        }
        
        .footer {
            display: flex;
            justify-content: space-between;
            font-size: 18px;
            font-weight: bold;
            padding-top: 10px;
            flex: 0 0 auto;
        }
        
        @media print {
            html, body {
                background: none;
                width: 190mm;
                height: 261mm;
            }
            .page {
                width: 190mm;
                height: 261mm;
                margin: 0;
                box-shadow: none;
                display: flex;
                flex-direction: column;
                justify-content: flex-start;
                page-break-after: always;
                page-break-inside: avoid;
            }
        }
        </style>
        </head>
        <body spellcheck='false'>
        {bodyContent}
        </body>
        </html>
        """
                .replace("{bodyContent}", bodyContent);
    }

    private static String getRoomDisplayName(String roomNo) {
        if (roomNo == null) return "";
        return roomNo.trim();
    }

    private static String extractBranch(String subCode) {
        if (subCode == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < subCode.length(); i++) {
            char c = subCode.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                break;
            }
        }
        return sb.toString().toUpperCase();
    }

    private static String fetchSubjectName(String subjectCode) {
        if (subjectCode == null || subjectCode.isEmpty()) return "";
        String name = "";
        try (Connection con = com.planner.Database.database.connection()) {
            if (con != null) {
                String query = "SELECT subject_name FROM rgpv_subjects WHERE subject_code = ?";
                try (PreparedStatement ps = con.prepareStatement(query)) {
                    ps.setString(1, subjectCode);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            name = rs.getString("subject_name");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching subject name for " + subjectCode + ": " + e.getMessage());
        }
        return name;
    }

    private static String cleanHtml(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "").trim();
    }

    private static String getBranchFromEnrollment(String enroll) {
        if (enroll == null || enroll.isEmpty()) return "";
        String clean = enroll.replaceAll("<[^>]*>", "").trim();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("[A-Za-z]+").matcher(clean);
        if (matcher.find()) {
            return matcher.group().toUpperCase();
        }
        return "";
    }
};













