package org.example.rf.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=RF;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa"; // Thay bằng username của SQL Server
    private static final String PASSWORD = "123"; // Thay bằng mật khẩu của SQL Server

    public static Connection getConnection() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Kết nối Database thất bại!", e);
        }
    }
}
