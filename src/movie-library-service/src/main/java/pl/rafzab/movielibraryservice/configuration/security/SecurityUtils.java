package pl.rafzab.movielibraryservice.configuration.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.exception.UnauthorizedException;

public class SecurityUtils {

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails(User user)) {
            return user;
        }
        throw new UnauthorizedException("Unauthorized");
    }
}
