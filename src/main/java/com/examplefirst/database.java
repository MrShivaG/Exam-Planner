package com.examplefirst;

public class database {
    static String URL = "jdbc:mysql://192.168.1.169:3306/hello";
    static String username = "root";
    static String password = "root";

    public static void main(String[] args) {


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("database connected");

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
