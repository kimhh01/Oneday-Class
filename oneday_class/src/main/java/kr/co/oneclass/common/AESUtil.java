package kr.co.oneclass.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    // 💡 32바이트 비밀키 & 16바이트 IV (실무에서는 application.properties 등 외부 설정파일 권장)
    private static final String SECRET_KEY = "OneClassSecretKeyForEncryption32"; // 32자
    private static final String INITIAL_IV = "OneClassIvKey123";               // 16자
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    // 암호화
    public static String encrypt(String text) {
        if (text == null || text.trim().isEmpty()) return text;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(INITIAL_IV.getBytes(StandardCharsets.UTF_8));
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }

    // 복호화
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.trim().isEmpty()) return cipherText;
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(INITIAL_IV.getBytes(StandardCharsets.UTF_8));
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decodedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // 암호화되지 않은 기존 평문 데이터이거나 복호화 실패 시 원본 반환
            return cipherText;
        }
    }
}