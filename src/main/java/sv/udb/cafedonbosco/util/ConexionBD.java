package sv.udb.cafedonbosco.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto unico de obtencion de conexiones JDBC a MySQL.
 * <p>
 * La URL, el usuario y la contrasena pueden sobreescribirse con las
 * variables de entorno DB_URL, DB_USUARIO y DB_PASSWORD para evitar dejar
 * credenciales fijas en el codigo en un despliegue real; si no estan
 * definidas se usan los valores por defecto de desarrollo local.
 */
public final class ConexionBD {

    private static final String URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://localhost:3306/cafe_don_bosco"
                    + "?useSSL=false"
                    + "&serverTimezone=UTC"
                    + "&allowPublicKeyRetrieval=true"
    );

    private static final String USUARIO = System.getenv().getOrDefault("DB_USUARIO", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private ConexionBD() {
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, PASSWORD);
    }
}
