'use client'

import { ReactNode } from 'react'
import { cn } from '@/utils'
import { Menu, Bell } from 'lucide-react'
import { IconButton } from '@/components/ui'

interface HeaderProps {
  title?: string
  subtitle?: string
  leftAction?: ReactNode
  rightAction?: ReactNode
  showNotifications?: boolean
  className?: string
}

export function Header({
  title = 'Warungku',
  subtitle,
  leftAction,
  rightAction,
  showNotifications = true,
  className,
}: HeaderProps) {
  return (
    <header
      className={cn(
        'sticky top-0 z-30 bg-white dark:bg-gray-900',
        'border-b border-gray-100 dark:border-gray-800',
        'pt-safe-top',
        className
      )}
    >
      <div className="flex items-center justify-between h-16 px-4">
        {/* Left */}
        <div className="flex items-center gap-3">
          {leftAction || (
            <div className="w-10 h-10 bg-gradient-to-br from-primary-500 to-primary-600 rounded-xl flex items-center justify-center">
              <span className="text-white font-bold text-lg">W</span>
            </div>
          )}
          <div>
            <h1 className="font-bold text-gray-900 dark:text-white text-lg">
              {title}
            </h1>
            {subtitle && (
              <p className="text-xs text-gray-500 dark:text-gray-400">
                {subtitle}
              </p>
            )}
          </div>
        </div>

        {/* Right */}
        <div className="flex items-center gap-2">
          {rightAction}
          {showNotifications && (
            <IconButton
              icon={Bell}
              variant="ghost"
              badge={3}
            />
          )}
        </div>
      </div>
    </header>
  )
}
