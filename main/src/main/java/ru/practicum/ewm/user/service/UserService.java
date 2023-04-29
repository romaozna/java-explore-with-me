package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUserById(Long userId);
}
