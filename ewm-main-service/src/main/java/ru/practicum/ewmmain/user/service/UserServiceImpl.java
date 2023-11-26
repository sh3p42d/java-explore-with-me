package ru.practicum.ewmmain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.user.dto.NewUserDto;
import ru.practicum.ewmmain.user.dto.UserDto;
import ru.practicum.ewmmain.user.error.UserExistsException;
import ru.practicum.ewmmain.user.error.UserNotFoundException;
import ru.practicum.ewmmain.user.mapper.UserMapper;
import ru.practicum.ewmmain.user.model.User;
import ru.practicum.ewmmain.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserDto newUserDto) {
        User userToAdd = UserMapper.toUser(newUserDto);

        User userByEmailAndName = userRepository.findByEmailAndName(userToAdd.getEmail(), userToAdd.getName());
        if (userByEmailAndName != null) {
            throw new UserExistsException(newUserDto.getEmail());
        }
        User userSaved = userRepository.save(userToAdd);
        return UserMapper.toUserDto(userSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<User> users;
        if (ids.size() != 0) {
            users = userRepository.findAllByIdIn(ids, page).getContent();
        } else {
            users = userRepository.findAll(page).getContent();
        }

        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(long userId) {
        User userToDelete = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId));
        userRepository.deleteById(userId);
    }
}
