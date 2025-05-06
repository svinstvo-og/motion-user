package motion.user.service;

import jakarta.transaction.Transactional;
import motion.user.DTO.AuthRequest;
import motion.user.model.User;
import motion.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        if (!Objects.equals(user.getPwdHash(), bCryptPasswordEncoder.encode(password))) {
            throw new BadCredentialsException("Wrong password");
        }
        return jwtService.generateToken(username);
    }

    @Transactional
    public void register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new BadCredentialsException("Username is already in use");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPwdHash(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
    }
}
