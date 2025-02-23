import java.sql.*;

public class Ejercicio10 {
    private Connection mysqlConnection;
    private Connection sqliteConnection;

    public Ejercicio10() throws SQLException {
        abrirConexionSQLite("add");
        abrirConexionMySQL("add", "localhost", "root", "");
    }

    public void abrirConexionSQLite(String bd) {
        try {
            String url = String.format("jdbc:sqlite:" + bd);
            // Establecemos la conexión con la BD
            this.sqliteConnection = DriverManager.getConnection(url);
            if (this.sqliteConnection != null) {
                System.out.println("conectado a " + bd);
            } else {
                System.out.println("no conectado a " + bd);
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public void abrirConexionMySQL(String bd, String servidor, String usuario,
            String password) {
        try {
            String url = String.format("jdbc:mysql://%s:3306/%s", servidor, bd);
            // Establecemos la conexión con la BD
            this.mysqlConnection = DriverManager.getConnection(url, usuario, password);
            if (this.mysqlConnection != null) {
                System.out.println("Conectado a " + bd + " en " + servidor);
            } else {
                System.out.println("No conectado a " + bd + " en " + servidor);
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    public void cerrarConexiones() {
        try {
            if (this.sqliteConnection != null)
                this.sqliteConnection.close();
            if (this.mysqlConnection != null)
                this.mysqlConnection.close();
        } catch (SQLException e) {
            System.err.println("Error al cerrar las conexiones: " + e.getMessage());
        }
    }

    // 1. Insertar datos en la tabla `fechas` en ambas bases de datos
    /**
     * En MySQL:
     * Si jdbcCompliantTruncation=false, MySQL truncará el nombre y lo guardará
     * hasta el límite que son 10 caracteres
     * Si no se usa ese parámetro, MySQL lanzará un error.
     * En SQLite:
     * SQLite permitirá la inserción, ya que no tiene un límite estricto por
     * defecto.
     * 
     * @param nombre
     * @param fecha
     * @throws SQLException
     */
    public void insertarFecha(String nombre, String fecha) throws SQLException {
        String query = "INSERT INTO fechas (nombre, fecha) VALUES (?, ?)";

        try (
                PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(query);
                PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(query)) {
            mysqlStmt.setString(1, nombre);
            mysqlStmt.setString(2, fecha);

            sqliteStmt.setString(1, nombre);
            sqliteStmt.setString(2, fecha);

            mysqlStmt.executeUpdate();
            sqliteStmt.executeUpdate();
            System.out.println("Datos insertados correctamente en ambas bases de datos.");
        } catch (SQLException e) {
            System.err.println(" Error al insertar datos: " + e.getMessage());
        }
    }

    // 2. Insertar un nombre con más de 10 caracteres
    /***
     * ¿Qué pasa si insertamos la fecha como cadena?
     * MySQL aceptará la fecha si está en formato YYYY-MM-DD HH:MM:SS.
     * SQLite también la aceptará, pero la almacenará como TEXT si la columna no
     * tiene restricciones adicionales.
     */
    public void insertarNombreLargo() {
        try {
            insertarFecha("NombreMuyLargo", "2024-01-01 12:00:00");
        } catch (SQLException e) {
            System.err.println(" Error al insertar nombre largo: " + e.getMessage());
        }
    }

    // 3. Insertar la fecha actual usando las funciones propias de cada SGBD
    /**
     * ¿Qué pasa si insertamos la fecha actual usando funciones de cada SGBD?
     * En MySQL (NOW())
     * 
     * Guarda la fecha y hora del servidor MySQL en DATETIME.
     * En SQLite (DATETIME('now'))
     * 
     * Guarda la fecha y hora en UTC, lo que puede causar una diferencia horaria si
     * el servidor usa otra zona horaria.
     * Solución: Para guardar en la zona horaria del sistema, se puede usar:
     * datetine("NOw", "Localtime")
     * 
     */
    public void insertarFechaActual() {
        String queryMySQL = "INSERT INTO fechas (nombre, fecha) VALUES ('MySQL', NOW())";
        String querySQLite = "INSERT INTO fechas (nombre, fecha) VALUES ('SQLite', DATETIME('now'))";

        try (
                Statement mysqlStmt = mysqlConnection.createStatement();
                Statement sqliteStmt = sqliteConnection.createStatement()) {
            mysqlStmt.executeUpdate(queryMySQL);
            sqliteStmt.executeUpdate(querySQLite);
            System.out.println(" Fecha actual insertada correctamente en ambas bases de datos.");
        } catch (SQLException e) {
            System.err.println(" Error al insertar fecha actual: " + e.getMessage());
        }
    }

    // 4. Insertar un valor vacío en el campo fecha
    /**
     * ¿Qué pasa si intentamos insertar un valor vacío ("") en el campo fecha?
     * En MySQL:
     * 
     * Si zeroDateTimeBehavior=convertToNull, guardará NULL.
     * Si no, lanzará un error.
     * En SQLite:
     * 
     * Guardará una cadena vacía "", pero esto no es válido en consultas de
     * fecha/hora.
     * Solución: Usar NULL en lugar de "" para evitar errores.
     */
    public void insertarFechaVacia() {
        try {
            insertarFecha("SinFecha", "");
        } catch (SQLException e) {
            System.err.println(" Error al insertar una fecha vacía: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Ejercicio10 fechasEjercicios = new Ejercicio10();

            // Insertar un registro normal
            fechasEjercicios.insertarFecha("Ejemplo", "2024-01-29 14:30:00");

            // Insertar un nombre con más de 10 caracteres
            fechasEjercicios.insertarNombreLargo();

            // Insertar la fecha actual con funciones de cada SGBD
            fechasEjercicios.insertarFechaActual();

            // Insertar un valor vacío en el campo fecha
            fechasEjercicios.insertarFechaVacia();

            fechasEjercicios.cerrarConexiones();
        } catch (Exception e) {
            System.err.println(" Error en la ejecución: " + e.getMessage());
        }
    }
}
