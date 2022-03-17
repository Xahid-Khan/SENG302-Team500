package nz.ac.canterbury.seng302.identityprovider.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
  public String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
    var secureRandomGenerator = new SecureRandom();
    var salt = secureRandomGenerator.generateSeed(12);

    var pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 20, 512);
    var keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");

    var hash = keyFactory.generateSecret(pbeKeySpec).getEncoded();
    return Base64.getEncoder().encodeToString(hash);
  }
}
