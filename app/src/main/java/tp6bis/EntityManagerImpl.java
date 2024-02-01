package tp6bis;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EntityManagerImpl {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/GolfClub";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    // Method to create a table in PostgreSQL based on an object of a class
    public void createTable(Object object) {
        String className = object.getClass().getSimpleName();
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

    public <T> T find(Class<T> entityClass, long id) {
        String tableName = entityClass.getSimpleName();
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                T entity = mapResultSetToEntity(resultSet, entityClass);
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static <T> T mapResultSetToEntity(ResultSet resultSet, Class<T> entityClass) throws SQLException {
        T entity;
        try {
            entity = entityClass.getDeclaredConstructor().newInstance();

            Field[] fields = entityClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = resultSet.getObject(fieldName);
                field.set(entity, value);
            }
        } catch (Exception e) {
            throw new SQLException("Error mapping ResultSet to entity.", e);
        }

        return entity;
    }

    public <T> T persist(T object) {
        String className = object.getClass().getSimpleName();
        Field[] fields = object.getClass().getDeclaredFields();

        StringBuilder columnNames = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (!fieldName.equals("id")) {
                columnNames.append(fieldName).append(", ");
                values.append("?, ");
            }
        }

        columnNames.delete(columnNames.length() - 2, columnNames.length());
        values.delete(values.length() - 2, values.length());

        String query = "INSERT INTO " + className + " (" + columnNames + ") VALUES (" + values + ")";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            int parameterIndex = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (!fieldName.equals("id")) {
                    Object value = field.get(object);
                    preparedStatement.setObject(parameterIndex++, value);
                }
            }

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the generated ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    // Set the generated ID back to the object
                    Field idField = object.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(object, generatedId);
                    return object;
                }
            }

        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // Return null if creation was unsuccessful
        return null;
    }

    public <T> T update(T object) throws IllegalAccessException, NoSuchFieldException {
        String className = object.getClass().getSimpleName();
        Field[] fields = object.getClass().getDeclaredFields();

        StringBuilder setClause = new StringBuilder();

        int version = getVersion(object);

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();

            if (!fieldName.equals("id")) {
                // Increment the version for non-id fields
                if (fieldName.equals("version")) {
                    setClause.append(fieldName).append(" = ?, ");
                } else {
                    setClause.append(fieldName).append(" = ?, ");
                }
            }
        }

        setClause.delete(setClause.length() - 2, setClause.length());
        setClause.append(" WHERE id = ? AND version = ?");

        String query = "UPDATE " + className + " SET " + setClause;

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int parameterIndex = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();

                if (!fieldName.equals("id")) {
                    // Increment the version for non-id fields
                    if (fieldName.equals("version")) {
                        preparedStatement.setInt(parameterIndex++, version + 1);
                    } else {
                        Object value = field.get(object);
                        preparedStatement.setObject(parameterIndex++, value);
                    }
                }
            }

            // Set the ID for the WHERE clause
            Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            long objectId = (long) idField.get(object);
            preparedStatement.setLong(parameterIndex++, objectId);

            // Set the version for the WHERE clause
            preparedStatement.setInt(parameterIndex++, version);

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                // Update the version in the object
                setVersion(object, version + 1);
                return object;
            }

        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        // Return null if update was unsuccessful
        return null;
    }

    private static <T> int getVersion(T object) throws IllegalAccessException, NoSuchFieldException {
        Field versionField = object.getClass().getDeclaredField("version");
        versionField.setAccessible(true);
        return versionField.getInt(object);
    }

    private static <T> void setVersion(T object, int newVersion) throws IllegalAccessException, NoSuchFieldException {
        Field versionField = object.getClass().getDeclaredField("version");
        versionField.setAccessible(true);
        versionField.setInt(object, newVersion);
    }

    public void delete(Object object) {
        String className = object.getClass().getSimpleName();
        String query = "DELETE FROM " + className + " WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            long objectId = (long) idField.get(object);

            preparedStatement.setLong(1, objectId);
            preparedStatement.executeUpdate();

        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}