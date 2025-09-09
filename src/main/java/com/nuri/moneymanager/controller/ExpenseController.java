package com.nuri.moneymanager.controller;

import com.nuri.moneymanager.dto.DtoExpense;
import com.nuri.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<DtoExpense> addExpense(@RequestBody DtoExpense dtoExpense) {
        DtoExpense createdExpense = expenseService.addExpense(dtoExpense);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExpense);
    }
    @GetMapping
    public ResponseEntity<List<DtoExpense>> getExpenses(){
        List<DtoExpense> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
