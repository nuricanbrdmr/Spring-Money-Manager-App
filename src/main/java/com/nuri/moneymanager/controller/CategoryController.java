package com.nuri.moneymanager.controller;

import com.nuri.moneymanager.dto.DtoCategory;
import com.nuri.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<DtoCategory> saveCategory(@RequestBody DtoCategory dtoCategory) {
        DtoCategory savedCategory = categoryService.saveCategory(dtoCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    @GetMapping
    public ResponseEntity<List<DtoCategory>> getCategories(){
        List<DtoCategory> categories = categoryService.getCategoriesForCurrentUser();
        return ResponseEntity.ok(categories);
    }
    @GetMapping("/{type}")
    public ResponseEntity<List<DtoCategory>> getCategoriesByType(@PathVariable String type) {
        List<DtoCategory> categories = categoryService.getCategoriesByTypeForCurrentUser(type);
        return ResponseEntity.ok(categories);
    }
    @PostMapping("/{id}")
    public ResponseEntity<DtoCategory> updateCategory(@PathVariable Long id, @RequestBody DtoCategory dtoCategory) {
        DtoCategory updatedCategory = categoryService.updateCategory(id, dtoCategory);
        return ResponseEntity.ok(updatedCategory);
    }
}
