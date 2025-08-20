package in.divyanshgarg.xpenso.controller;

import in.divyanshgarg.xpenso.entity.ProfileEntity;
import in.divyanshgarg.xpenso.service.*;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final ExcelService excelService;
    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final EmailService emailService;
    private final ProfileService profileService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException, MessagingException {
        ProfileEntity profile =  profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(outputStream, incomeService.getCurrentMonthIncomesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Income Excel Report",
                "Please find attached your income report.",
                outputStream.toByteArray(),
                "income.xlsx");
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException, MessagingException {
        ProfileEntity profile =  profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(outputStream, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(profile.getEmail(),
                "Your Expense Excel Report",
                "Please find attached your expense report.",
                outputStream.toByteArray(),
                "expense.xlsx");
        return ResponseEntity.ok().body(null);
    }

}
