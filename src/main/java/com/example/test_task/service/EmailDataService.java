package com.example.test_task.service;

import com.example.test_task.dto.emailData.EmailDataCreateDto;
import com.example.test_task.dto.emailData.EmailDataDto;
import com.example.test_task.entity.EmailData;
import com.example.test_task.entity.User;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.mapper.EmailDataMapper;
import com.example.test_task.repository.EmailDataRepository;
import com.example.test_task.security.SecurityUtils;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailDataService {

  private final EmailDataRepository emailDataRepository;
  private final EmailDataMapper emailDataMapper;
  private final SecurityUtils securityUtils;

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true)
      }
  )
  public EmailDataDto create(@Valid EmailDataCreateDto createDto) {
    if (emailDataRepository.existsByEmail(createDto.email())) {
      throw new ServiceException(ExceptionCode.DUPLICATE_EMAIL, HttpStatus.CONFLICT);
    }
    EmailData emailData = emailDataMapper.toEntity(createDto);
    User currentUser = securityUtils.getCurrentUser();
    emailData.setUser(currentUser);
    EmailData saved = emailDataRepository.save(emailData);
    log.debug("Added email for userId={}", currentUser.getId());
    return emailDataMapper.toDto(saved);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true)
      }
  )
  public EmailDataDto update(@Valid EmailDataDto updateDto) {
    User currentUser = securityUtils.getCurrentUser();
    if (!emailDataRepository.existsById(updateDto.id())) {
      throw new ServiceException(ExceptionCode.NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (emailDataRepository.existsByEmail(updateDto.email())) {
      throw new ServiceException(ExceptionCode.DUPLICATE_EMAIL, HttpStatus.CONFLICT);
    }
    if (!emailDataRepository.getUserById(updateDto.id()).equals(currentUser)) {
      throw new ServiceException(ExceptionCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    EmailData emailData = emailDataMapper.toEntity(updateDto);
    emailData.setUser(currentUser);
    EmailData saved = emailDataRepository.save(emailData);
    log.debug("Email updated for userId={}", currentUser.getId());
    return emailDataMapper.toDto(saved);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true) 
      }
  )
  public void delete(Long id) {
    User currentUser = securityUtils.getCurrentUser();
    EmailData phone = emailDataRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ExceptionCode.NOT_FOUND, HttpStatus.NOT_FOUND));
    if (!phone.getUser().getId().equals(currentUser.getId())) {
      throw new ServiceException(ExceptionCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    if (emailDataRepository.countByUserId(currentUser.getId()) <= 1) {
      throw new ServiceException(ExceptionCode.DELETING_LAST_EMAIL, HttpStatus.BAD_REQUEST);
    }
    log.debug("Email deleted for userId={}", currentUser.getId());
    emailDataRepository.delete(phone);
  }

}
