'use client'

import { ReactNode } from 'react'
import { cn } from '@/utils'
import { BottomNav } from './BottomNav'

interface AppShellProps {
  children: ReactNode
  hideNav?: boolean
  className?: string
}

export function AppShell({ children, hideNav, className }: AppShellProps) {
  return (
    <div className={cn(
      'min-h-screen bg-gray-50 dark:bg-gray-950',
      !hideNav && 'pb-20',
      className
    )}>
      {children}
      {!hideNav && <BottomNav />}
    </div>
  )
}
