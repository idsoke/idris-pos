package com.warungku.pos.controller;

import com.warungku.pos.dto.ApiResponse;
import com.warungku.pos.entity.Category;
import com.warungku.pos.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCategories() {
        List<Map<String, Object>> categories = categoryRepository.findAll().stream()
                .map(cat -> Map.<String, Object>of(
                        "id", cat.getId(),
                        "name", cat.getName(),
                        "icon", cat.getIcon() != null ? cat.getIcon() : "📦"
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}
