package com.examplefirst.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DB_Methods {
    static database databaseobj = new database();
    Connection con = databaseobj.connection();

    public DB_Methods() throws SQLException {
    }

    public static void main(String[] args) {
        System.out.println(databaseobj.connectDB());
    }


    public void insertData(int room_no, int rows, int column, int capacity, boolean availability) throws SQLException {

        PreparedStatement ps = con.prepareStatement("insert into class_room(room_no, class_row, class_column, capacity, availability) values(?, ?, ?, ?, ?)");

        ps.setInt(1,room_no);
        ps.setInt(2,rows);
        ps.setInt(3,column);
        ps.setInt(4,capacity);
        ps.setBoolean(5,availability);

        ps.executeUpdate();
        con.close();


    }
}
