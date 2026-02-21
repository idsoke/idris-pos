'use client'

import { useEffect, useState, useCallback } from 'react'
import Image from 'next/image'
import { cn, formatCurrency } from '@/utils'
import { useProductStore, useCartStore, Product } from '@/store'
import { productService, categoryService } from '@/services'
import { ProductCardSkeleton } from '@/components/ui'
import { Package, RefreshCw, AlertCircle } from 'lucide-react'

export function ProductGrid() {
  const { 
    categories, 
    selectedCategory, 
    setProducts, 
    setCategories, 
    setSelectedCategory,
    getFilteredProducts,
  } = useProductStore()
  
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const addItem = useCartStore((state) => state.addItem)
  const filteredProducts = getFilteredProducts()

  const loadData = useCallback(async () => {
    setIsLoading(true)
    setError(null)
    try {
      // Fetch categories and products from API
      const [categoriesData, products] = await Promise.all([
        categoryService.getAll(),
        productService.getAll(),
      ])
      
      // Transform categories for UI (add 'all' option)
      const formattedCategories = [
        { id: 'all', name: 'Semua', icon: '🏪' },
        ...categoriesData.map(c => ({ 
          id: c.id.toString(), 
          name: c.name, 
          icon: c.icon || '📦' 
        }))
      ]
      
      setCategories(formattedCategories)
      setProducts(products)
    } catch (err) {
      console.error('Failed to load data:', err)
      setError('Gagal memuat data. Pastikan server API berjalan.')
    } finally {
      setIsLoading(false)
    }
  }, [setCategories, setProducts])

  useEffect(() => {
    loadData()
  }, [loadData])

  const handleProductClick = (product: Product) => {
    addItem(product)
  }

  return (
    <div className="flex flex-col h-full">
      {/* Categories */}
      <div className="flex gap-2 px-4 py-3 overflow-x-auto scrollbar-hide">
        {categories.map((category) => (
          <button
            key={category.id}
            onClick={() => setSelectedCategory(category.id === 'all' ? null : category.id)}
            className={cn(
              'flex items-center gap-2 px-4 py-2 rounded-full whitespace-nowrap',
              'text-sm font-medium transition-all duration-200',
              'min-h-touch',
              (selectedCategory === category.id || (!selectedCategory && category.id === 'all'))
                ? 'bg-primary-500 text-white shadow-md'
                : 'bg-white dark:bg-gray-800 text-gray-700 dark:text-gray-300 border border-gray-200 dark:border-gray-700'
            )}
          >
            <span>{category.icon}</span>
            <span>{category.name}</span>
          </button>
        ))}
      </div>

      {/* Products Grid */}
      <div className="flex-1 overflow-y-auto px-4 pb-4">
        {isLoading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {Array.from({ length: 8 }).map((_, i) => (
              <ProductCardSkeleton key={i} />
            ))}
          </div>
        ) : error ? (
          <div className="flex flex-col items-center justify-center h-64 text-gray-500">
            <AlertCircle className="w-12 h-12 mb-3 text-red-500" />
            <p className="mb-4 text-center">{error}</p>
            <button
              onClick={loadData}
              className="flex items-center gap-2 px-4 py-2 bg-primary-500 text-white rounded-lg hover:bg-primary-600"
            >
              <RefreshCw className="w-4 h-4" />
              Coba Lagi
            </button>
          </div>
        ) : filteredProducts.length === 0 ? (
          <div className="flex flex-col items-center justify-center h-64 text-gray-500">
            <Package className="w-12 h-12 mb-3" />
            <p>Tidak ada produk</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
            {filteredProducts.map((product) => (
              <button
                key={product.id}
                onClick={() => handleProductClick(product)}
                className={cn(
                  'bg-white dark:bg-gray-800 rounded-2xl p-3',
                  'border border-gray-100 dark:border-gray-700',
                  'text-left transition-all duration-200',
                  'hover:shadow-lg hover:scale-[1.02]',
                  'active:scale-[0.98]',
                  'focus:outline-none focus:ring-2 focus:ring-primary-500/50'
                )}
              >
                <div className="aspect-square bg-gray-100 dark:bg-gray-700 rounded-xl mb-3 overflow-hidden relative">
                  {product.image ? (
                    <Image
                      src={product.image}
                      alt={product.name}
                      fill
                      className="object-cover"
                      sizes="(max-width: 640px) 50vw, 25vw"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-4xl">
                      🍽️
                    </div>
                  )}
                  {product.stock <= 5 && (
                    <span className="absolute top-2 right-2 px-2 py-0.5 bg-red-500 text-white text-[10px] font-bold rounded-full">
                      Stok: {product.stock}
                    </span>
                  )}
                </div>
                <h3 className="font-semibold text-gray-900 dark:text-white text-sm line-clamp-2 mb-1">
                  {product.name}
                </h3>
                <p className="text-primary-500 font-bold">
                  {formatCurrency(product.price)}
                </p>
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
