package com.example.test_task.dto.user;

import com.example.test_task.dto.emailData.EmailDataDto;
import com.example.test_task.dto.phoneData.PhoneDataDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDate;
import java.util.List;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.CLASS,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class"
)
public record UserDto (
  Long id,
  String name,
  @JsonFormat(pattern = "dd.MM.yyyy")
  LocalDate dateOfBirth,
  List<EmailDataDto> emails,
  List<PhoneDataDto> phones
) {}