package dto;

public record UserUpdateSecurityDtoIn(String email, String oldPassword, String newPassword, String confirmPassword) {

}
