import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export interface Outlet {
  id: string
  name: string
  address: string
  phone: string
  email?: string
  taxRate: number
  currency: string
  timezone: string
}

interface OutletState {
  currentOutlet: Outlet | null
  outlets: Outlet[]
  setCurrentOutlet: (outlet: Outlet) => void
  setOutlets: (outlets: Outlet[]) => void
}

export const useOutletStore = create<OutletState>()(
  persist(
    (set) => ({
      currentOutlet: null,
      outlets: [],

      setCurrentOutlet: (outlet: Outlet) => set({ currentOutlet: outlet }),
      
      setOutlets: (outlets: Outlet[]) => set({ outlets }),
    }),
    {
      name: 'outlet-storage',
    }
  )
)
