package com.open.openwikipedia.common.model.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDataDTO {
    private String id;
    private String email;
    private String username;
	private LocalDateTime createdAt;
}
