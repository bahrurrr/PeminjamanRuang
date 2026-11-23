package peminjamanruangpens.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL_ENV = "PEMINJAMAN_DB_URL";
    private static final String USER_ENV = "PEMINJAMAN_DB_USER";
    private static final String PASSWORD_ENV = "PEMINJAMAN_DB_PASSWORD";

    private DatabaseConnection() {}

    public static Connection openConnection() throws SQLException {
        String url = requiredEnvironmentVariable(URL_ENV);
        String user = requiredEnvironmentVariable(USER_ENV);
        String password = requiredEnvironmentVariable(PASSWORD_ENV);
        return DriverManager.getConnection(url, user, password);
    }

    private static String requiredEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Environment variable belum diatur: " + name);
        }
        return value;
    }
}