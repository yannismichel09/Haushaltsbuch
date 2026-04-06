package dto;

public record TransactionUpdateDtoIn(Integer userId, Integer categoryId, Double transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {

}
