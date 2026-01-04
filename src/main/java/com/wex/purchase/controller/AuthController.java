package com.wex.purchase.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wex.purchase.dto.RegisterRequest;
import com.wex.purchase.security.JwtService;
import com.wex.purchase.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;



@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthController {


  private static final Logger log = LogManager.getLogger(AuthController.class);
	
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserService userService;
  
  public AuthController(AuthenticationManager authenticationManager, 
		  JwtService jwtService,
		  UserService userService) {
	  this.authenticationManager = authenticationManager;
	  this.jwtService = jwtService;
	  this.userService = userService;
  }

  public record LoginRequest(
      @NotBlank String username,
      @NotBlank String password
  ) {}

  public record LoginResponse(String token) {}

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
    try {
      // Delegate authentication to Spring Security (UserDetailsService + PasswordEncoder)
      var authToken = new UsernamePasswordAuthenticationToken(req.username(), req.password());
      authenticationManager.authenticate(authToken);

      // Only generate JWT after successful auth
      String jwt = jwtService.generate(req.username());
      log.info("User authenticated successfully username={}", req.username());

      return ResponseEntity.ok(new LoginResponse(jwt));
    } catch (BadCredentialsException ex) {
      log.warn("Authentication failed for username={}", req.username());
      throw new BadCredentialsException("Invalid username or password");
    }
  }
  
  
  @PostMapping("/register")
  public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
      userService.registerNewUser(request.username(), request.password());
      return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
  }
  
  
  
}
