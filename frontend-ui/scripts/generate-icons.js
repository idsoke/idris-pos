const fs = require('fs')
const path = require('path')

const sizes = [72, 96, 128, 144, 152, 192, 384, 512]
const iconsDir = path.join(__dirname, '..', 'public', 'icons')

// Simple PNG generator (1x1 orange pixel, scaled)
function createSimplePNG(size) {
  // PNG header + IHDR + IDAT + IEND for a simple orange square
  // This creates a minimal valid PNG with orange color (#f97316)
  const png = Buffer.alloc(67 + size * size * 4)
  
  // For simplicity, create SVG instead which is more manageable
  return null
}

// Create SVG icons (works as PWA icons in modern browsers)
function createSVG(size) {
  const rx = Math.round(size * 0.15)
  const fontSize = Math.round(size * 0.4)
  return `<svg xmlns="http://www.w3.org/2000/svg" width="${size}" height="${size}" viewBox="0 0 ${size} ${size}">
  <rect width="${size}" height="${size}" rx="${rx}" fill="#f97316"/>
  <text x="50%" y="55%" font-family="Arial, sans-serif" font-size="${fontSize}" font-weight="bold" fill="white" text-anchor="middle" dominant-baseline="middle">W</text>
</svg>`
}

// Ensure icons directory exists
if (!fs.existsSync(iconsDir)) {
  fs.mkdirSync(iconsDir, { recursive: true })
}

// Generate SVG icons
sizes.forEach(size => {
  const svg = createSVG(size)
  const filename = `icon-${size}x${size}.svg`
  fs.writeFileSync(path.join(iconsDir, filename), svg)
  console.log(`Created ${filename}`)
})

console.log('\nIcons generated! Update manifest.json to use .svg extension.')
