package gui;

import database.VeritabaniBaglantisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TeklifEkrani extends JFrame {
    private int urunId;
    private double mevcutFiyat;
    private JTextField txtYeniTeklif;
    private String teklifVerenKullanici;

    public TeklifEkrani(int urunId, String urunAdi, double mevcutFiyat, String teklifVerenKullanici) {
        this.urunId = urunId;
        this.mevcutFiyat = mevcutFiyat;
        this.teklifVerenKullanici = teklifVerenKullanici;

        setTitle("Teklif Ver - " + urunAdi);
        setSize(400, 350); // Ekranı butonlar sığsın diye biraz daha büyüttük
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Elemanları alt alta dizmek için BoxLayout kullanıyoruz
        JPanel anaPanel = new JPanel();
        anaPanel.setLayout(new BoxLayout(anaPanel, BoxLayout.Y_AXIS));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        // --- 1. BİLGİ ALANI ---
        JLabel lblBilgi = new JLabel("Ürün: " + urunAdi, SwingConstants.CENTER);
        lblBilgi.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBilgi.setAlignmentX(Component.CENTER_ALIGNMENT);
        anaPanel.add(lblBilgi);
        anaPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Araya boşluk ekleme

        JLabel lblMevcut = new JLabel("Mevcut Fiyat: " + mevcutFiyat + " ₺", SwingConstants.CENTER);
        lblMevcut.setForeground(new Color(231, 76, 60));
        lblMevcut.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMevcut.setAlignmentX(Component.CENTER_ALIGNMENT);
        anaPanel.add(lblMevcut);
        anaPanel.add(Box.createRigidArea(new Dimension(0, 20)));


        // --- 2. HIZLI TEKLİF BUTONLARI ---
        JLabel lblHizli = new JLabel("Hızlı Teklif Ver (Mevcut Fiyata Ekler):");
        lblHizli.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHizli.setAlignmentX(Component.CENTER_ALIGNMENT);
        anaPanel.add(lblHizli);

        JPanel hizliTeklifPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        hizliTeklifPanel.setBackground(Color.WHITE);

        // Hızlı butonlarımızı oluşturup panele ekliyoruz
        hizliTeklifPanel.add(createHizliButon("+ 50 ₺", 50));
        hizliTeklifPanel.add(createHizliButon("+ 100 ₺", 100));
        hizliTeklifPanel.add(createHizliButon("+ 500 ₺", 500));

        anaPanel.add(hizliTeklifPanel);
        anaPanel.add(Box.createRigidArea(new Dimension(0, 20)));


        // --- 3. MANUEL TEKLİF GİRİŞİ ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(new JLabel("Veya Kendi Teklifiniz (₺):"));

        txtYeniTeklif = new JTextField(8);
        // Kullanıcıya kolaylık olsun diye kutuya direkt geçmesi gereken minimum rakamı yazıyoruz
        txtYeniTeklif.setText(String.valueOf(mevcutFiyat + 1));
        txtYeniTeklif.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(txtYeniTeklif);

        anaPanel.add(inputPanel);
        anaPanel.add(Box.createRigidArea(new Dimension(0, 15)));


        // --- 4. MANUEL ONAY BUTONU ---
        JButton btnOnayla = new JButton("Özel Teklifi Onayla");
        btnOnayla.setBackground(new Color(52, 152, 219));
        btnOnayla.setForeground(Color.WHITE);
        btnOnayla.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnOnayla.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnOnayla.setFocusPainted(false);
        btnOnayla.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Kutudaki yazılı olan rakamı okuyup gönderir
        btnOnayla.addActionListener(e -> teklifiGonder(txtYeniTeklif.getText()));

        anaPanel.add(btnOnayla);
    }

    // HIZLI BUTON ÜRETİCİSİ (Tek tıkla fiyatı hesaplar ve direkt gönderir)
    private JButton createHizliButon(String text, double eklenecekMiktar) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(46, 204, 113)); // Yeşil renk
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            double yeniFiyat = mevcutFiyat + eklenecekMiktar;
            teklifiGonder(String.valueOf(yeniFiyat)); // Hesaplanmış fiyatı direkt veritabanına gönderir
        });

        return btn;
    }

    // ASIL TEKLİF GÖNDERME MOTORU (Hem hızlı butonlar hem de manuel giriş burayı kullanır)
    private void teklifiGonder(String teklifMetni) {
        try {
            double yeniTeklif = Double.parseDouble(teklifMetni);

            if (yeniTeklif <= mevcutFiyat) {
                JOptionPane.showMessageDialog(this, "Teklifiniz mevcut fiyattan yüksek olmalıdır!");
                return;
            }

            // Süreyi 10 saniye uzatmalı SQL komutumuz
            String sql = "UPDATE Urunler SET mevcut_fiyat = ?, son_teklif_veren = ?, bitis_zamani = bitis_zamani + 10000 WHERE id = ? AND durum = 'ACIK'";

            try (Connection conn = VeritabaniBaglantisi.baglan();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setDouble(1, yeniTeklif);
                pstmt.setString(2, teklifVerenKullanici);
                pstmt.setInt(3, urunId);
                int etkilenenSatir = pstmt.executeUpdate();

                if (etkilenenSatir > 0) {
                    JOptionPane.showMessageDialog(this, "Tebrikler, teklif başarılı! Süre 10 saniye uzatıldı.");
                    dispose(); // İşlem bitince ekranı kapat
                } else {
                    JOptionPane.showMessageDialog(this, "Bu ürünün süresi dolmuş veya kapalı!");
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lütfen geçerli bir sayı girin!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }
}