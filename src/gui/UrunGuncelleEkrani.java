package gui;

import database.VeritabaniBaglantisi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UrunGuncelleEkrani extends JFrame {
    private JTextField txtUrunAdi;
    private JTextArea txtAciklama;
    private JTextField txtFiyat;
    private int urunId;

    public UrunGuncelleEkrani(int urunId, String mevcutAd, String mevcutAciklama, double mevcutFiyat) {
        this.urunId = urunId;

        setTitle("İlanı Güncelle");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 10));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Yeni Ürün Adı:"));
        txtUrunAdi = new JTextField(mevcutAd);
        formPanel.add(txtUrunAdi);

        formPanel.add(new JLabel("Yeni Açıklama:"));
        txtAciklama = new JTextArea(mevcutAciklama);
        txtAciklama.setLineWrap(true);
        txtAciklama.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        formPanel.add(new JScrollPane(txtAciklama));

        formPanel.add(new JLabel("Yeni Başlangıç Fiyatı (₺):"));
        txtFiyat = new JTextField(String.valueOf(mevcutFiyat));
        formPanel.add(txtFiyat);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnGuncelle = new JButton("Değişiklikleri Kaydet");
        btnGuncelle.setBackground(new Color(52, 152, 219));
        btnGuncelle.setForeground(Color.WHITE);
        btnGuncelle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGuncelle.addActionListener(e -> guncelle());
        anaPanel.add(btnGuncelle, BorderLayout.SOUTH);
    }

    private void guncelle() {
        String ad = txtUrunAdi.getText();
        String aciklama = txtAciklama.getText();
        String fiyatStr = txtFiyat.getText();

        try {
            double fiyat = Double.parseDouble(fiyatStr);
            String sql = "UPDATE Urunler SET urun_adi = ?, aciklama = ?, baslangic_fiyati = ?, mevcut_fiyat = ? WHERE id = ?";

            try (Connection conn = VeritabaniBaglantisi.baglan();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, ad);
                pstmt.setString(2, aciklama);
                pstmt.setDouble(3, fiyat);
                pstmt.setDouble(4, fiyat); // Güncellendiğinde fiyatı da resetliyoruz
                pstmt.setInt(5, urunId);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "İlan başarıyla güncellendi!");
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: Geçerli bir fiyat girin!");
        }
    }
}