'use client'

import { cn } from '@/utils'
import { AppShell, Header } from '@/components/layout'
import { Card } from '@/components/ui'
import { useThemeStore, useAuthStore, useOutletStore } from '@/store'
import { mockOutlet } from '@/services'
import { useRouter } from 'next/navigation'
import { useEffect } from 'react'
import { 
  Store,
  User,
  Bell,
  Moon,
  Sun,
  Monitor,
  Printer,
  Receipt,
  Shield,
  HelpCircle,
  LogOut,
  ChevronRight,
  CreditCard,
  Globe,
} from 'lucide-react'

type Theme = 'light' | 'dark' | 'system'

const themeOptions: { value: Theme; label: string; icon: typeof Sun }[] = [
  { value: 'light', label: 'Terang', icon: Sun },
  { value: 'dark', label: 'Gelap', icon: Moon },
  { value: 'system', label: 'Sistem', icon: Monitor },
]

const settingsSections = [
  {
    title: 'Toko',
    items: [
      { id: 'store', label: 'Informasi Toko', icon: Store, description: 'Nama, alamat, kontak' },
      { id: 'outlets', label: 'Outlet', icon: Globe, description: 'Kelola lokasi toko' },
      { id: 'staff', label: 'Staff & Hak Akses', icon: User, description: '3 staff' },
    ]
  },
  {
    title: 'Penjualan',
    items: [
      { id: 'payment', label: 'Metode Pembayaran', icon: CreditCard, description: 'Tunai, Kartu, QRIS' },
      { id: 'receipt', label: 'Pengaturan Struk', icon: Receipt, description: 'Kustomisasi struk' },
      { id: 'printer', label: 'Printer', icon: Printer, description: 'Tidak terhubung' },
    ]
  },
  {
    title: 'Akun',
    items: [
      { id: 'notifications', label: 'Notifikasi', icon: Bell, description: 'Push & email' },
      { id: 'security', label: 'Keamanan', icon: Shield, description: 'Password, PIN' },
      { id: 'help', label: 'Bantuan', icon: HelpCircle, description: 'FAQ, hubungi kami' },
    ]
  },
]

export default function SettingsPage() {
  const router = useRouter()
  const { theme, setTheme } = useThemeStore()
  const { user, logout } = useAuthStore()
  const { currentOutlet, setCurrentOutlet } = useOutletStore()

  useEffect(() => {
    if (!currentOutlet) {
      setCurrentOutlet(mockOutlet)
    }
  }, [currentOutlet, setCurrentOutlet])

  const handleLogout = () => {
    if (confirm('Keluar dari akun?')) {
      logout()
      router.push('/login')
    }
  }

  return (
    <AppShell>
      <Header title="Pengaturan" />

      <div className="p-4 space-y-6">
        {/* Store Profile */}
        <Card className="p-4">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 bg-gradient-to-br from-primary-400 to-primary-600 rounded-2xl flex items-center justify-center">
              <Store className="w-8 h-8 text-white" />
            </div>
            <div className="flex-1">
              <h2 className="font-bold text-gray-900 dark:text-white">
                {currentOutlet?.name || 'Warungku'}
              </h2>
              <p className="text-sm text-gray-500">{currentOutlet?.address}</p>
              <span className="inline-flex items-center gap-1 mt-1 px-2 py-0.5 bg-green-100 dark:bg-green-900/30 text-green-700 dark:text-green-400 text-xs font-medium rounded-full">
                <span className="w-1.5 h-1.5 bg-green-500 rounded-full" />
                Aktif
              </span>
            </div>
            <ChevronRight className="w-5 h-5 text-gray-400" />
          </div>
        </Card>

        {/* Theme Toggle */}
        <Card className="p-4">
          <p className="font-semibold text-gray-900 dark:text-white mb-3">
            Tampilan
          </p>
          <div className="flex gap-2">
            {themeOptions.map((option) => {
              const Icon = option.icon
              const isActive = theme === option.value
              
              return (
                <button
                  key={option.value}
                  onClick={() => setTheme(option.value)}
                  className={cn(
                    'flex-1 flex flex-col items-center gap-2 p-3 rounded-xl',
                    'border-2 transition-all duration-200',
                    isActive
                      ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                      : 'border-gray-200 dark:border-gray-700'
                  )}
                >
                  <Icon className={cn(
                    'w-6 h-6',
                    isActive ? 'text-primary-500' : 'text-gray-400'
                  )} />
                  <span className={cn(
                    'text-sm font-medium',
                    isActive ? 'text-primary-600 dark:text-primary-400' : 'text-gray-600 dark:text-gray-400'
                  )}>
                    {option.label}
                  </span>
                </button>
              )
            })}
          </div>
        </Card>

        {/* Settings Sections */}
        {settingsSections.map((section) => (
          <div key={section.title}>
            <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wide mb-3 px-1">
              {section.title}
            </h3>
            <Card padding="none">
              {section.items.map((item, index) => {
                const Icon = item.icon
                return (
                  <button
                    key={item.id}
                    className={cn(
                      'w-full flex items-center gap-4 p-4 text-left',
                      'hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors',
                      index !== section.items.length - 1 && 'border-b border-gray-100 dark:border-gray-700'
                    )}
                  >
                    <div className="w-10 h-10 bg-gray-100 dark:bg-gray-700 rounded-xl flex items-center justify-center">
                      <Icon className="w-5 h-5 text-gray-600 dark:text-gray-400" />
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-medium text-gray-900 dark:text-white">{item.label}</p>
                      <p className="text-sm text-gray-500 truncate">{item.description}</p>
                    </div>
                    <ChevronRight className="w-5 h-5 text-gray-400 flex-shrink-0" />
                  </button>
                )
              })}
            </Card>
          </div>
        ))}

        {/* Logout */}
        <button
          onClick={handleLogout}
          className="w-full flex items-center justify-center gap-3 h-14 bg-red-50 dark:bg-red-900/20 text-red-600 dark:text-red-400 font-semibold rounded-2xl hover:bg-red-100 dark:hover:bg-red-900/30 transition-colors"
        >
          <LogOut className="w-5 h-5" />
          Keluar
        </button>

        {/* Version */}
        <p className="text-center text-sm text-gray-400">
          Warungku v1.0.0
        </p>
      </div>
    </AppShell>
  )
}
