package utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class HashTools {
    public static byte[] hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 алгоритм не найден", e);
        }
    }
}
