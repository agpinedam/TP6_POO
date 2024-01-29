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

    private final String url = "jdbc:postgresql://localhost:5432/GolfClub";
    private final String user = "postgres";
    private final String password = "postgres";

    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    public Connection connect() {
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

    public int getNumberOfObjects() {
        String SQL = "SELECT count(*) FROM club";
        int count = 0;

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) {
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("hi\n");
        System.out.println(count);
        return count;
    }

    public int createAnObject() {
        String SQL = "INSERT INTO club (fabricant, poids,version) VALUES ('fabricant3', 10.5, 1);";
        int count = 0;

        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) {
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("hi\n");
        System.out.println(count);
        return count;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ConnectionPostgreSQL app = new ConnectionPostgreSQL();
        app.connect();
        app.createAnObject();
        app.getNumberOfObjects();
    }

    public static Connection getInstance() {    
        return null;
    }
}