package com.grenade.main.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {
  private String username;
  @Email
  private String email;
  private String password;
  private String steamId;
}
