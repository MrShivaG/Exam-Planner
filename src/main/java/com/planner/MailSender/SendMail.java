package com.planner.MailSender;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import com.planner.Database.database;

public class SendMail {
    String templete = "<!DOCTYPE html>\n" +
            "\n" +
            "<html>\n" +
            "<head>\n" +
            "  <meta charset=\"UTF-8\">\n" +
            "  <title>Sitting Arrangement</title>\n" +
            "</head>\n" +
            "<body style=\"margin:0; padding:0; font-family: Arial, sans-serif; background-color:#f4f4f4;\">\n" +
            "\n" +
            "  <table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f4f4; padding:20px;\">\n" +
            "    <tr>\n" +
            "      <td align=\"center\">\n" +
            "\n" +
            "```\n" +
            "    <!-- Main Container -->\n" +
            "    <table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#ffffff; border-radius:8px; overflow:hidden;\">\n" +
            "\n" +
            "      <!-- Header -->\n" +
            "      <tr>\n" +
            "        <td style=\"background:#1a237e; color:#ffffff; padding:20px; text-align:center;\">\n" +
            "          <h2 style=\"margin:0;\">SISTec-R</h2>\n" +
            "          <p style=\"margin:5px 0 0;\">Examination Seating Details</p>\n" +
            "        </td>\n" +
            "      </tr>\n" +
            "\n" +
            "      <!-- Body -->\n" +
            "      <tr>\n" +
            "        <td style=\"padding:25px; color:#333333;\">\n" +
            "\n" +
            "          <p>Dear <strong>{{NAME}}</strong>,</p>\n" +
            "\n" +
            "          <p>\n" +
            "            You are hereby informed about your examination seating arrangement.\n" +
            "            Please find your details below:\n" +
            "          </p>\n" +
            "\n" +
            "          <!-- Info Box -->\n" +
            "          <table width=\"100%\" cellpadding=\"10\" cellspacing=\"0\" style=\"margin:20px 0; border:1px solid #ddd; border-radius:6px;\">\n" +
            "            <tr style=\"background:#f0f0f0;\">\n" +
            "              <td><strong>Name</strong></td>\n" +
            "              <td>{{NAME}}</td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "              <td><strong>Enrollment Number</strong></td>\n" +
            "              <td>{{Enroll_NO}}</td>\n" +
            "            </tr>\n" +
            "            <tr>\n" +
            "              <td><strong>Room Number</strong></td>\n" +
            "              <td>{{ROOM_NO}}</td>\n" +
            "            </tr>\n" +
            "          </table>\n" +
            "\n" +
            "          <p>\n" +
            "            Kindly arrive at least <strong>15 minutes early</strong> and carry your\n" +
            "            admit card/ID for verification.\n" +
            "          </p>\n" +
            "\n" +
            "          <p>\n" +
            "            Wishing you all the best for your examination.\n" +
            "          </p>\n" +
            "\n" +
            "          <p>\n" +
            "            Regards,<br>\n" +
            "            <strong>SISTec-R Examination Cell</strong>\n" +
            "          </p>\n" +
            "\n" +
            "        </td>\n" +
            "      </tr>\n" +
            "\n" +
            "      <!-- Footer -->\n" +
            "      <tr>\n" +
            "        <td style=\"background:#eeeeee; padding:15px; text-align:center; font-size:12px; color:#666;\">\n" +
            "          This is an automated email. Please do not reply.\n" +
            "        </td>\n" +
            "      </tr>\n" +
            "\n" +
            "    </table>\n" +
            "\n" +
            "  </td>\n" +
            "</tr>\n" +
            "```\n" +
            "\n" +
            "  </table>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n";
    public String Sendmail(String[] Enroll_no,String room_no) throws SQLException {
        database db = new database();
        Connection conn = db.connection();
        final String from = "shivaahirwar2005@gmail.com";
        final String password = "fkaw ttha grxr isvb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        String ReturnStatement="Mails sent succesfully\nUnable to send mail to these candidates: ";
        for (String enroll : Enroll_no) {

            PreparedStatement ps = conn.prepareStatement("select Email_ID,Name from students where Enroll_no= '"+enroll+"';");
            ResultSet rs = ps.executeQuery();
            String email_id = null;
            String name = "";
            while (rs.next()) {
                email_id = rs.getString("Email_ID");
                name = rs.getString("Name");
            }
            if(email_id!=null) {
                System.out.println(name+email_id+"ji");
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(from));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email_id));
                    String html = templete
                            .replace("{{NAME}}", name)
                            .replace("{{ROOM_NO}}", room_no)
                            .replace("{{Enroll_NO}}",enroll);

                    message.setSubject("Regarding your today examination");
                    message.setContent(html, "text/html");
                    //message.setText("Hi,\n "+name+"\n from SISTec-R\n here are your sitting details"+room_no);

                    Transport.send(message);

                    System.out.println("Email sent");
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
            else {
                ReturnStatement=ReturnStatement+enroll;
            }


        }
        return ReturnStatement;

    }

}