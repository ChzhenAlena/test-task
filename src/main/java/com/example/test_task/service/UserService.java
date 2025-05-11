package com.example.test_task.service;

import com.example.test_task.dto.accountData.AccountDto;
import com.example.test_task.dto.user.UserDto;
import com.example.test_task.dto.user.UserFullDto;
import com.example.test_task.entity.User;
import com.example.test_task.mapper.UserMapper;
import com.example.test_task.repository.UserRepository;
import com.example.test_task.repository.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AccountService accountService;
  private final UserMapper userMapper;
  private final UserInfoCacheService userInfoCacheService;

  @Cacheable(
      value = "userSearches",
      key = "'findUsers:' + #name + ':' + #email + ':' + #phone + ':' + #dateOfBirth + ':' + #size + ':' + #page"
  )
  @Transactional(readOnly = true)
  public List<UserDto> findUsers(int size, Integer page,
                                 String name, String email,
                                 String phone, LocalDate dateOfBirth) {

    Pageable pageable = PageRequest.of(Optional.ofNullable(page).orElse(0), size, Sort.by(Sort.Direction.ASC, "id"));
    Specification<User> spec = buildSearchSpecification(name, email, phone, dateOfBirth);

    return userRepository.findAll(spec, pageable)
        .map(userMapper::toDto)
        .toList();
  }

  public UserFullDto getCurrentUserInfo() {
    UserFullDto dto = userInfoCacheService.getCurrentUserInfoWithoutBalance();
    AccountDto accountDto = accountService.getByUserId(dto.getId());
    dto.setAccount(accountDto);
    return dto;
  }

  private Specification<User> buildSearchSpecification(String name, String email,
                                                       String phone, LocalDate dateOfBirth) {
    return Stream.of(
            Optional.ofNullable(name).map(UserSpecification::nameStartsWith),
            Optional.ofNullable(phone).map(UserSpecification::phoneEquals),
            Optional.ofNullable(email).map(UserSpecification::emailEquals),
            Optional.ofNullable(dateOfBirth).map(UserSpecification::dateOfBirthAfter)
        )
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(Specification::and)
        .orElse(Specification.where(null));
  }
}
