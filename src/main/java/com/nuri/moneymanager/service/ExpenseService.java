package com.nuri.moneymanager.service;

import com.nuri.moneymanager.dto.DtoExpense;
import com.nuri.moneymanager.entity.CategoryEntity;
import com.nuri.moneymanager.entity.ExpenseEntity;
import com.nuri.moneymanager.entity.ProfileEntity;
import com.nuri.moneymanager.repository.CategoryRepository;
import com.nuri.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;
    private final ExpenseRepository expenseRepository;

    public DtoExpense addExpense(DtoExpense dtoExpense){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(dtoExpense.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ExpenseEntity expenseEntity = toEntity(dtoExpense, profile, category);
        expenseEntity = expenseRepository.save(expenseEntity);
        return toDto(expenseEntity);
    }

    public List<DtoExpense> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetween(
                profile.getId(), startDate, endDate);
        return list.stream().map(this::toDto).toList();
    }

    public void deleteExpense(Long id) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity expenseEntity = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expenseEntity.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("You do not have permission to delete this expense");
        }
        expenseRepository.delete(expenseEntity);
    }

    public List<DtoExpense> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDto).toList();
    }
    public BigDecimal getTotalExpenseForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<DtoExpense> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(), startDate, endDate, keyword, sort);
        return list.stream().map(this::toDto).toList();
    }

    public List<DtoExpense> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> list = expenseRepository.findByProfileIdAndDate(profileId, date);
        return list.stream().map(this::toDto).toList();
    }

    private ExpenseEntity toEntity(DtoExpense dtoExpense, ProfileEntity profile, CategoryEntity category) {
        return ExpenseEntity.builder()
                .name(dtoExpense.getName())
                .icon(dtoExpense.getIcon())
                .amount(dtoExpense.getAmount())
                .date(dtoExpense.getDate())
                .profile(profile)
                .category(category)
                .build();
    }
    private DtoExpense toDto(ExpenseEntity expenseEntity) {
        return DtoExpense.builder()
                .id(expenseEntity.getId())
                .name(expenseEntity.getName())
                .icon(expenseEntity.getIcon())
                .categoryId(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getId() : null)
                .categoryName(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getName() : "N/A")
                .amount(expenseEntity.getAmount())
                .date(expenseEntity.getDate())
                .createdAt(expenseEntity.getCreatedAt())
                .updatedAt(expenseEntity.getUpdatedAt())
                .build();
    }
}
