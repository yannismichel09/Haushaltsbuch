package dto;

import model.TransactionType;

public record TransactionFilterDtoIn(
    Integer transactionId,
    Integer userId,
    Integer categoryId,
    Double amountMin,
    Double amountMax,
    String transactionDateFrom,
    String transactionDateTo,
    TransactionType transactionType,
    String keyword,
    String transactionFrequency) {

}
