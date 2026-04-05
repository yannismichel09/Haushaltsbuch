package dto;

public record TransactionCreateDtoIn(int userId, int categoryId, Integer transactionAmount, String transactionDate, String transactionType, String transactionDescription, String transactionFrequency) {

}
