import { create } from 'zustand'

export interface Product {
  id: string
  name: string
  price: number
  category: string
  image?: string
  sku: string
  stock: number
  isActive: boolean
}

export interface Category {
  id: string
  name: string
  icon?: string
}

interface ProductState {
  products: Product[]
  categories: Category[]
  selectedCategory: string | null
  searchQuery: string
  isLoading: boolean
  setProducts: (products: Product[]) => void
  setCategories: (categories: Category[]) => void
  setSelectedCategory: (categoryId: string | null) => void
  setSearchQuery: (query: string) => void
  getFilteredProducts: () => Product[]
  addProduct: (product: Product) => void
  updateProduct: (id: string, updates: Partial<Product>) => void
  deleteProduct: (id: string) => void
}

export const useProductStore = create<ProductState>((set, get) => ({
  products: [],
  categories: [],
  selectedCategory: null,
  searchQuery: '',
  isLoading: false,

  setProducts: (products: Product[]) => set({ products }),
  
  setCategories: (categories: Category[]) => set({ categories }),
  
  setSelectedCategory: (categoryId: string | null) => set({ selectedCategory: categoryId }),
  
  setSearchQuery: (query: string) => set({ searchQuery: query }),

  getFilteredProducts: () => {
    const { products, selectedCategory, searchQuery } = get()
    
    return products.filter(product => {
      const matchesCategory = !selectedCategory || product.category === selectedCategory
      const matchesSearch = !searchQuery || 
        product.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        product.sku.toLowerCase().includes(searchQuery.toLowerCase())
      
      return matchesCategory && matchesSearch && product.isActive
    })
  },

  addProduct: (product: Product) => {
    set((state) => ({
      products: [...state.products, product],
    }))
  },

  updateProduct: (id: string, updates: Partial<Product>) => {
    set((state) => ({
      products: state.products.map(p =>
        p.id === id ? { ...p, ...updates } : p
      ),
    }))
  },

  deleteProduct: (id: string) => {
    set((state) => ({
      products: state.products.filter(p => p.id !== id),
    }))
  },
}))
