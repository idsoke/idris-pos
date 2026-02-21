import { api } from './api'

export interface CategoryDto {
  id: number
  name: string
  icon: string
}

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
}

export const categoryService = {
  async getAll(): Promise<CategoryDto[]> {
    try {
      const response = await api.get<ApiResponse<CategoryDto[]>>('/api/categories')
      return response.data
    } catch (error) {
      console.error('Failed to fetch categories:', error)
      return []
    }
  },
}

export default categoryService
