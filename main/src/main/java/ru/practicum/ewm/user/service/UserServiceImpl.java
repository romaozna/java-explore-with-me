package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    public static final  String USER_NOT_FOUND_MESSAGE = "User with id=%s was not found";

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(NewUserDto newUserDto) {
        User user = UserMapper.toUser(newUserDto);

        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        return UserMapper
                .toUserDto(userRepository.getAll(ids, pageable));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        Integer count = userRepository.deleteUserById(userId);

        if (count == 0) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
    }
}
