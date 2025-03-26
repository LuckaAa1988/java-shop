package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.entity.User;
import ru.practicum.response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toDto(User user);
}
