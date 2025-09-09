package com.nuri.moneymanager.service;

import com.nuri.moneymanager.dto.DtoCategory;
import com.nuri.moneymanager.entity.CategoryEntity;
import com.nuri.moneymanager.entity.ProfileEntity;
import com.nuri.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Save a new category
    public DtoCategory saveCategory(DtoCategory dtoCategory) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile not found or not authenticated");
        }

        if (categoryRepository.existsByNameAndProfileId(dtoCategory.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists for the profile");
        }

        CategoryEntity newCategory = toEntity(dtoCategory, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDto(newCategory);
    }

    //Get category for current profile
    public List<DtoCategory> getCategoriesForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        if (profileEntity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile not found or not authenticated");
        }
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profileEntity.getId());
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

    public List<DtoCategory> getCategoriesByTypeForCurrentUser(String type) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        if (profileEntity == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile not found or not authenticated");
        }
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type,profileEntity.getId());
        return categories.stream()
                .map(this::toDto)
                .toList();
    }

    public DtoCategory updateCategory(Long id, DtoCategory dtoCategory) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Profile not found or not authenticated");
        }

        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(id, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible for this profile"));

        if (!existingCategory.getName().equals(dtoCategory.getName()) &&
                categoryRepository.existsByNameAndProfileId(dtoCategory.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exists for the profile");
        }

        existingCategory.setName(dtoCategory.getName());
        existingCategory.setType(dtoCategory.getType());
        existingCategory.setIcon(dtoCategory.getIcon());

        CategoryEntity updatedCategory = categoryRepository.save(existingCategory);
        return toDto(updatedCategory);
    }

    private CategoryEntity toEntity(DtoCategory dtoCategory, ProfileEntity profile) {
        return CategoryEntity.builder()
                .name(dtoCategory.getName())
                .type(dtoCategory.getType())
                .icon(dtoCategory.getIcon())
                .profile(profile)
                .build();
    }
    private DtoCategory toDto(CategoryEntity categoryEntity) {
        return DtoCategory.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .name(categoryEntity.getName())
                .type(categoryEntity.getType())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .build();
    }
}
