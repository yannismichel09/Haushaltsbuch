package dto;

public record CategoryFilterDtoIn( 
    Integer categoryId,
    String keyword,
    String categoryColor,
    Double amountMin,
    Double amountMax) {

}
