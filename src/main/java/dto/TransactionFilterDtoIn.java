package dto;

public record TransactionFilterDtoIn(
    Integer transactionId,
    Integer userId,
    Integer categoryId,
    Double amountMin,
    Double amountMax,
    String transactionDateFrom,
    String transactionDateTo,
    String transactionType,
    String keyword,
    String transactionFrequency) {

}
