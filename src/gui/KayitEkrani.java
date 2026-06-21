package gui;

import database.VeritabaniBaglantisi;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class KayitEkrani extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtSifre;
    private JComboBox<String> cmbRol;

    public KayitEkrani() {
        setTitle("Açık Artırma Sistemi - Yeni Kayıt");
        setSize(400, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel anaPanel = new JPanel(new BorderLayout(10, 20));
        anaPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        anaPanel.setBackground(Color.WHITE);
        setContentPane(anaPanel);

        JLabel lblBaslik = new JLabel("Yeni Hesap Oluştur", SwingConstants.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBaslik.setForeground(new Color(44, 62, 80));
        anaPanel.add(lblBaslik, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Kullanıcı Adı:"));
        txtKullaniciAdi = new JTextField();
        formPanel.add(txtKullaniciAdi);

        formPanel.add(new JLabel("Şifre (Min 6 Karakter):"));
        txtSifre = new JPasswordField();
        formPanel.add(txtSifre);

        formPanel.add(new JLabel("Hesap Türü:"));
        cmbRol = new JComboBox<>(new String[]{"ALICI", "SATICI"});
        formPanel.add(cmbRol);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnKayit = new JButton("Kayıt Ol");
        btnKayit.setBackground(new Color(52, 152, 219));
        btnKayit.setForeground(Color.WHITE);
        btnKayit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnKayit.setFocusPainted(false);
        btnKayit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnKayit.addActionListener(e -> kayitOl());

        anaPanel.add(btnKayit, BorderLayout.SOUTH);
    }

    private void kayitOl() {
        String kullaniciAdi = txtKullaniciAdi.getText().trim();
        String sifre = new String(txtSifre.getPassword()).trim();
        String rol = (String) cmbRol.getSelectedItem();

        if (kullaniciAdi.isEmpty() || sifre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lütfen tüm alanları doldurun!");
            return;
        }

        // YENİLİK: Kayıt olurken minimum 6 karakter kontrolü
        if (sifre.length() < 6) {
            JOptionPane.showMessageDialog(this, "Sistem Kuralı: Oluşturulan hesap şifresi en az 6 karakter olmalıdır!");
            return;
        }

        try (Connection conn = VeritabaniBaglantisi.baglan()) {
            // Önce kullanıcı adı alınmış mı kontrol et
            String kontrolSql = "SELECT id FROM Kullanicilar WHERE kullanici_adi = ?";
            try (PreparedStatement kPstmt = conn.prepareStatement(kontrolSql)) {
                kPstmt.setString(1, kullaniciAdi);
                ResultSet rs = kPstmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Bu kullanıcı adı zaten alınmış, lütfen başka bir tane seçin!");
                    return;
                }
            }

            // Kayıt İşlemi
            String sql = "INSERT INTO Kullanicilar (kullanici_adi, sifre, rol) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, kullaniciAdi);
                pstmt.setString(2, sifre);
                pstmt.setString(3, rol);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Kayıt Başarılı! Şimdi giriş yapabilirsiniz.");
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Kayıt Hatası: " + ex.getMessage());
        }
    }
}