package com.nuri.moneymanager.controller;

import com.nuri.moneymanager.dto.DtoFilter;
import com.nuri.moneymanager.service.ExpenseService;
import com.nuri.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filter")
public class FilterController {
    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody DtoFilter dtoFilter) {
        LocalDate startDate = dtoFilter.getStartDate() != null ? dtoFilter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = dtoFilter.getEndDate() != null ? dtoFilter.getEndDate() : LocalDate.now();
        String keyword = dtoFilter.getKeyword() != null ? dtoFilter.getKeyword() : "";
        String sortField = dtoFilter.getSortField() != null ? dtoFilter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(dtoFilter.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        if ("income".equalsIgnoreCase(dtoFilter.getType())) {
            return ResponseEntity.ok(incomeService.filterIncomes(startDate, endDate, keyword, sort));
        } else if ("expense".equalsIgnoreCase(dtoFilter.getType())) {
            return ResponseEntity.ok(expenseService.filterExpenses(startDate, endDate, keyword, sort));
        } else {
            return ResponseEntity.badRequest().body("Invalid type specified. Use 'income' or 'expense'.");
        }
    }
}
