package com.github.dperezcabrera.test;

import com.github.dperezcabrera.test.architecture.auth.dtos.UserDto;
import com.github.dperezcabrera.test.architecture.auth.entities.Role;
import com.github.dperezcabrera.test.architecture.auth.repositories.UserRepository;
import com.github.dperezcabrera.test.architecture.auth.repositories.RoleRepository;
import com.github.dperezcabrera.test.architecture.auth.repositories.PermissionRepository;
import com.github.dperezcabrera.test.architecture.auth.services.UserService;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

@Slf4j
@Configuration
@AllArgsConstructor
public class Initializer implements CommandLineRunner {

    private UserService userService;

    private RoleRepository roleRepository;

    private UserRepository userRepository;

    private PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... strings) throws Exception {
		if (roleRepository.count() == 0) {
			roleRepository.save(new Role("admin" , "Administrator"));
			roleRepository.save(new Role("user" , "User"));
		}
        if (userRepository.count() == 0){
			userService.addUser(new UserDto("marina", "Marina", "marina@mail.com", "password", Arrays.asList("user")));
			userService.addUser(new UserDto("david", "David", "david@mail.com", "password", Arrays.asList("admin")));
		}
              
        log.info("\"david\" -> {}",userService.getUser("david"));
    }
}
