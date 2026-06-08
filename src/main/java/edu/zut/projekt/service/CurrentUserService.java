package edu.zut.projekt.service;

import edu.zut.projekt.config.DataInitializer;
import edu.zut.projekt.model.User;
import edu.zut.projekt.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public UUID getCurrentUserId() {
        String email = getCurrentEmail();

        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            return DataInitializer.ADMIN_ID;
        }
        return DataInitializer.USER_ID;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public User getCurrentUser() {
        String email = getCurrentEmail();

        User user = new User();
        user.setEmail(email);
        user.setActive(true);

        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            user.setId(DataInitializer.ADMIN_ID);
            user.setFirstName("Admin");
            user.setLastName("Systemowy");
            user.setRole(UserRole.ADMIN);
        } else {
            user.setId(DataInitializer.USER_ID);
            user.setFirstName("Jan");
            user.setLastName("Kowalski");
            user.setRole(UserRole.USER);
        }

        return user;
    }
}