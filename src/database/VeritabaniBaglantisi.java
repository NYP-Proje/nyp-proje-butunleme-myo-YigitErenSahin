package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VeritabaniBaglantisi {


    private static final String URL = "jdbc:sqlite:AcikArtirma.db";

    public static Connection baglan() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println("SQLite Veritabanına başarıyla bağlandık, Yiğit!");
        } catch (SQLException e) {
            System.out.println("Bağlantı hatası: " + e.getMessage());
        }
        return conn;
    }
}