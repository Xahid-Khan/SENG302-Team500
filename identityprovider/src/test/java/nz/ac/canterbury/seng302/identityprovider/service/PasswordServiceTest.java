package nz.ac.canterbury.seng302.identityprovider.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PasswordServiceTest {
  @Autowired
  private PasswordService passwordService;

  @Test
  public void testHashPasswordReturnsVaryForSameInput()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    // Makes sure that rainbow tables won't work.
    var hash1 = passwordService.hashPassword("test1");
    var hash2 = passwordService.hashPassword("test1");

    assertNotEquals(hash1, hash2);
  }

  @Test
  public void testHashPasswordReturnsVary()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    var hash1 = passwordService.hashPassword("test1");
    var hash2 = passwordService.hashPassword("test2");

    assertNotEquals(hash1, hash2);
  }

  @Test
  public void verifyPasswordAcceptsCorrectPassword()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    var hash = passwordService.hashPassword("test1");

    assertTrue(passwordService.verifyPassword("test1", hash));
  }

  @Test
  public void verifyPasswordRejectsInvalidPassword()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    var hash = passwordService.hashPassword("test1");

    assertFalse(passwordService.verifyPassword("test2", hash));
  }

  @Test
  public void verifyPasswordRejectsInvalidHashFormat()
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    var hash = passwordService.hashPassword("test1");

    assertFalse(passwordService.verifyPassword("test1", "2|" + hash.substring(2)));
  }
}
