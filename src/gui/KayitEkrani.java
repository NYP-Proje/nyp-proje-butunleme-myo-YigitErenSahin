package gui;

import database.VeritabaniBaglantisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class KayitEkrani extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtSifre;
    private JComboBox<String> cmbRol;
    private JButton btnKaydet;
    private JButton btnGeri;

    public KayitEkrani() {
        setTitle("Açık Artırma - Yeni Kayıt");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(245, 245, 245));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(anaPanel);

        // Başlık
        JLabel lblBaslik = new JLabel("Yeni Hesap Oluştur", JLabel.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 22));
        anaPanel.add(lblBaslik, BorderLayout.NORTH);

        // Form Paneli
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);

        // Kullanıcı Adı
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtKullaniciAdi = new JTextField();
        formPanel.add(txtKullaniciAdi, gbc);

        // Şifre
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtSifre = new JPasswordField();
        formPanel.add(txtSifre, gbc);

        // Rol Seçimi (Alıcı / Satıcı)
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(new JLabel("Rolünüz:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        String[] roller = {"ALICI", "SATICI"};
        cmbRol = new JComboBox<>(roller);
        formPanel.add(cmbRol, gbc);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        // Butonlar Paneli
        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        butonPanel.setBackground(new Color(245, 245, 245));

        btnKaydet = new JButton("Kaydı Tamamla");
        btnKaydet.setBackground(new Color(46, 204, 113)); // Yeşil
        btnKaydet.setForeground(Color.WHITE);
        butonPanel.add(btnKaydet);

        btnGeri = new JButton("Geri Dön");
        butonPanel.add(btnGeri);

        anaPanel.add(butonPanel, BorderLayout.SOUTH);

        // --- VERİTABANINA KAYDETME İŞLEMİ ---
        btnKaydet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kAdi = txtKullaniciAdi.getText();
                String sifre = new String(txtSifre.getPassword());
                String rol = cmbRol.getSelectedItem().toString();

                if(kAdi.isEmpty() || sifre.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Lütfen boş alan bırakmayın!");
                    return;
                }

                // Veritabanına veri ekleyen SQL komutu
                String sql = "INSERT INTO Kullanicilar (kullanici_adi, sifre, rol) VALUES (?, ?, ?)";

                try (Connection conn = VeritabaniBaglantisi.baglan();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, kAdi);
                    pstmt.setString(2, sifre);
                    pstmt.setString(3, rol);
                    pstmt.executeUpdate(); // Veriyi kaydet!

                    JOptionPane.showMessageDialog(null, "Kayıt Başarılı! Giriş yapabilirsiniz.");

                    // Kayıt olunca bu pencereyi kapat, giriş ekranını aç
                    new GirisEkrani().setVisible(true);
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Bu kullanıcı adı zaten alınmış olabilir!");
                }
            }
        });

        // Geri Dön Butonu
        btnGeri.addActionListener(e -> {
            new GirisEkrani().setVisible(true);
            dispose();
        });
    }
}