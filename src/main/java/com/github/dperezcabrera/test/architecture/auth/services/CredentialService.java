package com.github.dperezcabrera.test.architecture.auth.services;

import com.github.dperezcabrera.test.architecture.auth.repositories.UserRepository;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class CredentialService {

	private final UserRepository userRepository;
	
	public String generateSalt() {
		return UUID.randomUUID().toString();
	}

	public String hash(@NonNull String username, @NonNull String password, @NonNull String salt) {
		return Hashing.sha512().hashString(String.join(":", salt, username.toLowerCase(), password), StandardCharsets.UTF_8).toString();
	}
	
	public boolean check(@NonNull String login, @NonNull String password){
		String username = login.toLowerCase();
		return userRepository.findById(username)
				.map(u -> u.getHash().equals(hash(username, password, u.getSalt())))
				.orElse(Boolean.FALSE);
	}
}
