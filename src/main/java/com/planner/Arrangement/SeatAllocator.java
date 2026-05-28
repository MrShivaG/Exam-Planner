package com.planner.Arrangement;

import java.sql.*;
import java.util.*;

/*
    Seat Recommendation Engine

    Assumptions:
    1. ID is continuous and represents seat position.
    2. Blank seat means:
            - Status = "back"
            OR
            - Enroll_no = "null"
            OR
            - Enroll_no IS NULL
    3. rows = number of seats per row.
    4. We must avoid same subject adjacency as much as possible.
    5. We prioritize:
            a. Front/Back analysis
            b. Left/Right analysis
            c. Frequency scoring
            d. Conflict minimization

    Returns:
        Map<SeatID, SuggestedSubCode>
*/

public class SeatAllocator {

    public static Map<Integer, String> getBestSeats(
            Connection conn,
            String table,
            int rows,
            List<String> allSubCodes
    ) throws Exception {

        Map<Integer, String> result = new LinkedHashMap<>();

        /*
            Load all seats once.
            Avoid querying DB repeatedly.
        */
        String query = "SELECT Id, Enroll_no, SubCode, Status FROM " + table + " ORDER BY Id";

        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        Map<Integer, Seat> seats = new HashMap<>();

        while (rs.next()) {

            Seat s = new Seat();

            s.id = rs.getInt("Id");
            s.enroll = rs.getString("Enroll_no");
            s.subCode = rs.getString("SubCode");
            s.status = rs.getString("Status");

            seats.put(s.id, s);
        }

        /*
            Process all blank seats
        */
        for (Seat current : seats.values()) {

            if (!isBlank(current))
                continue;

            int id = current.id;

            /*
                Neighbors

                    FRONT = id - 1
                    BACK  = id + 1

                    LEFT  = id - rows
                    RIGHT = id + rows
            */

            String front = getSubCode(seats, id - 1);
            String back  = getSubCode(seats, id + 1);

            String left  = getSubCode(seats, id - rows);
            String right = getSubCode(seats, id + rows);

            /*
                Count surrounding frequencies
            */
            Map<String, Integer> frequency = new HashMap<>();

            addFreq(frequency, front);
            addFreq(frequency, back);
            addFreq(frequency, left);
            addFreq(frequency, right);

            /*
                Blocked subjects
                We should avoid these if possible.
            */
            Set<String> blocked = new HashSet<>();

            if (front != null) blocked.add(front);
            if (back != null) blocked.add(back);
            if (left != null) blocked.add(left);
            if (right != null) blocked.add(right);

            /*
                BEST CASE:
                Choose a subject NOT present around seat.
            */
            String selected = null;

            for (String sub : allSubCodes) {
                if (!blocked.contains(sub)) {
                    selected = sub;
                    break;
                }
            }

            /*
                If impossible to avoid all conflicts,
                choose minimum conflict subject.
            */
            if (selected == null) {

                int minConflict = Integer.MAX_VALUE;

                for (String sub : allSubCodes) {

                    int score = frequency.getOrDefault(sub, 0);

                    /*
                        Additional penalty:
                        front/back same
                    */
                    if (Objects.equals(front, sub))
                        score += 5;

                    if (Objects.equals(back, sub))
                        score += 5;

                    /*
                        Additional penalty:
                        left/right same
                    */
                    if (Objects.equals(left, sub))
                        score += 3;

                    if (Objects.equals(right, sub))
                        score += 3;

                    /*
                        Prefer balanced placement.
                    */
                    if (score < minConflict) {
                        minConflict = score;
                        selected = sub;
                    }
                }
            }

            result.put(id, selected);
        }

        return result;
    }

    /*
        Blank seat checker
    */
    private static boolean isBlank(Seat s) {

        if (s == null)
            return false;

//        if (s.status != null &&
//                s.status.equalsIgnoreCase("back"))
//            return true;

        if (s.enroll == null)
            return true;

        return s.enroll.equalsIgnoreCase("null");
    }

    /*
        Safe neighbor fetch
    */
    private static String getSubCode(Map<Integer, Seat> seats, int id) {

        Seat s = seats.get(id);

        if (s == null)
            return null;

        if (isBlank(s))
            return null;

        if (s.subCode == null)
            return null;

        if (s.subCode.equalsIgnoreCase("null"))
            return null;

        return s.subCode;
    }

    private static void addFreq(Map<String, Integer> map, String sub) {

        if (sub == null)
            return;

        map.put(sub, map.getOrDefault(sub, 0) + 1);
    }

    static class Seat {

        int id;

        String enroll;

        String subCode;

        String status;
    }
}