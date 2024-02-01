package tp6bis;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EntityManagerImpl {

    private Connection connection;

    public EntityManagerImpl() {
        this.connection = ConnectionPostgreSQL.getInstance();
    }

    // Método para crear una tabla en PostgreSQL basada en un objeto de una clase
    public void createTable(Object object) {
        String className = object.getClass().getSimpleName();
        if (tableExists(className)) {
            System.out.println("Table already exists.");
            return;
        }

        // Obtener los campos de la clase
        Field[] fields = object.getClass().getDeclaredFields();

        // Construir la consulta SQL para crear la tabla
        StringBuilder sqlQuery = new StringBuilder("CREATE TABLE " + className + " (");

        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();

            // Ajustar el campo id para que sea serial (autoincremental) y la clave primaria
            if (fieldName.equals("id")) {
                sqlQuery.append(fieldName).append(" SERIAL PRIMARY KEY, ");
            } else {
                // Suponer que todos los demás campos son de tipo primitivo o String en este ejemplo
                sqlQuery.append(fieldName).append(" ").append(getSQLDataType(fieldType)).append(", ");
            }
        }

        // Eliminar la coma adicional al final y cerrar la consulta
        sqlQuery.delete(sqlQuery.length() - 2, sqlQuery.length()).append(");");

        // Ejecutar la consulta SQL
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sqlQuery.toString());
            System.out.println("Table created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para verificar si una tabla existe en la base de datos
    private boolean tableExists(String tableName) {
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT to_regclass('" + tableName + "')");
            resultSet.next();
            return resultSet.getString(1) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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
            // Agrega más casos según sea necesario para otros tipos de datos
            default:
                return "VARCHAR(255)";
        }
    }
    // Método para cerrar la conexión
    public void closeConnection() {
        ConnectionPostgreSQL.closeConnection();
    }

    // Método para buscar un objeto por su ID
    public <T> T find(Class<T> entityClass, long id) {
        String tableName = entityClass.getSimpleName();
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
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
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
    
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
                // Obtener el ID generado
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    long generatedId = generatedKeys.getLong(1);
                    // Establecer el ID generado de nuevo en el objeto
                    Field idField = object.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(object, generatedId);
                    return object;
                }
            }
    
        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    
        // Devolver null si la creación no tuvo éxito
        return null;
    }
    
    // Método para actualizar un objeto en la base de datos
    public <T> T update(T object) throws IllegalAccessException, NoSuchFieldException, IllegalAccessException, NoSuchFieldException{
        String className = object.getClass().getSimpleName();
        Field[] fields = object.getClass().getDeclaredFields();
    
        StringBuilder setClause = new StringBuilder();
    
        int version = getVersion(object);
    
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
    
            if (!fieldName.equals("id")) {
                // Incrementar la versión para los campos que no son id
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
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
            int parameterIndex = 1;
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
    
                if (!fieldName.equals("id")) {
                    // Incrementar la versión para los campos que no son id
                    if (fieldName.equals("version")) {
                        preparedStatement.setInt(parameterIndex++, version + 1);
                    } else {
                        Object value = field.get(object);
                        preparedStatement.setObject(parameterIndex++, value);
                    }
                }
            }
    
            // Establecer el ID para la cláusula WHERE
            Field idField = object.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            long objectId = (long) idField.get(object);
            preparedStatement.setLong(parameterIndex++, objectId);
    
            // Establecer la versión para la cláusula WHERE
            preparedStatement.setInt(parameterIndex++, version);
    
            int affectedRows = preparedStatement.executeUpdate();
    
            if (affectedRows > 0) {
                // Actualizar la versión en el objeto
                setVersion(object, version + 1);
                return object;
            }
    
        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    
        // Devolver null si la actualización no tuvo éxito
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
    
    // Método para eliminar un objeto de la base de datos
    public void delete(Object object) {
        String className = object.getClass().getSimpleName();
        String query = "DELETE FROM " + className + " WHERE id = ?";
    
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
    
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