package dto;

public record TransactionFilterDtoIn(
    Integer userId,
    Integer categoryId,
    Integer amountMin,
    Integer amountMax,
    String transactionDateFrom,
    String transactionDateTo,
    String transactionType,
    String keyword,
    String transactionFrequency) {

}
