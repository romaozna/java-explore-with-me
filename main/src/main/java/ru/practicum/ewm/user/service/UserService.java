package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserDto newUserDto);

    List<UserDto> getAll(List<Long> ids, Pageable pageable);

    void deleteById(Long userId);
}
