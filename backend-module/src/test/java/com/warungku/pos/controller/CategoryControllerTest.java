package com.warungku.pos.controller;

import com.warungku.pos.entity.Category;
import com.warungku.pos.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryController categoryController;

    private List<Category> mockCategories;

    @BeforeEach
    void setUp() {
        Category food = Category.builder()
                .name("Food")
                .icon("🍔")
                .sortOrder(1)
                .isActive(true)
                .build();
        // Simulate ID set by JPA
        setId(food, 1L);

        Category drinks = Category.builder()
                .name("Drinks")
                .icon("🥤")
                .sortOrder(2)
                .isActive(true)
                .build();
        setId(drinks, 2L);

        Category snacks = Category.builder()
                .name("Snacks")
                .icon("🍿")
                .sortOrder(3)
                .isActive(true)
                .build();
        setId(snacks, 3L);

        mockCategories = Arrays.asList(food, drinks, snacks);
    }

    private void setId(Category category, Long id) {
        try {
            var field = category.getClass().getSuperclass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(category, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("GET /api/categories should return all categories with numeric IDs")
    void getAllCategories_ShouldReturnCategoriesWithNumericIds() {
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        var response = categoryController.getAllCategories();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isSuccess()).isTrue();
        
        List<Map<String, Object>> categories = response.getBody().getData();
        assertThat(categories).hasSize(3);

        // Verify category IDs are numeric (Long type)
        assertThat(categories.get(0).get("id")).isEqualTo(1L);
        assertThat(categories.get(1).get("id")).isEqualTo(2L);
        assertThat(categories.get(2).get("id")).isEqualTo(3L);

        // Verify names
        assertThat(categories.get(0).get("name")).isEqualTo("Food");
        assertThat(categories.get(1).get("name")).isEqualTo("Drinks");
        assertThat(categories.get(2).get("name")).isEqualTo("Snacks");

        // Verify icons
        assertThat(categories.get(0).get("icon")).isEqualTo("🍔");
        assertThat(categories.get(1).get("icon")).isEqualTo("🥤");
        assertThat(categories.get(2).get("icon")).isEqualTo("🍿");
    }

    @Test
    @DisplayName("GET /api/categories should return default icon for null icon")
    void getAllCategories_ShouldReturnDefaultIconForNullIcon() {
        Category noIcon = Category.builder()
                .name("Others")
                .icon(null)
                .sortOrder(4)
                .isActive(true)
                .build();
        setId(noIcon, 4L);

        when(categoryRepository.findAll()).thenReturn(List.of(noIcon));

        var response = categoryController.getAllCategories();

        List<Map<String, Object>> categories = response.getBody().getData();
        assertThat(categories.get(0).get("icon")).isEqualTo("📦");
    }

    @Test
    @DisplayName("GET /api/categories should return empty list when no categories")
    void getAllCategories_ShouldReturnEmptyListWhenNoCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of());

        var response = categoryController.getAllCategories();

        assertThat(response.getBody().getData()).isEmpty();
    }
}
