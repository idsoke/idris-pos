'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import Link from 'next/link'
import { Button, Input } from '@/components/ui'
import { useAuthStore } from '@/store'
import { 
  Check, 
  X, 
  Mail, 
  Lock, 
  User, 
  Building2, 
  Phone,
  Eye,
  EyeOff,
  AlertCircle,
  Loader2,
  ArrowLeft,
  Crown,
  Zap,
  Star,
} from 'lucide-react'
import { formatCurrency } from '@/utils'

interface Plan {
  id: number
  code: string
  name: string
  description: string
  monthlyPrice: number
  yearlyPrice: number
  trialDays: number
  isPopular: boolean
  limits: {
    maxOutlets: number
    maxUsers: number
    maxProducts: number
    maxTransactionsPerMonth: number
  }
  features: {
    reports: boolean
    inventory: boolean
    multiOutlet: boolean
    apiAccess: boolean
    export: boolean
    emailSupport: boolean
    prioritySupport: boolean
  }
}

// Google Icon SVG Component
function GoogleIcon({ className }: { className?: string }) {
  return (
    <svg className={className} viewBox="0 0 24 24">
      <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
      <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
      <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
      <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
    </svg>
  )
}

const defaultPlans: Plan[] = [
  {
    id: 1,
    code: 'FREE',
    name: 'Gratis',
    description: 'Untuk memulai usaha kecil',
    monthlyPrice: 0,
    yearlyPrice: 0,
    trialDays: 0,
    isPopular: false,
    limits: {
      maxOutlets: 1,
      maxUsers: 1,
      maxProducts: 50,
      maxTransactionsPerMonth: 100,
    },
    features: {
      reports: false,
      inventory: false,
      multiOutlet: false,
      apiAccess: false,
      export: false,
      emailSupport: true,
      prioritySupport: false,
    },
  },
  {
    id: 2,
    code: 'STARTER',
    name: 'Starter',
    description: 'Untuk usaha yang berkembang',
    monthlyPrice: 99000,
    yearlyPrice: 990000,
    trialDays: 7,
    isPopular: true,
    limits: {
      maxOutlets: 1,
      maxUsers: 3,
      maxProducts: 500,
      maxTransactionsPerMonth: 1000,
    },
    features: {
      reports: true,
      inventory: true,
      multiOutlet: false,
      apiAccess: false,
      export: true,
      emailSupport: true,
      prioritySupport: false,
    },
  },
  {
    id: 3,
    code: 'PRO',
    name: 'Professional',
    description: 'Untuk bisnis profesional',
    monthlyPrice: 249000,
    yearlyPrice: 2490000,
    trialDays: 7,
    isPopular: false,
    limits: {
      maxOutlets: 5,
      maxUsers: 10,
      maxProducts: 5000,
      maxTransactionsPerMonth: 10000,
    },
    features: {
      reports: true,
      inventory: true,
      multiOutlet: true,
      apiAccess: true,
      export: true,
      emailSupport: true,
      prioritySupport: true,
    },
  },
]

export default function SubscribePage() {
  const router = useRouter()
  const { register, loginWithGoogle, isLoading } = useAuthStore()
  
  const [step, setStep] = useState<'plans' | 'register'>('plans')
  const [selectedPlan, setSelectedPlan] = useState<Plan | null>(null)
  const [billingCycle, setBillingCycle] = useState<'monthly' | 'yearly'>('monthly')
  const [plans, setPlans] = useState<Plan[]>(defaultPlans)
  const [googleLoading, setGoogleLoading] = useState(false)
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: '',
    businessName: '',
    phone: '',
  })
  const [showPassword, setShowPassword] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    // Fetch plans from API
    const fetchPlans = async () => {
      try {
        const response = await fetch(`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/subscription/plans`)
        if (response.ok) {
          const data = await response.json()
          if (data.data && data.data.length > 0) {
            setPlans(data.data)
          }
        }
      } catch {
        // Use default plans if API fails
      }
    }
    fetchPlans()
  }, [])

  const handlePlanSelect = (plan: Plan) => {
    setSelectedPlan(plan)
    setStep('register')
  }

  const handleGoogleSignup = async () => {
    setError('')
    setGoogleLoading(true)
    
    try {
      const success = await loginWithGoogle()
      if (success) {
        router.push('/')
      } else {
        setError('Gagal mendaftar dengan Google')
      }
    } catch {
      setError('Gagal mendaftar dengan Google')
    } finally {
      setGoogleLoading(false)
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError('')

    if (!formData.name || !formData.email || !formData.password || !formData.businessName) {
      setError('Semua field wajib harus diisi')
      return
    }

    if (formData.password !== formData.confirmPassword) {
      setError('Password tidak cocok')
      return
    }

    if (formData.password.length < 6) {
      setError('Password minimal 6 karakter')
      return
    }

    const success = await register({
      name: formData.name,
      email: formData.email,
      password: formData.password,
      businessName: formData.businessName,
      phone: formData.phone,
    })

    if (success) {
      router.push('/')
    } else {
      setError('Gagal mendaftar. Coba lagi.')
    }
  }

  const getPlanIcon = (code: string) => {
    switch (code) {
      case 'FREE': return <Star className="w-6 h-6" />
      case 'STARTER': return <Zap className="w-6 h-6" />
      case 'PRO': return <Crown className="w-6 h-6" />
      default: return <Star className="w-6 h-6" />
    }
  }

  const FeatureItem = ({ enabled, label }: { enabled: boolean; label: string }) => (
    <div className="flex items-center gap-2 text-sm">
      {enabled ? (
        <Check className="w-4 h-4 text-green-500" />
      ) : (
        <X className="w-4 h-4 text-gray-300" />
      )}
      <span className={enabled ? 'text-gray-700 dark:text-gray-300' : 'text-gray-400'}>
        {label}
      </span>
    </div>
  )

  return (
    <div className="min-h-screen bg-gradient-to-br from-primary-500 to-primary-700">
      {/* Header */}
      <div className="bg-white/10 backdrop-blur-sm">
        <div className="max-w-6xl mx-auto px-4 py-4 flex items-center justify-between">
          <Link href="/login" className="flex items-center gap-2 text-white hover:text-white/80">
            <ArrowLeft className="w-5 h-5" />
            <span>Kembali</span>
          </Link>
          <div className="flex items-center gap-2">
            <div className="w-10 h-10 bg-white rounded-xl flex items-center justify-center">
              <span className="text-xl font-bold text-primary-500">W</span>
            </div>
            <span className="text-xl font-bold text-white">Warungku</span>
          </div>
          <div className="w-24" />
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-4 py-8">
        {step === 'plans' ? (
          <>
            {/* Plans Header */}
            <div className="text-center mb-8">
              <h1 className="text-3xl md:text-4xl font-bold text-white mb-3">
                Pilih Paket yang Tepat
              </h1>
              <p className="text-white/80 text-lg">
                Mulai gratis, upgrade kapan saja sesuai kebutuhan bisnis Anda
              </p>
            </div>

            {/* Billing Toggle */}
            <div className="flex justify-center mb-8">
              <div className="bg-white/20 rounded-full p-1 flex">
                <button
                  onClick={() => setBillingCycle('monthly')}
                  className={`px-6 py-2 rounded-full text-sm font-medium transition-all ${
                    billingCycle === 'monthly'
                      ? 'bg-white text-primary-600'
                      : 'text-white hover:text-white/80'
                  }`}
                >
                  Bulanan
                </button>
                <button
                  onClick={() => setBillingCycle('yearly')}
                  className={`px-6 py-2 rounded-full text-sm font-medium transition-all ${
                    billingCycle === 'yearly'
                      ? 'bg-white text-primary-600'
                      : 'text-white hover:text-white/80'
                  }`}
                >
                  Tahunan
                  <span className="ml-1 text-xs bg-green-500 text-white px-2 py-0.5 rounded-full">
                    -17%
                  </span>
                </button>
              </div>
            </div>

            {/* Plans Grid */}
            <div className="grid md:grid-cols-3 gap-6">
              {plans.map((plan) => (
                <div
                  key={plan.id}
                  className={`bg-white dark:bg-gray-800 rounded-3xl p-6 relative ${
                    plan.isPopular ? 'ring-4 ring-primary-400 shadow-2xl scale-105' : 'shadow-xl'
                  }`}
                >
                  {plan.isPopular && (
                    <div className="absolute -top-3 left-1/2 -translate-x-1/2">
                      <span className="bg-primary-500 text-white text-xs font-bold px-4 py-1 rounded-full">
                        PALING POPULER
                      </span>
                    </div>
                  )}

                  <div className="flex items-center gap-3 mb-4">
                    <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                      plan.code === 'FREE' ? 'bg-gray-100 text-gray-600' :
                      plan.code === 'STARTER' ? 'bg-primary-100 text-primary-600' :
                      'bg-yellow-100 text-yellow-600'
                    }`}>
                      {getPlanIcon(plan.code)}
                    </div>
                    <div>
                      <h3 className="font-bold text-lg text-gray-900 dark:text-white">
                        {plan.name}
                      </h3>
                      <p className="text-sm text-gray-500">{plan.description}</p>
                    </div>
                  </div>

                  <div className="mb-6">
                    <div className="flex items-baseline gap-1">
                      <span className="text-3xl font-bold text-gray-900 dark:text-white">
                        {formatCurrency(billingCycle === 'monthly' ? plan.monthlyPrice : plan.yearlyPrice / 12)}
                      </span>
                      <span className="text-gray-500">/bulan</span>
                    </div>
                    {plan.trialDays > 0 && (
                      <p className="text-sm text-green-600 mt-1">
                        {plan.trialDays} hari gratis trial
                      </p>
                    )}
                  </div>

                  <div className="space-y-3 mb-6">
                    <div className="text-sm font-medium text-gray-900 dark:text-white mb-2">
                      Batasan:
                    </div>
                    <div className="text-sm text-gray-600 dark:text-gray-400 space-y-1">
                      <p>• {plan.limits.maxOutlets} outlet</p>
                      <p>• {plan.limits.maxUsers} user</p>
                      <p>• {plan.limits.maxProducts.toLocaleString()} produk</p>
                      <p>• {plan.limits.maxTransactionsPerMonth.toLocaleString()} transaksi/bulan</p>
                    </div>
                  </div>

                  <div className="space-y-2 mb-6">
                    <div className="text-sm font-medium text-gray-900 dark:text-white mb-2">
                      Fitur:
                    </div>
                    <FeatureItem enabled={plan.features.reports} label="Laporan & Analitik" />
                    <FeatureItem enabled={plan.features.inventory} label="Manajemen Stok" />
                    <FeatureItem enabled={plan.features.multiOutlet} label="Multi Outlet" />
                    <FeatureItem enabled={plan.features.export} label="Export Data" />
                    <FeatureItem enabled={plan.features.apiAccess} label="API Access" />
                    <FeatureItem enabled={plan.features.prioritySupport} label="Priority Support" />
                  </div>

                  <Button
                    variant={plan.isPopular ? 'primary' : 'secondary'}
                    fullWidth
                    onClick={() => handlePlanSelect(plan)}
                  >
                    {plan.monthlyPrice === 0 ? 'Mulai Gratis' : 'Pilih Paket'}
                  </Button>
                </div>
              ))}
            </div>
          </>
        ) : (
          /* Registration Form */
          <div className="max-w-md mx-auto">
            <button
              onClick={() => setStep('plans')}
              className="flex items-center gap-2 text-white hover:text-white/80 mb-6"
            >
              <ArrowLeft className="w-5 h-5" />
              <span>Kembali ke Paket</span>
            </button>

            <div className="bg-white dark:bg-gray-800 rounded-3xl p-6 shadow-2xl">
              <div className="text-center mb-6">
                <h2 className="text-xl font-bold text-gray-900 dark:text-white">
                  Buat Akun Baru
                </h2>
                {selectedPlan && (
                  <p className="text-sm text-gray-500 mt-1">
                    Paket: <span className="font-medium text-primary-500">{selectedPlan.name}</span>
                    {selectedPlan.trialDays > 0 && ` (${selectedPlan.trialDays} hari trial gratis)`}
                  </p>
                )}
              </div>

              {error && (
                <div className="flex items-center gap-2 p-3 mb-4 bg-red-50 dark:bg-red-900/20 rounded-xl text-red-600 text-sm">
                  <AlertCircle className="w-5 h-5 flex-shrink-0" />
                  {error}
                </div>
              )}

              {/* Google Signup */}
              <button
                type="button"
                onClick={handleGoogleSignup}
                disabled={googleLoading || isLoading}
                className="w-full flex items-center justify-center gap-3 px-4 py-3 mb-4 border-2 border-gray-200 dark:border-gray-600 rounded-xl hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors disabled:opacity-50"
              >
                {googleLoading ? (
                  <Loader2 className="w-5 h-5 animate-spin text-gray-500" />
                ) : (
                  <GoogleIcon className="w-5 h-5" />
                )}
                <span className="font-medium text-gray-700 dark:text-gray-300">
                  Daftar dengan Google
                </span>
              </button>

              {/* Divider */}
              <div className="relative my-6">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-gray-200 dark:border-gray-600" />
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-4 bg-white dark:bg-gray-800 text-gray-500">
                    atau daftar dengan email
                  </span>
                </div>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                <Input
                  label="Nama Lengkap"
                  placeholder="John Doe"
                  icon={User}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />

                <Input
                  label="Email"
                  type="email"
                  placeholder="email@contoh.com"
                  icon={Mail}
                  value={formData.email}
                  onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                  required
                />

                <Input
                  label="Nama Bisnis/Toko"
                  placeholder="Warung Makan Bahagia"
                  icon={Building2}
                  value={formData.businessName}
                  onChange={(e) => setFormData({ ...formData, businessName: e.target.value })}
                  required
                />

                <Input
                  label="No. Telepon (Opsional)"
                  placeholder="08123456789"
                  icon={Phone}
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                />

                <div className="relative">
                  <Input
                    label="Password"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Min. 6 karakter"
                    icon={Lock}
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    required
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-[38px] text-gray-400 hover:text-gray-600"
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>

                <Input
                  label="Konfirmasi Password"
                  type="password"
                  placeholder="Ulangi password"
                  icon={Lock}
                  value={formData.confirmPassword}
                  onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
                  required
                />

                <div className="text-xs text-gray-500 dark:text-gray-400">
                  Dengan mendaftar, Anda menyetujui{' '}
                  <a href="#" className="text-primary-500 hover:underline">Syarat & Ketentuan</a>
                  {' '}dan{' '}
                  <a href="#" className="text-primary-500 hover:underline">Kebijakan Privasi</a>
                </div>

                <Button
                  type="submit"
                  variant="primary"
                  size="xl"
                  fullWidth
                  loading={isLoading}
                  disabled={googleLoading}
                >
                  Daftar Sekarang
                </Button>
              </form>

              <div className="mt-6 text-center">
                <p className="text-sm text-gray-600 dark:text-gray-400">
                  Sudah punya akun?{' '}
                  <Link href="/login" className="text-primary-500 hover:underline font-medium">
                    Masuk
                  </Link>
                </p>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Footer */}
      <div className="text-center py-6">
        <p className="text-white/60 text-sm">
          © 2024 Warungku. All rights reserved.
        </p>
      </div>
    </div>
  )
}
