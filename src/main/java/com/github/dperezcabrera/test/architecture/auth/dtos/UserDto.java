package com.github.dperezcabrera.test.architecture.auth.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String username;
    private String name;
    private String email;
	private String password;
    private List<String> roles;
}
