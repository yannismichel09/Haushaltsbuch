package dto;

public record TransactionCreateDtoIn(int userId, int categoryId, Double transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {

}
