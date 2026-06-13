# Warungku POS

**Aplikasi Point of Sale (POS) multi-tenant untuk UMKM/warung**, dengan backend REST API berbasis Spring Boot dan frontend web (PWA) berbasis Next.js.

---

## Tech Stack

| Layer | Teknologi |
|---|---|
| Backend | Java 21, Spring Boot 3.2 |
| Auth | Spring Security + JWT (access & refresh token) |
| Database | MySQL |
| ORM | Spring Data JPA |
| Frontend | Next.js 14, TypeScript, Tailwind CSS |
| State Management | Zustand |
| PWA | next-pwa |
| Testing | JUnit 5 (backend), Jest + React Testing Library (frontend) |

---

## Struktur Project

```
idris-pos/
├── backend-module/   # REST API (Spring Boot)
│   └── src/main/java/com/warungku/pos/
│       ├── controller     # Auth, Product, Category, Stock, Sale, Transaction, Report, Subscription, Billing, User
│       ├── service         # Business logic
│       ├── entity          # Entity JPA (multi-tenant)
│       ├── security        # JWT filter, security config
│       ├── scheduler       # Subscription scheduler
│       └── exception       # Global exception handler
└── frontend-ui/       # Web App (Next.js)
    ├── app             # Halaman: kasir, products, transactions, reports, settings, subscribe, login
    ├── components       # UI components & layout (AppShell, Header, BottomNav)
    ├── features/cashier  # Modul kasir (ProductGrid, Cart, CheckoutModal)
    └── store             # Zustand stores
```

---

## Fitur Utama

- **Autentikasi & Multi-Tenant** — login, register, refresh token berbasis JWT
- **Manajemen Produk & Kategori** — CRUD produk, pencarian, filter kategori, lookup barcode
- **Manajemen Stok** — penyesuaian stok, penambahan stok, riwayat pergerakan, alert stok rendah
- **Kasir (POS)** — pencarian produk, keranjang, checkout
- **Transaksi & Penjualan** — riwayat transaksi, void, refund, cetak struk
- **Laporan Penjualan** — laporan harian, mingguan, dan ringkasan hari ini
- **Subscription & Billing** — paket berlangganan, invoice, pembayaran, webhook payment gateway

---

## API Endpoints

| Modul | Base Path | Contoh Endpoint |
|---|---|---|
| Auth | `/api/auth` | `POST /login`, `POST /register`, `POST /refresh` |
| Products | `/api/products` | `GET /`, `GET /{id}`, `GET /search`, `GET /barcode/{barcode}`, `GET /low-stock`, `POST /`, `PUT /{id}`, `DELETE /{id}` |
| Categories | `/api/categories` | `GET /` |
| Stock | `/api/stock` | `POST /adjust`, `POST /add`, `GET /movements`, `GET /movements/{productId}` |
| Sales | `/api/sales` | `GET /`, `GET /today`, `POST /`, `POST /{id}/void`, `POST /{id}/refund`, `GET /receipt/{receiptNumber}` |
| Transactions | `/api/transactions` | `GET /`, `GET /today`, `POST /`, `POST /{id}/cancel`, `GET /invoice/{invoiceNumber}` |
| Reports | `/api/reports` | `GET /daily`, `GET /weekly`, `GET /today` |
| Subscription | `/api/subscription` | `GET /plans`, `GET /status`, `POST /subscribe`, `POST /cancel` |
| Billing | `/api/billing` | `GET /invoices`, `GET /invoices/{invoiceNumber}`, `POST /webhook` |
| Users | `/api/users` | `GET /me`, `GET /me/tenant` |
| Public | `/api/public` | `GET /plans`, `POST /register`, `GET /health` |

---

## Menjalankan Project

### Prasyarat

- Java 21 & Maven 3.9+
- Node.js 18+
- MySQL 8

### Backend

```bash
cd backend-module

# Pastikan database MySQL "warungku_pos" tersedia (akan dibuat otomatis jika belum ada)
mvn spring-boot:run
```

Backend berjalan di `http://localhost:8080`

### Frontend

```bash
cd frontend-ui
npm install
npm run dev
```

Frontend berjalan di `http://localhost:3000`

---

## Environment Variables (Backend)

| Variable | Default | Keterangan |
|---|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/warungku_pos` | Koneksi database MySQL |
| `spring.datasource.username` | `root` | Username database |
| `spring.datasource.password` | `""` | Password database |
| `jwt.secret` | (lihat `application.properties`) | Secret key JWT (HS256, minimal 256-bit) |
| `jwt.expiration` | `86400000` | Masa berlaku access token (ms) |
| `jwt.refresh-expiration` | `604800000` | Masa berlaku refresh token (ms) |

---

## Menjalankan Tests

```bash
# Backend
cd backend-module
mvn test

# Frontend
cd frontend-ui
npm test
```

---

## Lisensi

MIT License
