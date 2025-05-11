package com.example.test_task.controller;

import com.example.test_task.dto.phoneData.PhoneDataCreateDto;
import com.example.test_task.dto.phoneData.PhoneDataDto;
import com.example.test_task.service.PhoneDataService;
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
@RequestMapping("/api/users/phones")
@RequiredArgsConstructor
public class PhoneController {

  private final PhoneDataService phoneDataService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PhoneDataDto create(@RequestBody @Valid PhoneDataCreateDto createDto) {
    return phoneDataService.create(createDto);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public PhoneDataDto update(@RequestBody @Valid PhoneDataDto updateDto) {
    return phoneDataService.update(updateDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable("id") Long id) {
    phoneDataService.delete(id);
  }
}
