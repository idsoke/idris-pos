'use client'

import { Fragment, ReactNode } from 'react'
import { cn } from '@/utils'
import { X } from 'lucide-react'
import { IconButton } from './IconButton'

export interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title?: string
  children: ReactNode
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  showClose?: boolean
}

export function Modal({
  isOpen,
  onClose,
  title,
  children,
  size = 'md',
  showClose = true,
}: ModalProps) {
  if (!isOpen) return null

  const sizes = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
    full: 'max-w-full mx-4',
  }

  return (
    <Fragment>
      {/* Backdrop */}
      <div
        className="fixed inset-0 bg-black/60 backdrop-blur-sm z-50 animate-fade-in"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="fixed inset-0 z-50 flex items-center justify-center p-4 pointer-events-none">
        <div
          className={cn(
            'w-full bg-white dark:bg-gray-800 rounded-3xl shadow-2xl',
            'pointer-events-auto animate-scale-in',
            'max-h-[90vh] overflow-hidden flex flex-col',
            sizes[size]
          )}
          onClick={(e) => e.stopPropagation()}
        >
          {/* Header */}
          {(title || showClose) && (
            <div className="flex items-center justify-between p-4 border-b border-gray-100 dark:border-gray-700">
              {title && (
                <h2 className="text-lg font-bold text-gray-900 dark:text-white">
                  {title}
                </h2>
              )}
              {showClose && (
                <IconButton
                  icon={X}
                  variant="ghost"
                  size="sm"
                  onClick={onClose}
                  className="ml-auto"
                />
              )}
            </div>
          )}

          {/* Content */}
          <div className="flex-1 overflow-y-auto">
            {children}
          </div>
        </div>
      </div>
    </Fragment>
  )
}
