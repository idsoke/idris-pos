package com.warungku.pos.service;

import com.warungku.pos.dto.ProductDto;
import com.warungku.pos.entity.Category;
import com.warungku.pos.entity.Product;
import com.warungku.pos.repository.CategoryRepository;
import com.warungku.pos.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category foodCategory;
    private Category drinksCategory;
    private List<Product> mockProducts;

    @BeforeEach
    void setUp() {
        foodCategory = Category.builder()
                .name("Food")
                .icon("🍔")
                .build();
        setEntityId(foodCategory, 1L);

        drinksCategory = Category.builder()
                .name("Drinks")
                .icon("🥤")
                .build();
        setEntityId(drinksCategory, 2L);

        Product nasiGoreng = Product.builder()
                .name("Nasi Goreng")
                .sku("FOOD001")
                .price(new BigDecimal("25000"))
                .stock(50)
                .minStock(5)
                .category(foodCategory)
                .isActive(true)
                .build();
        setEntityId(nasiGoreng, 1L);

        Product esTeh = Product.builder()
                .name("Es Teh")
                .sku("DRK001")
                .price(new BigDecimal("5000"))
                .stock(100)
                .minStock(10)
                .category(drinksCategory)
                .isActive(true)
                .build();
        setEntityId(esTeh, 2L);

        mockProducts = Arrays.asList(nasiGoreng, esTeh);
    }

    private void setEntityId(Object entity, Long id) {
        try {
            Class<?> clazz = entity.getClass();
            while (clazz != null) {
                try {
                    var field = clazz.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, id);
                    return;
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("getAllProducts")
    class GetAllProducts {

        @Test
        @DisplayName("should return products with categoryId for frontend filtering")
        void shouldReturnProductsWithCategoryId() {
            when(productRepository.findAllActive()).thenReturn(mockProducts);

            List<ProductDto> products = productService.getAllProducts();

            assertThat(products).hasSize(2);

            // CRITICAL: Verify categoryId is returned (not just categoryName)
            ProductDto nasiGoreng = products.get(0);
            assertThat(nasiGoreng.getId()).isEqualTo(1L);
            assertThat(nasiGoreng.getName()).isEqualTo("Nasi Goreng");
            assertThat(nasiGoreng.getCategoryId()).isEqualTo(1L);
            assertThat(nasiGoreng.getCategoryName()).isEqualTo("Food");

            ProductDto esTeh = products.get(1);
            assertThat(esTeh.getId()).isEqualTo(2L);
            assertThat(esTeh.getName()).isEqualTo("Es Teh");
            assertThat(esTeh.getCategoryId()).isEqualTo(2L);
            assertThat(esTeh.getCategoryName()).isEqualTo("Drinks");
        }

        @Test
        @DisplayName("should handle products without category")
        void shouldHandleProductsWithoutCategory() {
            Product uncategorized = Product.builder()
                    .name("Uncategorized")
                    .sku("UNC001")
                    .price(new BigDecimal("10000"))
                    .stock(20)
                    .minStock(5)
                    .category(null)
                    .isActive(true)
                    .build();
            setEntityId(uncategorized, 3L);

            when(productRepository.findAllActive()).thenReturn(List.of(uncategorized));

            List<ProductDto> products = productService.getAllProducts();

            assertThat(products).hasSize(1);
            assertThat(products.get(0).getCategoryId()).isNull();
            assertThat(products.get(0).getCategoryName()).isNull();
        }
    }

    @Nested
    @DisplayName("getProductsByCategory")
    class GetProductsByCategory {

        @Test
        @DisplayName("should filter products by category ID")
        void shouldFilterProductsByCategoryId() {
            Product mieGoreng = Product.builder()
                    .name("Mie Goreng")
                    .sku("FOOD002")
                    .price(new BigDecimal("22000"))
                    .stock(45)
                    .minStock(5)
                    .category(foodCategory)
                    .isActive(true)
                    .build();
            setEntityId(mieGoreng, 3L);

            when(productRepository.findByCategoryId(1L))
                    .thenReturn(Arrays.asList(mockProducts.get(0), mieGoreng));

            List<ProductDto> products = productService.getProductsByCategory(1L);

            assertThat(products).hasSize(2);
            assertThat(products).allMatch(p -> p.getCategoryId().equals(1L));
            assertThat(products.stream().map(ProductDto::getName))
                    .containsExactlyInAnyOrder("Nasi Goreng", "Mie Goreng");
        }

        @Test
        @DisplayName("should return empty list for non-existent category")
        void shouldReturnEmptyListForNonExistentCategory() {
            when(productRepository.findByCategoryId(999L)).thenReturn(List.of());

            List<ProductDto> products = productService.getProductsByCategory(999L);

            assertThat(products).isEmpty();
        }
    }

    @Nested
    @DisplayName("searchProducts")
    class SearchProducts {

        @Test
        @DisplayName("should search products and return categoryId")
        void shouldSearchProductsAndReturnCategoryId() {
            when(productRepository.search("goreng")).thenReturn(List.of(mockProducts.get(0)));

            List<ProductDto> products = productService.searchProducts("goreng");

            assertThat(products).hasSize(1);
            assertThat(products.get(0).getName()).isEqualTo("Nasi Goreng");
            assertThat(products.get(0).getCategoryId()).isEqualTo(1L);
        }
    }
}
