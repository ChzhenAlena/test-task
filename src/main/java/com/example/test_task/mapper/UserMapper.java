package com.example.test_task.mapper;

import com.example.test_task.dto.user.UserDto;
import com.example.test_task.dto.user.UserFullDto;
import com.example.test_task.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AccountMapper.class, PhoneDataMapper.class, EmailDataMapper.class})
public interface UserMapper {

  UserDto toDto(User entity);

  UserFullDto toFullDto(User entity);

}

