import { api } from './api'
import { Product } from '@/store/products'

export interface ProductDto {
  id: number
  name: string
  description?: string
  sku: string
  price: number
  stock: number
  minStock?: number
  image?: string
  barcode?: string
  categoryId?: number
  categoryName?: string
  isActive: boolean
  lowStock?: boolean
}

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
}

// Convert API response to frontend Product type
function toProduct(dto: ProductDto): Product {
  return {
    id: dto.id.toString(),
    name: dto.name,
    price: dto.price,
    category: dto.categoryId?.toString() || '',
    sku: dto.sku,
    stock: dto.stock,
    isActive: dto.isActive,
    image: dto.image || undefined,
  }
}

// Convert frontend Product to API request
function toProductDto(product: Partial<Product> & { categoryId?: number }): Partial<ProductDto> {
  return {
    name: product.name,
    sku: product.sku,
    price: product.price,
    stock: product.stock,
    image: product.image || undefined,
    categoryId: product.categoryId,
    isActive: product.isActive,
  }
}

export const productService = {
  async getAll(): Promise<Product[]> {
    const response = await api.get<ApiResponse<ProductDto[]>>('/api/products/all')
    return response.data.map(toProduct)
  },

  async getById(id: string): Promise<Product> {
    const response = await api.get<ApiResponse<ProductDto>>(`/api/products/${id}`)
    return toProduct(response.data)
  },

  async search(query: string): Promise<Product[]> {
    const response = await api.get<ApiResponse<ProductDto[]>>('/api/products/search', { q: query })
    return response.data.map(toProduct)
  },

  async create(product: Omit<Product, 'id'> & { categoryId?: number }): Promise<Product> {
    const response = await api.post<ApiResponse<ProductDto>>('/api/products', toProductDto(product))
    return toProduct(response.data)
  },

  async update(id: string, product: Partial<Product> & { categoryId?: number }): Promise<Product> {
    const response = await api.put<ApiResponse<ProductDto>>(`/api/products/${id}`, toProductDto(product))
    return toProduct(response.data)
  },

  async delete(id: string): Promise<void> {
    await api.delete(`/api/products/${id}`)
  },

  async updateStock(id: string, quantity: number): Promise<Product> {
    const response = await api.patch<ApiResponse<ProductDto>>(`/api/products/${id}/stock`, { quantity })
    return toProduct(response.data)
  },
}

export default productService
