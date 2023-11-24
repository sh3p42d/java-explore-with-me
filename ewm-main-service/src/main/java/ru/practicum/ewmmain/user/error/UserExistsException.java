package ru.practicum.ewmmain.user.error;

public class UserExistsException extends RuntimeException {
    public UserExistsException(String email) {
        super(String.format("User c email=%s уже существует.", email));
    }
}
