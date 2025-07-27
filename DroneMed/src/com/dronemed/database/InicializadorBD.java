package com.dronemed.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicializadorBD {

    private static final String URL = "jdbc:sqlite:dronemed.db";
    private static final Logger LOGGER = Logger.getLogger(InicializadorBD.class.getName());

    //metodo principal para inicializar la base de datos completa
    //crea el archivo .db y tambien inserta las tablas y algunos datos de prueba
    public static void inicializarBaseDatos() {
        try {
            // 1. Cargar driver SQLite
            Class.forName("org.sqlite.JDBC");

            // 2. Crear conexión (esto crea automáticamente el archivo .db)
            Connection conn = DriverManager.getConnection(URL);
            LOGGER.info("Archivo dronemed.db creado exitosamente");

            // 3. Habilitar foreign keys
            Statement stmt = conn.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;");

            // 4. Crear todas las tablas
            crearTablas(conn);

            // 5. Insertar datos de prueba
            insertarDatosPrueba(conn);

            // 6. Verificar estructura
            verificarEstructura(conn);

            conn.close();
            LOGGER.info("Base de datos inicializada completamente");

        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Driver SQLite no encontrado", e);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al crear la base de datos", e);
        }
    }

    //crear todas las tablas del sistema
    private static void crearTablas(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        //tabla usuarios
        String sqlUsuarios = """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL,
                nombre VARCHAR(100) NOT NULL,
                apellido VARCHAR(100) NOT NULL,
                email VARCHAR(150) UNIQUE NOT NULL,
                rol VARCHAR(20) CHECK(rol IN ('ADMIN', 'TECNICO', 'CLIENTE')) NOT NULL,
                activo BOOLEAN DEFAULT 1,
                fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
                ultimo_acceso DATETIME,
                intentos_fallidos INTEGER DEFAULT 0,
                bloqueado BOOLEAN DEFAULT 0
            )""";
        stmt.execute(sqlUsuarios);
        LOGGER.info("Tabla 'usuarios' creada");

        //tabla clientes
        String sqlClientes = """
            CREATE TABLE IF NOT EXISTS clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre VARCHAR(200) NOT NULL,
                tipo_institucion VARCHAR(20) CHECK(tipo_institucion IN ('HOSPITAL', 'CLINICA', 'CENTRO_SALUD', 'FARMACIA')) NOT NULL,
                direccion TEXT NOT NULL,
                telefono VARCHAR(20),
                email VARCHAR(150),
                latitud DECIMAL(10, 8),
                longitud DECIMAL(11,8),
                prioridad VARCHAR(10) CHECK(prioridad IN ('BAJA', 'MEDIA', 'ALTA', 'CRITICA')) DEFAULT 'MEDIA',
                activo BOOLEAN DEFAULT 1,
                fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
            )""";
        stmt.execute(sqlClientes);
        LOGGER.info("Tabla 'clientes' creada");

        //tabla drones
        String sqlDrones = """
            CREATE TABLE IF NOT EXISTS drones (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                modelo VARCHAR(100) NOT NULL,
                tipo_carga VARCHAR(20) CHECK(tipo_carga IN ('LIGERA', 'MEDIA', 'PESADA')) NOT NULL,
                capacidad_kg DECIMAL(5, 2) NOT NULL,
                autonomia_km DECIMAL(6, 2) NOT NULL,
                velocidad_max_kmh DECIMAL(5, 2) NOT NULL,
                estado VARCHAR(20) CHECK(estado IN ('DISPONIBLE', 'EN_VUELO', 'MANTENIMIENTO', 'FUERA_SERVICIO')) DEFAULT 'DISPONIBLE',
                ubicacion_actual VARCHAR(100),
                horas_vuelo DECIMAL(8, 2) DEFAULT 0,
                fecha_adquisicion DATE,
                ultimo_mantenimiento DATETIME,
                proximo_mantenimiento DATETIME,
                activo BOOLEAN DEFAULT 1
            )""";
        stmt.execute(sqlDrones);
        LOGGER.info("Tabla 'drones' creada");

        //tabla pedidos
        String sqlPedidos = """
            CREATE TABLE IF NOT EXISTS pedidos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER NOT NULL,
                dron_id INTEGER,
                descripcion_medicamento TEXT NOT NULL,
                peso_kg DECIMAL(5, 2) NOT NULL,
                prioridad VARCHAR(10) CHECK(prioridad IN ('URGENTE', 'ESTANDAR')) NOT NULL,
                estado VARCHAR(20) CHECK(estado IN ('PENDIENTE', 'ASIGNADO', 'EN_TRANSITO', 'ENTREGADO', 'CANCELADO')) DEFAULT 'PENDIENTE',
                direccion_origen TEXT NOT NULL,
                direccion_destino TEXT NOT NULL,
                latitud_origen DECIMAL(10, 8),
                longitud_origen DECIMAL(11, 8),
                latitud_destino DECIMAL(10, 8),
                longitud_destino DECIMAL(11, 8),
                fecha_solicitud DATETIME DEFAULT CURRENT_TIMESTAMP,
                fecha_asignacion DATETIME,
                fecha_entrega DATETIME,
                tiempo_estimado_minutos INTEGER,
                observaciones TEXT,
                FOREIGN KEY (cliente_id) REFERENCES clientes(id),
                FOREIGN KEY (dron_id) REFERENCES drones(id)
            )""";
        stmt.execute(sqlPedidos);
        LOGGER.info("Tabla 'pedidos' creada");

        //tabla mantenimiento
        String sqlMantenimientos = """
            CREATE TABLE IF NOT EXISTS mantenimientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                dron_id INTEGER NOT NULL,
                tipo VARCHAR(20) CHECK(tipo IN ('PREVENTIVO', 'CORRECTIVO', 'EMERGENCIA')) NOT NULL,
                descripcion TEXT NOT NULL,
                fecha_programada DATETIME NOT NULL,
                fecha_inicio DATETIME,
                fecha_fin DATETIME,
                tecnico_responsable VARCHAR(100),
                costo DECIMAL(10, 2),
                estado VARCHAR(20) CHECK(estado IN('PROGRAMADO', 'EN_PROCESO', 'COMPLETADO', 'CANCELADO')) DEFAULT 'PROGRAMADO',
                observaciones TEXT,
                duracion_minutos INTEGER,
                FOREIGN KEY (dron_id) REFERENCES drones(id)
            )""";
        stmt.execute(sqlMantenimientos);
        LOGGER.info("Tabla 'mantenimientos' creada");

        stmt.close();
    }

    //INSERTAR DATOS DE PRUEBA EN LAS TABLAS SOLO SI NO EXISTEN
    private static void insertarDatosPrueba(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // Verificar si ya existen datos antes de insertar
        if (!existenDatos(conn, "usuarios")) {
            //datos usuarios
            String insertUsuarios = """
                    INSERT INTO usuarios (nombre_usuario, password, nombre, apellido, email, rol) VALUES
                    ('admin', 'admin123', 'Administrador', 'Sistema', 'admin@dronemed.com', 'ADMIN'),
                    ('tecnico1', 'tecnico123', 'Juan', 'Perez', 'juan.perez@dronemed.com', 'TECNICO'),
                    ('cliente1', 'cliente123', 'Hospital', 'Central', 'contacto@hospitalcentral.com', 'CLIENTE')""";
            stmt.execute(insertUsuarios);
            LOGGER.info("Datos de usuarios insertados");
        } else {
            LOGGER.info("Datos de usuarios ya existen, omitiendo inserción");
        }

        if (!existenDatos(conn, "clientes")) {
            //datos clientes - Establecimientos de Arequipa con códigos únicos
            String insertClientes = """
                    INSERT INTO clientes (nombre, tipo_institucion, direccion, telefono, email, latitud, longitud, prioridad) VALUES
                    ('HOSP-001 - Hospital Nacional Carlos Alberto Seguín Escobedo', 'HOSPITAL', 'Av. Parra 202, Cercado de Arequipa', '054-231-010', 'contacto@hnseguin.gob.pe', -16.3988, -71.5350, 'ALTA'),
                    ('HOSP-002 - Hospital Regional Honorio Delgado', 'HOSPITAL', 'Av. Alcides Carrión s/n, Arequipa', '054-231-818', 'info@hrhd.gob.pe', -16.3950, -71.5200, 'ALTA'),
                    ('CLIN-001 - Clínica Arequipa', 'CLINICA', 'Av. Bolognesi 132, Arequipa', '054-252-424', 'contacto@clinicaarequipa.pe', -16.3988, -71.5370, 'MEDIA'),
                    ('CLIN-002 - Clínica San Juan de Dios', 'CLINICA', 'Calle Puente Grau 114, Arequipa', '054-381-300', 'info@sanjuandedios.pe', -16.3950, -71.5320, 'MEDIA'),
                    ('CLIN-003 - Clínica Maison de Santé', 'CLINICA', 'Av. Ejercito 1020, Yanahuara', '054-605-050', 'contacto@maisonsante.pe', -16.3900, -71.5450, 'MEDIA'),
                    ('CENT-001 - Centro de Salud Paucarpata', 'CENTRO_SALUD', 'Av. Mariscal Castilla 500, Paucarpata', '054-426-789', 'paucarpata@minsa.gob.pe', -16.4200, -71.5100, 'CRITICA'),
                    ('CENT-002 - Centro de Salud Cayma', 'CENTRO_SALUD', 'Av. Cayma 200, Cayma', '054-287-456', 'cayma@minsa.gob.pe', -16.3700, -71.5500, 'CRITICA'),
                    ('FARM-001 - Farmacia Inkafarma Plaza de Armas', 'FARMACIA', 'Portal de Flores 136, Cercado', '054-215-678', 'plazaarmas@inkafarma.pe', -16.3988, -71.5369, 'BAJA'),
                    ('FARM-002 - Farmacia Boticas y Salud Cercado', 'FARMACIA', 'Calle Mercaderes 121, Cercado', '054-234-567', 'cercado@boticasysalud.pe', -16.3975, -71.5380, 'BAJA'),
                    ('FARM-003 - Farmacia MiFarma Yanahuara', 'FARMACIA', 'Av. Lima 456, Yanahuara', '054-245-890', 'yanahuara@mifarma.pe', -16.3920, -71.5420, 'BAJA'),
                    ('FARM-004 - Farmacia Arcángel Cayma', 'FARMACIA', 'Calle Bolívar 789, Cayma', '054-267-123', 'cayma@arcangel.pe', -16.3750, -71.5480, 'BAJA'),
                    ('FARM-005 - Farmacia Universal Paucarpata', 'FARMACIA', 'Av. Kennedy 321, Paucarpata', '054-445-234', 'paucarpata@universal.pe', -16.4180, -71.5120, 'BAJA')""";
            stmt.execute(insertClientes);
            LOGGER.info("Datos de clientes insertados");
        } else {
            LOGGER.info("Datos de clientes ya existen, omitiendo inserción");
        }

        if (!existenDatos(conn, "drones")) {
            //datos drones
            String insertDrones = """
                    INSERT INTO drones (modelo, tipo_carga, capacidad_kg, autonomia_km, velocidad_max_kmh, estado) VALUES
                    ('DJI Matrice 300', 'LIGERA', 2.7, 15.0, 82.0, 'DISPONIBLE'),
                    ('DJI Matrice 600', 'MEDIA', 6.0, 5.0, 65.0, 'DISPONIBLE'),
                    ('Freefly Alta X', 'PESADA', 15.0, 8.0, 50.0, 'MANTENIMIENTO')""";
            stmt.execute(insertDrones);
            LOGGER.info("Datos de drones insertados");
        } else {
            LOGGER.info("Datos de drones ya existen, omitiendo inserción");
        }

        if (!existenDatos(conn, "pedidos")) {
            //datos pedidos - 2 pedidos pendientes para simulación
            String insertPedidos = """
                    INSERT INTO pedidos (cliente_id, descripcion_medicamento, peso_kg, prioridad, estado, 
                                       direccion_origen, direccion_destino, latitud_origen, longitud_origen, 
                                       latitud_destino, longitud_destino, tiempo_estimado_minutos, observaciones) VALUES
                    (1, 'Insulina de acción rápida - 10 viales', 0.5, 'URGENTE', 'PENDIENTE', 
                     'Centro de Distribución DroneMed - Arequipa', 'Hospital Nacional Carlos Alberto Seguín Escobedo', 
                     -16.3988, -71.5350, -16.3988, -71.5350, 15, 'Medicamento crítico para paciente diabético'),
                    (6, 'Antibióticos - Amoxicilina 500mg x 20 tabletas', 0.3, 'ESTANDAR', 'PENDIENTE', 
                     'Centro de Distribución DroneMed - Arequipa', 'Centro de Salud Paucarpata', 
                     -16.3988, -71.5350, -16.4200, -71.5100, 25, 'Tratamiento para infección respiratoria')""";
            stmt.execute(insertPedidos);
            LOGGER.info("Datos de pedidos insertados");
        } else {
            LOGGER.info("Datos de pedidos ya existen, omitiendo inserción");
        }

        stmt.close();
    }

    //VERIFICAR SI YA EXISTEN DATOS EN UNA TABLA
    private static boolean existenDatos(Connection conn, String nombreTabla) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + nombreTabla;
        try (Statement stmt = conn.createStatement();
             var rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    //VERIFICAR QUE TODAS LAS TABLAS SE CREARON CORRECTAMENTE
    private static void verificarEstructura(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        String[] tablas = {"usuarios", "clientes", "drones", "pedidos", "mantenimientos"};

        for (String tabla : tablas) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tabla);
            if (rs.next()) {
                int count = rs.getInt(1);
                LOGGER.info(String.format("tabla '%s' : %d registros", tabla, count));
            }
            rs.close();
        }

        stmt.close();
    }

    //metodo para usar en el main de la aplicacion
    public static void main(String[] args) {
        System.out.println("Iniciando creacion de base de datos...");
        inicializarBaseDatos();
        System.out.println("Proceso completado. Revisa el archivo 'dronemed.db' en el proyecto.");
    }
}
