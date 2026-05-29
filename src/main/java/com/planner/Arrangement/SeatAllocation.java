package com.planner.Arrangement;

import com.planner.Database.ArrangementsDB;

import java.sql.*;
import java.util.*;

public class SeatAllocation {

    /**
     * Returns a list of SubCodes from subCodes[] that can be assigned
     * to the given seatNumber without conflicting with adjacent seats.
     *
     * Adjacency rules (no same SubCode allowed):
     *   seatNumber - 1    → left seat  (same row only)
     *   seatNumber + 1    → right seat (same row only)
     *   seatNumber - rows → seat directly in front
     *   seatNumber + rows → seat directly behind
     *
     * @param connection  Active MySQL JDBC connection
     * @param tableName   Name of the exam seating table
     * @param rows        Number of seats per row (columns in classroom)
     * @param subCodes    List of SubCodes to evaluate for this seat
     * @param seatNumber  The seat (Id) being evaluated — 1-based
     * @return            List of SubCodes safe to place at seatNumber
     */
    public static List<String> seatAllocation(
            Connection connection,
            String tableName,
            int rows,
            List<String> subCodes,
            int seatNumber
    ) throws SQLException {

        // ── 1. Determine adjacent seat IDs ───────────────────────────────────
        List<Integer> adjacentIds = new ArrayList<>();

        int currentRow = (seatNumber - 1) / rows;   // 0-based row index

        int leftSeat  = seatNumber - 1;
        int rightSeat = seatNumber + 1;

        int leftRow  = (leftSeat  >= 1) ? (leftSeat  - 1) / rows : -1;
        int rightRow = (rightSeat - 1) / rows;

        // Left neighbour — only if it's in the same row
        if (leftSeat >= 1 && leftRow == currentRow) {
            adjacentIds.add(leftSeat);
        }

        // Right neighbour — only if it's in the same row
        if (rightRow == currentRow) {
            adjacentIds.add(rightSeat);
        }

        // Front neighbour
        int frontSeat = seatNumber - rows;
        if (frontSeat >= 1) {
            adjacentIds.add(frontSeat);
        }

        // Back neighbour (upper bound validated against DB results)
        int backSeat = seatNumber + rows;
        adjacentIds.add(backSeat);

        // If no neighbours, all subCodes are valid
        if (adjacentIds.isEmpty()) {
            return new ArrayList<>(subCodes);
        }

        // ── 2. Fetch SubCodes of adjacent seats from DB ───────────────────────
        Set<String> conflictingSubCodes = new HashSet<>();

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < adjacentIds.size(); i++) {
            placeholders.append(i == 0 ? "?" : ", ?");
        }

        String query = String.format(
                "SELECT Id, SubCode FROM `%s` " +
                        "WHERE Id IN (%s) " +
                        "AND Enroll_no IS NOT NULL " +
                        "AND SubCode IS NOT NULL",
                tableName, placeholders
        );

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < adjacentIds.size(); i++) {
                stmt.setInt(i + 1, adjacentIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    conflictingSubCodes.add(rs.getString("SubCode"));
                }
            }
        }

        // ── 3. Filter and return allowed SubCodes ─────────────────────────────
        List<String> allowed = new ArrayList<>();
        for (String sc : subCodes) {
            if (!conflictingSubCodes.contains(sc)) {
                allowed.add(sc);
            }
        }

        return allowed;
    }


    /**
     * Bulk allocation: returns a map of { seatNumber → [allowedSubCodes] }
     * for every seat from 1 to totalSeats.
     *
     * @param connection  Active MySQL JDBC connection
     * @param tableName   Name of the exam seating table
     * @param rows        Number of seats per row
     * @param subCodes    List of all SubCodes to evaluate
     * @param totalSeats  Total number of seats in the room
     * @return            Map of seatNumber to list of allowed SubCodes
     */
    public static Map<Integer, List<String>> allocateAllSeats(
            Connection connection,
            String tableName,
            int rows,
            List<String> subCodes,
            int totalSeats
    ) throws SQLException {

        Map<Integer, List<String>> result = new LinkedHashMap<>();

        for (int seat = 1; seat <= totalSeats; seat++) {
            List<String> allowed = seatAllocation(
                    connection, tableName, rows, subCodes, seat
            );
            result.put(seat, allowed);
        }

        return result;
    }


    // ── Demo / quick test ────────────────────────────────────────────────────
    public static void main(String[] args) throws SQLException {

        // ── DB credentials ───────────────────────────────────────────────────
//        String url      = "jdbc:mysql://localhost:3306/exam_db";
//        String user     = "root";
//        String password = "yourpassword";
        ArrangementsDB db = new ArrangementsDB();
        Connection con = db.connection();

        String tableName = "SISTecR_GRP_9_erghgfdfghnj_June_26_III";
        int    rows      = 9;   // seats per row (class has 2-wide columns)

        List<String> subCodes = Arrays.asList("CE801", "CS801", "ME801");
        List<String> allowedForSeat5 = seatAllocation(
                    con, tableName, rows, subCodes, 5
            );
        System.out.println("Allowed SubCodes for seat " + 5 + ": " + allowedForSeat5);


//        try (Connection conn = DriverManager.getConnection(url, user, password)) {
//
//            // ── Single seat check ────────────────────────────────────────────
//            int targetSeat = 5;
//            List<String> allowedForSeat5 = seatAllocation(
//                    conn, tableName, rows, subCodes, targetSeat
//            );
//            System.out.println("Allowed SubCodes for seat " + targetSeat + ": " + allowedForSeat5);
//
//            // ── Bulk check for first 10 seats ────────────────────────────────
//            System.out.println("\nBulk allocation (seats 1–10):");
//            Map<Integer, List<String>> allSeats = allocateAllSeats(
//                    conn, tableName, rows, subCodes, 10
//            );
//            allSeats.forEach((seat, codes) ->
//                    System.out.printf("  Seat %2d → %s%n", seat, codes)
//            );
//
//        } catch (SQLException e) {
//            System.err.println("DB error: " + e.getMessage());
//            e.printStackTrace();
//        }
    }
}