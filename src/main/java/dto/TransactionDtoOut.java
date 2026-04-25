package dto;

import model.Transaction;
import model.TransactionFrequency;
import model.TransactionType;

public record TransactionDtoOut(int transactionId, Integer userId, String userName, Integer categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, TransactionFrequency transactionFrequency) {
    public TransactionDtoOut(Transaction transaction) {
        this(
            transaction.getTransactionId(),
            transaction.getUser() != null ? transaction.getUser().getUserId() : null,
            transaction.getUser() != null ? transaction.getUser().getUserName() : null,
            transaction.getCategory() != null ? transaction.getCategory().getCategoryId() : null,
            transaction.getTransactionAmount(),
            transaction.getTransactionDate(),
            transaction.getTransactionType(),
            transaction.getTransactionDescription(),
            transaction.getTransactionFrequency()
        );
    }
}
