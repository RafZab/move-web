package pl.rafzab.movielibraryservice.service.user;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import pl.rafzab.movielibraryservice.configuration.security.CustomUserDetails;
import pl.rafzab.movielibraryservice.entity.User;
import pl.rafzab.movielibraryservice.exception.NotFoundException;
import pl.rafzab.movielibraryservice.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return new CustomUserDetails(user);
    }
}
