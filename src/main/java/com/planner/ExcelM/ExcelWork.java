package com.planner.ExcelM;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.planner.Database.database;


public class ExcelWork {

    public ArrayList<String> fatchExcel(String excelFilePath) {
        ArrayList<String> result = new ArrayList<>();
        try (
                FileInputStream inputStream = new FileInputStream(excelFilePath);
                Workbook workbook = new XSSFWorkbook(inputStream);
        ) {
            database db = new database();
            Connection conn = db.connection();
            PreparedStatement ps0 = conn.prepareStatement("DROP table RawData");
            ps0.executeUpdate();
            PreparedStatement ps = conn.prepareStatement("create table RawData (Enroll_no varchar(100),Sub_code varchar(50),Branch varchar(50))");
            ps.executeUpdate();

            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = 0;

            for (Row row : sheet) {
                if (rowCount == 0) {
                    rowCount++;
                    continue;
                }

//                String Enroll_no = row.getCell(0).getStringCellValue();
//                String Sub_code = row.getCell(1).getStringCellValue();
//                String Branch = row.getCell(2).getStringCellValue();
                PreparedStatement ps1 = conn.prepareStatement("Insert into RawData values (?,?,?)");
                ps1.setString(1, row.getCell(0).getStringCellValue());
                ps1.setString(2, row.getCell(1).getStringCellValue());
                ps1.setString(3, row.getCell(2).getStringCellValue());
                ps1.executeUpdate();

            }


            workbook.close();
            PreparedStatement ps3 =conn.prepareStatement("SELECT Count(*) as total from rawdata");
            ResultSet rs = ps3.executeQuery();
            if (rs.next()) {
                result.add(rs.getString("total"));
            }
            PreparedStatement ps4 =conn.prepareStatement("SELECT DISTINCT Sub_code from rawdata");
            ResultSet rs2 = ps4.executeQuery();
            while (rs2.next()) {
                result.add(rs2.getString("Sub_code"));
            }



            System.out.println("Data inserted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        ExcelWork excelWork = new ExcelWork();
        String path = "C:\\Users\\shiva\\Documents\\java-projects\\Exam-Planner\\src\\main\\java\\com\\planner\\ExcelM\\Students.xlsx";
        ArrayList<String> result = excelWork.fatchExcel(path);
        result.forEach(System.out::println);

    }

}
