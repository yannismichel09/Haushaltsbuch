package dto;

import model.TransactionType;
import model.TransactionFrequency;

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
    TransactionFrequency transactionFrequency) {

}
