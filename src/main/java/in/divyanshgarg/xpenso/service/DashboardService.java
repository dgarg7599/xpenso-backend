package in.divyanshgarg.xpenso.service;

import in.divyanshgarg.xpenso.dto.ExpenseDTO;
import in.divyanshgarg.xpenso.dto.IncomeDTO;
import in.divyanshgarg.xpenso.dto.RecentTransactionDTO;
import in.divyanshgarg.xpenso.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatestFiveIncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatestFiveExpensesForCurrentUser();

        List<RecentTransactionDTO> recentTransactions = concat(latestIncomes.stream().map(income ->
                RecentTransactionDTO.builder()
                        .id(income.getId())
                        .profileId(profile.getId())
                        .icon(income.getIcon())
                        .name(income.getName())
                        .amount(income.getAmount())
                        .type("income")
                        .date(income.getDate())
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .build()),
                latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .type("expense")
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .build()))
                .sorted((RecentTransactionDTO a, RecentTransactionDTO b) -> {
                  int cmp = b.getDate().compareTo(a.getDate());
                  if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                      return b.getCreatedAt().compareTo(a.getCreatedAt());
                  }
                  return cmp;
                }).collect(Collectors.toList());

        returnValue.put("totalBalance",
                incomeService.getTotalIncomesForCurrentUser()
                        .subtract(expenseService.getTotalExpensesForCurrentUser()));

        returnValue.put("totalIncome",
                incomeService.getTotalIncomesForCurrentUser());

        returnValue.put("totalExpense",
                expenseService.getTotalExpensesForCurrentUser());

        returnValue.put("recentFiveExpenses", latestExpenses);
        returnValue.put("recentFiveIncomes", latestIncomes);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }

}
