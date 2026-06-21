package database;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class TabloOlusturucu {

    public static void tablolariKur() {
        Connection conn = VeritabaniBaglantisi.baglan();
        if (conn == null) return;

        String kullaniciTablosu = "CREATE TABLE IF NOT EXISTS Kullanicilar ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "kullanici_adi TEXT NOT NULL UNIQUE,"
                + "sifre TEXT NOT NULL,"
                + "rol TEXT NOT NULL"
                + ");";


        String urunTablosu = "CREATE TABLE IF NOT EXISTS Urunler ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "urun_adi TEXT NOT NULL,"
                + "aciklama TEXT,"
                + "baslangic_fiyati REAL NOT NULL,"
                + "mevcut_fiyat REAL NOT NULL,"
                + "satici_id INTEGER,"
                + "son_teklif_veren TEXT DEFAULT 'Henüz Teklif Yok'," // KİM KAZANDI?
                + "bitis_zamani INTEGER,"
                + "durum TEXT DEFAULT 'ACIK',"
                + "FOREIGN KEY(satici_id) REFERENCES Kullanicilar(id)"
                + ");";

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(kullaniciTablosu);
            stmt.execute(urunTablosu);

            System.out.println("Yeni Veritabanı tabloları başarıyla oluşturuldu, Yiğit!");

            stmt.close();
            conn.close();
        } catch (SQLException e) {
            System.out.println("Tablo oluşturma hatası: " + e.getMessage());
        }
    }
}