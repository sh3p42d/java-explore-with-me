package ru.practicum.ewmmain.user.mapper;

import ru.practicum.ewmmain.user.dto.NewUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.dto.UserMinDto;
import ru.practicum.ewmmain.user.model.User;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public static User toUser(NewUserDto newUserDto) {
        return User.builder()
                .name(newUserDto.getName())
                .email(newUserDto.getEmail())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public static UserMinDto toUserMinDto(User user) {
        return UserMinDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public static UserMinDto toUserMinDto(UserDto user) {
        return UserMinDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
