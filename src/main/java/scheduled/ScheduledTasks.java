package scheduled;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dbbackground.DBBackgroundTasks;
import model.Category;

@Component
public class ScheduledTasks {

    @Autowired
    private DBBackgroundTasks dbBackgroundTasks;

    private static final Logger log=LoggerFactory.getLogger(ScheduledTasks.class);

    private List<Category> budgetWarnings = new ArrayList<>();
    private boolean isNegativeBalance = false;

    @Scheduled(fixedRate = 60_000)
    public void checkBudgetLimit() {
        log.info("checkBudgetLimit");
        budgetWarnings = dbBackgroundTasks.backgroundCheckBudgetLimit();
    }

    @Scheduled(fixedRate = 60_000)
    public void checkNetBalance() {
        log.info("checkNetBalance");
        isNegativeBalance = dbBackgroundTasks.backgroundCheckNetBalance();
    }

    public List<Category> getBudgetWarnings() { return budgetWarnings; }
    public boolean getIsNegativeBalance() { return isNegativeBalance; }
}