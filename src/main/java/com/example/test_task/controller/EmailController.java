package com.example.test_task.controller;

import com.example.test_task.dto.emailData.EmailDataCreateDto;
import com.example.test_task.dto.emailData.EmailDataDto;
import com.example.test_task.service.EmailDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/emails")
@RequiredArgsConstructor
public class EmailController {

  private final EmailDataService emailDataService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public EmailDataDto create(@RequestBody @Valid EmailDataCreateDto createDto) {
    return emailDataService.create(createDto);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public EmailDataDto update(@RequestBody @Valid EmailDataDto updateDto) {
    return emailDataService.update(updateDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") Long id) {
    emailDataService.delete(id);
  }
}
