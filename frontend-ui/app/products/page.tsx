'use client'

import { useState, useEffect } from 'react'
import { cn, formatCurrency } from '@/utils'
import { AppShell, Header } from '@/components/layout'
import { Button, Input, Card, Badge, Modal, IconButton } from '@/components/ui'
import { useProductStore, Product } from '@/store'
import { productService } from '@/services/products'
import { categoryService, CategoryDto } from '@/services/categories'
import { 
  Search, 
  Plus, 
  Edit2, 
  Trash2, 
  Package,
  Loader2,
} from 'lucide-react'
import { ImageUpload } from '@/components/ui'

export default function ProductsPage() {
  const { 
    products, 
    setProducts, 
    addProduct,
    updateProduct,
    deleteProduct,
    searchQuery,
    setSearchQuery,
  } = useProductStore()

  const [showModal, setShowModal] = useState(false)
  const [editingProduct, setEditingProduct] = useState<Product | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [categories, setCategories] = useState<CategoryDto[]>([])
  const [formData, setFormData] = useState({
    name: '',
    price: '',
    sku: '',
    stock: '',
    categoryId: 0,
    image: '' as string | null,
  })

  // Fetch products and categories from API
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true)
        const [productsData, categoriesData] = await Promise.all([
          productService.getAll(),
          categoryService.getAll()
        ])
        setProducts(productsData)
        setCategories(categoriesData)
      } catch (error) {
        console.error('Failed to fetch data:', error)
        setProducts([])
      } finally {
        setLoading(false)
      }
    }
    
    fetchData()
  }, [setProducts])

  const filteredProducts = products.filter(p => 
    p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.sku.toLowerCase().includes(searchQuery.toLowerCase())
  )

  const handleEdit = (product: Product) => {
    setEditingProduct(product)
    // product.category is now categoryId as string
    const categoryId = parseInt(product.category) || categories[0]?.id || 0
    setFormData({
      name: product.name,
      price: product.price.toString(),
      sku: product.sku,
      stock: product.stock.toString(),
      categoryId: categoryId,
      image: product.image || null,
    })
    setShowModal(true)
  }

  const handleDelete = async (id: string) => {
    if (confirm('Hapus produk ini?')) {
      try {
        await productService.delete(id)
        deleteProduct(id)
      } catch (error) {
        console.error('Failed to delete product:', error)
        alert('Gagal menghapus produk')
      }
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSaving(true)
    
    try {
      const productData = {
        name: formData.name,
        price: parseInt(formData.price),
        sku: formData.sku,
        stock: parseInt(formData.stock),
        category: formData.categoryId.toString(),
        categoryId: formData.categoryId > 0 ? formData.categoryId : undefined,
        image: formData.image || undefined,
        isActive: true,
      }

      if (editingProduct) {
        const updated = await productService.update(editingProduct.id, productData)
        updateProduct(editingProduct.id, updated)
      } else {
        const created = await productService.create(productData)
        addProduct(created)
      }

      setShowModal(false)
      setEditingProduct(null)
      setFormData({ name: '', price: '', sku: '', stock: '', categoryId: categories[0]?.id || 0, image: null })
    } catch (error) {
      console.error('Failed to save product:', error)
      alert('Gagal menyimpan produk: ' + (error as Error).message)
    } finally {
      setSaving(false)
    }
  }

  const openNewProduct = () => {
    setEditingProduct(null)
    setFormData({ name: '', price: '', sku: '', stock: '', categoryId: categories[0]?.id || 0, image: null })
    setShowModal(true)
  }

  const getCategoryEmoji = (categoryId: string) => {
    const cat = categories.find(c => c.id.toString() === categoryId)
    return cat?.icon || '📦'
  }

  return (
    <AppShell>
      <Header 
        title="Produk" 
        subtitle={`${products.length} produk`}
        rightAction={
          <Button variant="primary" size="sm" onClick={openNewProduct}>
            <Plus className="w-4 h-4 mr-1" />
            Tambah
          </Button>
        }
      />

      {/* Search */}
      <div className="px-4 py-3 bg-white dark:bg-gray-900 border-b border-gray-100 dark:border-gray-800">
        <Input
          placeholder="Cari produk..."
          icon={Search}
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Product List */}
      <div className="p-4 space-y-3">
        {loading ? (
          <div className="text-center py-12">
            <Loader2 className="w-12 h-12 text-primary-500 mx-auto mb-3 animate-spin" />
            <p className="text-gray-500">Memuat produk...</p>
          </div>
        ) : filteredProducts.length === 0 ? (
          <div className="text-center py-12">
            <Package className="w-12 h-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500">Tidak ada produk</p>
          </div>
        ) : (
          filteredProducts.map((product) => (
            <Card key={product.id} className="flex items-center gap-4">
              <div className="w-14 h-14 bg-gray-100 dark:bg-gray-700 rounded-xl flex items-center justify-center text-2xl flex-shrink-0 overflow-hidden">
                {product.image && product.image.length > 0 ? (
                  <img 
                    src={product.image.startsWith('data:') ? product.image : `/api/images/${product.image}`} 
                    alt={product.name} 
                    className="w-full h-full object-cover" 
                  />
                ) : (
                  getCategoryEmoji(product.category)
                )}
              </div>
              
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <h3 className="font-semibold text-gray-900 dark:text-white truncate">
                    {product.name}
                  </h3>
                  {product.stock <= 5 && (
                    <Badge variant="danger" size="sm">Stok Rendah</Badge>
                  )}
                </div>
                <p className="text-sm text-gray-500">{product.sku}</p>
                <div className="flex items-center gap-3 mt-1">
                  <span className="text-primary-500 font-bold">
                    {formatCurrency(product.price)}
                  </span>
                  <span className="text-sm text-gray-500">
                    Stok: {product.stock}
                  </span>
                </div>
              </div>

              <div className="flex gap-1">
                <IconButton
                  icon={Edit2}
                  variant="ghost"
                  size="sm"
                  onClick={() => handleEdit(product)}
                />
                <IconButton
                  icon={Trash2}
                  variant="danger"
                  size="sm"
                  onClick={() => handleDelete(product.id)}
                />
              </div>
            </Card>
          ))
        )}
      </div>

      {/* Add/Edit Modal */}
      <Modal
        isOpen={showModal}
        onClose={() => setShowModal(false)}
        title={editingProduct ? 'Edit Produk' : 'Tambah Produk'}
      >
        <form onSubmit={handleSubmit} className="p-4 space-y-4">
          {/* Image Upload */}
          <div className="flex justify-center">
            <ImageUpload
              value={formData.image}
              onChange={(dataUrl) => setFormData({ ...formData, image: dataUrl })}
              onRemove={() => setFormData({ ...formData, image: null })}
              size="lg"
              placeholder="Tambah Foto Produk"
            />
          </div>

          <Input
            label="Nama Produk"
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            required
          />
          <Input
            label="SKU"
            value={formData.sku}
            onChange={(e) => setFormData({ ...formData, sku: e.target.value })}
            required
          />
          <div className="grid grid-cols-2 gap-3">
            <Input
              label="Harga"
              type="number"
              value={formData.price}
              onChange={(e) => setFormData({ ...formData, price: e.target.value })}
              required
            />
            <Input
              label="Stok"
              type="number"
              value={formData.stock}
              onChange={(e) => setFormData({ ...formData, stock: e.target.value })}
              required
            />
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">
              Kategori
            </label>
            <select
              value={formData.categoryId}
              onChange={(e) => setFormData({ ...formData, categoryId: parseInt(e.target.value) })}
              className="w-full h-12 px-4 rounded-xl border bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700"
            >
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.icon} {cat.name}</option>
              ))}
            </select>
          </div>
          <div className="flex gap-3 pt-2">
            <Button type="button" variant="secondary" fullWidth onClick={() => setShowModal(false)} disabled={saving}>
              Batal
            </Button>
            <Button type="submit" variant="primary" fullWidth disabled={saving}>
              {saving ? (
                <><Loader2 className="w-4 h-4 mr-2 animate-spin" />Menyimpan...</>
              ) : (
                editingProduct ? 'Simpan' : 'Tambah'
              )}
            </Button>
          </div>
        </form>
      </Modal>
    </AppShell>
  )
}
