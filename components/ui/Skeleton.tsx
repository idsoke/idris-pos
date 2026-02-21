'use client'

import { cn } from '@/utils'

interface SkeletonProps {
  className?: string
}

export function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={cn(
        'bg-gray-200 dark:bg-gray-700 rounded-lg animate-pulse',
        className
      )}
    />
  )
}

export function ProductCardSkeleton() {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-2xl p-3 border border-gray-100 dark:border-gray-700">
      <Skeleton className="aspect-square rounded-xl mb-3" />
      <Skeleton className="h-4 w-3/4 mb-2" />
      <Skeleton className="h-5 w-1/2" />
    </div>
  )
}

export function CartItemSkeleton() {
  return (
    <div className="flex items-center gap-3 p-3">
      <Skeleton className="w-12 h-12 rounded-xl" />
      <div className="flex-1">
        <Skeleton className="h-4 w-3/4 mb-2" />
        <Skeleton className="h-4 w-1/2" />
      </div>
      <Skeleton className="w-20 h-8 rounded-lg" />
    </div>
  )
}

export function TransactionSkeleton() {
  return (
    <div className="bg-white dark:bg-gray-800 rounded-2xl p-4 border border-gray-100 dark:border-gray-700">
      <div className="flex items-center justify-between mb-3">
        <Skeleton className="h-5 w-24" />
        <Skeleton className="h-5 w-20" />
      </div>
      <Skeleton className="h-4 w-full mb-2" />
      <Skeleton className="h-4 w-2/3" />
    </div>
  )
}
