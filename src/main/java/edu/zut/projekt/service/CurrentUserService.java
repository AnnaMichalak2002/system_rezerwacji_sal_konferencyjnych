package edu.zut.projekt.service;
import org.springframework.beans.factory.annotation.Value;
import edu.zut.projekt.config.DataInitializer;
import edu.zut.projekt.model.User;
import edu.zut.projekt.model.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentUserService {

    @Value("${app.test.user.id}")
    private UUID testUserId;

    @Value("${app.test.user.email}")
    private String testUserEmail;

    @Value("${app.test.user.first-name}")
    private String testUserFirstName;

    @Value("${app.test.user.last-name}")
    private String testUserLastName;

    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public UUID getCurrentUserId() {
        String email = getCurrentEmail();

        if ("admin@yourfurniture.pl".equalsIgnoreCase(email)) {
            return DataInitializer.ADMIN_ID;
        }

        if ("jan.kowalski@yourfurniture.pl".equalsIgnoreCase(email)) {
            return DataInitializer.USER_ID;
        }

        if (testUserEmail.equalsIgnoreCase(email)) {
            return testUserId;
        }

        throw new IllegalStateException("Nie udalo sie odnalezc identyfikatora aktualnie zalogowanego uzytkownika.");
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
            return user;
        }

        if ("jan.kowalski@yourfurniture.pl".equalsIgnoreCase(email)) {
            user.setId(DataInitializer.USER_ID);
            user.setFirstName("Jan");
            user.setLastName("Kowalski");
            user.setRole(UserRole.USER);
            return user;
        }

        if (testUserEmail.equalsIgnoreCase(email)) {
            user.setId(testUserId);
            user.setFirstName(testUserFirstName);
            user.setLastName(testUserLastName);
            user.setRole(UserRole.USER);
            return user;
        }


        throw new IllegalStateException("Nie udalo sie odnalezc danych aktualnie zalogowanego uzytkownika.");
    }
}