package com.fanus.controller;

import com.fanus.dto.AuthRequest;
import com.fanus.dto.AuthResponse;
import com.fanus.dto.RefreshRequest;
import com.fanus.entity.User;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.UserRepository;
import com.fanus.security.JwtTokenProvider;
import com.fanus.security.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = tokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        String tokenId = refreshTokenService.create(user.getId());
        String refreshToken = user.getId() + ":" + tokenId;

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, user.getEmail(), user.getRole()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshRequest req) {
        String[] parts = req.refreshToken().split(":");
        if (parts.length != 2) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));

        Long userId;
        try { userId = Long.parseLong(parts[0]); } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid refresh token"));
        }
        String tokenId = parts[1];

        if (!refreshTokenService.validate(userId, tokenId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Refresh token expired or revoked"));
        }

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccess = tokenProvider.generateAccessToken(user.getEmail(), user.getRole());
        refreshTokenService.revoke(userId, tokenId);
        String newRefresh = userId + ":" + refreshTokenService.create(userId);

        return ResponseEntity.ok(new AuthResponse(newAccess, newRefresh, user.getEmail(), user.getRole()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest req) {
        String[] parts = req.refreshToken().split(":");
        if (parts.length == 2) {
            try {
                Long userId = Long.parseLong(parts[0]);
                refreshTokenService.revoke(userId, parts[1]);
            } catch (NumberFormatException ignored) {}
        }
        return ResponseEntity.noContent().build();
    }
}
