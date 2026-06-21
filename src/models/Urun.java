package models;

public class Urun {
    private int id;
    private String urunAdi;
    private double baslangicFiyati;
    private double mevcutFiyat;

    public Urun(int id, String urunAdi, double baslangicFiyati, double mevcutFiyat) {
        this.id = id;
        this.urunAdi = urunAdi;
        this.baslangicFiyati = baslangicFiyati;
        this.mevcutFiyat = mevcutFiyat;
    }

    public int getId() { return id; }
    public String getUrunAdi() { return urunAdi; }
    public double getBaslangicFiyati() { return baslangicFiyati; }
    public double getMevcutFiyat() { return mevcutFiyat; }
}