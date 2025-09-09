# Money Manager Backend

Money Manager, kişisel gelir ve giderlerinizi takip etmenize, kategorilere ayırmanıza ve dashboard üzerinden analiz etmenize yardımcı olan bir **backend uygulamasıdır**.

Bu proje **Spring Boot, Spring Security (JWT Authentication), JPA, MySQL** teknolojileriyle geliştirilmiştir.

---

## 🚀 Özellikler

* ✅ Kullanıcı kayıt ve giriş sistemi (JWT Authentication)
* ✅ Gelir & gider ekleme, güncelleme ve silme
* ✅ Kategori yönetimi
* ✅ Dashboard verileri (istatistiksel özetler)
* ✅ Filtreleme özelliği (tarihe, kategoriye vb. göre)
* ✅ Bildirim yapısı (örn: belirli eşiklerde uyarı)
* ✅ Postman koleksiyonu ile kolay test imkanı

---

## 🛠 Kullanılan Teknolojiler

* **Spring Boot**
* **Spring Data JPA (Hibernate)**
* **MySQL**
* **Spring Security**
* **Spring Security (JWT Authentication)**
* **Lombok**

---

## 📌 API Başlıkları

Aşağıda uygulamada bulunan ana endpoint’lerin listesi verilmiştir:

* **Auth API**

  * `POST /register` → Yeni kullanıcı kaydı
  * `POST /login` → Kullanıcı girişi, JWT token üretimi

* **Category API**

  * `POST /create-category` → Yeni kategori ekle
  * `POST /update-category` → Kategori güncelle
  * `GET /current-user/get-categories` → Kullanıcının kategorilerini getir
  * `GET /current-user/get-categories-by-type` → Gelir / Gider kategorilerini getir

* **Income & Expense API**

  * `POST /create-income` → Gelir ekle
  * `POST /create-expenses` → Gider ekle
  * `GET /get-incomes` → Gelirleri listele
  * `GET /get-expenses` → Giderleri listele
  * `DELETE /delete-income/{id}` → Gelir sil
  * `DELETE /delete-expense/{id}` → Gider sil

* **Dashboard API**

  * `GET /get-dashboard-data` → Dashboard istatistik verileri

* **Filter API**

  * `POST /filter` → Tarih/kategori bazlı filtreleme

---

## 📸 Postman Görselleri

### 🔑 Kullanıcı İşlemleri

#### 📝 Kayıt Olma

<img width="1621" height="893" alt="Register" src="https://github.com/user-attachments/assets/2751b7e7-80df-4a71-b279-4160c25362f5" />  

#### 📧 Email Aktivasyon Doğrulama

<img width="1041" height="637" alt="Email Activation" src="https://github.com/user-attachments/assets/e25efe6d-031a-4bb8-b36b-3f414fcbe4da" />  

---

### 📂 Kategori İşlemleri

#### 📑 Kategorileri Listeleme

<img width="2404" height="1133" alt="List Categories" src="https://github.com/user-attachments/assets/a972f90f-51ca-4554-bd0e-f53dbcda3b8a" />  

#### ➕ Oluşturulan Kategoriye Veri Ekleme

<img width="1289" height="870" alt="Add Data to Category" src="https://github.com/user-attachments/assets/0e276aaa-9f59-4e81-a8d9-1f347c883e0e" />  

#### ✏️ Kategori Datası Güncelleme

<img width="1699" height="884" alt="Update Category" src="https://github.com/user-attachments/assets/e4e8452a-453b-4e32-a020-cf16972f0c8d" />  

#### 🗑️ Kategori Data Silme

<img width="2445" height="853" alt="Delete Category" src="https://github.com/user-attachments/assets/c738ac6d-dd9d-44bb-867b-0796a30fcf57" />  

---

## ⚙️ Kurulum

### 1. Repoyu klonla

```bash
git clone https://github.com/kullaniciadi/money-manager-backend.git
cd money-manager-backend
```

### 2. MySQL veritabanı oluştur

```sql
CREATE DATABASE money_manager;
```

### 3. `application.properties` ayarlarını yap

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/money_manager
spring.datasource.username=root
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=yourSecretKey
jwt.expiration=86400000
```

### 4. Uygulamayı çalıştır

```bash
mvn spring-boot:run
```

---
## References

Bu projeyi geliştirmek için[Engineer Talks With Bushan YouTube kanalını](https://www.youtube.com/@engineertalkswithbushan) kullandım. Faydalı içerik için teşekkür ederim.
