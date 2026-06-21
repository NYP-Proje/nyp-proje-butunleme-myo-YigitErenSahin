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
    private boolean isAdmin;

    public AnaEkran(String kullaniciAdi, String rol) {
        this.aktifKullanici = kullaniciAdi;
        this.aktifRol = rol;
        this.globalConn = VeritabaniBaglantisi.baglan();
        this.isAdmin = aktifKullanici.equalsIgnoreCase("admin");

        setTitle(isAdmin ? "Açık Artırma Sistemi - YÖNETİCİ PANELİ" : "Açık Artırma Sistemi - Yönetim Paneli");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel anaPanel = new JPanel(new BorderLayout());
        setContentPane(anaPanel);

        // --- SOL MENÜ ---
        JPanel solMenu = new JPanel(new BorderLayout());
        solMenu.setBackground(new Color(44, 62, 80));
        solMenu.setPreferredSize(new Dimension(250, 0));
        solMenu.setBorder(new EmptyBorder(30, 20, 30, 20));

        JPanel profilPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        profilPanel.setOpaque(false);
        JLabel lblIkon = new JLabel(isAdmin ? "👑" : "👤", SwingConstants.CENTER);
        lblIkon.setFont(new Font("Segoe UI", Font.PLAIN, 56));
        lblIkon.setForeground(Color.WHITE);

        JLabel lblKullaniciAd = new JLabel("@" + aktifKullanici, SwingConstants.CENTER);
        lblKullaniciAd.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblKullaniciAd.setForeground(isAdmin ? new Color(241, 196, 15) : Color.WHITE);

        JLabel lblRol = new JLabel(isAdmin ? "SİSTEM YÖNETİCİSİ" : aktifRol, SwingConstants.CENTER);
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRol.setForeground(new Color(189, 195, 199));

        profilPanel.add(lblIkon);
        profilPanel.add(lblKullaniciAd);
        profilPanel.add(lblRol);
        solMenu.add(profilPanel, BorderLayout.NORTH);

        JPanel menuButonlarPaneli = new JPanel(new GridLayout(2, 1, 0, 10));
        menuButonlarPaneli.setOpaque(false);
        menuButonlarPaneli.setBorder(new EmptyBorder(40, 0, 0, 0));

        JButton btnProfilim = new JButton();
        btnProfilim.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProfilim.setForeground(Color.WHITE);
        btnProfilim.setFocusPainted(false);
        btnProfilim.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isAdmin) {
            btnProfilim.setText("🛠️ Gelişmiş Denetim Masası");
            btnProfilim.setBackground(new Color(155, 89, 182));
            btnProfilim.addActionListener(e -> new AdminYonetimPaneli(this).setVisible(true));
        } else if (aktifRol.equals("ALICI")) {
            btnProfilim.setText("💼 Koleksiyonum");
            btnProfilim.setBackground(new Color(52, 152, 219));
            btnProfilim.addActionListener(e -> koleksiyonumuGoster());
        } else {
            btnProfilim.setText("💰 Sattıklarım & Kazanç");
            btnProfilim.setBackground(new Color(52, 152, 219));
            btnProfilim.addActionListener(e -> sattiklarimiGoster());
        }
        menuButonlarPaneli.add(btnProfilim);
        solMenu.add(menuButonlarPaneli, BorderLayout.CENTER);

        JPanel altMenuPaneli = new JPanel(new GridLayout(2, 1, 0, 10));
        altMenuPaneli.setOpaque(false);

        JButton btnHesapSil = new JButton("Hesabımı Sil");
        btnHesapSil.setBackground(new Color(192, 57, 43));
        btnHesapSil.setForeground(Color.WHITE);
        btnHesapSil.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHesapSil.setFocusPainted(false);
        btnHesapSil.addActionListener(e -> hesabiSil());

        JButton btnCikis = new JButton("Pencereyi Kapat");
        btnCikis.setBackground(new Color(231, 76, 60));
        btnCikis.setForeground(Color.WHITE);
        btnCikis.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCikis.setFocusPainted(false);
        btnCikis.addActionListener(e -> dispose());

        if(!isAdmin) altMenuPaneli.add(btnHesapSil);
        altMenuPaneli.add(btnCikis);
        solMenu.add(altMenuPaneli, BorderLayout.SOUTH);
        anaPanel.add(solMenu, BorderLayout.WEST);

        // --- SAĞ İÇERİK ---
        JPanel sagIcerik = new JPanel(new BorderLayout(20, 20));
        sagIcerik.setBackground(new Color(245, 247, 250));
        sagIcerik.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel lblBaslik = new JLabel(isAdmin ? "Tüm Aktif İlanlar (Yönetici Görünümü)" : "Aktif Açık Artırmalar");
        lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBaslik.setForeground(isAdmin ? new Color(192, 57, 43) : new Color(44, 62, 80));
        sagIcerik.add(lblBaslik, BorderLayout.NORTH);

        String[] sutunlar = {"ID", "Ürün Adı", "Açıklama", "İlan Sahibi", "Fiyat (₺)", "Son Teklif Veren", "Kalan Süre / Durum"};
        tabloModeli = new DefaultTableModel(sutunlar, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        urunTablosu = new JTable(tabloModeli);
        urunTablosu.setRowHeight(35);
        urunTablosu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        urunTablosu.setSelectionBackground(new Color(52, 152, 219));
        urunTablosu.setSelectionForeground(Color.WHITE);

        JTableHeader header = urunTablosu.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(urunTablosu);
        sagIcerik.add(scrollPane, BorderLayout.CENTER);

        // --- AKSİYON BUTONLARI ---
        JPanel aksiyonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        aksiyonPanel.setOpaque(false);

        JButton btnYenile = createStyledButton("Yenile", new Color(149, 165, 166));
        JButton btnTeklifVer = createStyledButton("Teklif Ver", new Color(241, 196, 15));
        btnTeklifVer.setForeground(Color.BLACK);

        if (isAdmin) {
            JButton btnZorlaSil = createStyledButton("🚨 İlanı Kaldır (Admin)", new Color(192, 57, 43));
            btnZorlaSil.addActionListener(e -> adminUrunSil());
            aksiyonPanel.add(btnZorlaSil);
        } else if (aktifRol.equals("SATICI")) {
            JButton btnGuncelle = createStyledButton("İlanı Güncelle", new Color(52, 152, 219));
            btnGuncelle.addActionListener(e -> ilaniGuncelleHazirla());
            aksiyonPanel.add(btnGuncelle);

            JButton btnUrunSil = createStyledButton("İlanı Sil", new Color(231, 76, 60));
            btnUrunSil.addActionListener(e -> urunuSil());
            aksiyonPanel.add(btnUrunSil);

            JButton btnUrunEkle = createStyledButton("+ Yeni İlan", new Color(46, 204, 113));
            btnUrunEkle.addActionListener(e -> new UrunEkleEkrani(aktifKullanici).setVisible(true));
            aksiyonPanel.add(btnUrunEkle);
        }

        aksiyonPanel.add(btnTeklifVer);
        aksiyonPanel.add(btnYenile);
        sagIcerik.add(aksiyonPanel, BorderLayout.SOUTH);
        anaPanel.add(sagIcerik, BorderLayout.CENTER);

        btnYenile.addActionListener(e -> urunleriVeritabanindanGetir());

        // --- GÜNCELLENEN TEKLİF VERME MOTORU ---
        btnTeklifVer.addActionListener(e -> {
            int seciliSatir = urunTablosu.getSelectedRow();
            if (seciliSatir == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen teklif vermek için tablodan bir ürün seçin!");
                return;
            }

            // 1. Kural: Kendi ilanına teklif veremez (Satıcı diğer ilanlara verebilir)
            String ilanSahibi = (String) tabloModeli.getValueAt(seciliSatir, 3);
            if (ilanSahibi.equals("@" + aktifKullanici)) {
                JOptionPane.showMessageDialog(this, "Güvenlik Politikası: Kendi açtığınız ilana teklif veremezsiniz!");
                return;
            }

            // 2. Kural: En yüksek teklif zaten bu kullanıcıdaysa tekrar veremesin
            String sonTeklifVeren = (String) tabloModeli.getValueAt(seciliSatir, 5);
            if (sonTeklifVeren != null && sonTeklifVeren.equals(aktifKullanici)) {
                JOptionPane.showMessageDialog(this, "Uyarı: Bu üründe zaten en yüksek teklif sizde! Başka biri teklif artırana kadar yeni teklif veremezsiniz.");
                return;
            }

            String durum = (String) tabloModeli.getValueAt(seciliSatir, 6);
            if (durum.equals("SÜRE DOLDU") || durum.equals("KAPANDI")) {
                JOptionPane.showMessageDialog(this, "Süresi bitmiş bir ürüne teklif veremezsiniz!");
                return;
            }

            int urunId = (int) tabloModeli.getValueAt(seciliSatir, 0);
            String urunAdi = (String) tabloModeli.getValueAt(seciliSatir, 1);
            String fiyatMetni = (String) tabloModeli.getValueAt(seciliSatir, 4);
            double mevcutFiyat = Double.parseDouble(fiyatMetni.replace(" ₺", ""));
            new TeklifEkrani(urunId, urunAdi, mevcutFiyat, aktifKullanici).setVisible(true);
        });

        urunleriVeritabanindanGetir();
        new javax.swing.Timer(1000, event -> urunleriVeritabanindanGetir()).start();
    }

    private void adminUrunSil() {
        int seciliSatir = urunTablosu.getSelectedRow();
        if (seciliSatir == -1) return;
        int urunId = (int) tabloModeli.getValueAt(seciliSatir, 0);
        String urunAdi = (String) tabloModeli.getValueAt(seciliSatir, 1);

        if(JOptionPane.showConfirmDialog(this, "🚨 '" + urunAdi + "' adlı ilanı zorla silmek istiyor musunuz?", "Admin İlan Kaldır", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = globalConn.prepareStatement("DELETE FROM Urunler WHERE id = ?")) {
                pstmt.setInt(1, urunId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "İlan sistemden kaldırıldı.");
                urunleriVeritabanindanGetir();
            } catch(Exception ex) {}
        }
    }

    private void ilaniGuncelleHazirla() {
        int seciliSatir = urunTablosu.getSelectedRow();
        if (seciliSatir == -1) return;
        int id = (int) tabloModeli.getValueAt(seciliSatir, 0);
        String ad = (String) tabloModeli.getValueAt(seciliSatir, 1);
        String aciklama = (String) tabloModeli.getValueAt(seciliSatir, 2);
        String fiyatMetni = (String) tabloModeli.getValueAt(seciliSatir, 4);
        double fiyat = Double.parseDouble(fiyatMetni.replace(" ₺", ""));

        String sql = "SELECT id FROM Urunler WHERE id = ? AND satici_id = (SELECT id FROM Kullanicilar WHERE kullanici_adi = ?)";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, aktifKullanici);
            if (pstmt.executeQuery().next()) {
                new UrunGuncelleEkrani(id, ad, aciklama, fiyat).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Sadece kendi ilanlarınızı güncelleyebilirsiniz!");
            }
        } catch(Exception ex) {}
    }

    private void urunuSil() {
        int seciliSatir = urunTablosu.getSelectedRow();
        if (seciliSatir == -1) return;
        int urunId = (int) tabloModeli.getValueAt(seciliSatir, 0);
        if(JOptionPane.showConfirmDialog(this, "Bu ürünü silmek istediğinize emin misiniz?", "Ürün Sil", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        String sql = "DELETE FROM Urunler WHERE id = ? AND satici_id = (SELECT id FROM Kullanicilar WHERE kullanici_adi = ?)";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setInt(1, urunId);
            pstmt.setString(2, aktifKullanici);
            if (pstmt.executeUpdate() > 0) urunleriVeritabanindanGetir();
            else JOptionPane.showMessageDialog(this, "Yetkiniz yok!");
        } catch(Exception ex) {}
    }

    private void hesabiSil() {
        if (JOptionPane.showConfirmDialog(this, "Hesabınızı kalıcı olarak silmek istediğinize emin misiniz?", "Hesap Sil", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try (PreparedStatement pstmt = globalConn.prepareStatement("DELETE FROM Kullanicilar WHERE kullanici_adi = ?")) {
                pstmt.setString(1, aktifKullanici);
                pstmt.executeUpdate();
                dispose();
            } catch(Exception ex) {}
        }
    }

    public void urunleriVeritabanindanGetir() {
        if(globalConn == null) return;
        int seciliSatir = urunTablosu.getSelectedRow();
        int seciliUrunId = -1;
        if (seciliSatir != -1) seciliUrunId = (int) tabloModeli.getValueAt(seciliSatir, 0);

        tabloModeli.setRowCount(0);
        long suAn = System.currentTimeMillis();

        String sql = "SELECT u.*, k.kullanici_adi AS satici_adi FROM Urunler u LEFT JOIN Kullanicilar k ON u.satici_id = k.id";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                long bitis = rs.getLong("bitis_zamani");
                String durum = rs.getString("durum");
                String saticiAdi = rs.getString("satici_adi");
                String kalan = "KAPANDI";
                if (saticiAdi == null) saticiAdi = "Bilinmiyor";
                if (durum.equals("ACIK")) {
                    long fark = bitis - suAn;
                    if (fark <= 0) {
                        kalan = "SÜRE DOLDU";
                        durumuKapat(rs.getInt("id"));
                    } else {
                        kalan = String.format("%02d:%02d", (fark/1000)/60, (fark/1000)%60);
                    }
                }
                tabloModeli.addRow(new Object[]{ rs.getInt("id"), rs.getString("urun_adi"), rs.getString("aciklama"), "@" + saticiAdi, rs.getDouble("mevcut_fiyat") + " ₺", rs.getString("son_teklif_veren"), kalan });
            }
        } catch (Exception ex) {}

        if (seciliUrunId != -1) {
            for (int i = 0; i < urunTablosu.getRowCount(); i++) {
                if ((int) urunTablosu.getValueAt(i, 0) == seciliUrunId) {
                    urunTablosu.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
    }

    private void durumuKapat(int id) {
        try (PreparedStatement pstmt = globalConn.prepareStatement("UPDATE Urunler SET durum = 'KAPANDI' WHERE id = ?")) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (Exception ignored) {}
    }

    private void koleksiyonumuGoster() {
        String sql = "SELECT urun_adi, mevcut_fiyat FROM Urunler WHERE son_teklif_veren = ? AND durum = 'KAPANDI'";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setString(1, aktifKullanici);
            ResultSet rs = pstmt.executeQuery();
            StringBuilder res = new StringBuilder("💼 Kazandıklarınız:\n");
            boolean varMi = false;
            while(rs.next()) {
                varMi = true;
                res.append("- ").append(rs.getString(1)).append(" (").append(rs.getDouble(2)).append(" ₺)\n");
            }
            if(!varMi) res.append("Henüz kazandığınız ürün yok.");
            JOptionPane.showMessageDialog(this, res.toString());
        } catch(Exception ex) {}
    }

    private void sattiklarimiGoster() {
        String sql = "SELECT urun_adi, mevcut_fiyat FROM Urunler WHERE satici_id = (SELECT id FROM Kullanicilar WHERE kullanici_adi = ?) AND durum = 'KAPANDI'";
        try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
            pstmt.setString(1, aktifKullanici);
            ResultSet rs = pstmt.executeQuery();
            double total = 0;
            StringBuilder res = new StringBuilder("💰 Satılanlar:\n");
            boolean varMi = false;
            while(rs.next()) {
                varMi = true;
                res.append("- ").append(rs.getString(1)).append(" (").append(rs.getDouble(2)).append(" ₺)\n");
                total += rs.getDouble(2);
            }
            if(!varMi) res.append("Henüz satılan ürününüz yok.\n");
            res.append("\nToplam Kazanç: ").append(total).append(" ₺");
            JOptionPane.showMessageDialog(this, res.toString());
        } catch(Exception ex) {}
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    private class AdminYonetimPaneli extends JDialog {
        private JTable kullanıcıTablosu;
        private DefaultTableModel kulModel;
        private JTextArea txtDetayAlan;

        public AdminYonetimPaneli(JFrame anaPencere) {
            super(anaPencere, "Merkezi Sistem Denetim Masası (Admin)", true);
            setSize(850, 600);
            setLocationRelativeTo(anaPencere);
            setLayout(new BorderLayout(15, 15));

            JPanel pnlBaslik = new JPanel(new BorderLayout());
            pnlBaslik.setBackground(new Color(155, 89, 182));
            pnlBaslik.setBorder(new EmptyBorder(15, 20, 15, 20));
            JLabel lblBaslik = new JLabel("Sistemdeki Tüm Kullanıcılar ve Canlı Hareketleri", SwingConstants.CENTER);
            lblBaslik.setFont(new Font("Segoe UI", Font.BOLD, 20));
            lblBaslik.setForeground(Color.WHITE);
            pnlBaslik.add(lblBaslik, BorderLayout.CENTER);
            add(pnlBaslik, BorderLayout.NORTH);

            kulModel = new DefaultTableModel(new String[]{"ID", "Kullanıcı Adı", "Sistem Rolü"}, 0){
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            kullanıcıTablosu = new JTable(kulModel);
            kullanıcıTablosu.setRowHeight(30);
            kullanıcıTablosu.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            kullanıcıTablosu.getSelectionModel().addListSelectionListener(e -> {
                if(!e.getValueIsAdjusting()) kullanıcıGecmisiniYukle();
            });

            JScrollPane scrSol = new JScrollPane(kullanıcıTablosu);
            scrSol.setPreferredSize(new Dimension(450, 0));

            txtDetayAlan = new JTextArea();
            txtDetayAlan.setFont(new Font("Consolas", Font.PLAIN, 13));
            txtDetayAlan.setEditable(false);
            txtDetayAlan.setBorder(new EmptyBorder(10, 10, 10, 10));
            txtDetayAlan.setBackground(new Color(248, 249, 250));
            JScrollPane scrSag = new JScrollPane(txtDetayAlan);
            scrSag.setBorder(BorderFactory.createTitledBorder("🕵️ Seçilen Kullanıcının Canlı Geçmişi"));

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrSol, scrSag);
            splitPane.setDividerLocation(420);
            add(splitPane, BorderLayout.CENTER);

            JPanel pnlButonlar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));

            JButton btnGuncelle = new JButton("✏️ Kullanıcı Adı Düzelt");
            btnGuncelle.setBackground(new Color(52, 152, 219));
            btnGuncelle.setForeground(Color.WHITE);
            btnGuncelle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnGuncelle.addActionListener(e -> kullaniciAdiniDuzelt());

            JButton btnBanla = new JButton("🚨 Kullanıcıyı Sistemden Banla");
            btnBanla.setBackground(new Color(192, 57, 43));
            btnBanla.setForeground(Color.WHITE);
            btnBanla.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btnBanla.addActionListener(e -> kullaniciBanla());

            pnlButonlar.add(btnGuncelle);
            pnlButonlar.add(btnBanla);
            add(pnlButonlar, BorderLayout.SOUTH);

            kullanicilariListele();
        }

        private void kullanicilariListele() {
            kulModel.setRowCount(0);
            String sql = "SELECT id, kullanici_adi, rol FROM Kullanicilar WHERE kullanici_adi != 'admin'";
            try (PreparedStatement pstmt = globalConn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    kulModel.addRow(new Object[]{rs.getInt("id"), rs.getString("kullanici_adi"), rs.getString("rol")});
                }
            } catch(Exception ex){}
        }

        private void kullanıcıGecmisiniYukle() {
            int satir = kullanıcıTablosu.getSelectedRow();
            if(satir == -1) {
                txtDetayAlan.setText("");
                return;
            }
            int uid = (int) kulModel.getValueAt(satir, 0);
            String kadi = (String) kulModel.getValueAt(satir, 1);

            StringBuilder rapor = new StringBuilder();
            rapor.append("=== KULLANICI ETKİNLİK RAPORU ===\n");
            rapor.append("Hedef: @").append(kadi).append(" (ID: ").append(uid).append(")\n\n");

            rapor.append("📦 Açtığı İlanlar:\n");
            String sqlIlan = "SELECT id, urun_adi, mevcut_fiyat, durum FROM Urunler WHERE satici_id = ?";
            try (PreparedStatement pstmt = globalConn.prepareStatement(sqlIlan)) {
                pstmt.setInt(1, uid);
                ResultSet rs = pstmt.executeQuery();
                boolean buldu = false;
                while(rs.next()) {
                    buldu = true;
                    rapor.append(" - ID: ").append(rs.getInt("id"))
                            .append(" | ").append(rs.getString("urun_adi"))
                            .append(" (").append(rs.getDouble("mevcut_fiyat")).append(" ₺) [")
                            .append(rs.getString("durum")).append("]\n");
                }
                if(!buldu) rapor.append(" - Henüz ilan açmamış.\n");
            } catch(Exception ex){}

            rapor.append("\n💼 Satın Aldığı/Kazandığı Ürünler:\n");
            String sqlKazan = "SELECT id, urun_adi, mevcut_fiyat FROM Urunler WHERE son_teklif_veren = ? AND durum = 'KAPANDI'";
            try (PreparedStatement pstmt = globalConn.prepareStatement(sqlKazan)) {
                pstmt.setString(1, kadi);
                ResultSet rs = pstmt.executeQuery();
                boolean buldu = false;
                while(rs.next()) {
                    buldu = true;
                    rapor.append(" - ").append(rs.getString("urun_adi")).append(" (").append(rs.getDouble("mevcut_fiyat")).append(" ₺)\n");
                }
                if(!buldu) rapor.append(" - Henüz kazandığı bir ilan yok.\n");
            } catch(Exception ex){}

            rapor.append("\n💬 Verdiği Son Teklif Hareketleri:\n");
            String sqlTeklif = "SELECT id, urun_adi, mevcut_fiyat, son_teklif_veren FROM Urunler WHERE son_teklif_veren = ?";
            try (PreparedStatement pstmt = globalConn.prepareStatement(sqlTeklif)) {
                pstmt.setString(1, kadi);
                ResultSet rs = pstmt.executeQuery();
                boolean buldu = false;
                while(rs.next()) {
                    buldu = true;
                    rapor.append(" - ").append(rs.getString("urun_adi")).append(" ilanına en son lider teklifi o verdi! (").append(rs.getDouble("mevcut_fiyat")).append(" ₺)\n");
                }
                if(!buldu) rapor.append(" - Aktif lider teklifi bulunmuyor.\n");
            } catch(Exception ex){}

            txtDetayAlan.setText(rapor.toString());
        }

        private void kullaniciAdiniDuzelt() {
            int satir = kullanıcıTablosu.getSelectedRow();
            if(satir == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen ismini düzeltmek istediğiniz kullanıcıyı seçin!");
                return;
            }
            int uid = (int) kulModel.getValueAt(satir, 0);
            String eskiAd = (String) kulModel.getValueAt(satir, 1);

            String yeniAd = JOptionPane.showInputDialog(this, "@" + eskiAd + " kullanıcısı için yeni ve uygun bir isim girin:", eskiAd);
            if(yeniAd != null && !yeniAd.trim().isEmpty()) {
                try {
                    String sql = "UPDATE Kullanicilar SET kullanici_adi = ? WHERE id = ?";
                    try (PreparedStatement pstmt = globalConn.prepareStatement(sql)) {
                        pstmt.setString(1, yeniAd.trim());
                        pstmt.setInt(2, uid);
                        pstmt.executeUpdate();
                    }
                    String sqlUrun = "UPDATE Urunler SET son_teklif_veren = ? WHERE son_teklif_veren = ?";
                    try (PreparedStatement pstmt2 = globalConn.prepareStatement(sqlUrun)) {
                        pstmt2.setString(1, yeniAd.trim());
                        pstmt2.setString(2, eskiAd);
                        pstmt2.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Kullanıcı adı başarıyla güncellendi!");
                    kullanicilariListele();
                    urunleriVeritabanindanGetir();
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
                }
            }
        }

        private void kullaniciBanla() {
            int satir = kullanıcıTablosu.getSelectedRow();
            if(satir == -1) {
                JOptionPane.showMessageDialog(this, "Lütfen banlamak istediğiniz kullanıcıyı seçin!");
                return;
            }
            int uid = (int) kulModel.getValueAt(satir, 0);
            String kadi = (String) kulModel.getValueAt(satir, 1);

            int onay = JOptionPane.showConfirmDialog(this, "🚨 @" + kadi + " kullanıcısını ve ona ait tüm ilanları silerek sistemden tamamen banlamak istiyor musunuz?", "Kullanıcı Banlama", JOptionPane.YES_NO_OPTION);
            if(onay == JOptionPane.YES_OPTION) {
                try {
                    try (PreparedStatement p1 = globalConn.prepareStatement("DELETE FROM Urunler WHERE satici_id = ?")) {
                        p1.setInt(1, uid);
                        p1.executeUpdate();
                    }
                    try (PreparedStatement p2 = globalConn.prepareStatement("DELETE FROM Kullanicilar WHERE id = ?")) {
                        p2.setInt(1, uid);
                        p2.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(this, "Kullanıcı sistemden kalıcı olarak banlandı.");
                    kullanicilariListele();
                    txtDetayAlan.setText("");
                    urunleriVeritabanindanGetir();
                } catch(Exception ex){}
            }
        }
    }
}