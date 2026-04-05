package dto;

import model.User;

public record UserDtoOut(String username, String email, byte[] profilePicture) {
    public UserDtoOut(User user) {
        this(user.getUserName(), user.getUserEmail(), user.getUserProfilePicture());
    }
}
