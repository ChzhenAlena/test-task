package com.example.test_task.service;

import com.example.test_task.dto.phoneData.PhoneDataCreateDto;
import com.example.test_task.dto.phoneData.PhoneDataDto;
import com.example.test_task.entity.PhoneData;
import com.example.test_task.entity.User;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.mapper.PhoneDataMapper;
import com.example.test_task.repository.PhoneDataRepository;
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
public class PhoneDataService {

  private final PhoneDataRepository phoneDataRepository;
  private final PhoneDataMapper phoneDataMapper;
  private final SecurityUtils securityUtils;

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true)
      }
  )
  public PhoneDataDto create(@Valid PhoneDataCreateDto createDto) {
    if (phoneDataRepository.existsByPhone(createDto.phone())) {
      throw new ServiceException(ExceptionCode.DUPLICATE_PHONE, HttpStatus.CONFLICT);
    }
    PhoneData phoneData = phoneDataMapper.toEntity(createDto);
    User currentUser = securityUtils.getCurrentUser();
    phoneData.setUser(currentUser);
    PhoneData saved = phoneDataRepository.save(phoneData);
    log.debug("Added phone for userId={}", currentUser.getId());
    return phoneDataMapper.toDto(saved);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true)
      }
  )
  public PhoneDataDto update(@Valid PhoneDataDto updateDto) {
    User currentUser = securityUtils.getCurrentUser();
    if (!phoneDataRepository.existsById(updateDto.id())) {
      throw new ServiceException(ExceptionCode.NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (phoneDataRepository.existsByPhone(updateDto.phone())) {
      throw new ServiceException(ExceptionCode.DUPLICATE_PHONE, HttpStatus.CONFLICT);
    }
    if (!phoneDataRepository.getUserById(updateDto.id()).equals(currentUser)) {
      throw new ServiceException(ExceptionCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    PhoneData phoneData = phoneDataMapper.toEntity(updateDto);
    phoneData.setUser(currentUser);
    PhoneData saved = phoneDataRepository.save(phoneData);
    log.debug("Phone updated for userId={}", currentUser.getId());
    return phoneDataMapper.toDto(saved);
  }

  @Caching(
      evict = {
          @CacheEvict(value = "users", key = "'user:' + T(com.example.test_task.security.SecurityUtils).getCurrentUserId()"),
          @CacheEvict(value = "userSearches", allEntries = true)
      }
  )
  public void delete(Long id) {
    User currentUser = securityUtils.getCurrentUser();
    PhoneData phone = phoneDataRepository.findById(id)
        .orElseThrow(() -> new ServiceException(ExceptionCode.NOT_FOUND, HttpStatus.NOT_FOUND));
    if (!phone.getUser().getId().equals(currentUser.getId())) {
      throw new ServiceException(ExceptionCode.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    if (phoneDataRepository.countByUserId(currentUser.getId()) <= 1) {
      throw new ServiceException(ExceptionCode.DELETING_LAST_PHONE, HttpStatus.BAD_REQUEST);
    }
    log.debug("Phone deleted for userId={}", currentUser.getId());
    phoneDataRepository.delete(phone);
  }
}
