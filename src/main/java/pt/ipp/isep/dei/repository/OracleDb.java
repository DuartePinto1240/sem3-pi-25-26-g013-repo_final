package main.java.pt.ipp.isep.dei.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDb {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Oracle JDBC driver not found", e);
        }

        return DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASS
        );
    }
}