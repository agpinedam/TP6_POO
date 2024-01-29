package tp6bis;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class EntityManagerImpl {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/GolfClub";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    // Method to create a table in PostgreSQL based on an object of a class
    public static void createTableFromClass(Object object) {
        // Get the class name
        String className = object.getClass().getSimpleName();

        // Check if the table already exists
        if (tableExists(className)) {
            System.out.println("Table already exists.");
            return;
        }

        // Get the fields of the class
        Field[] fields = object.getClass().getDeclaredFields();

        // Build the SQL query to create the table
        StringBuilder sqlQuery = new StringBuilder("CREATE TABLE " + className + " (");

        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();

            // Adjust the id field to be serial (autoincrement) and the primary key
            if (fieldName.equals("id")) {
                sqlQuery.append(fieldName).append(" SERIAL PRIMARY KEY, ");
            } else {
                // Assume all other fields are of primitive type or String in this example
                sqlQuery.append(fieldName).append(" ").append(getSQLDataType(fieldType)).append(", ");
            }
        }

        // Remove the extra comma at the end and close the query
        sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length()).append(");");

        // Execute the SQL query
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery.toString());
            System.out.println("Table created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to check if a table exists in the database
    private static boolean tableExists(String tableName) {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT to_regclass('" + tableName + "')");
            resultSet.next();
            return resultSet.getString(1) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Method to map Java data types to SQL data types (simplified)
    private static String getSQLDataType(String javaType) {
        switch (javaType) {
            case "int":
            case "Integer":
                return "INT";
            case "double":
            case "Double":
                return "DOUBLE PRECISION";
            case "float":
            case "Float":
                return "REAL";
            case "String":
                return "VARCHAR(255)";
            // Add more cases as needed for other data types
            default:
                return "VARCHAR(255)";
        }
    }

    public static void main(String[] args) {
        Club clubExample = new Club();
        createTableFromClass(clubExample);
    }
}