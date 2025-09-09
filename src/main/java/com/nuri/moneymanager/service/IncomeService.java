package com.nuri.moneymanager.service;

import com.nuri.moneymanager.dto.DtoExpense;
import com.nuri.moneymanager.dto.DtoIncome;
import com.nuri.moneymanager.entity.CategoryEntity;
import com.nuri.moneymanager.entity.ExpenseEntity;
import com.nuri.moneymanager.entity.IncomeEntity;
import com.nuri.moneymanager.entity.ProfileEntity;
import com.nuri.moneymanager.repository.CategoryRepository;
import com.nuri.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final IncomeRepository incomeRepository;

    public DtoIncome addIncome(DtoIncome dtoIncome) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dtoIncome.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        IncomeEntity incomeEntity = toEntity(dtoIncome, profile, category);
        incomeEntity = incomeRepository.save(incomeEntity);
        return toDto(incomeEntity);
    }

    public List<DtoIncome> getCurrentMonthIncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetween(
                profile.getId(), startDate, endDate);
        return list.stream().map(this::toDto).toList();
    }

    public void deleteIncome(Long id) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity incomeEntity = incomeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!incomeEntity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("You do not have permission to delete this income");
        }
        incomeRepository.delete(incomeEntity);
    }

    public List<DtoIncome> getLatest5IncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }

    public BigDecimal getTotalIncomeForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = incomeRepository.findTotalByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<DtoIncome> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDto).toList();
    }

    private IncomeEntity toEntity(DtoIncome dtoIncome, ProfileEntity profile, CategoryEntity category) {
        return IncomeEntity.builder()
                .name(dtoIncome.getName())
                .icon(dtoIncome.getIcon())
                .amount(dtoIncome.getAmount())
                .date(dtoIncome.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
    private DtoIncome toDto(IncomeEntity incomeEntity) {
        return DtoIncome.builder()
                .id(incomeEntity.getId())
                .name(incomeEntity.getName())
                .icon(incomeEntity.getIcon())
                .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : "N/A")
                .amount(incomeEntity.getAmount())
                .date(incomeEntity.getDate())
                .createdAt(incomeEntity.getCreatedAt())
                .updatedAt(incomeEntity.getUpdatedAt())
                .build();
    }
}
