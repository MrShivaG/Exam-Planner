package com.planner.Arrangement;

import com.planner.Database.ArrangementsDB;

import java.sql.*;
import java.util.*;

public class EnrollmentRangeBuilder {
    public static void buildRanges(Connection conn, String sourceTable, String rangeTable,String RoomNo, String ArrName)
            throws SQLException {

        // ── 1. Create range table if it doesn't exist ──────────────────────────
        String createSQL = "CREATE TABLE IF NOT EXISTS `" + rangeTable + "` ("
                + "  ID       INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,"
                + "  SubCode  VARCHAR(20)  NOT NULL,"
                + "  RangeFrom VARCHAR(20) NULL,"
                + "  RangeTo   VARCHAR(20) NULL,"
                + "  Total     INT         NOT NULL,"
                + "  RoomNo    VARCHAR(20) NULL,"
                +"Arr_name Varchar(70) NULL"
                + ")";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(createSQL);
        }

        // ── 2. Fetch all rows ordered by prefix (first 8 chars) then Enroll_no ─
        String selectSQL = "SELECT Enroll_no, SubCode "
                + "FROM `" + sourceTable + "` "
                + "ORDER BY LEFT(Enroll_no, 8), Enroll_no";

        // Group by (prefix + SubCode) → sorted list of enroll numbers
        // LinkedHashMap preserves insertion order (already sorted from DB)
        Map<String, List<String>> groups = new LinkedHashMap<>();
        Map<String, String> groupSubCode = new LinkedHashMap<>();

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(selectSQL)) {

            while (rs.next()) {
                String enrollNo = rs.getString("Enroll_no");
                String subCode  = rs.getString("SubCode");

                // Grouping key: first 8 chars + SubCode
                String prefix = enrollNo.length() >= 8 ? enrollNo.substring(0, 8) : enrollNo;
                String key = prefix + "|" + subCode;

                groups.computeIfAbsent(key, k -> new ArrayList<>()).add(enrollNo);
                groupSubCode.put(key, subCode);
            }
        }

        // ── 3. Insert each group as one range row ───────────────────────────────
        String insertSQL = "INSERT INTO `" + rangeTable + "` "
                + "(SubCode, RangeFrom, RangeTo, Total, RoomNo, Arr_name) VALUES (?, ?, ?, ?, ?,?)";


        try (PreparedStatement ps = conn.prepareStatement(insertSQL)) {

            for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
                String key     = entry.getKey();
                List<String> enrollList = entry.getValue();
                String subCode = groupSubCode.get(key);

                int total = enrollList.size();

                String rangeFrom;
                String rangeTo;

                if (total == 1) {
                    // Single entry: RangeFrom = enroll_no, RangeTo = NULL
                    rangeFrom = enrollList.get(0);
                    rangeTo   = null;
                } else {
                    // Multiple: first and last after sort
                    rangeFrom = enrollList.get(0);
                    rangeTo   = enrollList.get(total - 1);
                }

                ps.setString(1, subCode);
                ps.setString(2, rangeFrom);


                if (rangeTo == null) {
                    ps.setNull(3, Types.VARCHAR);
                } else {
                    ps.setString(3, rangeTo);
                }

                ps.setInt(4, total);
                ps.setString(5, RoomNo);
                ps.setString(6, ArrName);

                ps.addBatch();
            }

            ps.executeBatch();
        }

        System.out.println("Ranges inserted successfully into `" + rangeTable + "`.");
    }

//    public static void main(String[] args) throws Exception {
//        ArrangementsDB db = new ArrangementsDB();
//        Connection con = db.connection();
//        buildRanges(con,"SISTEC0537_GRP_209_arrang422gh_203437_V","test");
//
//    }
}