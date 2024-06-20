package com.open.openwikipedia.common.model.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class JwtDTO {

    private String token;
    private String type = "Bearer";
    private String id;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public JwtDTO(String token, String id, String username, String email, LocalDateTime createdAt) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }
}
