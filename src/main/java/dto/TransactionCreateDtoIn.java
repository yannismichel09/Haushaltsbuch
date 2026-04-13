package dto;

import model.TransactionType;
import model.TransactionFrequency;

public record TransactionCreateDtoIn(int userId, int categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, TransactionFrequency transactionFrequency) {

}
