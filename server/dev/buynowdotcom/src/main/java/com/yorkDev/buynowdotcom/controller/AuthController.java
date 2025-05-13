package com.yorkDev.buynowdotcom.controller;

import com.yorkDev.buynowdotcom.model.User;
import com.yorkDev.buynowdotcom.repository.UserRepository;
import com.yorkDev.buynowdotcom.request.LoginRequest;
import com.yorkDev.buynowdotcom.security.jwt.JwtUtils;
import com.yorkDev.buynowdotcom.security.user.ShopUserDetails;
import com.yorkDev.buynowdotcom.security.user.ShopUserDetailsService;
import com.yorkDev.buynowdotcom.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/auth")
public class AuthController {
    private final JwtUtils jwtUtils;
    private final CookieUtils cookieUtils;
    private final ShopUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    @Value("${auth.token.refreshExpirationInMils}")
    private Long refreshTokenExpirationTime;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest request, HttpServletResponse response){
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // Extract user
        User user = userRepository.findByEmail(request.getEmail());

        String accessToken = jwtUtils.generateAccessTokenForUser(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(request.getEmail());
        cookieUtils.addRefreshTokenCookie(response, refreshToken, refreshTokenExpirationTime);
        Map<String, String> resBody = new HashMap<>();
        resBody.put("accessToken", accessToken);
        resBody.put("userId", user.getId().toString());
        resBody.put("firstName", user.getFirstName());
        resBody.put("lastName", user.getLastName());
        resBody.put("email", request.getEmail());
        return ResponseEntity.ok(resBody);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request){
        cookieUtils.logCookies(request);
        String refreshToken = cookieUtils.getRefreshTokenFromCookies(request);
        if (refreshToken != null){
            boolean isValid = jwtUtils.validateToken(refreshToken);
            if (isValid){
                String usernameFromToken = jwtUtils.getUsernameFromToken(refreshToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);
                String newAccessToken = jwtUtils.generateAccessTokenForUser(
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));
                if (newAccessToken != null){
                    Map<String, String> response = new HashMap<>();
                    response.put("accessToken", newAccessToken);
                    return ResponseEntity.ok(response);
                }else {
                    return ResponseEntity.status(500).body("Error generating new access token");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired access token");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // set true in production
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString()); // Delete refresh token

        return ResponseEntity.ok().body("Logged out successfully");
    }

}
