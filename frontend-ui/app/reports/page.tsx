'use client'

import { useState } from 'react'
import { cn, formatCurrency, formatNumber } from '@/utils'
import { AppShell, Header } from '@/components/layout'
import { Card, Button } from '@/components/ui'
import { mockTransactions } from '@/services'
import { 
  TrendingUp, 
  TrendingDown,
  DollarSign,
  ShoppingCart,
  Receipt,
  Users,
  Calendar,
  ChevronLeft,
  ChevronRight,
  Download,
} from 'lucide-react'

// Calculate mock stats
const calculateStats = () => {
  const totalRevenue = mockTransactions.reduce((sum, t) => sum + t.total, 0)
  const totalTransactions = mockTransactions.length
  const avgTransaction = totalRevenue / totalTransactions || 0
  const totalItems = mockTransactions.reduce((sum, t) => 
    sum + t.items.reduce((s, i) => s + i.quantity, 0), 0
  )

  return {
    revenue: totalRevenue,
    transactions: totalTransactions,
    avgTransaction,
    items: totalItems,
  }
}

export default function ReportsPage() {
  const [selectedDate, setSelectedDate] = useState(new Date())
  const stats = calculateStats()

  const formatDateDisplay = (date: Date) => {
    return new Intl.DateTimeFormat('id-ID', {
      weekday: 'long',
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }).format(date)
  }

  const changeDate = (days: number) => {
    const newDate = new Date(selectedDate)
    newDate.setDate(newDate.getDate() + days)
    setSelectedDate(newDate)
  }

  const statCards = [
    {
      title: 'Pendapatan',
      value: formatCurrency(stats.revenue),
      change: '+12.5%',
      trend: 'up' as const,
      icon: DollarSign,
      color: 'bg-green-500',
    },
    {
      title: 'Transaksi',
      value: formatNumber(stats.transactions),
      change: '+8.2%',
      trend: 'up' as const,
      icon: Receipt,
      color: 'bg-blue-500',
    },
    {
      title: 'Rata-rata',
      value: formatCurrency(stats.avgTransaction),
      change: '-2.1%',
      trend: 'down' as const,
      icon: ShoppingCart,
      color: 'bg-purple-500',
    },
    {
      title: 'Item Terjual',
      value: formatNumber(stats.items),
      change: '+15.3%',
      trend: 'up' as const,
      icon: Users,
      color: 'bg-orange-500',
    },
  ]

  // Mock hourly data
  const hourlyData = [
    { hour: '08:00', revenue: 150000 },
    { hour: '09:00', revenue: 280000 },
    { hour: '10:00', revenue: 420000 },
    { hour: '11:00', revenue: 380000 },
    { hour: '12:00', revenue: 650000 },
    { hour: '13:00', revenue: 520000 },
    { hour: '14:00', revenue: 340000 },
    { hour: '15:00', revenue: 280000 },
    { hour: '16:00', revenue: 390000 },
    { hour: '17:00', revenue: 480000 },
  ]

  const maxRevenue = Math.max(...hourlyData.map(d => d.revenue))

  // Mock top products
  const topProducts = [
    { name: 'Nasi Goreng', quantity: 45, revenue: 1125000 },
    { name: 'Es Teh Manis', quantity: 82, revenue: 410000 },
    { name: 'Ayam Goreng', quantity: 38, revenue: 1064000 },
    { name: 'Mie Goreng', quantity: 32, revenue: 704000 },
    { name: 'Es Kopi Susu', quantity: 28, revenue: 420000 },
  ]

  return (
    <AppShell>
      <Header 
        title="Laporan" 
        subtitle="Statistik penjualan"
        rightAction={
          <Button variant="secondary" size="sm">
            <Download className="w-4 h-4 mr-1" />
            Export
          </Button>
        }
      />

      <div className="p-4 space-y-6">
        {/* Date Selector */}
        <div className="flex items-center justify-between">
          <button
            onClick={() => changeDate(-1)}
            className="w-10 h-10 rounded-xl bg-white dark:bg-gray-800 flex items-center justify-center border border-gray-200 dark:border-gray-700"
          >
            <ChevronLeft className="w-5 h-5" />
          </button>
          
          <div className="text-center">
            <div className="flex items-center gap-2 text-gray-500">
              <Calendar className="w-4 h-4" />
              <span className="text-sm">{formatDateDisplay(selectedDate)}</span>
            </div>
          </div>

          <button
            onClick={() => changeDate(1)}
            className="w-10 h-10 rounded-xl bg-white dark:bg-gray-800 flex items-center justify-center border border-gray-200 dark:border-gray-700"
          >
            <ChevronRight className="w-5 h-5" />
          </button>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-3">
          {statCards.map((stat) => {
            const Icon = stat.icon
            const TrendIcon = stat.trend === 'up' ? TrendingUp : TrendingDown
            
            return (
              <Card key={stat.title} className="p-4">
                <div className="flex items-center gap-3 mb-3">
                  <div className={cn('w-10 h-10 rounded-xl flex items-center justify-center', stat.color)}>
                    <Icon className="w-5 h-5 text-white" />
                  </div>
                  <span className="text-sm text-gray-500">{stat.title}</span>
                </div>
                <p className="text-xl font-bold text-gray-900 dark:text-white mb-1">
                  {stat.value}
                </p>
                <div className={cn(
                  'flex items-center gap-1 text-sm font-medium',
                  stat.trend === 'up' ? 'text-green-500' : 'text-red-500'
                )}>
                  <TrendIcon className="w-4 h-4" />
                  {stat.change}
                </div>
              </Card>
            )
          })}
        </div>

        {/* Hourly Chart */}
        <Card className="p-4">
          <h3 className="font-semibold text-gray-900 dark:text-white mb-4">
            Penjualan per Jam
          </h3>
          <div className="flex items-end gap-2 h-32">
            {hourlyData.map((data) => (
              <div key={data.hour} className="flex-1 flex flex-col items-center gap-1">
                <div
                  className="w-full bg-primary-500 rounded-t-lg transition-all duration-300"
                  style={{ height: `${(data.revenue / maxRevenue) * 100}%` }}
                />
                <span className="text-[10px] text-gray-500">{data.hour.split(':')[0]}</span>
              </div>
            ))}
          </div>
        </Card>

        {/* Top Products */}
        <Card className="p-4">
          <h3 className="font-semibold text-gray-900 dark:text-white mb-4">
            Produk Terlaris
          </h3>
          <div className="space-y-3">
            {topProducts.map((product, index) => (
              <div key={product.name} className="flex items-center gap-3">
                <span className={cn(
                  'w-6 h-6 rounded-full flex items-center justify-center text-xs font-bold',
                  index === 0 ? 'bg-yellow-100 text-yellow-700' :
                  index === 1 ? 'bg-gray-100 text-gray-700' :
                  index === 2 ? 'bg-orange-100 text-orange-700' :
                  'bg-gray-50 text-gray-500'
                )}>
                  {index + 1}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-900 dark:text-white text-sm truncate">
                    {product.name}
                  </p>
                  <p className="text-xs text-gray-500">{product.quantity} terjual</p>
                </div>
                <span className="font-semibold text-gray-900 dark:text-white text-sm">
                  {formatCurrency(product.revenue)}
                </span>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </AppShell>
  )
}
