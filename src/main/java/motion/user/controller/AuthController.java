package motion.user.controller;

import motion.user.DTO.AuthRequest;
import motion.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @GetMapping
    public String login(@RequestBody AuthRequest request) {
        return authService.authenticate(request.getUsername(), request.getPassword());
    }

    public void Register(@RequestBody AuthRequest request) {

    }
}
