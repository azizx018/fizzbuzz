package net.yorksolutions.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Mockito - is a framework that makes it easy to mock
//    java classes
// jest.fn()


@ExtendWith(MockitoExtension.class)
public class UserControllerTests {

    @InjectMocks
    @Spy
    UserController controller;

    @Mock
    UserAccountRepository repository;

    @Mock
    HashMap<UUID, Long> tokenMap;


    @Test
    void itShouldReturnUnauthWhenUserIsWrong() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();
        // ArgumentMatcher - it is a test that can be run on an argument
        // any() - always return true
        // eq(<expected>) - the passed argument must match <expected>
        lenient().when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());
        // lenient() - I know that a stubbing will not be called in the passing case,
        //      but I want to test for it anyway
        lenient().when(repository.findByUsernameAndPassword(not(eq(username)), eq(password)))
                .thenReturn(Optional.of(new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> controller.login(username, password));
    }

    @Test
    void itShouldReturnUnauthWhenPassIsWrong() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();
        lenient().when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.empty());
        lenient().when(repository.findByUsernameAndPassword(eq(username), not(eq(password))))
                .thenReturn(Optional.of(new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> controller.login(username, password));
    }

    @Test
    void itShouldMapTheUUIDToTheIdWhenLoginSuccess() {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();
        final Long id = (long) (Math.random() * 9999999); // the id of the user account associated with username, password
        final UserAccount expected = new UserAccount();
        expected.id = id;
        expected.username = username;
        expected.password = password;
        when(repository.findByUsernameAndPassword(username, password))
                .thenReturn(Optional.of(expected));
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        when(tokenMap.put(captor.capture(), eq(id))).thenReturn(0L);
        final var token = controller.login(username, password);
        assertEquals(token, captor.getValue());
    }

    @Test
    void itShouldReturnInvalidIfUsernameExists() {
        final String username = "some username";
        when(repository.findByUsername(username)).thenReturn(Optional.of(
                new UserAccount()));
        assertThrows(ResponseStatusException.class, () -> controller.register(username, ""));
    }

    @Test
    void itShouldSaveANewUserAccountWhenUserIsUnique() {
        final String username = "some username";
        final String password = "some password";
        when(repository.findByUsername(username)).thenReturn(Optional.empty());
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        when(repository.save(captor.capture())).thenReturn(new UserAccount());
        Assertions.assertDoesNotThrow(() -> controller.register(username, password));
        assertEquals(new UserAccount(), captor.getValue());
    }

    @Test
    void itShouldNotThrowWhenTokenIsCorrect() {
        final var token = UUID.randomUUID();
        when(tokenMap.containsKey(token)).thenReturn(true);
        assertDoesNotThrow(() -> controller.isAuthorized(token));

    }

    @Test
    void itShouldThrowUnauthWhenTokenIsBad() {
        final UUID token = UUID.randomUUID();
        when(tokenMap.containsKey(token)).thenReturn(false);
        final ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> controller.isAuthorized(token));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

}
