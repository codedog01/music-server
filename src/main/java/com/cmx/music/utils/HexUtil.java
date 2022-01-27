package com.cmx.music.utils;

/**
 * @Author: LengAo
 * @Date: 2021/08/31/14:09
 */
public class HexUtil {
	/**
	 * 	字符串转换成十六进制字符串
	 *
	 * @param str
	 * @return
	 */
	public static String str2HexStr(String str) {
		return bytes2HexStr(str.getBytes());
	}

	/**
	 * 	字节数组转换为16进制字符串
	 *
	 * @param bs
	 * @return
	 */
	public static String bytes2HexStr(byte[] bs) {
		if (bs == null) {
			return "";
		}
		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
		}
		return sb.toString().trim();
	}

	/**
	 *	 十六进制转换字符串
	 *
	 * @param hexStr
	 * @return
	 */
	public static String hexStr2Str(String hexStr) {
		return hexStr2Str(hexStr, "utf-8");
	}

	/**
	 * 	十六进制转换字符串
	 *
	 * @param hexStr
	 * @param charsetName
	 * @return
	 */
	public static String hexStr2Str(String hexStr, String charsetName) {
		try {
			return new String(hexStr2Byte(hexStr), charsetName);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("hexStr2Str error!", e);
		}
	}

	/**
	 * 	十六进制转换字符串
	 *
	 * @param hexStr
	 * @return
	 */
	public static byte[] hexStr2Byte(String hexStr) {
		String str = "0123456789ABCDEF";
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int n;
		for (int i = 0; i < bytes.length; i++) {
			n = str.indexOf(hexs[2 * i]) * 16;
			n += str.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (n & 0xff);
		}
		try {
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("hexStr2Byte error!", e);
		}
	}
}
