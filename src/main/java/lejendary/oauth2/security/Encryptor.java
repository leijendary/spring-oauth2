package lejendary.oauth2.security;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * An encryption utility to encrypt strings to sha-256, sha-512, AES, BCrypt formats.
 *
 * @author Jonathan Leijendekker
 */

@Component
public class Encryptor {

    // ===================================
    // SHA-256 encryption
    // ===================================

    /**
     * Encrypt the string into sha-256 hash format
     *
     * @param s
     * @return Encrypted string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String sha256(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(s.getBytes());
        byte[] digest = md.digest();

        return convertByteToHex(digest);
    }

    /**
     * Encrypt the string into sha-256 hash format. Requires java 8 and above
     *
     * @param s
     * @return Encrypted string using the Base64 encoder
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String sha256Base64(String s) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(s.getBytes());
        byte[] digest = md.digest();

        return Base64.getEncoder().encodeToString(digest);

    }

    // ===================================
    // SHA-512 encryption
    // ===================================

    /**
     * Encrypt the string into sha-512 hash format
     *
     * @param s
     * @return Encrypted string
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String sha512(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");

        md.update(s.getBytes());
        byte[] digest = md.digest();

        return convertByteToHex(digest);
    }

    /**
     * Encrypt the string into sha-512 hash format. Requires java 8 and above
     *
     * @param s
     * @return Encrypted string using the Base64 encoder
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String sha512Base64(String s) throws NoSuchAlgorithmException {
        final MessageDigest md = MessageDigest.getInstance("SHA-512");

        md.update(s.getBytes());
        byte[] digest = md.digest();

        return Base64.getEncoder().encodeToString(digest);

    }

    private static String convertByteToHex(byte data[]) {
        StringBuilder hexData = new StringBuilder();
        for (byte aData : data) hexData.append(Integer.toString((aData & 0xff) + 0x100, 16).substring(1));

        return hexData.toString();
    }

    // ===================================
    // AES encryption
    // ===================================

    private static byte[] key = {
            0x2d, 0x2a, 0x2d, 0x42, 0x55, 0x49, 0x4c, 0x44, 0x41, 0x43, 0x4f, 0x44, 0x45, 0x2d, 0x2a, 0x2d
    };

    public String aesEncrypt(String plainText) {
        return getAesValue(plainText, Cipher.ENCRYPT_MODE);
    }

    public String aesDecrypt(String encryptedText) {
        return getAesValue(encryptedText, Cipher.DECRYPT_MODE);
    }

    private static String getAesValue(String text, int mode) {
        String value = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            cipher.init(mode, secretKey);

            byte[] cipherText;

            if (mode == Cipher.ENCRYPT_MODE) {
                cipherText = cipher.doFinal(text.getBytes("UTF8"));
                value = new String(Base64.getEncoder().encode(cipherText), "UTF-8");
            } else if (mode == Cipher.DECRYPT_MODE) {
                cipherText = Base64.getDecoder().decode(text.getBytes("UTF8"));
                value = new String(cipher.doFinal(cipherText), "UTF-8");
            }

            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
