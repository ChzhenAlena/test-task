package com.example.test_task;

import com.example.test_task.dto.transfer.TransferRequest;
import com.example.test_task.dto.transfer.TransferResponse;
import com.example.test_task.entity.Account;
import com.example.test_task.entity.EmailData;
import com.example.test_task.entity.PhoneData;
import com.example.test_task.entity.User;
import com.example.test_task.exception.ExceptionCode;
import com.example.test_task.exception.ServiceException;
import com.example.test_task.repository.AccountRepository;
import com.example.test_task.repository.UserRepository;
import com.example.test_task.security.SecurityUtils;
import com.example.test_task.service.TransferService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransferServiceTests {

  @Mock
  private AccountRepository accountRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private TransferService transferService;

  private final Long currentUserId = 1L;
  private final Long targetUserId = 2L;
  private User currentUser;
  private User targetUser;
  private final BigDecimal defaultBalance = new BigDecimal("1000.00");
  private final BigDecimal maxTransferAmount = new BigDecimal("1000000");
  private AutoCloseable closeable;
  private AutoCloseable closeableStatic;

  @BeforeEach
  void setUp() throws Exception {
    closeable = MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(transferService, "maxTransferAmount", maxTransferAmount);

    closeableStatic = mockStatic(SecurityUtils.class);
    when(SecurityUtils.getCurrentUserId()).thenReturn(currentUserId);

    currentUser = createTestUser(currentUserId, "Current User", "01.01.1990");
    targetUser = createTestUser(targetUserId, "Target User", "01.01.1995");

    when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
    when(userRepository.findById(targetUserId)).thenReturn(Optional.of(targetUser));
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
    closeableStatic.close();
  }

  private User createTestUser(Long id, String name, String birthDate) {
    User user = new User();
    user.setId(id);
    user.setName(name);
    user.setDateOfBirth(LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    user.setPassword("Password123!");

    EmailData email = new EmailData();
    email.setId(id);
    email.setEmail(name.toLowerCase().replace(" ", "") + "@example.com");
    email.setUser(user);
    user.setEmails(List.of(email));

    PhoneData phone = new PhoneData();
    phone.setId(id);
    phone.setPhone("7910" + String.format("%07d", id));
    phone.setUser(user);
    user.setPhones(List.of(phone));

    Account account = new Account();
    account.setId(id);
    account.setUser(user);
    account.setBalance(defaultBalance);
    user.setAccount(account);

    return user;
  }

  @Test
  void transfer_success() {
    TransferRequest transferRequest = new TransferRequest(targetUserId, BigDecimal.valueOf(100));
    Account from = currentUser.getAccount();
    Account to = targetUser.getAccount();

    when(accountRepository.findAccountByUserIdForUpdate(currentUserId))
        .thenReturn(Optional.of(from));
    when(accountRepository.findAccountByUserIdForUpdate(targetUserId))
        .thenReturn(Optional.of(to));

    TransferResponse response = transferService.transfer(transferRequest);

    assertEquals(new BigDecimal("900.00"), from.getBalance());
    assertEquals(new BigDecimal("1100.00"), to.getBalance());
    assertEquals("COMPLETED", response.status());
    assertEquals(new BigDecimal("900.00"), response.currentBalance());
    verify(accountRepository, times(2)).findAccountByUserIdForUpdate(anyLong());
  }

  @Test
  void transfer_selfTransfer_throws() {
    TransferRequest transferRequest = new TransferRequest(currentUserId, BigDecimal.valueOf(100));
    Account from = currentUser.getAccount();

    when(accountRepository.findAccountByUserIdForUpdate(currentUserId)).thenReturn(Optional.of(from));
    when(accountRepository.findAccountByUserIdForUpdate(currentUserId)).thenReturn(Optional.of(from));

    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(transferRequest));
    assertEquals(ExceptionCode.SELF_TRANSFER, ex.getExceptionCode());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  void transfer_insufficientFunds_throws() {
    TransferRequest transferRequest = new TransferRequest(targetUserId, BigDecimal.valueOf(100));
    Account from = currentUser.getAccount();
    from.setBalance(BigDecimal.valueOf(50));
    Account to = targetUser.getAccount();

    when(accountRepository.findAccountByUserIdForUpdate(currentUserId)).thenReturn(Optional.of(from));
    when(accountRepository.findAccountByUserIdForUpdate(targetUserId)).thenReturn(Optional.of(to));

    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(transferRequest));
    assertEquals(ExceptionCode.INSUFFICIENT_FUNDS, ex.getExceptionCode());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  void transfer_nullAmount_throws() {
    TransferRequest transferRequest = new TransferRequest(targetUserId, null);
    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(transferRequest));
    assertEquals(ExceptionCode.NULL_TRANSFER_AMOUNT, ex.getExceptionCode());
    assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
  }

  @Test
  void transfer_negativeOrZeroAmount_throws() {

    ServiceException ex1 = assertThrows(ServiceException.class,
        () -> transferService.transfer(new TransferRequest(targetUserId, BigDecimal.ZERO)));
    assertEquals(ExceptionCode.NON_POSITIVE_TRANSFER_AMOUNT, ex1.getExceptionCode());

    ServiceException ex2 = assertThrows(ServiceException.class,
        () -> transferService.transfer(new TransferRequest(targetUserId, BigDecimal.valueOf(-100))));
    assertEquals(ExceptionCode.NON_POSITIVE_TRANSFER_AMOUNT, ex2.getExceptionCode());
  }

  @Test
  void transfer_amountExceedsLimit_throws() {
    TransferRequest request = new TransferRequest(targetUserId, BigDecimal.valueOf(2000000000));
    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(request));
    assertEquals(ExceptionCode.TRANSFER_LIMIT_EXCEEDED, ex.getExceptionCode());
  }

  @Test
  void transfer_accountFromNotFound_throws() {
    when(accountRepository.findAccountByUserIdForUpdate(currentUserId)).thenReturn(Optional.empty());
    TransferRequest request = new TransferRequest(targetUserId, BigDecimal.valueOf(100));

    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(request));
    assertEquals(ExceptionCode.ACCOUNT_NOT_FOUND, ex.getExceptionCode());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }

  @Test
  void transfer_accountToNotFound_throws() {
    TransferRequest transferRequest = new TransferRequest(targetUserId, BigDecimal.valueOf(100));
    Account from = currentUser.getAccount();

    when(accountRepository.findAccountByUserIdForUpdate(currentUserId)).thenReturn(Optional.of(from));
    when(accountRepository.findAccountByUserIdForUpdate(targetUserId)).thenReturn(Optional.empty());

    ServiceException ex = assertThrows(ServiceException.class, () -> transferService.transfer(transferRequest));
    assertEquals(ExceptionCode.ACCOUNT_NOT_FOUND, ex.getExceptionCode());
    assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
  }
}
