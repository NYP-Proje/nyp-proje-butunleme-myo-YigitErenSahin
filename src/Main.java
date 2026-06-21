import database.TabloOlusturucu;
import gui.GirisEkrani;

public class Main {
    public static void main(String[] args) {
        // 1. Önce arka planda veritabanı tablolarını hazırlıyoruz (zaten yazmıştık)
        TabloOlusturucu.tablolariKur();

        // 2. Şimdi görsel giriş ekranımızı yaratıp görünür hale getiriyoruz
        GirisEkrani ekran = new GirisEkrani();
        ekran.setVisible(true);
    }
}