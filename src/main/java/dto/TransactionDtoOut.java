package dto;

import model.Transaction;
import model.TransactionType;

public record TransactionDtoOut(int userId, int categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, String transactionFrequency) {
    public TransactionDtoOut(Transaction transaction) {
        this(transaction.getUser().getUserId(), transaction.getCategory().getCategoryId(), transaction.getTransactionAmount(), transaction.getTransactionDate(), 
             transaction.getTransactionType(), transaction.getTransactionDescription(), transaction.getTransactionFrequency());
    }
}
