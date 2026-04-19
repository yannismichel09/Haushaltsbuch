package dbbackground;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbaccess.DBAccessTransaction;
import jakarta.transaction.Transactional;
import model.Transaction;
import model.TransactionFrequency;

@Service
@Transactional
public class DBBackgroundTasks {
    @Autowired
    public DBAccessTransaction dbAccessTransaction;

    public void processRecurringTransactions(TransactionFrequency frequency) {
        List<Transaction> transactions = dbAccessTransaction.getTransactionsByFrequency(frequency);

        LocalDate now = LocalDate.now();

        for (Transaction t : transactions) {
            LocalDate transactionDate = LocalDate.parse(t.getTransactionDate());
            if (t.getTransactionFrequency() == TransactionFrequency.weekly) {
                if (now.getDayOfWeek() == transactionDate.getDayOfWeek()) {
                    dbAccessTransaction.createTransaction(
                        t.getUser().getUserId(),
                        t.getCategory().getCategoryId(),
                        t.getTransactionAmount(),
                        LocalDate.now().toString(), 
                        t.getTransactionType(),
                        t.getTransactionDescription(),
                        TransactionFrequency.once
                    );
                }
            } else if (t.getTransactionFrequency() == TransactionFrequency.monthly) {
                int dayOfMonth = Math.min(now.lengthOfMonth(), transactionDate.lengthOfMonth());
                if (now.getDayOfMonth() == dayOfMonth) {
                    dbAccessTransaction.createTransaction(
                        t.getUser().getUserId(),
                        t.getCategory().getCategoryId(),
                        t.getTransactionAmount(),
                        LocalDate.now().toString(), 
                        t.getTransactionType(),
                        t.getTransactionDescription(),
                        TransactionFrequency.once
                    );
                }
            } else if (t.getTransactionFrequency() == TransactionFrequency.yearly) {
                int dayOfYear = transactionDate.getDayOfYear();
                int dayOfMonth = transactionDate.getDayOfMonth();
                if (now.getDayOfYear() == dayOfYear && now.getDayOfMonth() == dayOfMonth) {
                    dbAccessTransaction.createTransaction(
                        t.getUser().getUserId(),
                        t.getCategory().getCategoryId(),
                        t.getTransactionAmount(),
                        LocalDate.now().toString(), 
                        t.getTransactionType(),
                        t.getTransactionDescription(),
                        TransactionFrequency.once
                    );
                }
            }
        }
    }
}
