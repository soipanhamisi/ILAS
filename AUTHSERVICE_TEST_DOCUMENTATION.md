# AuthService Unit Test Documentation

## Test Class: `AuthServiceTest`

**Location:** `src/test/java/org/soipan/ilas/services/AuthServiceTest.java`

**Framework:** JUnit 5 + Mockito

**Total Tests:** 21

**Status:** ✅ ALL TESTS PASSING

---

## Overview

The `AuthServiceTest` class provides comprehensive unit test coverage for the `AuthService` class. It uses Mockito to mock the repository dependencies and tests all login and signup scenarios including success cases, error cases, and edge cases.

---

## Test Structure

### Setup
- Uses `@ExtendWith(MockitoExtension.class)` for Mockito integration
- Mocks `StudentRepository` and `InstructorRepository`
- Creates test fixtures for Student and Instructor objects

### Test Data
```java
// Test Student
Username: john_student
Password: password123
Name: John Doe
Email: john.doe@student.edu
ID: 1

// Test Instructor
Username: smith_instructor
Password: password123
Name: Dr. Smith
Email: dr.smith@university.edu
ID: 1
```

---

## Test Categories

### 1. Login Tests (9 tests)

#### Success Cases
- **`testLoginStudentSuccess`** - Student can login with correct credentials
- **`testLoginInstructorSuccess`** - Instructor can login with correct credentials
- **`testLoginCaseInsensitiveUserTypeStudent`** - User type is case-insensitive for students
- **`testLoginCaseInsensitiveUserTypeInstructor`** - User type is case-insensitive for instructors

#### Error Cases
- **`testLoginStudentWrongPassword`** - Student login fails with wrong password
- **`testLoginStudentNonExistentUser`** - Student login fails with non-existent username
- **`testLoginInstructorWrongPassword`** - Instructor login fails with wrong password
- **`testLoginInstructorNonExistentUser`** - Instructor login fails with non-existent username
- **`testLoginInvalidUserType`** - Login fails with invalid user type

### 2. Signup Tests (8 tests)

#### Success Cases
- **`testSignupStudentSuccess`** - New student can register successfully
- **`testSignupInstructorSuccess`** - New instructor can register successfully
- **`testSignupCaseInsensitiveUserTypeStudent`** - User type is case-insensitive for student signup
- **`testSignupCaseInsensitiveUserTypeInstructor`** - User type is case-insensitive for instructor signup

#### Error Cases
- **`testSignupStudentUsernameDuplicate`** - Student signup fails with duplicate username
- **`testSignupStudentEmailDuplicate`** - Student signup fails with duplicate email
- **`testSignupInstructorUsernameDuplicate`** - Instructor signup fails with duplicate username
- **`testSignupInstructorEmailDuplicate`** - Instructor signup fails with duplicate email

### 3. Integration Tests (4 tests)

- **`testSignupInvalidUserType`** - Signup fails with invalid user type
- **`testMultipleLoginTokensAreUnique`** - Each login generates a unique token
- **`testAuthResponsePreservesUserInfo`** - Auth response contains correct user data
- **`testTokenIsValidUUID`** - Generated token is in valid UUID format

---

## Test Method Details

### Login Test Example
```java
@Test
@DisplayName("Should successfully login student with correct credentials")
void testLoginStudentSuccess() {
    // Arrange: Setup test data and mock
    AuthRequest request = new AuthRequest("john_student", "password123", "student");
    when(studentRepository.findByUsername("john_student")).thenReturn(Optional.of(testStudent));

    // Act: Execute the method being tested
    AuthResponse response = authService.login(request);

    // Assert: Verify results
    assertNotNull(response);
    assertEquals(1, response.getUserId());
    assertEquals("john_student", response.getUsername());
    verify(studentRepository, times(1)).findByUsername("john_student");
}
```

### Signup Test Example
```java
@Test
@DisplayName("Should successfully signup new student")
void testSignupStudentSuccess() {
    // Arrange
    SignupRequest request = new SignupRequest(
            "Jane Smith",
            "jane.smith@student.edu",
            "jane_student",
            "password123",
            "student"
    );
    Student newStudent = new Student(...);
    newStudent.setStudentId(2);

    when(studentRepository.findByUsername("jane_student")).thenReturn(Optional.empty());
    when(studentRepository.findByEmail("jane.smith@student.edu")).thenReturn(Optional.empty());
    when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

    // Act
    AuthResponse response = authService.signup(request);

    // Assert
    assertNotNull(response);
    assertEquals(2, response.getUserId());
    assertEquals("jane_student", response.getUsername());
    verify(studentRepository, times(1)).save(any(Student.class));
}
```

---

## Assertions Used

| Assertion | Usage |
|-----------|-------|
| `assertNotNull()` | Verify response is not null |
| `assertEquals()` | Compare expected and actual values |
| `assertThrows()` | Verify exception is thrown |
| `assertNotEquals()` | Verify values are different (unique tokens) |
| `assertTrue()` | Verify condition is true (UUID format) |
| `assertFalse()` | Verify condition is false |

---

## Mocking Patterns

### Repository Mocking
```java
// Mock finding by username
when(studentRepository.findByUsername("john_student")).thenReturn(Optional.of(testStudent));

// Mock saving new student
when(studentRepository.save(any(Student.class))).thenReturn(newStudent);

// Mock empty result
when(studentRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());
```

### Verification
```java
// Verify method was called exactly once
verify(studentRepository, times(1)).findByUsername("john_student");

// Verify method was never called
verify(studentRepository, never()).save(any(Student.class));
```

---

## Coverage Summary

### Methods Tested
- ✅ `AuthService.login(AuthRequest)`
- ✅ `AuthService.signup(SignupRequest)`
- ✅ `AuthService.generateToken()` (indirectly)

### Scenarios Covered
- ✅ Successful student login
- ✅ Successful instructor login
- ✅ Failed login (wrong password)
- ✅ Failed login (non-existent user)
- ✅ Failed login (invalid user type)
- ✅ Successful student signup
- ✅ Successful instructor signup
- ✅ Failed signup (duplicate username)
- ✅ Failed signup (duplicate email)
- ✅ Failed signup (invalid user type)
- ✅ Case-insensitive user type handling
- ✅ Token generation and uniqueness
- ✅ User information preservation
- ✅ UUID token format

---

## Test Execution

### Run All Tests
```bash
.\mvnw clean test
```

### Run Only AuthService Tests
```bash
.\mvnw clean test -Dtest=AuthServiceTest
```

### Run Tests with Verbose Output
```bash
.\mvnw clean test -DtestFailureIgnore=true -X
```

### Generate Test Report
```bash
.\mvnw clean test
# Reports located in: target/surefire-reports/
```

---

## Test Results

### Latest Run
```
Tests run: 21
Failures: 0
Errors: 0
Skipped: 0
Time elapsed: 1.601 s
Status: ✅ BUILD SUCCESS
```

---

## Best Practices Demonstrated

✅ **AAA Pattern (Arrange-Act-Assert)**
- Clear separation of test phases
- Easy to understand test flow

✅ **Descriptive Test Names**
- Using `@DisplayName` for human-readable names
- Method names clearly indicate what is being tested

✅ **Test Isolation**
- Each test is independent
- Using `@BeforeEach` to reset test state

✅ **Proper Mocking**
- Using Mockito for repository mocking
- Verifying mock interactions

✅ **Edge Case Coverage**
- Testing error conditions
- Testing case-insensitive input
- Testing duplicate data scenarios

✅ **Assertion Clarity**
- Using specific assertions
- Clear expected vs actual values

---

## Dependencies

The test class requires the following Maven dependencies (should already be in pom.xml):

```xml
<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<!-- Mockito JUnit Jupiter -->
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## Future Enhancements

Potential additions to test coverage:

1. **Integration Tests** - Test with real database
2. **Performance Tests** - Test login/signup performance
3. **Concurrency Tests** - Test multiple simultaneous logins
4. **Security Tests** - Test password validation rules
5. **Email Validation Tests** - Test email format validation
6. **Token Expiration Tests** - Test token TTL (when implemented)
7. **Password Hashing Tests** - Test bcrypt integration (when implemented)

---

## Troubleshooting

### Tests Won't Compile
- Ensure `@ExtendWith(MockitoExtension.class)` is present
- Check that all imports are correct
- Verify Mockito dependency is in pom.xml

### Tests Won't Run
- Check test class is in `src/test/java` directory
- Ensure class name ends with `Test` or `Tests`
- Verify methods are annotated with `@Test`

### Tests Fail
- Check mock setup is correct
- Verify test data matches expectations
- Check assertion values are correct

---

## Summary

The `AuthServiceTest` class provides comprehensive coverage of the authentication service with 21 well-designed unit tests covering:
- ✅ Login functionality (success and failure cases)
- ✅ Signup functionality (success and failure cases)
- ✅ Edge cases and error handling
- ✅ Token generation and uniqueness
- ✅ User data preservation

All tests follow best practices and are ready for CI/CD integration.

