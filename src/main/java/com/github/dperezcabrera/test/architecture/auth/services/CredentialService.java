package com.github.dperezcabrera.test.architecture.auth.services;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CredentialService {

	public String generateSalt() {
		return UUID.randomUUID().toString();
	}

	public String hash(@NonNull String username, @NonNull String password, @NonNull String salt) {
		return Hashing.sha512().hashString(String.join(":", salt, username, password), StandardCharsets.UTF_8).toString();
	}
}
