package motion.user.controller;

import lombok.extern.slf4j.Slf4j;
import motion.user.DTO.AuthRequest;
import motion.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("motion/api/v1/user/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        log.info("Login request: {}", request.getUsername());
        return authService.authenticate(request.getUsername(), request.getPassword());
    }

    @PostMapping("/signup")
    public void signup(@RequestBody AuthRequest request) {
        log.info("Signup request: {}", request.getUsername());
        authService.signup(request);
    }
}
