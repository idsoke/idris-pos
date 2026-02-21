import { Product, Category } from '@/store/products'
import { Outlet } from '@/store/outlet'

export const mockCategories: Category[] = [
  { id: 'all', name: 'All', icon: '🏪' },
  { id: 'food', name: 'Food', icon: '🍔' },
  { id: 'drinks', name: 'Drinks', icon: '🥤' },
  { id: 'snacks', name: 'Snacks', icon: '🍿' },
  { id: 'desserts', name: 'Desserts', icon: '🍰' },
  { id: 'others', name: 'Others', icon: '📦' },
]

export const mockProducts: Product[] = [
  // Food
  { id: '1', name: 'Nasi Goreng', price: 25000, category: 'food', sku: 'FOOD001', stock: 50, isActive: true, image: '/products/nasi-goreng.jpg' },
  { id: '2', name: 'Mie Goreng', price: 22000, category: 'food', sku: 'FOOD002', stock: 45, isActive: true, image: '/products/mie-goreng.jpg' },
  { id: '3', name: 'Ayam Goreng', price: 28000, category: 'food', sku: 'FOOD003', stock: 30, isActive: true, image: '/products/ayam-goreng.jpg' },
  { id: '4', name: 'Sate Ayam', price: 30000, category: 'food', sku: 'FOOD004', stock: 25, isActive: true, image: '/products/sate-ayam.jpg' },
  { id: '5', name: 'Gado-gado', price: 20000, category: 'food', sku: 'FOOD005', stock: 40, isActive: true, image: '/products/gado-gado.jpg' },
  { id: '6', name: 'Bakso', price: 18000, category: 'food', sku: 'FOOD006', stock: 35, isActive: true, image: '/products/bakso.jpg' },
  
  // Drinks
  { id: '7', name: 'Es Teh Manis', price: 5000, category: 'drinks', sku: 'DRK001', stock: 100, isActive: true, image: '/products/es-teh.jpg' },
  { id: '8', name: 'Es Jeruk', price: 8000, category: 'drinks', sku: 'DRK002', stock: 80, isActive: true, image: '/products/es-jeruk.jpg' },
  { id: '9', name: 'Kopi Hitam', price: 10000, category: 'drinks', sku: 'DRK003', stock: 60, isActive: true, image: '/products/kopi-hitam.jpg' },
  { id: '10', name: 'Es Kopi Susu', price: 15000, category: 'drinks', sku: 'DRK004', stock: 50, isActive: true, image: '/products/kopi-susu.jpg' },
  { id: '11', name: 'Jus Alpukat', price: 18000, category: 'drinks', sku: 'DRK005', stock: 30, isActive: true, image: '/products/jus-alpukat.jpg' },
  { id: '12', name: 'Air Mineral', price: 4000, category: 'drinks', sku: 'DRK006', stock: 200, isActive: true, image: '/products/air-mineral.jpg' },
  
  // Snacks
  { id: '13', name: 'Pisang Goreng', price: 10000, category: 'snacks', sku: 'SNK001', stock: 40, isActive: true, image: '/products/pisang-goreng.jpg' },
  { id: '14', name: 'Tahu Goreng', price: 8000, category: 'snacks', sku: 'SNK002', stock: 50, isActive: true, image: '/products/tahu-goreng.jpg' },
  { id: '15', name: 'Tempe Goreng', price: 8000, category: 'snacks', sku: 'SNK003', stock: 50, isActive: true, image: '/products/tempe-goreng.jpg' },
  { id: '16', name: 'Kentang Goreng', price: 15000, category: 'snacks', sku: 'SNK004', stock: 35, isActive: true, image: '/products/kentang-goreng.jpg' },
  
  // Desserts
  { id: '17', name: 'Es Campur', price: 15000, category: 'desserts', sku: 'DST001', stock: 25, isActive: true, image: '/products/es-campur.jpg' },
  { id: '18', name: 'Es Cendol', price: 12000, category: 'desserts', sku: 'DST002', stock: 30, isActive: true, image: '/products/es-cendol.jpg' },
  { id: '19', name: 'Pudding', price: 10000, category: 'desserts', sku: 'DST003', stock: 20, isActive: true, image: '/products/pudding.jpg' },
  { id: '20', name: 'Kue Lapis', price: 8000, category: 'desserts', sku: 'DST004', stock: 40, isActive: true, image: '/products/kue-lapis.jpg' },
]

export const mockOutlet: Outlet = {
  id: '1',
  name: 'Warungku - Cabang Utama',
  address: 'Jl. Sudirman No. 123, Jakarta Pusat',
  phone: '021-12345678',
  email: 'main@warungku.com',
  taxRate: 0.1,
  currency: 'IDR',
  timezone: 'Asia/Jakarta',
}

export interface Transaction {
  id: string
  items: {
    productId: string
    productName: string
    quantity: number
    price: number
  }[]
  subtotal: number
  tax: number
  total: number
  paymentMethod: 'cash' | 'card' | 'qris' | 'ewallet'
  cashReceived?: number
  change?: number
  createdAt: string
  cashierId: string
  cashierName: string
}

export const mockTransactions: Transaction[] = [
  {
    id: 'TRX001',
    items: [
      { productId: '1', productName: 'Nasi Goreng', quantity: 2, price: 25000 },
      { productId: '7', productName: 'Es Teh Manis', quantity: 2, price: 5000 },
    ],
    subtotal: 60000,
    tax: 6000,
    total: 66000,
    paymentMethod: 'cash',
    cashReceived: 70000,
    change: 4000,
    createdAt: new Date().toISOString(),
    cashierId: '1',
    cashierName: 'Admin User',
  },
  {
    id: 'TRX002',
    items: [
      { productId: '3', productName: 'Ayam Goreng', quantity: 1, price: 28000 },
      { productId: '10', productName: 'Es Kopi Susu', quantity: 1, price: 15000 },
    ],
    subtotal: 43000,
    tax: 4300,
    total: 47300,
    paymentMethod: 'qris',
    createdAt: new Date(Date.now() - 3600000).toISOString(),
    cashierId: '1',
    cashierName: 'Admin User',
  },
]

export function generateTransactionId(): string {
  const date = new Date()
  const dateStr = date.toISOString().slice(0, 10).replace(/-/g, '')
  const random = Math.random().toString(36).substring(2, 8).toUpperCase()
  return `TRX-${dateStr}-${random}`
}
