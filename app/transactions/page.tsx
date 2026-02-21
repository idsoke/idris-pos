'use client'

import { useState } from 'react'
import { cn, formatCurrency, formatDate, getRelativeTime } from '@/utils'
import { AppShell, Header } from '@/components/layout'
import { Input, Card, Badge, Modal } from '@/components/ui'
import { mockTransactions, Transaction } from '@/services'
import { 
  Search, 
  Receipt, 
  Calendar,
  Banknote,
  CreditCard,
  QrCode,
  Wallet,
  ChevronRight,
} from 'lucide-react'

const paymentIcons = {
  cash: Banknote,
  card: CreditCard,
  qris: QrCode,
  ewallet: Wallet,
}

const paymentLabels = {
  cash: 'Tunai',
  card: 'Kartu',
  qris: 'QRIS',
  ewallet: 'E-Wallet',
}

export default function TransactionsPage() {
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedTransaction, setSelectedTransaction] = useState<Transaction | null>(null)

  const filteredTransactions = mockTransactions.filter(t =>
    t.id.toLowerCase().includes(searchQuery.toLowerCase())
  )

  // Group by date
  const groupedTransactions = filteredTransactions.reduce((groups, transaction) => {
    const date = new Date(transaction.createdAt).toLocaleDateString('id-ID')
    if (!groups[date]) {
      groups[date] = []
    }
    groups[date].push(transaction)
    return groups
  }, {} as Record<string, Transaction[]>)

  return (
    <AppShell>
      <Header 
        title="Transaksi" 
        subtitle={`${mockTransactions.length} transaksi hari ini`}
      />

      {/* Search */}
      <div className="px-4 py-3 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800">
        <Input
          placeholder="Cari ID transaksi..."
          icon={Search}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Transaction List */}
      <div className="p-4">
        {Object.keys(groupedTransactions).length === 0 ? (
          <div className="text-center py-12">
            <Receipt className="w-12 h-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500">Tidak ada transaksi</p>
          </div>
        ) : (
          Object.entries(groupedTransactions).map(([date, transactions]) => (
            <div key={date} className="mb-6">
              <div className="flex items-center gap-2 mb-3">
                <Calendar className="w-4 h-4 text-gray-400" />
                <span className="text-sm font-medium text-gray-500">{date}</span>
              </div>
              
              <div className="space-y-3">
                {transactions.map((transaction) => {
                  const PaymentIcon = paymentIcons[transaction.paymentMethod]
                  
                  return (
                    <Card
                      key={transaction.id}
                      hoverable
                      onClick={() => setSelectedTransaction(transaction)}
                      className="cursor-pointer"
                    >
                      <div className="flex items-center gap-4">
                        <div className="w-12 h-12 bg-primary-100 dark:bg-primary-900/30 rounded-xl flex items-center justify-center">
                          <Receipt className="w-6 h-6 text-primary-500" />
                        </div>
                        
                        <div className="flex-1 min-w-0">
                          <div className="flex items-center gap-2">
                            <span className="font-mono font-semibold text-gray-900 dark:text-white text-sm">
                              {transaction.id}
                            </span>
                          </div>
                          <p className="text-sm text-gray-500 mt-0.5">
                            {transaction.items.length} item • {getRelativeTime(transaction.createdAt)}
                          </p>
                        </div>

                        <div className="text-right">
                          <p className="font-bold text-gray-900 dark:text-white">
                            {formatCurrency(transaction.total)}
                          </p>
                          <div className="flex items-center gap-1 mt-1 text-gray-500">
                            <PaymentIcon className="w-4 h-4" />
                            <span className="text-xs">{paymentLabels[transaction.paymentMethod]}</span>
                          </div>
                        </div>

                        <ChevronRight className="w-5 h-5 text-gray-400" />
                      </div>
                    </Card>
                  )
                })}
              </div>
            </div>
          ))
        )}
      </div>

      {/* Transaction Detail Modal */}
      <Modal
        isOpen={!!selectedTransaction}
        onClose={() => setSelectedTransaction(null)}
        title="Detail Transaksi"
      >
        {selectedTransaction && (
          <div className="p-4">
            {/* Header */}
            <div className="text-center pb-4 border-b border-gray-100 dark:border-gray-700">
              <p className="text-sm text-gray-500">ID Transaksi</p>
              <p className="font-mono font-bold text-lg">{selectedTransaction.id}</p>
              <p className="text-sm text-gray-500 mt-1">
                {formatDate(selectedTransaction.createdAt)}
              </p>
            </div>

            {/* Items */}
            <div className="py-4 space-y-3">
              {selectedTransaction.items.map((item, index) => (
                <div key={index} className="flex justify-between">
                  <div>
                    <p className="text-gray-900 dark:text-white">{item.productName}</p>
                    <p className="text-sm text-gray-500">
                      {item.quantity} x {formatCurrency(item.price)}
                    </p>
                  </div>
                  <p className="font-medium text-gray-900 dark:text-white">
                    {formatCurrency(item.quantity * item.price)}
                  </p>
                </div>
              ))}
            </div>

            {/* Summary */}
            <div className="border-t border-gray-100 dark:border-gray-700 pt-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Subtotal</span>
                <span>{formatCurrency(selectedTransaction.subtotal)}</span>
              </div>
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Pajak (10%)</span>
                <span>{formatCurrency(selectedTransaction.tax)}</span>
              </div>
              <div className="flex justify-between font-bold text-lg pt-2 border-t border-gray-100 dark:border-gray-700">
                <span>Total</span>
                <span className="text-primary-500">{formatCurrency(selectedTransaction.total)}</span>
              </div>
            </div>

            {/* Payment Info */}
            <div className="mt-4 p-3 bg-gray-50 dark:bg-gray-800 rounded-xl">
              <p className="text-sm text-gray-500 mb-1">Metode Pembayaran</p>
              <p className="font-medium">{paymentLabels[selectedTransaction.paymentMethod]}</p>
              {selectedTransaction.cashReceived && (
                <>
                  <div className="flex justify-between mt-2 text-sm">
                    <span className="text-gray-500">Diterima</span>
                    <span>{formatCurrency(selectedTransaction.cashReceived)}</span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Kembalian</span>
                    <span>{formatCurrency(selectedTransaction.change || 0)}</span>
                  </div>
                </>
              )}
            </div>
          </div>
        )}
      </Modal>
    </AppShell>
  )
}
