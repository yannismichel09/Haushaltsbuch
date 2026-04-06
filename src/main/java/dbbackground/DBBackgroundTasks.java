package dbbackground;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dbaccess.DBAccessTransaction;
import jakarta.transaction.Transactional;
import model.Transaction;

@Service
@Transactional
public class DBBackgroundTasks {
    @Autowired
    public DBAccessTransaction dbAccessTransaction;

    public void processRecurringTransactions(String frequency) {
    List<Transaction> transactions = dbAccessTransaction.getTransactionsByFrequency(frequency);
    
    for (Transaction t : transactions) {
        dbAccessTransaction.createTransaction(
            t.getUser().getUserId(),
            t.getCategory().getCategoryId(),
            t.getTransactionAmount(),
            LocalDate.now().toString(), 
            t.getTransactionType(),
            t.getTransactionDescription(),
            t.getTransactionFrequency()
        );
    }
}
}
