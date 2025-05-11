package com.example.test_task.dto.user;

import com.example.test_task.dto.accountData.AccountDto;
import com.example.test_task.dto.emailData.EmailDataDto;
import com.example.test_task.dto.phoneData.PhoneDataDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserFullDto {
  private Long id;
  private String name;
  @JsonFormat(pattern = "dd.MM.yyyy")
  private LocalDate dateOfBirth;
  private List<EmailDataDto> emails;
  private List<PhoneDataDto> phones;
  private AccountDto account;
}