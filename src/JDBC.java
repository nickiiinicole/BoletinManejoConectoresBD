import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBC {
    private Connection conexion;
    private Connection conexion2;

    public void abrirConexion2(String bd) {
        try {
            String url = String.format("jdbc:sqlite:" + bd);
            // Establecemos la conexión con la BD
            this.conexion = DriverManager.getConnection(url);
            if (this.conexion != null) {
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

    public void abrirConexion(String bd, String servidor, String usuario,
            String password) {
        try {
            String url = String.format("jdbc:mysql://%s:3306/%s", servidor, bd);
            // Establecemos la conexión con la BD
            this.conexion = DriverManager.getConnection(url, usuario, password);
            if (this.conexion != null) {
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

    public void cerrarConexion() {
        try {
            this.conexion.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.getLocalizedMessage());
        }
    }

    public void consultaAlumnos(String bd) {
        abrirConexion("futbol", "localhost", "root", "");

        try (Statement stmt = this.conexion.createStatement()) {

            String query = "select * from jugadores_celta";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {

                System.out.println(rs.getInt(1) + "\t" +
                        rs.getString("nombre") + "\t" + rs.getInt("goles"));
            }
        } catch (SQLException e) {
            System.out.println("Se ha producido un error: " + e.getLocalizedMessage());
        } finally {
            cerrarConexion();
        }
    }

    public void insertarFila() {
        try (Statement sta = this.conexion.createStatement()) {
            String query = "INSERT INTO jugadores_celta VALUES (20,'Nicole diaz', 'Defensa central', 19,'colombia', 12, 50,20,100 ) ";
            // Se ejecuta la sentencia de inserción mediante executeUpdate
            int filasAfectadas = sta.executeUpdate(query);
            System.out.println("Filas insertadas: " + filasAfectadas);
        } catch (SQLException e) {
            System.out.println("Se ha producido un error: " + e.getLocalizedMessage());
        }
    }

    public void addColumna() {
        Statement sta = null;
        try {
            sta = this.conexion.createStatement();
            String query = "ALTER TABLE jugadores_celta ADD peso int DEFAULT NULL";
            // Se ejecuta la modificación de la tabla
            int filasAfectadas = sta.executeUpdate(query);
            System.out.println("Filas afectadas: " + filasAfectadas);
        } catch (SQLException e) {
            System.out.println("Se ha producido un error: " + e.getLocalizedMessage());
        } finally {
            if (sta != null) {
                try {
                    sta.close();// Se cierra el Statement
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PreparedStatement ps = null;

    public void consultaAlumnosPS(String patron, int numResultados)
            throws SQLException {
        String query = "UPDATE peso from jugadores_celta where nombre like ? limit ?";
        if (this.ps == null)
            this.ps = this.conexion.prepareStatement(query);
        ps.setString(1, patron);
        ps.setInt(2, numResultados);
        ResultSet resu = ps.executeQuery();
        while (resu.next()) {
            System.out.println(resu.getInt(1) + "\t" + resu.getString("nombreAula"));
        }
    }

    public void getInfo(String bd) {
        DatabaseMetaData dbmt;
        ResultSet tablas, columnas;
        try {
            dbmt = this.conexion.getMetaData();
            tablas = dbmt.getTables(bd, null, null, null);
            while (tablas.next()) {
                System.out.println(String.format("%s %s",
                        tablas.getString("TABLE_NAME"), tablas.getString("TABLE_TYPE")));
                columnas = dbmt.getColumns(bd, null,
                        tablas.getString("TABLE_NAME"), null);
                while (columnas.next()) {
                    System.out.println(String.format("   %s %s %d %s %s",
                            columnas.getString("COLUMN_NAME"),
                            columnas.getString("TYPE_NAME"),
                            columnas.getInt("COLUMN_SIZE"),
                            columnas.getString("IS_NULLABLE"),
                            columnas.getString("IS_AUTOINCREMENT")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obteniendo datos " + e.getLocalizedMessage());
        }
    }

    public void getInfoConsulta(String consulta) throws SQLException {
        Statement st = this.conexion.createStatement();
        ResultSet filas = st.executeQuery(consulta);
        ResultSetMetaData rsmd = filas.getMetaData();
        System.out.println("Num\tNombre\tAlias\tTipoDatos");
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            System.out.println(String.format("%d \t %s \t %s \t %s", i,
                    rsmd.getColumnName(i),
                    rsmd.getColumnLabel(i),
                    rsmd.getColumnTypeName(i)));
        }
    }

    public void probandoProcedimiento(int numero, String cadena) throws SQLException {
        CallableStatement cs = this.conexion.prepareCall("CALL getAulas(?,?)");
        cs.setInt(1, numero);
        cs.setString(2, cadena);
        ResultSet resultado = cs.executeQuery();
        while (resultado.next()) {
            System.out.println(resultado.getInt(1) + "\t" +
                    resultado.getString("nombreAula") + "\t" + resultado.getInt("puestos"));
        }
    }

    public static void main(String[] args) {
        JDBC jdbc = new JDBC();
        // jdbc.abrirConexion("futbol", "localhost", "root", "");
        // jdbc.addColumna();
        // jdbc.consultaAlumnos("futbol");
         jdbc.abrirConexion2("add");
        // jdbc.getInfo("futbol");
        // try {
        //     jdbc.getInfoConsulta("select * from jugadores_celta");

        // } catch (SQLException e) {
        //     e.printStackTrace();
        // }
    }
}
