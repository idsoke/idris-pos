'use client'

import { useState } from 'react'
import { cn, formatCurrency } from '@/utils'
import { useCartStore, useAuthStore } from '@/store'
import { generateTransactionId } from '@/services'
import { Button, Modal } from '@/components/ui'
import { 
  Banknote, 
  CreditCard, 
  QrCode, 
  Wallet,
  Check,
  Printer,
  Share2,
} from 'lucide-react'

interface CheckoutModalProps {
  isOpen: boolean
  onClose: () => void
}

type PaymentMethod = 'cash' | 'card' | 'qris' | 'ewallet'

const paymentMethods = [
  { id: 'cash' as PaymentMethod, name: 'Tunai', icon: Banknote },
  { id: 'card' as PaymentMethod, name: 'Kartu', icon: CreditCard },
  { id: 'qris' as PaymentMethod, name: 'QRIS', icon: QrCode },
  { id: 'ewallet' as PaymentMethod, name: 'E-Wallet', icon: Wallet },
]

const quickAmounts = [10000, 20000, 50000, 100000, 200000, 500000]

export function CheckoutModal({ isOpen, onClose }: CheckoutModalProps) {
  const { items, getSubtotal, getTax, getTotal, clearCart } = useCartStore()
  const user = useAuthStore((state) => state.user)
  
  const [step, setStep] = useState<'payment' | 'cash' | 'success'>('payment')
  const [selectedMethod, setSelectedMethod] = useState<PaymentMethod | null>(null)
  const [cashReceived, setCashReceived] = useState('')
  const [transactionId, setTransactionId] = useState('')

  const total = getTotal()
  const cashAmount = parseInt(cashReceived) || 0
  const change = cashAmount - total

  const handlePaymentSelect = (method: PaymentMethod) => {
    setSelectedMethod(method)
    if (method === 'cash') {
      setStep('cash')
    } else {
      // Simulate payment processing
      setTimeout(() => {
        setTransactionId(generateTransactionId())
        setStep('success')
      }, 1500)
    }
  }

  const handleCashPayment = () => {
    if (cashAmount >= total) {
      setTransactionId(generateTransactionId())
      setStep('success')
    }
  }

  const handleComplete = () => {
    clearCart()
    setStep('payment')
    setSelectedMethod(null)
    setCashReceived('')
    setTransactionId('')
    onClose()
  }

  const handleQuickAmount = (amount: number) => {
    setCashReceived(amount.toString())
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Pembayaran" size="md">
      {/* Payment Method Selection */}
      {step === 'payment' && (
        <div className="p-4 space-y-4">
          <div className="text-center py-4">
            <p className="text-gray-500 text-sm">Total Pembayaran</p>
            <p className="text-3xl font-bold text-primary-500 mt-1">
              {formatCurrency(total)}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-3">
            {paymentMethods.map((method) => {
              const Icon = method.icon
              return (
                <button
                  key={method.id}
                  onClick={() => handlePaymentSelect(method.id)}
                  className={cn(
                    'flex flex-col items-center justify-center gap-2 p-4 rounded-2xl',
                    'border-2 transition-all duration-200',
                    'min-h-[100px]',
                    'hover:border-primary-500 hover:bg-primary-50 dark:hover:bg-primary-900/20',
                    'active:scale-[0.98]',
                    selectedMethod === method.id
                      ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                      : 'border-gray-200 dark:border-gray-700'
                  )}
                >
                  <Icon className="w-8 h-8 text-primary-500" />
                  <span className="font-semibold text-gray-900 dark:text-white">
                    {method.name}
                  </span>
                </button>
              )
            })}
          </div>
        </div>
      )}

      {/* Cash Payment */}
      {step === 'cash' && (
        <div className="p-4 space-y-4">
          <div className="text-center py-2">
            <p className="text-gray-500 text-sm">Total Pembayaran</p>
            <p className="text-2xl font-bold text-gray-900 dark:text-white mt-1">
              {formatCurrency(total)}
            </p>
          </div>

          {/* Quick Amounts */}
          <div className="grid grid-cols-3 gap-2">
            {quickAmounts.map((amount) => (
              <button
                key={amount}
                onClick={() => handleQuickAmount(amount)}
                className={cn(
                  'py-3 rounded-xl font-medium text-sm',
                  'border-2 transition-all duration-200',
                  'active:scale-[0.98]',
                  cashAmount === amount
                    ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20 text-primary-600'
                    : 'border-gray-200 dark:border-gray-700 text-gray-700 dark:text-gray-300'
                )}
              >
                {formatCurrency(amount)}
              </button>
            ))}
          </div>

          {/* Cash Input */}
          <div>
            <label className="text-sm text-gray-500 mb-1 block">
              Uang Diterima
            </label>
            <input
              type="number"
              value={cashReceived}
              onChange={(e) => setCashReceived(e.target.value)}
              className={cn(
                'w-full h-14 px-4 rounded-xl border-2 text-xl font-bold text-center',
                'bg-white dark:bg-gray-800 text-gray-900 dark:text-white',
                'border-gray-200 dark:border-gray-700',
                'focus:outline-none focus:border-primary-500'
              )}
              placeholder="0"
            />
          </div>

          {/* Change Display */}
          {cashAmount > 0 && (
            <div className={cn(
              'p-4 rounded-xl text-center',
              change >= 0 
                ? 'bg-green-50 dark:bg-green-900/20' 
                : 'bg-red-50 dark:bg-red-900/20'
            )}>
              <p className="text-sm text-gray-500 mb-1">
                {change >= 0 ? 'Kembalian' : 'Kurang'}
              </p>
              <p className={cn(
                'text-2xl font-bold',
                change >= 0 ? 'text-green-600' : 'text-red-600'
              )}>
                {formatCurrency(Math.abs(change))}
              </p>
            </div>
          )}

          <Button
            variant="primary"
            size="xl"
            fullWidth
            disabled={cashAmount < total}
            onClick={handleCashPayment}
          >
            Konfirmasi Pembayaran
          </Button>
        </div>
      )}

      {/* Success */}
      {step === 'success' && (
        <div className="p-6 text-center">
          <div className="w-20 h-20 bg-green-100 dark:bg-green-900/30 rounded-full flex items-center justify-center mx-auto mb-4">
            <Check className="w-10 h-10 text-green-500" />
          </div>
          
          <h3 className="text-xl font-bold text-gray-900 dark:text-white mb-2">
            Pembayaran Berhasil!
          </h3>
          <p className="text-gray-500 mb-1">ID Transaksi</p>
          <p className="font-mono font-semibold text-gray-900 dark:text-white mb-6">
            {transactionId}
          </p>

          {selectedMethod === 'cash' && change > 0 && (
            <div className="bg-green-50 dark:bg-green-900/20 p-4 rounded-xl mb-6">
              <p className="text-sm text-gray-500">Kembalian</p>
              <p className="text-2xl font-bold text-green-600">
                {formatCurrency(change)}
              </p>
            </div>
          )}

          <div className="flex gap-3">
            <Button variant="secondary" fullWidth onClick={handleComplete}>
              <Printer className="w-5 h-5 mr-2" />
              Cetak
            </Button>
            <Button variant="primary" fullWidth onClick={handleComplete}>
              Selesai
            </Button>
          </div>
        </div>
      )}
    </Modal>
  )
}
