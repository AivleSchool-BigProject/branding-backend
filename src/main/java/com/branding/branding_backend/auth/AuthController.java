package com.branding.branding_backend.auth;

import com.branding.branding_backend.security.JwtProvider;
import com.branding.branding_backend.security.LoginResponse;
import com.branding.branding_backend.user.User;
import com.branding.branding_backend.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PostMapping("/register")
    public void register(@RequestBody RegisterRequest request){
        userService.register(
                request.getEmail(),
                request.getPassword(),
                request.getMobileNumber(),
                request.getUsername()
        );
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        User user = userService.findByEmail(request.getEmail());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(
                user.getId(),
                user.getEmail()
        );

        return new LoginResponse(accessToken);
    }
}
