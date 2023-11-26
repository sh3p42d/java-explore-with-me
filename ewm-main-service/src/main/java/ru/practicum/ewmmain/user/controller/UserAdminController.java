package ru.practicum.ewmmain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmmain.config.exceptions.ErrorMessages;
import ru.practicum.ewmmain.user.dto.NewUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
public class UserAdminController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserDto newUserDto) {
        return service.addUser(newUserDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(defaultValue = "") List<Long> ids,
                                  @PositiveOrZero(message = ErrorMessages.FROM_ERROR_MESSAGE) @RequestParam(defaultValue = "0") int from,
                                  @Positive(message = ErrorMessages.SIZE_ERROR_MESSAGE) @RequestParam(defaultValue = "10") int size) {
        return service.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}
