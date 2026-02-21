import { create } from 'zustand'
import { persist } from 'zustand/middleware'

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'

export interface User {
  id: string
  name: string
  email: string
  role: 'admin' | 'cashier'
  avatar?: string
}

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (email: string, password: string) => Promise<boolean>
  loginWithGoogle: () => Promise<boolean>
  register: (data: RegisterData) => Promise<boolean>
  logout: () => void
  setUser: (user: User) => void
}

interface RegisterData {
  name: string
  email: string
  password: string
  businessName: string
  phone?: string
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,

      login: async (email: string, password: string) => {
        set({ isLoading: true })
        
        try {
          const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password }),
          })

          if (!response.ok) {
            set({ isLoading: false })
            return false
          }

          const data = await response.json()
          const { accessToken, user } = data.data

          set({ 
            user: {
              id: user.id.toString(),
              name: user.name,
              email: user.email,
              role: user.role.toLowerCase(),
              avatar: user.avatar,
            },
            token: accessToken,
            isAuthenticated: true, 
            isLoading: false 
          })
          return true
        } catch {
          set({ isLoading: false })
          return false
        }
      },

      loginWithGoogle: async () => {
        set({ isLoading: true })
        
        try {
          // Open Google OAuth popup
          const googleClientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID
          const redirectUri = `${window.location.origin}/api/auth/google/callback`
          const scope = 'openid email profile'
          
          const authUrl = `https://accounts.google.com/o/oauth2/v2/auth?` +
            `client_id=${googleClientId}&` +
            `redirect_uri=${encodeURIComponent(redirectUri)}&` +
            `response_type=code&` +
            `scope=${encodeURIComponent(scope)}&` +
            `access_type=offline&` +
            `prompt=consent`

          // Open popup window for OAuth
          const popup = window.open(authUrl, 'google-oauth', 'width=500,height=600')
          
          // Wait for popup to complete (simplified - in production use postMessage)
          return new Promise((resolve) => {
            const checkPopup = setInterval(() => {
              if (!popup || popup.closed) {
                clearInterval(checkPopup)
                set({ isLoading: false })
                // Check if we got the token from callback
                const token = localStorage.getItem('google-auth-token')
                if (token) {
                  localStorage.removeItem('google-auth-token')
                  // Exchange token with backend
                  resolve(true)
                } else {
                  resolve(false)
                }
              }
            }, 500)
          })
        } catch {
          set({ isLoading: false })
          return false
        }
      },

      register: async (data: RegisterData) => {
        set({ isLoading: true })
        
        try {
          const response = await fetch(`${API_BASE_URL}/api/subscription/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
              ownerName: data.name,
              email: data.email,
              password: data.password,
              businessName: data.businessName,
              phone: data.phone,
            }),
          })

          if (!response.ok) {
            const error = await response.json()
            throw new Error(error.message || 'Registration failed')
          }

          const result = await response.json()
          
          // Auto-login after registration if token is returned
          if (result.data?.accessToken) {
            set({
              user: {
                id: result.data.user.id.toString(),
                name: result.data.user.name,
                email: result.data.user.email,
                role: result.data.user.role.toLowerCase(),
              },
              token: result.data.accessToken,
              isAuthenticated: true,
              isLoading: false
            })
          } else {
            set({ isLoading: false })
          }
          
          return true
        } catch {
          set({ isLoading: false })
          return false
        }
      },

      logout: () => {
        set({ 
          user: null, 
          token: null,
          isAuthenticated: false 
        })
      },

      setUser: (user: User) => {
        set({ user, isAuthenticated: true })
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({ 
        user: state.user, 
        token: state.token,
        isAuthenticated: state.isAuthenticated 
      }),
    }
  )
)
