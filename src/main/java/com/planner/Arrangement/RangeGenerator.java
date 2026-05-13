package com.planner.Arrangement;

import java.sql.*;
import java.util.*;
import java.util.regex.*;

class EnrollRecord {

    String enroll;
    String subCode;

    String groupKey;
    int sequence;
}

public class RangeGenerator {

    public static void generateRangeTable(
            Connection conn,
            String inputTable,
            String outputTable
    ) throws Exception {

        Statement st = conn.createStatement();

        // recreate output table
        st.executeUpdate(
                "DROP TABLE IF EXISTS " + outputTable
        );

        st.executeUpdate(
                "CREATE TABLE " + outputTable + " (" +
                        "Id INT AUTO_INCREMENT PRIMARY KEY," +
                        "PaperCode VARCHAR(100)," +
                        "RangeFrom VARCHAR(100)," +
                        "RangeTo VARCHAR(100)," +
                        "Total INT)"
        );

        ResultSet rs = st.executeQuery(
                "SELECT Enroll_no, SubCode FROM " + inputTable
        );

        Map<String, List<EnrollRecord>> groups =
                new HashMap<>();

        Set<String> seen = new HashSet<>();

        while (rs.next()) {

            String enroll =
                    rs.getString("Enroll_no");

            String sub =
                    rs.getString("SubCode");

            if (enroll == null)
                continue;

            String unique =
                    sub + "_" + enroll;

            // remove duplicate rows
            if (seen.contains(unique))
                continue;

            seen.add(unique);

            EnrollRecord r =
                    new EnrollRecord();

            r.enroll = enroll;
            r.subCode = sub;

            parseEnroll(r);

            String mapKey =
                    sub + "_" + r.groupKey;

            groups.computeIfAbsent(
                    mapKey,
                    k -> new ArrayList<>()
            ).add(r);
        }

        PreparedStatement ps =
                conn.prepareStatement(
                        "INSERT INTO " + outputTable +
                                "(PaperCode, RangeFrom, RangeTo, Total) " +
                                "VALUES (?, ?, ?, ?)"
                );

        for (String key : groups.keySet()) {

            List<EnrollRecord> list =
                    groups.get(key);

            list.sort(
                    Comparator.comparingInt(
                            a -> a.sequence
                    )
            );

            EnrollRecord start =
                    list.get(0);

            EnrollRecord prev =
                    start;

            int count = 1;

            for (int i = 1; i < list.size(); i++) {

                EnrollRecord cur =
                        list.get(i);

                // duplicate sequence
                if (cur.sequence ==
                        prev.sequence) {

                    continue;
                }

                // continuous
                if (cur.sequence ==
                        prev.sequence + 1) {

                    count++;

                } else {

                    insertRange(
                            ps,
                            start,
                            prev,
                            count
                    );

                    start = cur;
                    count = 1;
                }

                prev = cur;
            }

            insertRange(
                    ps,
                    start,
                    prev,
                    count
            );
        }

        ps.close();
        rs.close();
        st.close();
    }

    static void parseEnroll(
            EnrollRecord r
    ) {

        try {

            String cleaned =
                    r.enroll.replaceAll(
                            "[^A-Za-z0-9]",
                            ""
                    );

            Matcher m =
                    Pattern.compile("(\\d+)")
                            .matcher(cleaned);

            List<String> nums =
                    new ArrayList<>();

            while (m.find()) {

                nums.add(m.group());
            }

            // no numbers
            if (nums.isEmpty()) {

                r.groupKey = cleaned;

                r.sequence =
                        Integer.MAX_VALUE;

                return;
            }

            String last =
                    nums.get(nums.size() - 1);

            // tiny suffix like D2
            if (last.length() <= 2
                    && nums.size() >= 2) {

                last =
                        nums.get(nums.size() - 2);
            }

            String seqStr = last;

            // use last 4 digits only
            if (seqStr.length() > 4) {

                seqStr =
                        seqStr.substring(
                                seqStr.length() - 4
                        );
            }

            r.sequence =
                    Integer.parseInt(seqStr);

            r.groupKey =
                    cleaned.replaceFirst(
                            Pattern.quote(last),
                            ""
                    );

        } catch (Exception e) {

            r.groupKey = r.enroll;

            r.sequence =
                    Integer.MAX_VALUE;
        }
    }

    static void insertRange(
            PreparedStatement ps,
            EnrollRecord start,
            EnrollRecord end,
            int total
    ) throws Exception {

        ps.setString(
                1,
                start.subCode
        );

        ps.setString(
                2,
                start.enroll
        );

        // standalone/non-range
        if (total == 1) {

            ps.setNull(
                    3,
                    Types.VARCHAR
            );

        } else {

            ps.setString(
                    3,
                    end.enroll
            );
        }

        ps.setInt(
                4,
                total
        );

        ps.executeUpdate();
    }
}