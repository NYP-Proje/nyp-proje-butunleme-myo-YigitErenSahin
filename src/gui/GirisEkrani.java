package gui;

import database.VeritabaniBaglantisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GirisEkrani extends JFrame {
    private JTextField txtKullaniciAdi;
    private JPasswordField txtSifre;
    private JButton btnGiris;
    private JButton btnKayitOl;

    public GirisEkrani() {
        setTitle("Açık Artırma - Giriş");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        anaPanel.setBackground(new Color(245, 245, 245));
        anaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(anaPanel);

        JLabel lblBaslik = new JLabel("Hoş Geldiniz", JLabel.CENTER);
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 22));
        anaPanel.add(lblBaslik, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Kullanıcı Adı:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtKullaniciAdi = new JTextField();
        formPanel.add(txtKullaniciAdi, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Şifre:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        txtSifre = new JPasswordField();
        formPanel.add(txtSifre, gbc);

        anaPanel.add(formPanel, BorderLayout.CENTER);

        JPanel butonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        butonPanel.setBackground(new Color(245, 245, 245));

        btnGiris = new JButton("Giriş Yap");
        btnGiris.setBackground(new Color(52, 152, 219));
        btnGiris.setForeground(Color.WHITE);
        butonPanel.add(btnGiris);

        btnKayitOl = new JButton("Kayıt Ol");
        butonPanel.add(btnKayitOl);
        anaPanel.add(butonPanel, BorderLayout.SOUTH);

        // --- BUTON OLAYLARI ---

        // 1. Kayıt Ol Butonuna Basınca Kayıt Ekranını Aç
        btnKayitOl.addActionListener(e -> {
            new KayitEkrani().setVisible(true);
            dispose();
        });

        // 2. Giriş Yap Butonuna Basınca Veritabanında Kontrol Et
        btnGiris.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String kAdi = txtKullaniciAdi.getText();
                String sifre = new String(txtSifre.getPassword());

                String sql = "SELECT * FROM Kullanicilar WHERE kullanici_adi = ? AND sifre = ?";

                try (Connection conn = VeritabaniBaglantisi.baglan();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {

                    pstmt.setString(1, kAdi);
                    pstmt.setString(2, sifre);
                    ResultSet rs = pstmt.executeQuery();

                    if(rs.next()) {
                        String rol = rs.getString("rol");

                        // İŞTE BAĞLADIĞIMIZ YER BURASI YİĞİT:
                        // Giriş başarılı olunca yeni oluşturduğumuz AnaEkran'ı açıyoruz
                        new AnaEkran(kAdi, rol).setVisible(true);
                        dispose();

                    } else {
                        JOptionPane.showMessageDialog(null, "Hatalı Kullanıcı Adı veya Şifre!");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Veritabanı Hatası: " + ex.getMessage());
                }
            }
        });
    }
}