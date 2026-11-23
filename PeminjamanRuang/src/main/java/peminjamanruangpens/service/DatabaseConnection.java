package peminjamanruangpens.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/peminjaman_ruang_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345678"; // Sesuaikan dengan password pgAdmin kamu

    private static Connection connection = null;

    // Method untuk mendapatkan koneksi ke PostgreSQL
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Memuat driver PostgreSQL
                Class.forName("org.postgresql.Driver");
                // Membuka koneksi
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✓ Berhasil terhubung ke database PostgreSQL!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver PostgreSQL tidak ditemukan di pom.xml: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("✗ Gagal terhubung ke database: " + e.getMessage());
        }
        return connection;
    }

    // // ====== METHOD UNTUK MENGETES KONEKSI ======
    // public static void main(String[] args) {
    //     System.out.println("Sedang mencoba koneksi ke PostgreSQL...");
    //     Connection conn = getConnection();
    //     if (conn != null) {
    //         System.out.println("=========================================");
    //         System.out.println("🎉 SELAMAT! KONEKSI KE DATABASE BERHASIL!");
    //         System.out.println("=========================================");
    //         try {
    //             conn.close(); // Tutup koneksi setelah tes selesai
    //         } catch (SQLException ignored) {}
    //     } else {
    //         System.out.println("=========================================");
    //         System.out.println("❌ KONEKSI GAGAL! Periksa pesan error di atas.");
    //         System.out.println("=========================================");
    //     }
    // }

}