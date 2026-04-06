package scheduled;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dbbackground.DBBackgroundTasks;
import model.Category;

@Component
public class ScheduledTasks {

    @Autowired
    private DBBackgroundTasks dbBackgroundTasks;

    private List<Category> budgetWarnings = new ArrayList<>();
    private boolean isNegativeBalance = false;

    @Scheduled(fixedRate = 60_000)
    public void checkBudgetLimit() {
        budgetWarnings = dbBackgroundTasks.backgroundCheckBudgetLimit();
    }

    @Scheduled(fixedRate = 60_000)
    public void checkNetBalance() {
        isNegativeBalance = dbBackgroundTasks.backgroundCheckNetBalance();
    }

    public List<Category> getBudgetWarnings() { return budgetWarnings; }
    public boolean getIsNegativeBalance() { return isNegativeBalance; }
}