package com.github.dperezcabrera.test.architecture.auth.services;

import com.github.dperezcabrera.test.architecture.auth.dtos.UserDto;
import com.github.dperezcabrera.test.architecture.auth.entities.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "password", ignore = true)
    UserDto map(User user, List<String> roles);
	
	User map(UserDto userDto, String hash, String salt);
}
