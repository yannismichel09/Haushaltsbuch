package dto;

import model.Transaction;
import model.TransactionFrequency;
import model.TransactionType;

public record TransactionDtoOut(int transactionId, int userId, String userName, int categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, TransactionFrequency transactionFrequency) {
    public TransactionDtoOut(Transaction transaction) {
        this(transaction.getTransactionId(), transaction.getUser().getUserId(), transaction.getUser().getUserName(), transaction.getCategory().getCategoryId(), transaction.getTransactionAmount(), transaction.getTransactionDate(), 
             transaction.getTransactionType(), transaction.getTransactionDescription(), transaction.getTransactionFrequency());
    }
}
