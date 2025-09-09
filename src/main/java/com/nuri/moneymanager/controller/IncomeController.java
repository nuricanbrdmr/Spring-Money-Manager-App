package com.nuri.moneymanager.controller;

import com.nuri.moneymanager.dto.DtoIncome;
import com.nuri.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<DtoIncome> addIncome(@RequestBody DtoIncome dtoIncome) {
        DtoIncome createdIncome = incomeService.addIncome(dtoIncome);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdIncome);
    }
    @GetMapping
    public ResponseEntity<List<DtoIncome>> getIncomes(){
        List<DtoIncome> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIncome(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.noContent().build();
    }
}
