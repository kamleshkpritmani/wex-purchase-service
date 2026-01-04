package com.wex.purchase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
		@NotBlank(message = "Username is required")
	    @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
	    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Username can only contain alphanumeric characters")
	    String username,

	    @NotBlank(message = "Password is required")
	    @Size(min = 8, message = "Password must be at least 8 characters long")
	    @Pattern(
	        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", 
	        message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character (@#$%^&+=!)"
	    )
	    String password
) {}