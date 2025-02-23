import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.cj.protocol.a.SqlDateValueEncoder;

public class Ejercicio1 {

    // declaro primero la conexion
    private Connection conexion;

    public void abrirConexion(String bd, String servidor, String usuario, String password) {
        try {
            String url = String.format("jdbc:mysql://%s:3306/%s", servidor, bd);
            // establecer conexion a la bd :D
            this.conexion = DriverManager.getConnection(url, usuario, password);
            if (this.conexion != null) {
                System.out.println("Conectado a " + bd + " en " + servidor);
            } else {
                System.out.println("No conectado a " + bd + " en servidor");
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getLocalizedMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("Código error: " + e.getErrorCode());
        }
    }

    

    // vamos a realizar consulta prepara , donde el patron es lo de %a% por ejemplo
    // puedo meterle los parametros lo quiera es decir un nombre ppor eje,mploi....
    private PreparedStatement ps = null; // atributo de instancia
    private PreparedStatement ps2 = null; // atributo de instancia

    public void consultarDatosNombre(String patron) throws SQLException {

        int counter = 0;
        String query = "select * from alumnos where nombre like ?";
        if (this.ps == null) {
            this.ps = this.conexion.prepareStatement(query);
            ps.setString(1, patron);
            ResultSet resu = ps.executeQuery();
            // mientras haya resultado que lo muestre

            while (resu.next()) {
                System.out.println(resu.getString(2) + " tiene el patron " + patron);
                counter++;

            }
        }

    }

    public void altaAlumnosAsignatura(String nombre, String apellido, int altura, int aula)
            throws SQLException {

        String query = "INSERT INTO alumnos (nombre, apellidos, altura, aula) VALUES (?,?,?,?) ";
        if (this.ps2 == null) {
            this.ps2 = this.conexion.prepareStatement(query);
            ps2.setString(1, nombre);
            ps2.setString(2, apellido);
            ps2.setInt(3, altura);
            ps2.setInt(4, aula);
            int rowresult = ps2.executeUpdate();

        }
    }

    private PreparedStatement ps3 = null;

    public void altaAsignaturas(int cod, String nombre) throws SQLException {

        String query = "INSERT INTO asignaturas (cod, nombre) VALUES (?, ?)";
        if (ps3 == null) {
            ps3 = this.conexion.prepareStatement(query);
            ps3.setInt(1, cod);
            ps3.setString(2, nombre);
            int rowresult = ps3.executeUpdate();

        }

    }

    private PreparedStatement ps4 = null;

    public void bajaAsignaturas(int codigo) throws SQLException {

        String query = "delete from asignaturas where cod=?";
        if (ps4 == null) {
            ps4 = this.conexion.prepareStatement(query);
            ps4.setInt(1, codigo);

            int rowResult = ps4.executeUpdate();
        }
    }

    private PreparedStatement ps5 = null;

    public void bajarAlumno(int codigo) throws SQLException {
        String query = "delete from alumnos where codigo=?";
        if (ps5 == null) {
            ps5 = this.conexion.prepareStatement(query);
            ps5.setInt(1, codigo);
            int rowResult = ps5.executeUpdate();
        }
    }

    private PreparedStatement ps6 = null;

    public void modificarAlumno(int codigoCambiar, String nombre, String apellidos, int altura, int aula, int cod)
            throws SQLException {
        // UPDATE NOMBRETABLA ESTABLECIENDO LA COLUMNA SET = VALOR , MAS COLUMNAS
        // SEPARADS POR COMODAS WHERE
        String query = "UPDATE alumnos set codigo = ? , nombre = ? , apellidos = ? , altura = ? , aula = ? WHERE codigo = ?  ";
        if (ps6 == null) {
            ps6 = this.conexion.prepareStatement(query);
            ps6.setInt(1, codigoCambiar);
            ps6.setString(2, nombre);
            ps6.setString(3, apellidos);
            ps6.setInt(4, altura);
            ps6.setInt(5, aula);
            ps6.setInt(6, cod);
            int result = ps6.executeUpdate();

        }

    }

    private PreparedStatement ps7 = null;

    public void modificarAsignatura(int codigoCambiar, String nombre, int codigo) throws SQLException {

        String query = "UPDATE asignaturas set cod= ? , nombre = ?  where cod = ? ";
        if (ps7 == null) {
            ps7 = this.conexion.prepareStatement(query);
            ps7.setInt(1, codigoCambiar);
            ps7.setString(2, nombre);
            ps7.setInt(3, codigo);
            int result = ps7.executeUpdate();

        }
    }

    private PreparedStatement ps8 = null;

    public void consultaMostrarAulasAlumnos() throws SQLException {

        String query = "SELECT aulas.nombreAula FROM aulas WHERE numero IN ( SELECT aula FROM alumnos ) ";
        if (ps8 == null) {
            ps8 = this.conexion.prepareStatement(query);
            ResultSet result = ps8.executeQuery();
            while (result.next()) {
                String nombreAula = result.getString(1);
                System.out.println(nombreAula);
            }
        }

    }

    private PreparedStatement ps9 = null;

    public void consultarNotasAlumno() throws SQLException {

        String query = "SELECT alumnos.nombre, asignaturas.nombre, notas.nota " +
                "FROM notas " +
                "JOIN alumnos ON notas.alumno = alumnos.codigo " +
                "JOIN asignaturas ON notas.asignatura = asignaturas.COD " +
                "WHERE notas.NOTA >= 5";

        if (ps9 == null) {
            ps9 = this.conexion.prepareStatement(query);
            ResultSet result = ps9.executeQuery();
            while (result.next()) {
                String nombreAlumno = result.getString(1);
                String nombreAsignatura = result.getString(2);
                float nota = result.getFloat(3);

                System.out.println("Alumno: " + nombreAlumno +
                        ", Asignatura: " + nombreAsignatura +
                        ", Nota: " + nota);

            }
        }

    }

    private PreparedStatement ps10 = null;

    public void nombreAsignaturasSinNombre() throws SQLException {
        String query = "SELECT asignaturas.NOMBRE from asignaturas where asignaturas.COD not in (SELECT DISTINCT notas.asignatura FROM notas);";
        if (ps10 == null) {
            ps10 = this.conexion.prepareStatement(query);
            ResultSet result = ps10.executeQuery();
            while (result.next()) {
                String nombreAsignatura = result.getString(1);
                System.out.println(nombreAsignatura);
            }
        }
    }

    private PreparedStatement ps11 = null;

    public void ejercicio6a(String patron, int alturaMenor) throws SQLException {

        String query = "SELECT alumnos.nombre FROM alumnos WHERE alumnos.nombre like ? AND alumnos.altura > ? ; ";
        if (ps11 == null) {
            ps11 = this.conexion.prepareStatement(query);
            ps11.setString(1, "%" + patron + "%");

            ps11.setInt(2, alturaMenor);
            ResultSet result = ps11.executeQuery();
            while (result.next()) {
                String nombreAlumno = result.getString(1);
                System.out.println(nombreAlumno);
            }
        }
    }

    private Statement ps12 = null;

    public void ejercicio6b(String patron, int alturaMenor) throws SQLException {

        String query = "SELECT alumnos.nombre FROM alumnos WHERE alumnos.nombre LIKE '%" + patron + "%' " +
                "AND alumnos.altura > " + alturaMenor;

        if (ps12 == null) {
            ps12 = this.conexion.createStatement();
            ResultSet result = ps12.executeQuery(query);
            while (result.next()) {
                String nombreAlumno = result.getString(1);
                System.out.println(nombreAlumno);
            }
        }
    }

    // teimpo de ejecucion entre ese y la anterior
    public void tiempoEjecucion(int numeroVeces) throws SQLException {
        Ejercicio1 ejercicio = new Ejercicio1();
        ejercicio.abrirConexion("add", "localhost", "root", null);

        long inicio1 = System.nanoTime();
        for (int i = 0; i < numeroVeces; i++) {
            ejercicio.ejercicio6a("a", 160);
        }
        long final1 = System.nanoTime();

        System.out.println("Tiempo total de la función 1 (ejercicio6a): " + (final1 - inicio1) / 1000 + " ms");

        long inicio2 = System.nanoTime();
        for (int i = 0; i < numeroVeces; i++) {
            ejercicio.ejercicio6b("a", 160);
        }
        long final2 = System.nanoTime();

        System.out.println("Tiempo total de la función 2 (ejercicio6b): " + (final2 - inicio2) / 1000 + " ms");
    }

    private PreparedStatement ps13 = null;

    public void ejercicio8(String tabla, String nombreCampo, String tipoDato, String propiedades) throws SQLException {

        String query = String.format("ALTER TABLE %s  ADD COLUMN %s %s %s", tabla, nombreCampo, tipoDato, propiedades);
        if (ps13 == null) {
            ps13 = this.conexion.prepareStatement(query);
            int result = ps13.executeUpdate();

        }
    }

    private Statement ps14 = null;

    // ejercicio 9
    DatabaseMetaData dbmt;

    public void ejercicio9(String bd) throws SQLException {
        dbmt = this.conexion.getMetaData();
        ResultSet tablas = dbmt.getTables(bd, null, null, null);
        while (tablas.next()) {
            System.out.println("Nombre del Driver: " + dbmt.getDriverName());
            System.out.println("Versión del Driver: " + dbmt.getDriverVersion());
            System.out.println("URL de conexión: " + dbmt.getURL());
            System.out.println("Usuario conectado: " + dbmt.getUserName());
            System.out.println("Nombre del SGBD: " + dbmt.getDatabaseProductName());
            System.out.println("Versión del SGBD: " + dbmt.getDatabaseProductVersion());
            System.out.println("Palabras reservadas del SGBD: " + dbmt.getSQLKeywords());
        }
    }

    DatabaseMetaData dbmt2;

    public void ejercicio9a(String bd) throws SQLException {
        dbmt2 = this.conexion.getMetaData();
        ResultSet catalogs = conexion.getMetaData().getCatalogs();
        while (catalogs.next()) {
            System.out.println("Catalogo: " + catalogs.getString(1));
        }
        catalogs.close();
    }

    public void ejercicio9b(String catalog) throws SQLException {
        // Apartado c: Obtener nombres de tablas y tipo de tablas
        System.out.println("Tablas y sus tipos en el catálogo " + catalog + ":");
        ResultSet tables = conexion.getMetaData().getTables(catalog, null, null, new String[] { "TABLE" });
        while (tables.next()) {
            System.out
                    .println("Tabla: " + tables.getString("TABLE_NAME") + ", Tipo: " + tables.getString("TABLE_TYPE"));
        }
        tables.close();
        System.out.println("------------------------------------------------------");
    }

    DatabaseMetaData dbmt3;

    public void ejercicio9c(String bd) throws SQLException {
        // Obtener nombres de vistas
        System.out.println("Vistas en el catálogo " + bd + ":");
        ResultSet views = conexion.getMetaData().getTables(bd, null, null, new String[] { "VIEW" });
        while (views.next()) {
            System.out.println("Vista: " + views.getString("TABLE_NAME"));
        }
        views.close();

    }

    public void ejercicio9d(String catalog) throws SQLException {
        // Obtener nombres de vistas
        System.out.println("Vistas en el catálogo " + catalog + ":");
        ResultSet views = conexion.getMetaData().getTables(catalog, null, null, new String[] { "VIEW" });
        while (views.next()) {
            System.out.println("Vista: " + views.getString("TABLE_NAME"));
        }
        views.close();
    }

    DatabaseMetaData dbmt10;

    public void ejercicio9e(String catalog) throws SQLException {

        dbmt10 = this.conexion.getMetaData();

        System.out.println("Bases de datos (Catalogs) disponibles:");
        try (ResultSet catalogs = dbmt10.getCatalogs()) {
            while (catalogs.next()) {
                String catalogName = catalogs.getString("TABLE_CAT");
                System.out.println("- " + catalogName);
                // Si la base de datos es ADD, obtener sus tablas
                if ("ADD".equalsIgnoreCase(catalogName)) {
                    System.out.println("Tablas en la base de datos ADD:");
                    try (ResultSet tablas = dbmt10.getTables(catalogName, null, null, null)) {
                        while (tablas.next()) {
                            String tableName = tablas.getString("TABLE_NAME");
                            String tableType = tablas.getString("TABLE_TYPE");
                            System.out.println("  - Tabla: " + tableName + ", Tipo: " + tableType);

                        }
                    }
                }
            }
        }
    }

    public void ejercicio9f(String bd) throws SQLException {
        // obtener los procedimientos almacenados de la base de datos Add
        DatabaseMetaData metaData = conexion.getMetaData();

        try (ResultSet procedimientos = metaData.getProcedures(bd, null, null)) {
            System.out.println("Procedimientos almacenados en la base de datos: " + bd);
            while (procedimientos.next()) {
                String nombreProcedimiento = procedimientos.getString("PROCEDURE_NAME");
                String esquema = procedimientos.getString("PROCEDURE_SCHEM");
                String tipoProcedimiento = procedimientos.getString("PROCEDURE_TYPE");

                System.out.println("Nombre del procedimiento: " + nombreProcedimiento);
                System.out.println("Esquema: " + esquema);
                System.out.println("Tipo de procedimiento: " +
                        (tipoProcedimiento.equals("1") ? "PROCEDURE" : "FUNCTION"));
                System.out.println("---------------------------------------------");
            }
        }
    }

    DatabaseMetaData dbmt4;

    public void ejercicio9g(String bd) throws SQLException {
        dbmt4 = this.conexion.getMetaData();

        ResultSet result = conexion.getMetaData().getCatalogs();
        // Apartado g: Obtener columnas de tablas que comiencen por 'a'
        System.out.println("Columnas de tablas que comienzan con 'a' en el catálogo " + bd + ":");
        ResultSet tables = conexion.getMetaData().getTables(bd, null, "a%", new String[] { "TABLE" });
        while (tables.next()) {
            String tableName = tables.getString("TABLE_NAME");
            ResultSet columns = conexion.getMetaData().getColumns(bd, null, tableName, null);
            while (columns.next()) {
                System.out.println("Tabla: " + tableName +
                        ", Columna: " + columns.getString("COLUMN_NAME") +
                        ", Tipo de dato: " + columns.getString("TYPE_NAME") +
                        ", Tamaño: " + columns.getInt("COLUMN_SIZE") +
                        ", Permite Nulos: "
                        + (columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable ? "Sí" : "No"));
            }
            columns.close();
        }
        tables.close();

    }

    DatabaseMetaData dbmt7;

    public void ejercicio9h(String bd) throws SQLException {
        dbmt7 = this.conexion.getMetaData();

        // Obtener las tablas de la base de datos especificada
        try (ResultSet tablas = dbmt7.getTables(bd, null, null, new String[] { "TABLE" })) {
            while (tablas.next()) {
                String tableName = tablas.getString("TABLE_NAME");
                System.out.println("Tabla: " + tableName);

                // Obtener claves primarias
                try (ResultSet primaryKeys = dbmt7.getPrimaryKeys(bd, null, tableName)) {
                    System.out.println("Claves Primarias:");
                    while (primaryKeys.next()) {
                        String columnName = primaryKeys.getString("COLUMN_NAME");
                        String pkName = primaryKeys.getString("PK_NAME");
                        System.out.println("  - Columna: " + columnName + ", Nombre de la clave primaria: " + pkName);
                    }
                }

                // Obtener claves foráneas (exported keys)
                try (ResultSet exportedKeys = dbmt7.getExportedKeys(bd, null, tableName)) {
                    System.out.println("Claves Foráneas:");
                    while (exportedKeys.next()) {
                        String fkTableName = exportedKeys.getString("FKTABLE_NAME");
                        String fkColumnName = exportedKeys.getString("FKCOLUMN_NAME");
                        String pkColumnName = exportedKeys.getString("PKCOLUMN_NAME");
                        String fkName = exportedKeys.getString("FK_NAME");
                        System.out.println("  - Tabla Foránea: " + fkTableName + ", Columna Foránea: " + fkColumnName +
                                ", Columna Referenciada: " + pkColumnName + ", Nombre de la clave foránea: " + fkName);
                    }
                }

                System.out.println("---------------------------------");
            }

        }
    }

    DatabaseMetaData dbmdt5;

    public void ejercicio10() throws SQLException {
        String query = "SELECT *, nombre AS non FROM alumnos";

        try (PreparedStatement ps = conexion.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();

            System.out.println("Información de las columnas devueltas por la consulta:");
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                String columnAlias = metaData.getColumnLabel(i);
                String columnType = metaData.getColumnTypeName(i);
                boolean isAutoIncrement = metaData.isAutoIncrement(i);
                boolean isNullable = metaData.isNullable(i) == ResultSetMetaData.columnNullable;

                System.out.println("Columna: " + columnName);
                System.out.println("Alias: " + columnAlias);
                System.out.println("Tipo de dato: " + columnType);
                System.out.println("¿Autoincrementado?: " + (isAutoIncrement ? "Sí" : "No"));
                System.out.println("¿Permite nulos?: " + (isNullable ? "Sí" : "No"));
            }
        }
    }

    DatabaseMetaData dbmt6;

    /**
     * Para garantizar que no nos falle usamos -> Transaciones
     * Porque nos permite agrupar varias operaciones , asegurando que todas las
     * operaciones
     * se completen correctamente
     * 
     * @throws SQLException
     */
    public void ejercicio12a() throws SQLException {

        try {
            // 1º DESACTIVAR EL AUTOCOMMIT
            this.conexion.setAutoCommit(false);
            // Puedo hacerla como quiera, tanto con Prepared Stament como Stament
            Statement st = this.conexion.createStatement();
            st.executeUpdate("INSERT INTO ALUMNOS VALUES (11,'Javi', 'Santos', 200,20,88)");
            st.executeUpdate("INSERT INTO ALUMNOS VALUES (12, 'ferreira', 200,20,4)");

            this.conexion.commit();

            // st.executeUpdate("INSERT INTO ALUMNOS VALUES (11,'marcos', 'ferreira',
            // 200,20,0)");
            // st.executeUpdate("INSERT INTO ALUMNOS VALUES ('JAVI', 200, 20)");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.conexion.rollback();
            this.conexion.setAutoCommit(true);
        }

    }

    // con el autocomit desactivado
    public void ejercicio12b() throws SQLException {

        try {
            // 1º DESACTIVAR EL AUTOCOMMIT
            this.conexion.setAutoCommit(true);
            // Puedo hacerla como quiera, tanto con Prepared Stament como Stament
            Statement st = this.conexion.createStatement();

            st.executeUpdate("INSERT INTO ALUMNOS VALUES (11,'marcos', 'ferreira', 200,20,0)");
            st.executeUpdate("INSERT INTO ALUMNOS VALUES ('JAVI', 200, 20)");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            this.conexion.rollback();
            this.conexion.setAutoCommit(true);
        }

    }

    // leer en bd y guardar en disco duro
    public void ejercicio13a(String nombreArchivo, String nombreImg) throws SQLException {

        String query = "SELECT * from imagenes where nombre = ?";
        try (PreparedStatement ps = this.conexion.prepareStatement(query)) {
            ps.setString(1, nombreImg);
            // executeQuery ejecuta la consulta preparada y devuelve un objeto tipo Result
            // Set

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // extrae el contenido binario del campo imagen en la fila actual del result set
                    // -> como flujo de bytes en el inputStream .

                    InputStream inputStream = rs.getBinaryStream("imagen");
                    File archivoSalida = new File(nombreArchivo);

                    // escribir el binario en el disco duro

                    try (FileOutputStream fileOutputStrea = new FileOutputStream(archivoSalida)) {
                        byte[] buffer = new byte[1024];
                        int byteLeidos;
                        while ((byteLeidos = inputStream.read(buffer)) != -1) {
                            fileOutputStrea.write(buffer, 0, byteLeidos);
                        }
                        System.out.println("ImagenGuardada");
                    } catch (Exception e) {
                        System.out.println("No se encontró la imagen");
                    }

                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    // almacenar una imagen desde disco duro en la bd

    public void ejercicio13b(String rutaArchivo, String nombreImg) throws SQLException {
        String query = "INSERT INTO imagenes (nombre,imagen) VALUES (?, ? )";
        File archivoImg = new File(rutaArchivo);
        try (FileInputStream fileInputStream = new FileInputStream(archivoImg)) {
            PreparedStatement ps = this.conexion.prepareStatement(query);
            ps.setString(1, nombreImg);
            ps.setBinaryStream(2, fileInputStream, (int) archivoImg.length());

            int filasInsertadas = ps.executeUpdate();
            if (filasInsertadas > 0) {
                System.out.println("Imagen insertada correctamente");
            } else {
                System.out.println("Error al insertar la imagen");
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void ejercicio15a() throws SQLException {
        // para la ejecucion de elementos almacenados ->CallableStatement
        // se usa el metodo prepareCall de connection para crear la consulta
        // parametrizada
        // el parametro es la consulta con sus argumentos

        CallableStatement cs = this.conexion.prepareCall("CALL getAulas(?,?)");
        // ahora se ponen los valores de parametro
        cs.setInt(1, 10);
        cs.setString(2, "o");
        ResultSet result = cs.executeQuery();
        while (result.next()) {
            System.out.println(result.getInt(1) + "\t" +
                    result.getString("nombreAula") + "\t" + result.getInt("puestos"));
        }
    }

    public void ejercicio15b() throws SQLException {

        // ejecutar la funcion de suma de la bd
        CallableStatement st = this.conexion.prepareCall("SELECT SUMA()");
        ResultSet result = st.executeQuery();
        while (result.next()) {
            System.out.println(result.getInt(1));
        }
    }

    // permita buscar una cadena en cualquier tipo de columna de tipo char/varchar
    // de cualquier tabla de una bd dada , poner datos de la bd, tabla , columna ,
    // y donde se encontro la coincidencia ademas del txt completo

    public void ejercicio16(String bd, String texto) throws SQLException {
        // primero hay obtener la lista de tablas->DataBaseMetaData
        // esta nos permite obtener informacion a traves de != elem de la bd,
        // como datos, tb, vistas, proced.....
        DatabaseMetaData dbmt;
        ResultSet tablas;
        try {
            dbmt = this.conexion.getMetaData();
            tablas = dbmt.getTables(bd, null, null, null);
            while (tablas.next()) {
                // obtencion de las columnas de tipo char y varchar
                String nombreTabla = tablas.getString("TABLE_NAME");
                ResultSet columnas = dbmt.getColumns(bd, null, nombreTabla, null);
                while (columnas.next()) {
                    String nombreColumna = columnas.getString("COLUMN_NAME");
                    String tipoColumna = columnas.getString("TYPE_NAME");
                    // fitral por char y varchar
                    if (tipoColumna.equalsIgnoreCase("char") || tipoColumna.equalsIgnoreCase("varchar")) {
                        String query = "SELECT " + nombreColumna + " FROM " + nombreTabla + " WHERE " + nombreColumna
                                + " LIKE ?";
                        try (PreparedStatement pt = this.conexion.prepareStatement(query)) {
                            pt.setString(1, "%" + texto + "%");
                            try (ResultSet resultados = pt.executeQuery()) {
                                while (resultados.next()) {
                                    String textoEncontrado = resultados.getString(nombreColumna);
                                    System.out.println("Base de datos: " + bd);
                                    System.out.println("Tabla: " + nombreTabla);
                                    System.out.println("Columna: " + nombreColumna);
                                    System.out.println("Texto encontrado: " + textoEncontrado);
                                    System.out.println("----------------------------------------");
                                }
                            }

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) {

        Ejercicio1 ejercicio1 = new Ejercicio1();
        ejercicio1.abrirConexion("add", "localhost", "root", null);
        try {
            // ejercicio1.consultarDatosNombre("%nic%");
            // ejercicio1.altaAlumnosAsignatura("Nicoleee", "Diaz", 161, 20);
            // ejercicio1.altaAsignaturas(10, "Empresa");
            // ejercicio1.bajaAsignaturas(8);
            // ejercicio1.bajarAlumno(17);

            // ejercicio1.consultarDatosNombre("%e%");
            // ejercicio1.modificarAlumno(16, "Nicole", "Diaz", 165, 20, 16);
            // ejercicio1.modificarAsignatura(9, "Servicios prog. procesos", 9);
            // ejercicio1.consultarNotasAlumno();
            // ejercicio1.nombreAsignaturasSinNombre();
            // ejercicio1.ejercicio6a("a", 100);
            // ejercicio1.ejercicio6b("%" + "a" + "%", 150);

            // ejercicio1.ejercicio8(null, null, null, null);
            // ejercicio1.tiempoEjecucion(1000);
            // ejercicio1.ejercicio8("alumnos", "dni", "varchar(20)", "UNIQUE");

            // ejercicio1.ejercicio9("add");
            // ejercicio1.ejercicio9a("add");

            // ejercicio1.ejercicio9b("add");
            // ejercicio1.ejercicio9c("tema8");
            // ejercicio1.ejercicio9d("tema8");
            // ejercicio1.ejercicio9e("add");
            // ejercicio1.ejercicio9f("add");
            // ejercicio1.ejercicio9g("add");
            // ejercicio1.ejercicio9e("add");
            // ejercicio1.ejercicio9h("add");
            // ejercicio1.ejercicio12a();
            ejercicio1.ejercicio13a("h3.png", "h2.jpg");
            // ejercicio1.ejercicio13b(
            //         "C:\\Users\\nicki\\OneDrive\\Escritorio\\Miscosas\\CICLO\\AccesoDatos\\tema4\\img\\h.dat",
            //         "h3.jpg");
            // ejercicio1.ejercicio15a();
            //  ejercicio1.ejercicio15b();
            // ejercicio1.ejercicio16("add", "ca");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}