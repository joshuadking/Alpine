package com.arcturusmc.alpine.db;

import com.arcturusmc.alpine.configuration.ConfigManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLManager {

    private final String HOST = ConfigManager.getHost();
    private final String DATABASE = ConfigManager.getDatabase();
    private final String USER = ConfigManager.getUsername();
    private final String PASSWORD = ConfigManager.getPassword();
    private final int PORT = ConfigManager.getPort();

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(
                "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE,
                USER,
                PASSWORD);
    }

    public boolean isConnected() { return connection != null; }

    public void disconnect() {
        if(isConnected()) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("Failed to disconnect from the database!");
                e.printStackTrace();
            }
        }
    }
}
