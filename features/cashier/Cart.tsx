'use client'

import { useState } from 'react'
import { cn, formatCurrency } from '@/utils'
import { useCartStore } from '@/store'
import { Button, Modal } from '@/components/ui'
import { Minus, Plus, Trash2, ShoppingBag, CreditCard } from 'lucide-react'
import { CheckoutModal } from './CheckoutModal'

export function Cart() {
  const { items, updateQuantity, removeItem, getSubtotal, getTax, getTotal, clearCart } = useCartStore()
  const [showCheckout, setShowCheckout] = useState(false)

  if (items.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-full p-6 text-center">
        <div className="w-20 h-20 bg-gray-100 dark:bg-gray-800 rounded-full flex items-center justify-center mb-4">
          <ShoppingBag className="w-10 h-10 text-gray-400" />
        </div>
        <h3 className="font-semibold text-gray-900 dark:text-white mb-1">
          Keranjang Kosong
        </h3>
        <p className="text-sm text-gray-500">
          Pilih produk untuk memulai transaksi
        </p>
      </div>
    )
  }

  return (
    <div className="flex flex-col h-full">
      {/* Cart Header */}
      <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100 dark:border-gray-700">
        <h2 className="font-bold text-gray-900 dark:text-white">
          Keranjang ({items.length})
        </h2>
        <button
          onClick={clearCart}
          className="text-sm text-red-500 font-medium hover:underline"
        >
          Hapus Semua
        </button>
      </div>

      {/* Cart Items */}
      <div className="flex-1 overflow-y-auto">
        {items.map((item) => (
          <div
            key={item.product.id}
            className="flex items-center gap-3 p-4 border-b border-gray-50 dark:border-gray-800"
          >
            {/* Product Icon */}
            <div className="w-12 h-12 bg-gray-100 dark:bg-gray-700 rounded-xl flex items-center justify-center text-2xl flex-shrink-0">
              🍽️
            </div>

            {/* Product Info */}
            <div className="flex-1 min-w-0">
              <h4 className="font-medium text-gray-900 dark:text-white text-sm truncate">
                {item.product.name}
              </h4>
              <p className="text-primary-500 font-semibold text-sm">
                {formatCurrency(item.product.price)}
              </p>
            </div>

            {/* Quantity Controls */}
            <div className="flex items-center gap-2">
              <button
                onClick={() => updateQuantity(item.product.id, item.quantity - 1)}
                className={cn(
                  'w-8 h-8 rounded-lg flex items-center justify-center',
                  'bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300',
                  'hover:bg-gray-200 dark:hover:bg-gray-600',
                  'active:scale-95 transition-all'
                )}
              >
                {item.quantity === 1 ? (
                  <Trash2 className="w-4 h-4 text-red-500" />
                ) : (
                  <Minus className="w-4 h-4" />
                )}
              </button>
              
              <span className="w-8 text-center font-semibold text-gray-900 dark:text-white">
                {item.quantity}
              </span>
              
              <button
                onClick={() => updateQuantity(item.product.id, item.quantity + 1)}
                className={cn(
                  'w-8 h-8 rounded-lg flex items-center justify-center',
                  'bg-primary-500 text-white',
                  'hover:bg-primary-600',
                  'active:scale-95 transition-all'
                )}
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Cart Summary */}
      <div className="border-t border-gray-100 dark:border-gray-700 p-4 space-y-3">
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Subtotal</span>
          <span className="text-gray-900 dark:text-white font-medium">
            {formatCurrency(getSubtotal())}
          </span>
        </div>
        <div className="flex justify-between text-sm">
          <span className="text-gray-500">Pajak (10%)</span>
          <span className="text-gray-900 dark:text-white font-medium">
            {formatCurrency(getTax())}
          </span>
        </div>
        <div className="flex justify-between text-lg font-bold pt-2 border-t border-gray-100 dark:border-gray-700">
          <span className="text-gray-900 dark:text-white">Total</span>
          <span className="text-primary-500">
            {formatCurrency(getTotal())}
          </span>
        </div>

        <Button
          variant="primary"
          size="xl"
          fullWidth
          onClick={() => setShowCheckout(true)}
          className="mt-4"
        >
          <CreditCard className="w-5 h-5 mr-2" />
          Bayar {formatCurrency(getTotal())}
        </Button>
      </div>

      <CheckoutModal
        isOpen={showCheckout}
        onClose={() => setShowCheckout(false)}
      />
    </div>
  )
}
