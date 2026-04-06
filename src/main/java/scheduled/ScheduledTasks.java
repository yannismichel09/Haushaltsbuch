package scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dbbackground.DBBackgroundTasks;


@Component
public class ScheduledTasks {

    @Autowired
    private DBBackgroundTasks dbBackgroundTasks;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(cron = "0 0 0 * * MON") 
    public void processWeeklyTransactions() {
        log.info("Verarbeite wöchentliche Transaktionen");
        dbBackgroundTasks.processRecurringTransactions("weekly");
    }

    @Scheduled(cron = "0 0 0 1 * *") 
    public void processMonthlyTransactions() {
        log.info("Verarbeite monatliche Transaktionen");
        dbBackgroundTasks.processRecurringTransactions("monthly");
    }

    @Scheduled(cron = "0 0 0 1 1 *") 
    public void processYearlyTransactions() {
        log.info("Verarbeite jährliche Transaktionen");
        dbBackgroundTasks.processRecurringTransactions("yearly");
    }
}