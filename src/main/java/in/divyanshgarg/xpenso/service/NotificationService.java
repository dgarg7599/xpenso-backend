package in.divyanshgarg.xpenso.service;

import in.divyanshgarg.xpenso.dto.ExpenseDTO;
import in.divyanshgarg.xpenso.entity.ProfileEntity;
import in.divyanshgarg.xpenso.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${xpenso.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullName() + ",<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in your Xpenso App,<br><br>"
                    + "<a href="+frontendUrl+" style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:#fff;text-decoration:none;border-radius:5px;font-weight:bold; '>Go to Xpenso</a>"
                    + "<br><br>Best regards,<br>Xpenso Team";
            emailService.sendEmail(profile.getEmail(), "Daily reminder: Add your incomes and expenses", body);
        }
        log.info("Job finished: sendDailyIncomeExpenseReminder()");
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if (!todaysExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse: collapse;width: 100%;'>");
                table.append("<tr style='background-color: #f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Date</th>")
                        .append("</tr>");
                int i = 1;
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName() != null ? expense.getName() : "N/A").append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount() != null ? expense.getAmount() : "N/A").append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getCategoryId() != null && expense.getCategoryName() != null ? expense.getCategoryName() : "N/A")
                            .append("</td>");
                    table.append("<td style='border:1px solid #ddd;padding:8px;'>")
                            .append(expense.getDate() != null ? expense.getDate().toString() : "N/A")
                            .append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body = "Hi " + profile.getFullName() + ",<br/><br/>Here is a summary of your expenses for today: <br/><br/>" + table + "<br/><br/>Best regards,<br/><br/>Xpenso Team";
                emailService.sendEmail(profile.getEmail(), "Your daily Expense summary", body);
            }
        }
        log.info("Job finished: sendDailyExpenseSummary()");
    }


}
