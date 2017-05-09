package Connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) {
            System.out.println("Using open DB connection!");
            return connection;
        } else {
            System.out.println("DB connection closed!");
            connect();
        }
        return null;
    }

    public static void connect() {
        try {
            System.out.println("Attempting connection to DB...");
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            String serverName = "sql11.freemysqlhosting.net:3306";
            String myDatabase = "sql11168958";
            String url = "jdbc:mysql://" + serverName + "/" + myDatabase;
            String userName = "sql11168958";
            String password = "AD4x4SYdsz";
            connection = DriverManager.getConnection(url, userName, password);
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException ex) {
            System.out.println("Failed to connect to DB, please check your login data and internet connection, and restart server!");
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println("exception in Database at close()");
            ex.printStackTrace();
        }
    }

}
