package gui;

import database.VeritabaniBaglantisi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AnaEkran extends JFrame {
    private JTable urunTablosu;
    private DefaultTableModel tabloModeli;
    private String aktifKullanici;
    private String aktifRol;
    private Connection globalConn;

    public AnaEkran(String kullaniciAdi, String rol) {
        this.aktifKullanici = kullaniciAdi;
        this.aktifRol = rol;
        this.globalConn = VeritabaniBaglantisi.baglan();

        setTitle("Açık Artırma Sistemi - Yönetim Paneli");
        setSize(1050, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        setContentPane(anaPanel);

        // --- SOL MENÜ (Sidebar) ---
        JPanel solMenu = new JPanel(new BorderLayout());
        solMenu.setBackground(new Color(44, 62, 80));
        solMenu.setPreferredSize(new Dimension(250, 0));
        solMenu.setBorder(new EmptyBorder(30, 20, 30, 20));

        // Profil Bilgileri (Üst Kısım)
        JPanel profilPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        profilPanel.setOpaque(false);
        JLabel lblIkon = new JLabel("👤", SwingConstants.CENTER);
        lblIkon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        lblIkon.setForeground(Color.WHITE);
        JLabel lblKullaniciAd = new JLabel("@" + aktifKullanici, SwingConstants.CENTER);
        lblKullaniciAd.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblKullaniciAd.setForeground(Color.WHITE);
        JLabel lblRol = new JLabel(aktifRol, SwingConstants.CENTER);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRol.setForeground(new Color(189, 195, 199));
        profilPanel.add(lblIkon);
        profilPanel.add(lblKullaniciAd);
        profilPanel.add(lblRol);
        solMenu.add(profilPanel, BorderLayout.NORTH);

        // YENİLİK: Orta Kısıma İstatistik Butonu Ekleme (Koleksiyonum / Sattıklarım)
        JPanel menuButonlarPaneli = new JPanel(new GridLayout(2, 1, 0, 10));
        menuButonlarPaneli.setOpaque(false);
        menuButonlarPaneli.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton btnProfilim = new JButton();
        btnProfilim.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProfilim.setForeground(Color.WHITE);
        btnProfilim.setBackground(new Color(52, 152, 219));
        btnProfilim.setFocusPainted(false);
        btnProfilim.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rol ayrımına göre buton ismini ve işlevini belirliyoruz
        if (aktifRol.equals("ALICI")) {
            btnProfilim.setText("💼 Koleksiyonum");
            btnProfilim.addActionListener(e -> koleksiyonumuGoster());
        } else {
            btnProfilim.setText("💰 Sattıklarım & Kazanç");
            btnProfilim.addActionListener(e -> sattiklarimiGoster());
        }
        menuButonlarPaneli.add(btnProfilim);
        solMenu.add(menuButonlarPaneli, BorderLayout.CENTER);


        // Çıkış Butonu (Alt Kısım)
        JButton btnCikis = new JButton("Çıkış Yap");
        btnCikis.setBackground(new Color(231, 76, 60));
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> {
            try { if(globalConn != null && !globalConn.isClosed()) globalConn.close(); } catch(Exception ignored){}
            new GirisEkrani().setVisible(true);
            dispose();
        });
        solMenu.add(btnCikis, BorderLayout.SOUTH);
        anaPanel.add(solMenu, BorderLayout.WEST);

        // --- SAĞ İÇERİK ---
        JPanel sagIcerik = new JPanel(new BorderLayout(20, 20));
        sagIcerik.setBackground(new Color(245, 247, 250));
        sagIcerik.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblBaslik = new JLabel("Aktif Açık Artırmalar");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBaslik.setForeground(new Color(44, 62, 80));
        sagIcerik.add(lblBaslik, BorderLayout.NORTH);

        String[] sutunlar = {"ID", "Ürün Adı", "Açıklama", "Fiyat (₺)", "Son Teklif Veren", "Kalan Süre / Durum"};
        tabloModeli = new DefaultTableModel(sutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        urunTablosu = new JTable(tabloModeli);
        urunTablosu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        urunTablosu.setRowHeight(35);
        urunTablosu.setShowVerticalLines(false);
        urunTablosu.setSelectionBackground(new Color(52, 152, 219));
        urunTablosu.setSelectionForeground(Color.WHITE);

        JTableHeader header = urunTablosu.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(223, 230, 233));
        header.setForeground(new Color(45, 52, 54));
        header.setPreferredSize(new Dimension(100, 45));

        JScrollPane scrollPane = new JScrollPane(urunTablosu);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(223, 230, 233)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        sagIcerik.add(scrollPane, BorderLayout.CENTER);

        // --- AKSİYON BUTONLARI ---
        JPanel aksiyonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        aksiyonPanel.setOpaque(false);

        JButton btnYenile = createStyledButton("Listeyi Yenile", new Color(149, 165, 166));
        JButton btnTeklifVer = createStyledButton("Teklif Ver", new Color(241, 196, 15));
        btnTeklifVer.setForeground(Color.BLACK);

        if (aktifRol.equals("SATICI")) {
            JButton btnUrunEkle = createStyledButton("+ Yeni Ürün Ekle", new Color(46, 204, 113));
            btnUrunEkle.addActionListener(e -> new UrunEkleEkrani(aktifKullanici).setVisible(true));
            aksiyonPanel.add(btnUrunEkle);
        }

        aksiyonPanel.add(btnTeklifVer);
        aksiyonPanel.add(btnYenile);
        sagIcerik.add(aksiyonPanel, BorderLayout.SOUTH);
        anaPanel.add(sagIcerik, BorderLayout.CENTER);

        // --- EVENT HANDLERS ---
        btnYenile.addActionListener(e -> urunleriVeritabanindanGetir());

        btnTeklifVer.addActionListener(e -> {
            int seciliSatir = urunTablosu.getSelectedRow();
            if (seciliSatir == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen tablodan bir ürün seçin!");
                return;
            }

            String durum = (String) tabloModeli.getValueAt(seciliSatir, 5);
            if (durum.equals("SÜRE DOLDU") || durum.equals("KAPANDI")) {
                JOptionPane.showMessageDialog(this, "Süresi bitmiş bir ürüne teklif veremezsiniz!");
                return;
            }

            int urunId = (int) tabloModeli.getValueAt(seciliSatir, 0);
            String urunAdi = (String) tabloModeli.getValueAt(seciliSatir, 1);
            String fiyatMetni = (String) tabloModeli.getValueAt(seciliSatir, 3);
            double mevcutFiyat = Double.parseDouble(fiyatMetni.replace(" ₺", ""));

            new TeklifEkrani(urunId, urunAdi, mevcutFiyat, aktifKullanici).setVisible(true);
        });

        urunleriVeritabanindanGetir();

        javax.swing.Timer timer = new javax.swing.Timer(1000, event -> urunleriVeritabanindanGetir());
        timer.start();
    }

    private void urunleriVeritabanindanGetir() {
        if(globalConn == null) return;
        int seciliSira = urunTablosu.getSelectedRow();
        tabloModeli.setRowCount(0);

        String sql = "SELECT * FROM Urunler";
        long suAn = System.currentTimeMillis();

        try (PreparedStatement pstmt = globalConn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String urunAdi = rs.getString("urun_adi");
                String aciklama = rs.getString("aciklama");
                double mevcutFiyat = rs.getDouble("mevcut_fiyat");
                String sonTeklifVeren = rs.getString("son_teklif_veren");
                long bitisZamani = rs.getLong("bitis_zamani");
                String durum = rs.getString("durum");

                String kalanSureMetni = "";

                if (durum.equals("ACIK")) {
                    long fark = bitisZamani - suAn;
                    if (fark <= 0) {
                        kalanSureMetni = "SÜRE DOLDU";
                        durumuKapat(id);
                    } else {
                        long dk = (fark / 1000) / 60;
                        long sn = (fark / 1000) % 60;
                        kalanSureMetni = String.format("%02d:%02d", dk, sn);
                    }
                } else {
                    kalanSureMetni = "KAPANDI";
                }

                tabloModeli.addRow(new Object[]{id, urunAdi, aciklama, mevcutFiyat + " ₺", sonTeklifVeren, kalanSureMetni});
            }

            if(seciliSira != -1 && seciliSira < urunTablosu.getRowCount()){
                urunTablosu.setRowSelectionInterval(seciliSira, seciliSira);
            }
        } catch (Exception ex) {
            System.out.println("Hata: " + ex.getMessage());
        }
    }


    private void koleksiyonumuGoster() {
        StringBuilder sb = new StringBuilder("💼 KAZANDIĞINIZ ÜRÜNLER KUTUSU 💼\n\n");
        String sql = "SELECT urun_adi, mevcut_fiyat FROM Urunler WHERE son_teklif_veren = ? AND durum = 'KAPANDI'";
        boolean urunVarMi = false;

        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setString(1, aktifKullanici);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    urunVarMi = true;
                    sb.append("• ").append(rs.getString("urun_adi"))
                            .append(" — Fiyat: ").append(rs.getDouble("mevcut_fiyat")).append(" ₺\n");
                }
            }
        } catch (Exception ex) {
            System.out.println("Hata: " + ex.getMessage());
        }

        if (!urunVarMi) sb.append("Henüz süresi bitip kazandığınız bir ürün bulunmuyor.");
        JOptionPane.showMessageDialog(this, sb.toString(), "Koleksiyonum", JOptionPane.INFORMATION_MESSAGE);
    }


    private void sattiklarimiGoster() {
        StringBuilder sb = new StringBuilder("💰 SATIŞ VE KAZANÇ RAPORUNUZ 💰\n\n");
        // Satıcının kendi eklediği ve süresi bitip satılan ürünleri buluyoruz
        String sql = "SELECT urun_adi, mevcut_fiyat, son_teklif_veren FROM Urunler " +
                "WHERE satici_id = (SELECT id FROM Kullanicilar WHERE kullanici_adi = ?) AND durum = 'KAPANDI'";

        double toplamKazanc = 0;
        boolean satisVarMi = false;

        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setString(1, aktifKullanici);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    satisVarMi = true;
                    double fiyat = rs.getDouble("mevcut_fiyat");
                    toplamKazanc += fiyat;

                    sb.append("• Ürün: ").append(rs.getString("urun_adi"))
                            .append(" | Alan: ").append(rs.getString("son_teklif_veren"))
                            .append(" | Fiyat: ").append(fiyat).append(" ₺\n");
                }
            }
        } catch (Exception ex) {
            System.out.println("Hata: " + ex.getMessage());
        }

        if (satisVarMi) {
            sb.append("\n----------------------------------------\n");
            sb.append("💵 TOPLAM KAZANCINIZ: ").append(toplamKazanc).append(" ₺");
        } else {
            sb.append("Henüz süresi dolup satılan bir ürününüz bulunmuyor.");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Satış Paneli", JOptionPane.INFORMATION_MESSAGE);
    }

    private void durumuKapat(int id) {
        String sql = "UPDATE Urunler SET durum = 'KAPANDI' WHERE id = ?";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception ignored) {}
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}