package dto;

import model.TransactionType;
import model.TransactionFrequency;

public record TransactionUpdateDtoIn(Integer userId, Integer categoryId, Double transactionAmount, String transactionDate, TransactionType transactionType, String transactionDescription, TransactionFrequency transactionFrequency) {

}
