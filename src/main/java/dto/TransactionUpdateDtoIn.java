package dto;

public record TransactionUpdateDtoIn(Integer userId, Integer categoryId, Integer transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {

}
