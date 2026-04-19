package dto;

import model.User;

public record UserDtoOut(int userId, String username, String email, byte[] profilePicture) {
    public UserDtoOut(User user) {
        this(user.getUserId(), user.getUserName(), user.getUserEmail(), user.getUserProfilePicture());
    }
}
