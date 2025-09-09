package com.nuri.moneymanager.service;

import com.nuri.moneymanager.dto.DtoExpense;
import com.nuri.moneymanager.entity.ProfileEntity;
import com.nuri.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;
    @Scheduled(cron = "0 0 22 * * *", zone = "Europe/Istanbul")
    public void sendDailyIncomeExpenseReminder() {
        try {
            log.info("Job started: sendDailyIncomeExpenseReminder()");
            List<ProfileEntity> profiles = profileRepository.findAll();
            for (ProfileEntity profile: profiles){
                String body = "Hi " + profile.getFullName() + ", <br><br>"
                        + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br>"
                        + "<a href=\"" + frontendUrl + "\">Click here to go to Money Manager</a>.<br><br>"
                        + "<br><br>Best regards,<br> Money Manager Team";
                emailService.sendEmail(profile.getEmail(), "Daily Income and Expense Reminder", body);
            }
            log.info("Job completed: sendDailyIncomeExpenseReminder()");
        } catch (Exception e) {
            log.error("Failed to send daily income and expense reminder", e);
        }
    }

   @Scheduled(cron = "0 0 23 * * *", zone = "Europe/Istanbul")
    public void sendDailyExpenseSummary() {
        try {
            log.info("Job started: sendDailyExpenseSummary()");
            List<ProfileEntity> profiles = profileRepository.findAll();
            for (ProfileEntity profile : profiles) {
                List<DtoExpense> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
                if (todaysExpenses.isEmpty()) {
                    StringBuilder table = new StringBuilder();
                    table.append("<table style='border-collapse: collapse; width: 100%;'>")
                         .append("<tr style='background-color:#f2f2f2;'><th style='border:1px solid #ddd;padding:8px;'>#</th><th style='border:1px solid #ddd;padding:8px;'>Date</th><th style='border:1px solid #ddd;padding:8px;'>Category</th><th style='border:1px solid #ddd;padding:8px;'>Amount</th></tr>");
                    int i = 1;
                    for (DtoExpense expense : todaysExpenses) {
                        table.append("<tr>")
                             .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                             .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getDate()).append("</td>")
                             .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>")
                             .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getAmount()).append("</td>")
                             .append("</tr>");
                        i++;
                    }
                    table.append("</table>");
                    String body = "Hi " + profile.getFullName() + ", <br/><br/> Here is a summary of your expenses for today:<br/><br/>"
                            + table
                            + "<br><br>Best regards,<br> Money Manager Team";
                    emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary", body);
                }
            }
            log.info("Job completed: sendDailyExpenseSummary()");
        } catch (Exception e) {
            log.error("Failed to send daily expense summary", e);
        }
    }
}
