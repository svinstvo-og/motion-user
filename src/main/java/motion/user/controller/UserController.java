package motion.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import motion.user.DTO.UserRecord;
import motion.user.model.User;
import motion.user.repository.UserRepository;
import motion.user.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("motion/api/v1/user")
@Slf4j
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtService jwtService;

    @GetMapping("/username")
    public UserRecord getUserByUsername(@RequestParam String username) throws ResponseStatusException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            log.error("User not found {} not found", username);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOptional.get();
        return new UserRecord(user.getUserId(), user.getUsername(), user.getCreatedAt());
    }

    @GetMapping("/id")
    public UserRecord getUserById(@RequestParam UUID uuid) throws ResponseStatusException {
        Optional<User> userOptional = userRepository.findById(uuid);
        if (userOptional.isEmpty()) {
            log.error("User with id = {} not found", uuid);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOptional.get();
        return new UserRecord(user.getUserId(), user.getUsername(), user.getCreatedAt());
    }

    @GetMapping
    public UserRecord getUserByBearer(HttpServletRequest request) throws ResponseStatusException {
        String authorization = request.getHeader("Authorization");
        log.info("Authorization: {}", authorization);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            UUID uuid = jwtService.extractUuid(token);
            User user = userRepository.findById(uuid).get();
            return new UserRecord(user.getUserId(), user.getUsername(), user.getCreatedAt());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
