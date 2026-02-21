'use client'

import { useState } from 'react'
import { cn } from '@/utils'
import { AppShell, Header } from '@/components/layout'
import { Input, IconButton } from '@/components/ui'
import { ProductGrid, Cart } from '@/features/cashier'
import { Search, ShoppingCart, X } from 'lucide-react'
import { useProductStore, useCartStore } from '@/store'

export default function CashierPage() {
  const [showCart, setShowCart] = useState(false)
  const { searchQuery, setSearchQuery } = useProductStore()
  const itemCount = useCartStore((state) => state.getItemCount())

  return (
    <AppShell>
      <Header
        title="Kasir"
        subtitle="Warungku - Cabang Utama"
        rightAction={
          <div className="relative md:hidden">
            <IconButton
              icon={ShoppingCart}
              variant="primary"
              badge={itemCount}
              onClick={() => setShowCart(true)}
            />
          </div>
        }
      />

      {/* Search Bar */}
      <div className="px-4 py-3 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800">
        <Input
          placeholder="Cari produk atau scan barcode..."
          icon={Search}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Main Content */}
      <div className="flex h-[calc(100vh-200px)]">
        {/* Product Grid */}
        <div className="flex-1 overflow-hidden">
          <ProductGrid />
        </div>

        {/* Cart Sidebar - Desktop */}
        <div className="hidden md:flex w-96 border-l border-gray-100 dark:border-gray-800 bg-white dark:bg-gray-900">
          <Cart />
        </div>
      </div>

      {/* Cart Drawer - Mobile */}
      {showCart && (
        <div className="fixed inset-0 z-50 md:hidden">
          <div 
            className="absolute inset-0 bg-black/60"
            onClick={() => setShowCart(false)}
          />
          <div className="absolute right-0 top-0 bottom-0 w-full max-w-md bg-white dark:bg-gray-900 animate-slide-in-right">
            <div className="flex items-center justify-between p-4 border-b border-gray-100 dark:border-gray-700">
              <h2 className="font-bold text-lg">Keranjang</h2>
              <IconButton
                icon={X}
                variant="ghost"
                onClick={() => setShowCart(false)}
              />
            </div>
            <div className="h-[calc(100%-64px)]">
              <Cart />
            </div>
          </div>
        </div>
      )}
    </AppShell>
  )
}
