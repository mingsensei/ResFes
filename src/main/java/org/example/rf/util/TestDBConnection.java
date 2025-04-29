package org.example.rf.util;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDBConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Kết nối cơ sở dữ liệu thành công!");
                conn.close();
            } else {
                System.out.println("❌ Kết nối thất bại!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi SQL:");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("❌ Lỗi khác:");
            e.printStackTrace();
        }
    }
}
