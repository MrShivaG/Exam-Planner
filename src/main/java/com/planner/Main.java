package com.planner;

import com.planner.MailSender.SendMail;

import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.printf("Hello and welcome!");


        SendMail sendMail = new SendMail();
        String number = "103";
        System.out.println(sendMail.Sendmail(new String[] {"0537CS241051","0537CS241103"} ,number));
    }
}
