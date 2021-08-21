package com.comp90018.assignment2.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * data encrypter
 * @author xiaotian
 */
public class Encrypter {
    /**
     * MD5 and SHA-1 mixed encryption
     *
     * @param inputText Content to encrypt
     *
     * @return String The password after MD5 and SHA-1 mixed encryption
     */
    public static String md5AndSha(String inputText) {
        return sha(md5(inputText));
    }


    /**
     * md5encryption
     *
     * @param inputText content to encrypt
     *
     * @return String  after encryption
     */
    public static String md5(String inputText) {
        return encrypt(inputText, "md5");
    }


    /**
     * sha-1 encryption
     *
     * @param inputText content to encrypt
     *
     * @return  val after encryption
     */
    public static String sha(String inputText) {
        return encrypt(inputText, "sha-1");
    }


    /**
     * encryption with different algorithm
     *
     * @param inputText   content for encryption
     *
     * @param algorithmName  Encryption algorithm name: MD5 or SHA-1, case insensitive
     *
     * @return String  The result of MD5 or SHA-1 encryption
     */
    private static String encrypt(String inputText, String algorithmName) {
        if (inputText == null || "".equals(inputText.trim())) {
            throw new IllegalArgumentException("Please enter what you want to encrypt");
        }
        if (algorithmName == null || "".equals(algorithmName.trim())) {
            algorithmName = "md5";
        }
        String encryptText = null;
        try {
            MessageDigest m = MessageDigest.getInstance(algorithmName);
            m.update(inputText.getBytes("UTF8"));
            byte s[] = m.digest();
            return hex(s);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encryptText;
    }



    /**
     * Converts a byte array to a hexadecimal string
     *
     * @param arr bytes[] to convert
     *
     * @return  String Returns a hexadecimal string
     */
    private static String hex(byte[] arr) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < arr.length; ++i) {
            sb.append(Integer.toHexString((arr[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }



    /**
     * Generate a password with random salt
     *
     * @param password content for encryption
     *
     * @return String A code containing random salt
     */
    public static String encrypt(String password) {
        // 16 bits random number
        Random random = new Random();
        StringBuilder sBuilder = new StringBuilder(16);
        sBuilder.append(random.nextInt(99999999)).append(random.nextInt(99999999));
        int len = sBuilder.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sBuilder.append("0");
            }
        }
        // Generate the final compaction salt
        String salt = sBuilder.toString();
        password = md5AndSha(password + salt);

        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return String.valueOf(cs);
    }

    /**
     * Verify that the added salt is the same as the original password
     *
     * @param password original
     *
     * @param password after
     *
     *@return boolean True means the password is consistent with the original one
     */
    public static boolean verifyPlainAndCipher(String password, String md5str) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5str.charAt(i);
            cs1[i / 3 * 2 + 1] = md5str.charAt(i + 2);
            cs2[i / 3] = md5str.charAt(i + 1);
        }
        String salt = new String(cs2);
        String encrypPassword = md5AndSha(password + salt);

        // 加密密码去掉最后8位数
        encrypPassword = encrypPassword.substring(0 , encrypPassword.length() - 8);

        return encrypPassword.equals(String.valueOf(cs1));
    }


    /**
     * 测试用
     * @param args
     */
    public static void main(String[] args) {
        // original text
        String plaintext = "123456";

        // 获取密文用于存储
        String ciphertext = encrypt(plaintext);
        System.out.println("MD5：" + ciphertext);

        // 显示存储的密文和输入的明文的比对结果
        System.out.println("Is matched:" + verifyPlainAndCipher(plaintext, ciphertext));
    }
}
