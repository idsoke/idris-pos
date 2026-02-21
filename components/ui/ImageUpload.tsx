'use client'

import { useState, useRef, ChangeEvent, DragEvent } from 'react'
import { Camera, X, ImagePlus, Upload } from 'lucide-react'
import { cn } from '@/utils'

interface ImageUploadProps {
  value?: string | null
  onChange?: (dataUrl: string | null, file?: File | null) => void
  onRemove?: () => void
  className?: string
  size?: 'sm' | 'md' | 'lg'
  placeholder?: string
  accept?: string
  maxSize?: number // MB
}

export function ImageUpload({
  value,
  onChange,
  onRemove,
  className,
  size = 'md',
  placeholder = 'Tambah Foto',
  accept = 'image/*',
  maxSize = 5,
}: ImageUploadProps) {
  const inputRef = useRef<HTMLInputElement>(null)
  const [isDragging, setIsDragging] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const sizeClasses = {
    sm: 'w-20 h-20',
    md: 'w-32 h-32',
    lg: 'w-40 h-40',
  }

  const handleClick = () => {
    inputRef.current?.click()
  }

  const processFile = (file: File | undefined) => {
    setError(null)

    if (!file) return

    // Validate file type
    if (!file.type.startsWith('image/')) {
      setError('File harus berupa gambar')
      return
    }

    // Validate file size
    if (file.size > maxSize * 1024 * 1024) {
      setError(`Ukuran file maksimal ${maxSize}MB`)
      return
    }

    // Create preview URL
    const reader = new FileReader()
    reader.onload = (e) => {
      onChange?.(e.target?.result as string, file)
    }
    reader.readAsDataURL(file)
  }

  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    processFile(file)
    e.target.value = ''
  }

  const handleDragOver = (e: DragEvent) => {
    e.preventDefault()
    setIsDragging(true)
  }

  const handleDragLeave = (e: DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
  }

  const handleDrop = (e: DragEvent) => {
    e.preventDefault()
    setIsDragging(false)
    const file = e.dataTransfer.files?.[0]
    processFile(file)
  }

  const handleRemove = (e: React.MouseEvent) => {
    e.stopPropagation()
    onRemove?.()
    onChange?.(null, null)
  }

  return (
    <div className={cn('flex flex-col items-center gap-2', className)}>
      <div
        onClick={handleClick}
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        className={cn(
          sizeClasses[size],
          'relative cursor-pointer',
          'bg-gray-100 dark:bg-gray-700',
          'rounded-3xl overflow-hidden',
          'border-2 border-dashed',
          'transition-all duration-200',
          isDragging
            ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20 scale-105'
            : value
              ? 'border-transparent'
              : 'border-gray-300 dark:border-gray-600 hover:border-primary-400 dark:hover:border-primary-500',
          'group'
        )}
      >
        {value ? (
          <>
            <img
              src={value}
              alt="Preview"
              className="w-full h-full object-cover"
            />
            <div className="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
              <button
                type="button"
                onClick={handleClick}
                className="w-10 h-10 bg-white/20 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-white/30 transition-colors"
              >
                <Camera className="w-5 h-5" />
              </button>
              <button
                type="button"
                onClick={handleRemove}
                className="w-10 h-10 bg-red-500/80 backdrop-blur-sm rounded-full flex items-center justify-center text-white hover:bg-red-600 transition-colors"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
          </>
        ) : (
          <div className="w-full h-full flex flex-col items-center justify-center gap-2 text-gray-400">
            {isDragging ? (
              <>
                <Upload className="w-8 h-8 text-primary-500" />
                <span className="text-xs text-primary-500">Drop here</span>
              </>
            ) : (
              <>
                <ImagePlus className="w-8 h-8" />
                <span className="text-xs text-center px-2">{placeholder}</span>
              </>
            )}
          </div>
        )}

        <input
          ref={inputRef}
          type="file"
          accept={accept}
          onChange={handleFileChange}
          className="hidden"
        />
      </div>

      {error && (
        <p className="text-xs text-red-500 text-center">{error}</p>
      )}

      {!value && (
        <p className="text-xs text-gray-400 text-center">
          JPG, PNG max {maxSize}MB
        </p>
      )}
    </div>
  )
}
