package com.example.test_task.controller;

import com.example.test_task.dto.user.UserDto;
import com.example.test_task.dto.user.UserFullDto;
import com.example.test_task.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class UserController {

  private final UserService userService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<UserDto> findUsers(
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String email,
      @RequestParam(required = false) String phone,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateOfBirth
      ) {
    return userService.findUsers(size, page, name, email, phone, dateOfBirth);
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public UserFullDto getUserInfo() {
    return userService.getCurrentUserInfo();
  }

}
