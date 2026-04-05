package dto;

import model.Transaction;

public record TransactionDtoOut(int userId, int categoryId, Integer transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {
    public TransactionDtoOut(Transaction transaction) {
        this(transaction.getUser().getUserId(), transaction.getCategory().getCategoryId(), transaction.getTransactionAmount(), transaction.getTransactionDate(), 
             transaction.getTransactionType(), transaction.getTransactionDescription(), transaction.getTransactionFrequency());
    }
}
