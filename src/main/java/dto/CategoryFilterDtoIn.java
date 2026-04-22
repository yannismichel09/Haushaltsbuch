package dto;

import java.util.List;

public record CategoryFilterDtoIn(
    Integer categoryId,
    String keyword,
    Double amountMin,
    Double amountMax,
    List<String> categoryColors) {

}
