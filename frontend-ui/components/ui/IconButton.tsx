'use client'

import { forwardRef, ButtonHTMLAttributes } from 'react'
import { cn } from '@/utils'
import { LucideIcon } from 'lucide-react'

export interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  icon: LucideIcon
  variant?: 'primary' | 'secondary' | 'ghost' | 'danger'
  size?: 'sm' | 'md' | 'lg'
  badge?: number
}

const IconButton = forwardRef<HTMLButtonElement, IconButtonProps>(
  ({ className, icon: Icon, variant = 'ghost', size = 'md', badge, ...props }, ref) => {
    const variants = {
      primary: 'bg-primary-500 text-white hover:bg-primary-600',
      secondary: 'bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 hover:bg-gray-200 dark:hover:bg-gray-700',
      ghost: 'text-gray-600 dark:text-gray-400 hover:bg-gray-100 dark:hover:bg-gray-800',
      danger: 'text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20',
    }

    const sizes = {
      sm: 'w-9 h-9',
      md: 'w-11 h-11',
      lg: 'w-14 h-14',
    }

    const iconSizes = {
      sm: 'w-4 h-4',
      md: 'w-5 h-5',
      lg: 'w-6 h-6',
    }

    return (
      <button
        ref={ref}
        className={cn(
          'relative inline-flex items-center justify-center rounded-xl transition-all duration-200',
          'focus:outline-none focus:ring-2 focus:ring-primary-500/50',
          'active:scale-95',
          'min-h-touch min-w-touch',
          variants[variant],
          sizes[size],
          className
        )}
        {...props}
      >
        <Icon className={iconSizes[size]} />
        {badge !== undefined && badge > 0 && (
          <span className="absolute -top-1 -right-1 min-w-[20px] h-5 px-1.5 bg-red-500 text-white text-xs font-bold rounded-full flex items-center justify-center">
            {badge > 99 ? '99+' : badge}
          </span>
        )}
      </button>
    )
  }
)

IconButton.displayName = 'IconButton'

export { IconButton }
