package gui;

import database.VeritabaniBaglantisi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GirisEkrani extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtSifre;

    public GirisEkrani() {
        setTitle("Açık Artırma Sistemi - Giriş");
        setSize(400, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 20));
        anaPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        JLabel lblBaslik = new JLabel("Sisteme Giriş", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBaslik.setForeground(new Color(44, 62, 80));
        anaPanel.add(lblBaslik, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Kullanıcı Adı:"));
        txtKullaniciAdi = new JTextField();
        txtKullaniciAdi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtKullaniciAdi);

        formPanel.add(new JLabel("Şifre:"));
        txtSifre = new JPasswordField();
        txtSifre.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formPanel.add(txtSifre);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JPanel butonPaneli = new JPanel(new GridLayout(3, 1, 10, 10));
        butonPaneli.setBackground(Color.WHITE);
        butonPaneli.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnGiris = new JButton("Giriş Yap");
        btnGiris.setBackground(new Color(46, 204, 113));
        btnGiris.setForeground(Color.WHITE);
        btnGiris.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGiris.setFocusPainted(false);
        btnGiris.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGiris.addActionListener(e -> girisYap());

        JButton btnKayit = new JButton("Yeni Hesap Oluştur");
        btnKayit.setBackground(new Color(52, 152, 219));
        btnKayit.setForeground(Color.WHITE);
        btnKayit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKayit.setFocusPainted(false);
        btnKayit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKayit.addActionListener(e -> {
            new KayitEkrani().setVisible(true);
        });

        JButton btnSifremiUnuttum = new JButton("Şifremi Unuttum");
        btnSifremiUnuttum.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSifremiUnuttum.setForeground(new Color(127, 140, 141));
        btnSifremiUnuttum.setContentAreaFilled(false);
        btnSifremiUnuttum.setBorderPainted(false);
        btnSifremiUnuttum.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSifremiUnuttum.addActionListener(e -> new SifremiUnuttumEkrani().setVisible(true));

        butonPaneli.add(btnGiris);
        butonPaneli.add(btnKayit);
        butonPaneli.add(btnSifremiUnuttum);

        anaPanel.add(butonPaneli, BorderLayout.SOUTH);
    }

    private void girisYap() {
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = new String(txtSifre.getPassword()).trim();

        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen alanları doldurun!");
            return;
        }

        String sql = "SELECT rol FROM Kullanicilar WHERE kullanici_adi = ? AND sifre = ?";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, kullaniciAdi);
            pstmt.setString(2, sifre);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String rol = rs.getString("rol");

                    // Ana ekranı açıyoruz ama giriş ekranını kapatmıyoruz (dispose yok)
                    new AnaEkran(kullaniciAdi, rol).setVisible(true);

                    // İkinci hesap girişi için kutuları temizliyoruz
                    txtKullaniciAdi.setText("");
                    txtSifre.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Kullanıcı adı veya şifre hatalı!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Veritabanı Hatası: " + ex.getMessage());
        }
    }
}