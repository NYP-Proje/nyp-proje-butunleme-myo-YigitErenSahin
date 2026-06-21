package gui;

import database.VeritabaniBaglantisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UrunEkleEkrani extends JFrame {
    private JTextField txtUrunAdi;
    private JTextArea txtAciklama;
    private JTextField txtFiyat;
    private String saticiKullaniciAdi;

    public UrunEkleEkrani(String saticiKullaniciAdi) {
        this.saticiKullaniciAdi = saticiKullaniciAdi;

        setTitle("Yeni Ürün Ekle");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 10));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Ürün Adı:"));
        txtUrunAdi = new JTextField();
        formPanel.add(txtUrunAdi);

        formPanel.add(new JLabel("Açıklama:"));
        txtAciklama = new JTextArea();
        txtAciklama.setLineWrap(true);
        txtAciklama.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(new JScrollPane(txtAciklama));

        formPanel.add(new JLabel("Başlangıç Fiyatı (₺):"));
        txtFiyat = new JTextField();
        formPanel.add(txtFiyat);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnKaydet = new JButton("Ürünü 5 Dk Açık Artırmaya Çıkar");
        btnKaydet.setBackground(new Color(46, 204, 113));
        btnKaydet.setForeground(Color.WHITE);
        btnKaydet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKaydet.setFocusPainted(false);

        btnKaydet.addActionListener(e -> urunuKaydet());

        anaPanel.add(btnKaydet, BorderLayout.SOUTH);
    }

    private void urunuKaydet() {
        String ad = txtUrunAdi.getText();
        String aciklama = txtAciklama.getText();
        String fiyatStr = txtFiyat.getText();

        if (ad.isEmpty() || fiyatStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen alanları doldurun!");
            return;
        }

        try {
            double fiyat = Double.parseDouble(fiyatStr);


            long bitisZamani = System.currentTimeMillis() + (30 * 1000);

            String sql = "INSERT INTO Urunler (urun_adi, aciklama, baslangic_fiyati, mevcut_fiyat, satici_id, bitis_zamani) " +
                    "VALUES (?, ?, ?, ?, (SELECT id FROM Kullanicilar WHERE kullanici_adi = ?), ?)";

            try (Connection conn = VeritabaniBaglantisi.baglan();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ad);
                pstmt.setString(2, aciklama);
                pstmt.setDouble(3, fiyat);
                pstmt.setDouble(4, fiyat);
                pstmt.setString(5, saticiKullaniciAdi);
                pstmt.setLong(6, bitisZamani); // Zamanı kaydediyoruz

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Ürün 5 dakikalık açık artırmaya çıktı!");
                dispose();

            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Geçerli bir fiyat girin!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }
}