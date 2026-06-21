package models;

public class Kullanici {
    // Değişkenleri 'private' yaparak dışarıdan doğrudan erişimi kapatıyoruz (Encapsulation)
    private int id;
    private String kullaniciAdi;
    private String sifre;
    private String rol;

    // Sınıfın yapıcı metodu (Constructor)
    public Kullanici(int id, String kullaniciAdi, String sifre, String rol) {
        this.id = id;
        this.kullaniciAdi = kullaniciAdi;
        this.sifre = sifre;
        this.rol = rol;
    }

    // Bilgileri güvenli bir şekilde okumak için Getter metotları
    public int getId() { return id; }
    public String getKullaniciAdi() { return kullaniciAdi; }
    public String getSifre() { return sifre; }
    public String getRol() { return rol; }
}