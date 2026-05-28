package com.planner.Arrangement;

import com.planner.Arrangement.Student;

import java.sql.*;
import java.util.*;

public class SeatAllocatorV2 {

    /*
        FINAL RETURN OBJECT
    */
    public static class AllocationResult {

        /*
            KEY   = Seat ID
            VALUE = Allocated Student
        */
        public Map<Integer, Student> allocatedSeats =
                new LinkedHashMap<>();

        /*
            Remaining students grouped by subcode
        */
        public ArrayList<NewStudents> remainingGroups =
                new ArrayList<>();
    }


    /*
        MAIN ALLOCATION METHOD
    */
    public static AllocationResult allocateSeats(
            Connection conn,
            String table,
            int rows,
            ArrayList<NewStudents> groupedStudents
    ) throws Exception {

        AllocationResult result =
                new AllocationResult();

        /*
            LOAD EXISTING HALL
        */
        Map<Integer, Seat> seats =
                loadSeats(conn, table);

        /*
            WORKING COPY
        */
        ArrayList<NewStudents> workingGroups =
                deepCopyGroups(groupedStudents);

        /*
            PROCESS BLANK SEATS
        */
        for (Seat blankSeat : seats.values()) {

            if (!isBlank(blankSeat))
                continue;

            Student bestStudent = null;

            NewStudents ownerGroup = null;

            int bestScore = Integer.MAX_VALUE;

            /*
                FIND BEST STUDENT
            */
            for (NewStudents group : workingGroups) {

                if (group == null)
                    continue;

                List<Student> students =
                        group.getStudents();

                if (students == null)
                    continue;

                for (Student candidate : students) {

                    if (candidate == null)
                        continue;

                    int score =
                            calculateConflictScore(
                                    seats,
                                    blankSeat.id,
                                    rows,
                                    candidate
                            );

                    /*
                        LOWER SCORE = BETTER
                    */
                    if (score < bestScore) {

                        bestScore = score;

                        bestStudent = candidate;

                        ownerGroup = group;
                    }
                }
            }

            /*
                ALLOCATE
            */
            if (bestStudent != null) {

                result.allocatedSeats.put(
                        blankSeat.id,
                        bestStudent
                );

                /*
                    UPDATE VIRTUAL HALL
                */
                blankSeat.enroll =
                        bestStudent.Enroll_no;

                blankSeat.subCode =
                        bestStudent.SubCode;

                blankSeat.status =
                        "allocated";

                /*
                    REMOVE STUDENT
                */
                ownerGroup.getStudents()
                        .remove(bestStudent);
            }
        }

        /*
            REMOVE EMPTY GROUPS
        */
        Iterator<NewStudents> iterator =
                workingGroups.iterator();

        while (iterator.hasNext()) {

            NewStudents group = iterator.next();

            if (
                    group == null ||
                            group.getStudents() == null ||
                            group.getStudents().isEmpty()
            ) {

                iterator.remove();
            }
        }

        /*
            SAVE REMAINING
        */
        result.remainingGroups =
                workingGroups;

        return result;
    }


    /*
        CONFLICT SCORE ENGINE

        LOWER SCORE = BETTER
    */
    private static int calculateConflictScore(
            Map<Integer, Seat> seats,
            int seatId,
            int rows,
            Student candidate
    ) {

        int score = 0;

        /*
            PRIMARY NEIGHBORS
        */
        int front = seatId - 1;
        int back  = seatId + 1;

        int left  = seatId - rows;
        int right = seatId + rows;

        /*
            FRONT/BACK
            HIGH RISK
        */
        score += evaluateNeighbor(
                seats.get(front),
                candidate,
                15
        );

        score += evaluateNeighbor(
                seats.get(back),
                candidate,
                15
        );

        /*
            LEFT/RIGHT
            MEDIUM RISK
        */
        score += evaluateNeighbor(
                seats.get(left),
                candidate,
                10
        );

        score += evaluateNeighbor(
                seats.get(right),
                candidate,
                10
        );

        /*
            DISTANCE-2
        */
        score += evaluateNeighbor(
                seats.get(front - 1),
                candidate,
                4
        );

        score += evaluateNeighbor(
                seats.get(back + 1),
                candidate,
                4
        );

        /*
            DIAGONALS
        */
        score += evaluateNeighbor(
                seats.get(seatId - rows - 1),
                candidate,
                6
        );

        score += evaluateNeighbor(
                seats.get(seatId - rows + 1),
                candidate,
                6
        );

        score += evaluateNeighbor(
                seats.get(seatId + rows - 1),
                candidate,
                6
        );

        score += evaluateNeighbor(
                seats.get(seatId + rows + 1),
                candidate,
                6
        );

        return score;
    }


    /*
        SINGLE NEIGHBOR CHECK
    */
    private static int evaluateNeighbor(
            Seat neighbor,
            Student candidate,
            int subPenalty
    ) {

        if (neighbor == null)
            return 0;

        if (isBlank(neighbor))
            return 0;

        if (
                safeEquals(
                        neighbor.subCode,
                        candidate.SubCode
                )
        ) {

            return subPenalty;
        }

        return 0;
    }


    /*
        LOAD HALL
    */
    private static Map<Integer, Seat> loadSeats(
            Connection conn,
            String table
    ) throws Exception {

        Map<Integer, Seat> map =
                new LinkedHashMap<>();

        String sql =
                "SELECT Id, Enroll_no, SubCode, Status " +
                        "FROM " + table +
                        " ORDER BY Id";

        PreparedStatement ps =
                conn.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {

            Seat s = new Seat();

            s.id =
                    rs.getInt("Id");

            s.enroll =
                    rs.getString("Enroll_no");

            s.subCode =
                    rs.getString("SubCode");

            s.status =
                    rs.getString("Status");

            map.put(s.id, s);
        }

        return map;
    }


    /*
        BLANK SEAT CHECK
    */
    private static boolean isBlank(
            Seat s
    ) {

        if (s == null)
            return false;

        if (
                s.status != null &&
                        s.status.equalsIgnoreCase("back")
        ) {

            return true;
        }

        if (s.enroll == null)
            return true;

        return s.enroll.equalsIgnoreCase("null");
    }


    /*
        SAFE STRING COMPARE
    */
    private static boolean safeEquals(
            String a,
            String b
    ) {

        if (a == null || b == null)
            return false;

        return a.equalsIgnoreCase(b);
    }


    /*
        COPY GROUPS

        REQUIRES:
            setStudents(...)
    */
    private static ArrayList<NewStudents> deepCopyGroups(
            ArrayList<NewStudents> original
    ) {

        ArrayList<NewStudents> copied =
                new ArrayList<>();

        for (NewStudents group : original) {

            if (group == null)
                continue;

            NewStudents newGroup =
                    new NewStudents();

            ArrayList<Student> students =
                    new ArrayList<>();

            if (group.getStudents() != null) {

                students.addAll(
                        group.getStudents()
                );
            }

            newGroup.setStudents(students);

            copied.add(newGroup);
        }

        return copied;
    }


    /*
        INTERNAL SEAT MODEL
    */
    static class Seat {

        int id;

        String enroll;

        String subCode;

        String status;
    }
}