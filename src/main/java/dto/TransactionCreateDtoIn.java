package dto;

import model.TransactionType;

public record TransactionCreateDtoIn(int userId, int categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, String transactionFrequency) {

}
