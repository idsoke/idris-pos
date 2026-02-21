'use client'

import { usePathname } from 'next/navigation'
import Link from 'next/link'
import { cn } from '@/utils'
import { 
  ShoppingCart, 
  Package, 
  Receipt, 
  BarChart3, 
  Settings,
} from 'lucide-react'
import { useCartStore } from '@/store'

const navItems = [
  { href: '/', icon: ShoppingCart, label: 'Kasir' },
  { href: '/products', icon: Package, label: 'Produk' },
  { href: '/transactions', icon: Receipt, label: 'Transaksi' },
  { href: '/reports', icon: BarChart3, label: 'Laporan' },
  { href: '/settings', icon: Settings, label: 'Pengaturan' },
]

export function BottomNav() {
  const pathname = usePathname()
  const itemCount = useCartStore((state) => state.getItemCount())

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-white dark:bg-gray-900 border-t border-gray-200 dark:border-gray-800 pb-safe-bottom">
      <div className="flex items-center justify-around h-16">
        {navItems.map((item) => {
          const isActive = pathname === item.href
          const Icon = item.icon
          const showBadge = item.href === '/' && itemCount > 0

          return (
            <Link
              key={item.href}
              href={item.href}
              className={cn(
                'flex flex-col items-center justify-center flex-1 h-full',
                'transition-colors duration-200',
                'active:scale-95',
                'min-w-touch',
                isActive 
                  ? 'text-primary-500' 
                  : 'text-gray-500 dark:text-gray-400'
              )}
            >
              <div className="relative">
                <Icon className={cn(
                  'w-6 h-6',
                  isActive && 'stroke-[2.5px]'
                )} />
                {showBadge && (
                  <span className="absolute -top-2 -right-2 min-w-[18px] h-[18px] px-1 bg-red-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center">
                    {itemCount > 99 ? '99+' : itemCount}
                  </span>
                )}
              </div>
              <span className={cn(
                'text-xs mt-1 font-medium',
                isActive && 'font-semibold'
              )}>
                {item.label}
              </span>
            </Link>
          )
        })}
      </div>
    </nav>
  )
}
