package org.zigmoi.expncal.commons;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import org.apache.commons.codec.binary.Base64;
import org.zigmoi.expncal.exceptions.CipherException;

public class CipherOp {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "PBEWithMD5AndDES";
    private static final byte[] hash = new byte[]{12, 34, 67, 88, 43, 34, 77, 43};
    private static final int iterationCount = 17;
    private static final String csUtf8 = "UTF-8";
    private static final String SALT = "c!H@a#W$a%D^s&*I(m)B-s+";

    static byte[] encryptRSA(String plainText, byte[] pkByte) {
        try {
            return encryptRSA(plainText, getPublicKeyRSA(pkByte));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CipherException("Exception : " + ex);
        }
    }

    static byte[] decryptRSA(byte[] encryptedByte, byte[] pkByte) {
        try {
            return decryptRSA(encryptedByte, getPrivateKeyRSA(pkByte));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new CipherException("Exception : " + ex);
        }
    }

    public static Object[] getKeyPairRSA() throws Exception {

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyGen.initialize(1024);
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        byte[] publicKeyByte = publicKey.getEncoded();
        byte[] privateKeyByte = privateKey.getEncoded();
        return new Object[]{publicKeyByte, privateKeyByte};
    }

    static byte[] encryptRSA(String plainText, PublicKey pk) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            return cipherText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            throw new CipherException("Exception : " + ex);
        }
    }

    static byte[] decryptRSA(byte[] encryptedByte, PrivateKey pk) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, pk);
            byte[] newPlainText = cipher.doFinal(encryptedByte);
            return newPlainText;
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
            throw new CipherException("Exception : " + ex);
        }
    }

    static PublicKey getPublicKeyRSA(byte[] pkByte) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(pkByte);
        return kf.generatePublic(publicKeySpec);
    }

    static PrivateKey getPrivateKeyRSA(byte[] pkByte) throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(pkByte);
        return kf.generatePrivate(privateSpec);
    }

    public static String encryptAES(String plainText, String encKey) {
        return encryptAES(plainText.getBytes(), encKey);
    }

    public static String encryptAES(byte[] data, String encKey) {
        try {
            KeySpec keySpec = new PBEKeySpec(encKey.toCharArray(), hash, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(AES_ALGORITHM).generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(hash, iterationCount);
            Cipher ecipher = Cipher.getInstance(key.getAlgorithm());
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            byte[] out = ecipher.doFinal(data);
            return new String(Base64.encodeBase64(out), csUtf8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            throw new CipherException("Exception : " + e);
        }
    }

    public static String decryptAES(String encryptedText, String encKey) {
        return decryptAES(encryptedText.getBytes(), encKey);
    }

    public static String decryptAES(byte[] encrypted, String encKey) {
        try {
            KeySpec keySpec = new PBEKeySpec(encKey.toCharArray(), hash, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance(AES_ALGORITHM).generateSecret(keySpec);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(hash, iterationCount);
            Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
            byte[] enc = Base64.decodeBase64(encrypted);
            byte[] utf8 = dcipher.doFinal(enc);
            return new String(utf8, csUtf8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            throw new CipherException("Exception : " + e);
        }
    }

    public static byte[] decryptAESBytes(byte[] encrypted, String encKey) throws Exception {

        KeySpec keySpec = new PBEKeySpec(encKey.toCharArray(), hash, iterationCount);
        SecretKey key = SecretKeyFactory.getInstance(AES_ALGORITHM).generateSecret(keySpec);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(hash, iterationCount);
        Cipher dcipher = Cipher.getInstance(key.getAlgorithm());
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
        byte[] enc = Base64.decodeBase64(encrypted);
        return dcipher.doFinal(enc);
    }

    public static String encryptRSAKey(byte[] key, String password) throws Exception {
        return encryptAES(key, password);
    }

    private static byte[] decryptRSAKeyBytes(String key, String password) throws Exception {
        return decryptAESBytes(key.getBytes(), password);
    }

    public static byte[] encryptRSA(String plainText, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return encryptRSA(plainText,
            getPublicKeyRSA(Base64.decodeBase64(publicKey.getBytes())));
    }

    public static String decryptRSA(byte[] encrypted, String privateEncKey, String privateKey) throws Exception {
        return new String(decryptRSA(encrypted,
            getPrivateKeyRSA(decryptRSAKeyBytes(privateEncKey, privateKey))), csUtf8);
    }

//Takes a string, and converts it to md5 hashed string.
    public static String md5Hash(String message) {
        try {
            if (null == message) {
                return null;
            }
            message = message + SALT; //adding a salt to the string before it gets hashed.
            MessageDigest digest = MessageDigest.getInstance("MD5"); //Create MessageDigest object for MD5
            digest.update(message.getBytes(), 0, message.length()); //Update input string in message digest
            return new BigInteger(1, digest.digest()).toString(16); //Converts message digest value in base 16 (hex)
        } catch (NoSuchAlgorithmException ex) {
            throw new CipherException("Exception : " + ex);
        }
    }

    public static void main(String[] args1) throws Exception {

        Object[] keypair = getKeyPairRSA();
        byte[] pubKeyByte = (byte[]) keypair[0];
        byte[] priKeyByte = (byte[]) keypair[1];
        String str = "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff"
            + "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";

        String enc;
        byte[] dec;
        byte[] decrypted;

//		enc = new sun.misc.BASE64Encoder().encode(encrypted);
//		dec = new sun.misc.BASE64Decoder().decodeBuffer(enc);
//==========		
//		byte[] encrypted = CipherOp.encryptRSA(str, pubKeyByte);
//		enc = new String(Base64.encodeBase64(encrypted), "UTF-8");
//		dec = Base64.decodeBase64(enc.getBytes());
//		decrypted = CipherOp.decryptRSA(dec, Base64.decodeBase64(Base64.encodeBase64(priKeyByte)));
//
////		decrypted = CipherOp.decrypt(dec, priKeyByte);
////		byte[] decrypted = CipherOp.decrypt(encrypted, priKeyByte);
//		System.out.println("str: " + str);
//		System.out.println("baseenc: " + enc);
//		System.out.println("basedec: " + new String(dec, "UTF-8"));
//		System.out.println("dec str: " + new String(decrypted, "UTF-8"));
        //=============
        String pass = "asdasdasdasd";
        System.out.println(CipherOp.encryptAES(str, pass));
        System.out.println(CipherOp.decryptAES(CipherOp.encryptAES(str, pass), pass));
    }
}
