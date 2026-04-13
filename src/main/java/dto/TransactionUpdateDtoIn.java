package dto;

import model.TransactionType;

public record TransactionUpdateDtoIn(Integer userId, Integer categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, String transactionFrequency) {

}
