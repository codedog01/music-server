package com.cmx.music.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;

public class RSAUtil {
	private static final Log log = LogFactory.getLog(RSAUtil.class);
	private static final String kpStr = "rO0ABXNyABVqYXZhLnNlY3VyaXR5LktleVBhaXKXAww60s0SkwIAAkwACnByaXZhdGVLZXl0ABpMamF2YS9zZWN1cml0eS9Qcml2YXRlS2V5O0wACXB1YmxpY0tleXQAGUxqYXZhL3NlY3VyaXR5L1B1YmxpY0tleTt4cHNyABRqYXZhLnNlY3VyaXR5LktleVJlcL35T7OImqVDAgAETAAJYWxnb3JpdGhtdAASTGphdmEvbGFuZy9TdHJpbmc7WwAHZW5jb2RlZHQAAltCTAAGZm9ybWF0cQB+AAVMAAR0eXBldAAbTGphdmEvc2VjdXJpdHkvS2V5UmVwJFR5cGU7eHB0AANSU0F1cgACW0Ks8xf4BghU4AIAAHhwAAACfDCCAngCAQAwDQYJKoZIhvcNAQEBBQAEggJiMIICXgIBAAKBgQDNITp//CLrnCVMQwwVJa69/bQ569g6El9HTtNseBE6nnS6J9mmpiLYIvgEOvRpGd1+hdGV+RQnvGSm6/EkziYDTdzeN9mwj8XK/mPeKoWA/uETyvRDoxxbUqi+Et/JH9Y10TFL3YYAaoDyIgWucbFLgMCJAvvY2f5DEYiB/1YPlwIDAQABAoGAOFIpR76or9wdeYTnKl9ATTX9Z/HTWo50zpVcA2osANZE4l/SqKjw5DslsbOmK71ITVbcprrOx+I4GISrnxli4gNfMfuQr7O+3jQo87FJwj/N3m852S4ibWxWbGjZLmyPfRr8fsAkyqVMfiuBw2g4rbI7WZhVuYyCO6n4zcJl6uECQQDwvFWw1WDMs5ChCYCL/Wes9tAWK5AvIvsv1+WN20m3aEfxCyGvY5ayOWXExeJiwGujoYERv/DZLhIzUruzGdapAkEA2iLuwGYePvreN1m952jyzLEuuFYyVE97wP5N9ZZOVB1S7+sR18/My44X2G1eBvj8KZcqfWhho/FWVsNAWRncPwJBAK0AG3CmFxdkX6ZqxrdTl0LkZ+vWyJonQG5Eb9I+6b3lMfYWctGw9uYDe5AqSSOAdpo0ASY5UqMwiaHabcsq4WkCQQCO4Vi/X2QqXooVeUvBeuWmm9v9VEBtJw7pb+lClqxBvl9n1PKACJWdMzp9Qc3YBViuKVzkfe2Ow/KIrCXG9wBjAkEA1mzT0EdzAzQGzWe7jNzWgVsOthpzepinTWlf1m37xVputCgFI+FEVGj5/dMBYKcyA6Ts0GZoqIAM4fGkVtzez3QABlBLQ1MjOH5yABlqYXZhLnNlY3VyaXR5LktleVJlcCRUeXBlAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAHUFJJVkFURXNxAH4ABHEAfgAJdXEAfgAKAAAAojCBnzANBgkqhkiG9w0BAQEFAAOBjQAwgYkCgYEAzSE6f/wi65wlTEMMFSWuvf20OevYOhJfR07TbHgROp50uifZpqYi2CL4BDr0aRndfoXRlfkUJ7xkpuvxJM4mA03c3jfZsI/Fyv5j3iqFgP7hE8r0Q6McW1KovhLfyR/WNdExS92GAGqA8iIFrnGxS4DAiQL72Nn+QxGIgf9WD5cCAwEAAXQABVguNTA5fnEAfgANdAAGUFVCTElD";
	public static final String publicKeyStr = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDNITp//CLrnCVMQwwVJa69/bQ569g6El9HTtNseBE6nnS6J9mmpiLYIvgEOvRpGd1+hdGV+RQnvGSm6/EkziYDTdzeN9mwj8XK/mPeKoWA/uETyvRDoxxbUqi+Et/JH9Y10TFL3YYAaoDyIgWucbFLgMCJAvvY2f5DEYiB/1YPlwIDAQAB";
	// 临时使用
	private static String RSAKeyStore = "d:/RSAKey.txt";

	private static KeyPair pair;
	static {
		pair = (KeyPair) byteToObject(Base64.decodeBase64(kpStr));
	}

	public static Object byteToObject(byte[] bytes) {
		Object obj = null;
		try {
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);
			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			log.error("byteToObject error!", e);
			throw new RuntimeException("byteToObject error!", e);
		}
		return obj;
	}

	public static void saveKeyPair(KeyPair kp) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(RSAKeyStore);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(kp);
			oos.close();
			fos.close();
		} catch (Exception e) {
			log.error("saveKeyPair error!", e);
			throw new RuntimeException("saveKeyPair error!", e);
		}
	}

	public static String encrypt(String source) {
		byte[] data = source.getBytes();
		Cipher ci = null;
		try {
			ci = Cipher.getInstance("RSA");
			ci.init(Cipher.ENCRYPT_MODE, pair.getPublic());
			return Base64.encodeBase64String(ci.doFinal(data));
		} catch (Exception e) {
			log.error("RSAUtil encrypt error！", e);
			throw new RuntimeException("RSAUtil encrypt error！", e);
		}

	}

	public static String encryptHex(String source) {
		byte[] data = source.getBytes();
		Cipher ci = null;
		try {
			ci = Cipher.getInstance("RSA");
			ci.init(Cipher.ENCRYPT_MODE, pair.getPublic());
			byte[] secret = ci.doFinal(data);
			return HexUtil.bytes2HexStr(Base64.encodeBase64(secret));
		} catch (Exception e) {
			log.error("RSAUtil encrypt error！", e);
			throw new RuntimeException("RSAUtil encrypt error！", e);
		}

	}

	public static String decryptHex(String source) {
		byte[] data = HexUtil.hexStr2Byte(source);
		Cipher ci = null;
		try {
			ci = Cipher.getInstance("RSA");
			ci.init(Cipher.DECRYPT_MODE, pair.getPrivate());
			byte[] secret = Base64.decodeBase64(data);
			return new String(ci.doFinal(secret));
		} catch (Exception e) {
			log.error("RSAUtil decrypt error！", e);
			throw new RuntimeException("RSAUtil decrypt error！", e);
		}
	}

	public static String decrypt(String source) {
		byte[] data = Base64.decodeBase64(source);
		Cipher ci = null;
		try {
			ci = Cipher.getInstance("RSA");
			ci.init(Cipher.DECRYPT_MODE, pair.getPrivate());
			return new String(ci.doFinal(data));
		} catch (Exception e) {
			log.error("RSAUtil decrypt error！", e);
			throw new RuntimeException("RSAUtil decrypt error！", e);
		}
	}



}
