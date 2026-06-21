package gui;

import database.VeritabaniBaglantisi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SifremiUnuttumEkrani extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtYeniSifre;
    private JPasswordField txtYeniSifreTekrar;

    public SifremiUnuttumEkrani() {
        setTitle("Şifremi Unuttum / Yenile");
        setSize(380, 320);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 10));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        JLabel lblBaslik = new JLabel("Şifre Yenileme Paneli", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBaslik.setForeground(new Color(44, 62, 80));
        anaPanel.add(lblBaslik, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Kullanıcı Adınız:"));
        txtKullaniciAdi = new JTextField();
        formPanel.add(txtKullaniciAdi);

        formPanel.add(new JLabel("Yeni Şifre:"));
        txtYeniSifre = new JPasswordField();
        formPanel.add(txtYeniSifre);

        formPanel.add(new JLabel("Yeni Şifre (Tekrar):"));
        txtYeniSifreTekrar = new JPasswordField();
        formPanel.add(txtYeniSifreTekrar);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnGuncelle = new JButton("Şifremi Güncelle");
        btnGuncelle.setBackground(new Color(52, 152, 219));
        btnGuncelle.setForeground(Color.WHITE);
        btnGuncelle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuncelle.setFocusPainted(false);
        btnGuncelle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnGuncelle.addActionListener(e -> sifreyiGuncelle());
        anaPanel.add(btnGuncelle, BorderLayout.SOUTH);
    }

    private void sifreyiGuncelle() {
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String yeniSifre = new String(txtYeniSifre.getPassword()).trim();
        String yeniSifreTekrar = new String(txtYeniSifreTekrar.getPassword()).trim();

        if (kullaniciAdi.isEmpty() || yeniSifre.isEmpty() || yeniSifreTekrar.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
            return;
        }

        // YENİLİK: Minimum 6 karakter kontrolü
        if (yeniSifre.length() < 6) {
            JOptionPane.showMessageDialog(this, "Güvenlik Uyarısı: Yeni şifreniz en az 6 karakterden oluşmalıdır!");
            return;
        }

        if (!yeniSifre.equals(yeniSifreTekrar)) {
            JOptionPane.showMessageDialog(this, "Girdiğiniz şifreler birbiriyle uyuşmuyor!");
            return;
        }

        String kontrolSql = "SELECT id FROM Kullanicilar WHERE kullanici_adi = ?";
        String guncelleSql = "UPDATE Kullanicilar SET sifre = ? WHERE kullanici_adi = ?";

        try (Connection conn = VeritabaniBaglantisi.baglan();
             PreparedStatement kontrolPstmt = conn.prepareStatement(kontrolSql)) {

            kontrolPstmt.setString(1, kullaniciAdi);
            try (ResultSet rs = kontrolPstmt.executeQuery()) {
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(this, "Böyle bir kullanıcı adı bulunamadı!");
                    return;
                }
            }

            try (PreparedStatement guncellePstmt = conn.prepareStatement(guncelleSql)) {
                guncellePstmt.setString(1, yeniSifre);
                guncellePstmt.setString(2, kullaniciAdi);
                guncellePstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Şifreniz başarıyla güncellendi! Yeni şifrenizle giriş yapabilirsiniz.");
                dispose();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
        }
    }
}