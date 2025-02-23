import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class EjerciciosSQLite {

    private Connection sqliteConnection;
    private Connection mysqlConnection;

    public void abrirConexionSQLite(String bd) {
        try {
            String url = String.format("jdbc:sqlite:" + bd);
            // Establecemos la conexión con la BD:D
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

    public void abrirConexionMySQL(String bd, String servidor, String usuario, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Cargar el driver de MySQL
            String url = String.format("jdbc:mysql://%s:3306/%s", servidor, bd);
            this.mysqlConnection = DriverManager.getConnection(url, usuario, password);
            System.out.println("Conexión MySQL abierta a la base de datos: " + bd);
        } catch (ClassNotFoundException e) {
            System.err.println("No se encontró el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al abrir la conexión MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void cerrarConexiones() {
        try {
            if (this.sqliteConnection != null) {
                this.sqliteConnection.close();
                System.out.println("Conexión SQLite cerrada correctamente.");
            }
            if (this.mysqlConnection != null) {
                this.mysqlConnection.close();
                System.out.println("Conexión MySQL cerrada correctamente.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar las conexiones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Migra los datos de las tablas de la base de datos ADD a SQLite
     * 
     * 
     */
    public void migrarTablas(String bd) throws SQLException {
        // String queryTablas = "SHOW TABLES";
        try (Statement mysqlStmt = this.mysqlConnection.createStatement();
                ResultSet tablas = this.mysqlConnection.getMetaData().getTables("add", null, null, null)) {

            while (tablas.next()) {
                String tableName = tablas.getString(1);
                System.out.println("Procesando tabla: " + tableName);

                String queryCreate = "SHOW CREATE TABLE " + tableName;
                try (Statement stmtCreate = this.mysqlConnection.createStatement();
                        ResultSet createResult = stmtCreate.executeQuery(queryCreate)) {

                    if (createResult.next()) {
                        String originalSQL = createResult.getString(2);
                        String modifiedSQL = convertirSWLaSQLite(originalSQL);

                        try (Statement sqliteStmt = this.sqliteConnection.createStatement()) {
                            sqliteStmt.execute(modifiedSQL);
                            System.out.println("Tabla migrada: " + tableName);
                        } catch (SQLException e) {
                            System.err.println("Error al crear la tabla en SQLite: " + e.getMessage());
                        }
                    }
                } catch (SQLException e) {
                    System.err
                            .println("Error al obtener la estructura de la tabla " + tableName + ": " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener las tablas de MySQL: " + e.getMessage());
        }
    }

    /**
     *
     * ejecutar desde consola una consulta que nos permita listar la segunda y
     * tercera clase con mas puestos
     * select nombreAula from aulas order by puestos desc limit 2,3;
     * 
     */

    /**
     * Realiza un método, en java, que sin y con consultas preparadas que permita
     * consultar las aulas que tengan un número mínimo de puestos.
     * 
     * @param minPuestos
     * @return
     * @throws SQLException
     */
    public void getAulasPrepared(int minPuestos) throws SQLException {
        if (sqliteConnection == null || sqliteConnection.isClosed()) {
            throw new SQLException("La conexión con SQLite no está abierta.");
        }
        String query = "SELECT nombreAula FROM aulas WHERE puestos >= ?";
        PreparedStatement pstmt = sqliteConnection.prepareStatement(query);
        pstmt.setInt(1, minPuestos);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            System.out.println("Aula: " + rs.getString("nombreAula"));

        }

        pstmt.close();

    }

    public void getAulas(int minPuestos) throws SQLException {
        Statement stmt = sqliteConnection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT nombreAula FROM aulas WHERE puestos >= " + minPuestos);
        while (rs.next()) {
            System.out.println("Aulas: " + rs.getString("nombreAula"));
        }
        sqliteConnection.close();

    }

    /**
     * Realiza un método en java que permita insertar datos de aulas.
     * 
     * @param args
     */

    public void insertarAula(int numero, String nombre, int puestos) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:add");
        String query = "INSERT INTO aulas (numero,nombreAula, puestos) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, numero);
        pstmt.setString(2, nombre);
        pstmt.setInt(3, puestos);
        pstmt.executeUpdate();
        conn.close();
    }

    /**
     * Realiza un método que permita insertar datos en aulas en función de su código
     * aunque este ya exista (no se puede usar update)
     * 
     * @param args
     */
    public void insertarAulaSiExiste(String numero, String nombreAula, int puestos) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:sqlite:add");
        String query = "INSERT OR REPLACE INTO aulas (numero, nombreAula, puestos) VALUES (?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, numero);
        pstmt.setString(2, nombreAula);
        pstmt.setInt(3, puestos);
        pstmt.executeUpdate();
        conn.close();
    }

    /**
     * Por motivos de seguridad queremos realizar las inserciones en la tabla
     * alumnos. por duplicado en dos bases de datos distintas, una MySQL y otra
     * SQLite. Realiza un único método que nos permita realizar esta acción
     * 
     * @param nombre
     * @param apellidos
     * @throws SQLException
     */
    public void insertarAlumnoEnAmbasBD(int codigo, String nombre, String apellidos, int altura, int aula)
            throws SQLException {

        Connection sqliteConn = DriverManager.getConnection("jdbc:sqlite:add");
        String query = "INSERT INTO alumnos (codigo,nombre,apellidos,altura,aula) VALUES (?, ?, ?, ?, ?)";

        try {
            mysqlConnection.setAutoCommit(false);
            sqliteConn.setAutoCommit(false);

            PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(query);
            PreparedStatement sqliteStmt = sqliteConn.prepareStatement(query);

            mysqlStmt.setInt(1, codigo);
            mysqlStmt.setString(2, nombre);
            mysqlStmt.setString(3, apellidos);
            mysqlStmt.setInt(4, altura);
            mysqlStmt.setInt(5, aula);
            mysqlStmt.executeUpdate();

            sqliteStmt.setInt(1, codigo);
            sqliteStmt.setString(2, nombre);
            sqliteStmt.setString(3, apellidos);
            sqliteStmt.setInt(4, altura);
            sqliteStmt.setInt(5, aula);
            sqliteStmt.executeUpdate();

            mysqlConnection.commit();
            sqliteConn.commit();
        } catch (SQLException e) {
            // si me da el error vuelvo donde estaba
            mysqlConnection.rollback();
            sqliteConn.rollback();
            throw e;
        } finally {
            mysqlConnection.close();
            sqliteConn.close();
        }
    }

    /**
     * Realiza un método que nos permita buscar por el nombre (o parte de él) de
     * aula de forma simultánea en una base de datos MySQL y SQLite. ¿Qué
     * diferencia ves entre las búsquedas en MySQL y SQLite?
     * 
     * @param nombreAula
     * @throws SQLException
     */
    public void buscarAulaPorNombre(String nombreAula) throws SQLException {

        String query = "SELECT * FROM aulas WHERE nombreAula LIKE ?";
        try (PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(query);
                PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(query)) {

            String searchPattern = "%" + nombreAula + "%";
            mysqlStmt.setString(1, searchPattern);
            sqliteStmt.setString(1, searchPattern);

            try (ResultSet mysqlRs = mysqlStmt.executeQuery();
                    ResultSet sqliteRs = sqliteStmt.executeQuery()) {

                System.out.println("Resultados en MySQL:");
                while (mysqlRs.next()) {
                    System.out.println(" - " + mysqlRs.getString("nombreAula"));
                }

                System.out.println("\nResultados en SQLite:");
                while (sqliteRs.next()) {
                    System.out.println(" - " + sqliteRs.getString("nombreAula"));
                }
            }
        }

    }

    /**
     * Realizar un método que realice inserciones en ambas bases de datos, teniendo
     * en cuenta que si falla una hay que deshacer los cambios en ambas bases de
     * datos.
     * 
     * @param bd
     */

    public void insertarEnAmbasBD(int codigo, String nombre, String apellidos, int altura, int aula)
            throws SQLException {
        // Aseguramos que las conexiones están inicializadas
        if (mysqlConnection == null || sqliteConnection == null) {
            throw new SQLException("Las conexiones no están inicializadas.");
        }

        String query = "INSERT INTO alumnos (codigo, nombre, apellidos,altura, aula) VALUES (?, ?, ?, ?, ?)";

        // Desactivar auto-commit para manejar la transacción manualmente
        mysqlConnection.setAutoCommit(false);
        sqliteConnection.setAutoCommit(false);

        try (
                PreparedStatement mysqlStmt = mysqlConnection.prepareStatement(query);
                PreparedStatement sqliteStmt = sqliteConnection.prepareStatement(query)) {

            mysqlStmt.setInt(1, codigo);
            mysqlStmt.setString(2, nombre);
            mysqlStmt.setString(3, apellidos);
            mysqlStmt.setInt(4, altura);
            mysqlStmt.setInt(5, aula);
            mysqlStmt.executeUpdate();

            sqliteStmt.setInt(1, codigo);
            sqliteStmt.setString(2, nombre);
            sqliteStmt.setString(3, apellidos);
            sqliteStmt.setInt(4, altura);
            sqliteStmt.setInt(5, aula);
            sqliteStmt.executeUpdate();

            // Si todo va bien, hacer commit en ambas bases de datos
            mysqlConnection.commit();
            sqliteConnection.commit();
            System.out.println("Inserción realizada con éxito en ambas bases de datos.");

        } catch (SQLException e) {
            // Si hay error, hacer rollback para deshacer cambios en ambas bases de datos
            System.err.println("Error en la inserción: " + e.getMessage());
            if (mysqlConnection != null)
                mysqlConnection.rollback();
            if (sqliteConnection != null)
                sqliteConnection.rollback();
            System.err.println("Se han revertido los cambios en ambas bases de datos.");
        } finally {
            // Restaurar el auto-commit para futuras operaciones
            if (mysqlConnection != null)
                mysqlConnection.setAutoCommit(true);
            if (sqliteConnection != null)
                sqliteConnection.setAutoCommit(true);
        }
    }

    /**
     * Teniendo en cuenta la siguiente tabla realiza los siguientes ejercicios :
     * CREATE TABLE `fechas` (
     * `nombre` VARCHAR(10),
     * `fecha` DATETIME not null
     * )
     * 
     * @param args
     */

    public void tablesMigrate(String bd) {
        abrirConexionMySQL(bd, "localhost", "root", "");
        try (Statement sta = this.mysqlConnection.createStatement()) {
            ResultSet tablas = mysqlConnection.getMetaData().getTables(bd, null, null, null);
            while (tablas.next()) {

                String query = String.format("SHOW CREATE TABLE %s", tablas.getString("TABLE_NAME"));
                ResultSet result = sta.executeQuery(query);
                Statement staSQLite = this.sqliteConnection.createStatement();
                while (result.next()) {
                    System.out.println(result.getString(2));
                    String result2 = result.getString(2);
                    String query2 = convertirSWLaSQLite(result2);
                    System.out.println(query2);
                    System.out.println(staSQLite.executeUpdate(query2));

                    // String query2 = String.format("%s",result.getString(2) );
                    // System.out.println(staSQLite.executeUpdate(query2) +"");

                }

            }
            // Se ejecuta la sentencia de inserción mediante executeUpdate
        } catch (SQLException e) {
            System.out.println("Se ha producido un error: " + e.getLocalizedMessage());
        }

        // cerrarConexion(mysqlConnection);

    }

    public static String convertirSWLaSQLite(String mysqlSQL) {
        return mysqlSQL

                .replace("KEY `aula` (`aula`),", "")

                .replace("AUTO_INCREMENT", "")

                .replace("ENGINE=InnoDB =10 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("ENGINE=InnoDB =9 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("ENGINE=InnoDB =32 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("ENGINE=InnoDB =65 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("ENGINE=InnoDB =14 DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")
                .replace("ENGINE=InnoDB =32 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish_ci", "")
                .replace("ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci", "")

                .replace("KEY `alumno` (`alumno`),", "")

                .replace("KEY `asignatura` (`asignatura`),", "")

                .replace("DEFAULT current_timestamp()", "")

                .replace("DEFINER=`root`@`localhost` SQL SECURITY DEFINER", "")

                .replaceAll("ALGORITHM\\s*=\\s*\\w+", "");// Eliminar algoritm y sy
    }

    public static void main(String[] args) {
        EjerciciosSQLite ejercicios = new EjerciciosSQLite();
        String mysqlBD = "add";
        String sqliteBD = "add";

        try {
            ejercicios.abrirConexionMySQL(mysqlBD, "localhost", "root", "");
            ejercicios.abrirConexionSQLite(sqliteBD);

            // ejercicios.tablesMigrate("add");
            // ejercicios.migrarTablas("add");
            // ejercicios.getAulasPrepared(30);
            // ejercicios.getAulas(30);
            // ejercicios.insertarAula(2, "Desarrollo de Interfaces", 6);
            // ejercicios.insertarAulaSiExiste("2", "BBDD", 16);
            // ejercicios.insertarAlumnoEnAmbasBD(11, "marcos", "gonzalez", 180, 21);
            // ejercicios.buscarAulaPorNombre("ca");
            // ejercicios.insertarEnAmbasBD(0, mysqlBD, sqliteBD, 0, 0);
            ejercicios.insertarEnAmbasBD(23, "nick", "diaz", 0,5 );

            ejercicios.cerrarConexiones();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
