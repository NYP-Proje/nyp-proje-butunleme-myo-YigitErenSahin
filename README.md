# 🔨 Nesne Yönelimli Programlama - Masaüstü Canlı Açık Artırma Sistemi

Bu proje, **Beykoz Üniversitesi Bilgisayar Programcılığı** bölümü NYP (Nesne Yönelimli Programlama) dersi kapsamında geliştirilmiş, Java Swing tabanlı modern bir e-ticaret ve açık artırma simülasyonudur.

Sistem; satıcılar, alıcılar ve sistem yöneticileri (Admin) arasında gerçek zamanlı ve ilişkisel (SQLite) bir veritabanı mimarisi üzerinde çalışmaktadır.

---

## 👤 Geliştirici & Proje Sahibi
* **Yiğit Eren Şahin** - *Bilgisayar Programcılığı Öğrencisi*

**Proje Sorumlusu:** Öğr. Gör. Buket Dönmez

---

## 🚀 Sistemin Öne Çıkan Gelişmiş Özellikleri

### 🛡️ Merkezi Sistem Yöneticisi (God Mode)
* **Gelişmiş Denetim Masası:** Yönetici (`admin`), sistemdeki tüm kullanıcıların anlık hareketlerini (açtıkları ilanlar, kazandıkları ürünler, verdikleri teklifler) tek ekrandan takip edebilir.
* **Anlık Müdahale:** Uygunsuz kullanıcı adları tek tıkla düzeltilebilir veya kural ihlali yapan kullanıcılar (ve onlara ait tüm ilanlar) sistemden sınırsız banlanabilir.
* **Zorla İlan Kaldırma:** Sistem yöneticisi kurallara aykırı gördüğü herhangi bir ilanı anında veritabanından silebilir.

### ⚡ Akıllı Teklif & Zamanlayıcı Motoru
* **Dinamik Süre Yönetimi:** Her ürün için belirlenen 30 saniyelik açık artırma süresi, son saniyelerde yeni bir teklif geldiğinde otomatik olarak uzatılır ve rekabet korunur.
* **Katı Güvenlik Kuralları:**
    * Satıcılar **kendi açtıkları ilanlara** teklif vererek fiyatı manipüle edemezler.
    * Bir üründe zaten lider (en yüksek) teklif sahibi olan kullanıcı, teklifini durduk yere kendi kendine artıramaz.
    * Satıcılar kendi ilanları dışındaki diğer ürünlere normal bir müşteri gibi teklif verebilirler.

### 👤 Kapsamlı Kullanıcı ve Rol Yönetimi (CRUD)
* **Hesap Kurtarma & Güvenlik:** Şifremi Unuttum paneli ile kullanıcılar şifrelerini güncelleyebilir (Minimum 6 karakter zorunluluğu).
* **İlan Yönetimi:** Satıcılar kendi ilanlarını silebilir veya bilgilerini sonradan güncelleyebilir.
* **Kazanç ve Koleksiyon Takibi:** Kullanıcılar süresi dolan ilanlardaki başarılarına göre "Satış/Kazanç" veya "Kazanılanlar Koleksiyonu" raporlarını görüntüleyebilir.

---

## 🛠️ Kullanılan Teknolojiler
* **Programlama Dili:** Java (JDK 11+)
* **Masaüstü Arayüz:** Java Swing & AWT (Özelleştirilmiş modern tasarım)
* **Veritabanı:** SQLite & JDBC Sürücüsü (Singleton bağlantı mimarisi)
* **Versiyon Kontrol:** Git & GitHub

---

## 📦 Kurulum ve Çalıştırma
1. Bu depoyu bilgisayarınıza klonlayın veya `.zip` olarak indirin.
2. Projeyi bir Java IDE'sinde (IntelliJ IDEA vb.) açın.
3. Projeyi başlatmak için `src/Main.java` dosyasını çalıştırın. Veritabanı ve tablolar ilk açılışta otomatik olarak kurulacaktır.

> **💡 Sayın Hocam İçin Test Notu (Admin Girişi):**
> Yönetici panelini ve yetkilerini test etmek için uygulamaya şu bilgilerle kayıt olup giriş yapabilirsiniz:
> * **Kullanıcı Adı:** `admin`
> * (Sistem bu ismi otomatik algılayıp yönetici panellerini açacaktır. Ayrıca test kolaylığı açısından giriş ekranı kapatılmamış olup, tek seferde yan yana 3-4 farklı istemci açılarak canlı rekabet simüle edilebilir.)