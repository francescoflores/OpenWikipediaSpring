package com.open.openwikipedia.common.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    @NotBlank(message = "Name is mandatory")
    @Email
    private String email;
    
    @NotBlank(message = "Name is mandatory")
    private String username;
	private String password;
}
