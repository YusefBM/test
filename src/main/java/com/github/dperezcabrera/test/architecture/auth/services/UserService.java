package com.github.dperezcabrera.test.architecture.auth.services;

import com.github.dperezcabrera.test.architecture.auth.dtos.UserDto;
import com.github.dperezcabrera.test.architecture.auth.entities.Permission;
import com.github.dperezcabrera.test.architecture.auth.entities.PermissionId;
import com.github.dperezcabrera.test.architecture.auth.entities.Role;
import com.github.dperezcabrera.test.architecture.auth.entities.User;
import com.github.dperezcabrera.test.architecture.auth.repositories.PermissionRepository;
import com.github.dperezcabrera.test.architecture.auth.repositories.RoleRepository;
import com.github.dperezcabrera.test.architecture.auth.repositories.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PermissionRepository permissionRepository;

	private final UserMapper userMapper;
	
	private final RoleRepository roleRepository;
	
	private final CredentialService credentialService;
	
	@Transactional
	public void addUser(UserDto userDto){
		if (userRepository.findById(userDto.getUsername()).isPresent()) {
			
		} else {
			Map<String, Role> rolesByName = roleRepository.findAll().stream().collect(Collectors.toMap(r -> r.getName().toLowerCase(), Function.identity()));
			String salt = credentialService.generateSalt();
			String hash = credentialService.hash(userDto.getUsername().toLowerCase(), userDto.getPassword(), salt);
			User user = userMapper.map(userDto, hash, salt);
			
			final User u = userRepository.save(user);
			List<Permission> perrmissions = userDto.getRoles().stream()
					.filter(Objects::nonNull)
					.map(String::toLowerCase)
					.distinct()
					.map(rolesByName::get)
					.filter(Objects::nonNull)
					.map(r -> new Permission(new PermissionId(u, r)))
					.collect(Collectors.toList());
			
			permissionRepository.saveAll(perrmissions);
		}
	}
	
    private List<String> roles(String username) {
        return permissionRepository.findByUsername(username).stream()
                .map(p -> p.getId().getRole().getName())
                .collect(Collectors.toList());
    }

	@Transactional(readOnly = true)
	public Optional<UserDto> getUser(@NonNull String username) {
		return userRepository.findById(username.toLowerCase()).map(this::map);
	}

	private UserDto map(User user) {
		UserDto result = null;
		if (user != null) {
			result = userMapper.map(user, roles(user.getUsername()));
		}
		return result;
	}
}
