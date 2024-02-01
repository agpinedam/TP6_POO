package tp6bis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionPostgreSQL {

    private static final String url = "jdbc:postgresql://localhost:5432/GolfClub";
    private static final String user = "postgres";
    private static final String password = "postgres";

    private static Connection connection;

    private ConnectionPostgreSQL() {
        // Constructor privado para implementar el patrón Singleton
    }

    public static Connection getInstance() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the PostgreSQL server successfully.");
            } catch (SQLException e) {
                // Log or rethrow the exception
                System.out.println("Error connecting to the PostgreSQL server: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection closed.");
            } catch (SQLException e) {
                // Log or rethrow the exception
                System.out.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}
