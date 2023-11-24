package ru.practicum.ewmmain.user.service;

import ru.practicum.ewmmain.user.dto.NewUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserDto newUserDto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
