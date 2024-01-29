package tp6bis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author postgresqltutorial.com
 */
public class ConnectionPostgreSQL{

    private final static String url = "jdbc:postgresql://localhost:5432/GolfClub";
    private final static String user = "postgres";
    private final static String password = "postgres";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public static Connection getInstance() {
        Connection conn = null;
        try {
             Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);
        }

        return conn;
    }
}