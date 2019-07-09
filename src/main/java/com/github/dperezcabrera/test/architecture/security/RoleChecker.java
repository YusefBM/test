package com.github.dperezcabrera.test.architecture.security;

import com.github.dperezcabrera.test.architecture.auth.repositories.PermissionRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("roleChecker")
@AllArgsConstructor
public class RoleChecker {

    private final PermissionRepository permissionRepository;

    private final AuditorAware<String> autidorAware;

    @Transactional(readOnly = true)
    public boolean hasRole(String role) {
        Optional<String> opt = autidorAware.getCurrentAuditor();
        if (opt.isPresent()) {
			String roleLowerCase = role.toLowerCase();
            return permissionRepository.findByUsername(opt.get()).stream().map(p -> p.getId().getRole().getName().toLowerCase()).anyMatch(roleLowerCase::equals);
        } 
        return false;
    }
}
