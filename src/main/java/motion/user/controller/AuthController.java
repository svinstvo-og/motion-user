package motion.user.controller;

import motion.user.DTO.AuthRequest;
import motion.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("motion/api/v1/user/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping("/login")
    public String login(@RequestBody AuthRequest request) {
        return authService.authenticate(request.getUsername(), request.getPassword());
    }

    @PostMapping("/signup")
    public void signup(@RequestBody AuthRequest request) {
        authService.signup(request);
    }
}
