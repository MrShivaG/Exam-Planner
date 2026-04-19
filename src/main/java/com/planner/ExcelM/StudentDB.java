package com.planner.ExcelM;

import com.planner.Database.database;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class StudentDB {
    public String fatchExcel(String excelFilePath) {
        try (
                FileInputStream inputStream = new FileInputStream(excelFilePath);
                Workbook workbook = new XSSFWorkbook(inputStream);
        ) {
            database db = new database();
            Connection conn = db.connection();
            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = 0;
            for (Row row : sheet) {
                if (rowCount == 0) {
                    rowCount++;
                    continue;
                }
                PreparedStatement ps1 = conn.prepareStatement("Insert ignore into students values (?,?,?,?,?)");
                ps1.setString(1, row.getCell(0).getStringCellValue());
                ps1.setString(2, row.getCell(1).getStringCellValue());
                ps1.setString(3, row.getCell(2).getStringCellValue());
                ps1.setString(4, row.getCell(3).getStringCellValue());
                ps1.setString(5, row.getCell(4).getStringCellValue());
                ps1.executeUpdate();

            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Data inserted successfully";
    }


}
