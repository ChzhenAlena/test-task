package com.example.test_task;

import com.example.test_task.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest(classes = TestTaskApplication.class)
@AutoConfigureMockMvc
class UserControllerTests {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private JwtUtils jwtUtils;

  private String authToken;

  @BeforeEach
  void setup() {
    jdbcTemplate.execute("DELETE FROM phone_data");
    jdbcTemplate.execute("DELETE FROM email_data");
    jdbcTemplate.execute("DELETE FROM account");
    jdbcTemplate.execute("DELETE FROM \"user\"");

    jdbcTemplate.execute("""
        INSERT INTO "user" (id, name, date_of_birth, password)
        VALUES
            (1, 'Иван Иванов', '1990-05-01', 'Password123!'),
            (2, 'Петр Петров', '1985-03-10', 'Password123!'),
            (3, 'Иван Смирнов', '2002-08-15', 'Password123!')
        """);

    jdbcTemplate.execute("""
        INSERT INTO email_data (id, user_id, email)
        VALUES
            (1, 1, 'ivan@example.com'),
            (2, 1, 'ivan_work@example.com'),
            (3, 2, 'petr@example.com'),
            (4, 3, 'ivan_smirnov@example.com')
        """);

    jdbcTemplate.execute("""
        INSERT INTO phone_data (id, user_id, phone)
        VALUES
            (1, 1, '79101112233'),
            (2, 1, '79998887766'),
            (3, 2, '79202223344'),
            (4, 3, '79303334455')
        """);

    jdbcTemplate.execute("""
        INSERT INTO account (id, user_id, balance, init_balance)
        VALUES
            (1, 1, 1000.00, 1000.00),
            (2, 2, 2000.00, 2000.00),
            (3, 3, 1500.00, 1500.00)
        """);

    jdbcTemplate.execute("ALTER SEQUENCE user_id_seq RESTART WITH 4");
    jdbcTemplate.execute("ALTER SEQUENCE email_data_id_seq RESTART WITH 5");
    jdbcTemplate.execute("ALTER SEQUENCE phone_data_id_seq RESTART WITH 5");
    jdbcTemplate.execute("ALTER SEQUENCE account_id_seq RESTART WITH 4");

    this.authToken = "Bearer " + jwtUtils.generateToken(1L);
  }

  @Test
  void testFindUsers_filterByDateOfBirth() throws Exception {
    // dateOfBirth=2000-01-01 -> только Иван Смирнов (id=3)
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("dateOfBirth", "2000-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Иван Смирнов"));
  }

  @Test
  void testFindUsers_filterByPhone() throws Exception {
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("phone", "79998887766"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Иван Иванов"));
  }

  @Test
  void testFindUsers_filterByEmail() throws Exception {
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("email", "ivan_work@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Иван Иванов"));
  }

  @Test
  void testFindUsers_filterByNameLike() throws Exception {
    // Имя начинается на "Иван" — Иван Иванов и Иван Смирнов
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("name", "Иван"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void testFindUsers_combinedFilters() throws Exception {
    // Иван Иванов: name="Иван", phone="79101112233", email="ivan@example.com", dateOfBirth="1990-05-01"
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("name", "Иван")
            .param("phone", "79101112233")
            .param("email", "ivan@example.com")
            .param("dateOfBirth", "1980-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value("Иван Иванов"));
  }

  @Test
  void testFindUsers_pagination() throws Exception {
    // Всего 3 пользователя, размер страницы 2, первая страница
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("size", "2")
            .param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));
  }

  @Test
  void testFindUsers_noResults() throws Exception {
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("email", "notfound@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  void testFindUsers_badDateFormat() throws Exception {
    mockMvc.perform(get("/api/users")
            .header("Authorization", authToken)
            .param("dateOfBirth", "not-a-date"))
        .andExpect(status().isBadRequest());
  }
}