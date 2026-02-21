import { create } from 'zustand'
import { Product } from './products'

export interface CartItem {
  product: Product
  quantity: number
  notes?: string
}

interface CartState {
  items: CartItem[]
  addItem: (product: Product, quantity?: number) => void
  removeItem: (productId: string) => void
  updateQuantity: (productId: string, quantity: number) => void
  updateNotes: (productId: string, notes: string) => void
  clearCart: () => void
  getSubtotal: () => number
  getTax: () => number
  getTotal: () => number
  getItemCount: () => number
}

const TAX_RATE = 0.1 // 10%

export const useCartStore = create<CartState>((set, get) => ({
  items: [],

  addItem: (product: Product, quantity = 1) => {
    set((state) => {
      const existingItem = state.items.find(item => item.product.id === product.id)
      
      if (existingItem) {
        return {
          items: state.items.map(item =>
            item.product.id === product.id
              ? { ...item, quantity: item.quantity + quantity }
              : item
          ),
        }
      }
      
      return {
        items: [...state.items, { product, quantity }],
      }
    })
  },

  removeItem: (productId: string) => {
    set((state) => ({
      items: state.items.filter(item => item.product.id !== productId),
    }))
  },

  updateQuantity: (productId: string, quantity: number) => {
    if (quantity <= 0) {
      get().removeItem(productId)
      return
    }
    
    set((state) => ({
      items: state.items.map(item =>
        item.product.id === productId
          ? { ...item, quantity }
          : item
      ),
    }))
  },

  updateNotes: (productId: string, notes: string) => {
    set((state) => ({
      items: state.items.map(item =>
        item.product.id === productId
          ? { ...item, notes }
          : item
      ),
    }))
  },

  clearCart: () => {
    set({ items: [] })
  },

  getSubtotal: () => {
    return get().items.reduce(
      (sum, item) => sum + item.product.price * item.quantity,
      0
    )
  },

  getTax: () => {
    return get().getSubtotal() * TAX_RATE
  },

  getTotal: () => {
    return get().getSubtotal() + get().getTax()
  },

  getItemCount: () => {
    return get().items.reduce((sum, item) => sum + item.quantity, 0)
  },
}))
