'use client'

import { forwardRef, HTMLAttributes } from 'react'
import { cn } from '@/utils'

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  padding?: 'none' | 'sm' | 'md' | 'lg'
  hoverable?: boolean
}

const Card = forwardRef<HTMLDivElement, CardProps>(
  ({ className, padding = 'md', hoverable, children, ...props }, ref) => {
    const paddings = {
      none: '',
      sm: 'p-3',
      md: 'p-4',
      lg: 'p-6',
    }

    return (
      <div
        ref={ref}
        className={cn(
          'bg-white dark:bg-gray-800 rounded-2xl border border-gray-100 dark:border-gray-700',
          'shadow-sm',
          paddings[padding],
          hoverable && 'hover:shadow-md hover:border-gray-200 dark:hover:border-gray-600 transition-all duration-200 cursor-pointer',
          className
        )}
        {...props}
      >
        {children}
      </div>
    )
  }
)

Card.displayName = 'Card'

export { Card }
