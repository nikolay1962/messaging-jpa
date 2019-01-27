package my.messages;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class SandBox {

    public static void main(String[] args) {
        System.out.println("milis:" + System.currentTimeMillis());
        System.out.println(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        System.out.println(Instant.ofEpochMilli(1536092965124l).atZone(ZoneId.systemDefault()).toLocalDateTime().toString());
        System.out.println(LocalDateTime.parse("2018-09-04T23:29:25.124"));
//        long lastLogoutMillis = user.getlastLogoutTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
//        String timeOfLastGotMessage = Instant.ofEpochMilli(lastMessageGet).atZone(ZoneId.systemDefault()).toLocalDate().toString();

        String url = "jdbc:mysql://localhost:3306/messenger";
        String user = "root";
        String pass = "root";

        try (Connection conn = DriverManager.getConnection(url, user, pass);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("password");
                System.out.println(id + "," + name + "," + email + "," + country);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
