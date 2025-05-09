package motion.user.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import motion.user.DTO.AuthRequest;
import motion.user.model.User;
import motion.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10);

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String authenticate(String username, String password) throws UsernameNotFoundException, BadCredentialsException {
        try {
            Optional<User> user = userRepository.findByUsername(username);
            log.debug("User is present: {}", user.isPresent());
            user.orElseThrow();
            if (!bCryptPasswordEncoder.matches(password, user.get().getPwdHash())) {
                log.error("Wrong password");
                throw new BadCredentialsException("Wrong password");
            }
            log.info("Authenticated user {}", username);
            return jwtService.generateToken(username);
        }
        catch (NoSuchElementException e) {
            log.info("User not found");
            throw new UsernameNotFoundException("Username not found");
        }
    }

    @Transactional
    public void signup(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            log.error("Username is already in use");
            throw new BadCredentialsException("Username is already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPwdHash(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);
        log.info("User {} created", user.getUsername());
    }
}
