package com.bdmitech.android.huawei.qpay.utils;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {
	// String strResult;

	public String Encrypt(String text, String key) throws Exception {
		Cipher cipherEncrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] byteKeyEncrypt = new byte[16];
		byte[] byteBEncrypt = key.getBytes("UTF-8");
		int len = byteBEncrypt.length;
		if (len > byteKeyEncrypt.length)
			len = byteKeyEncrypt.length;
		System.arraycopy(byteBEncrypt, 0, byteKeyEncrypt, 0, len);
		SecretKeySpec secretKeySpecEncrypt = new SecretKeySpec(byteKeyEncrypt, "AES");
		IvParameterSpec ivParameterSpecEncrypt = new IvParameterSpec(byteKeyEncrypt);
		cipherEncrypt.init(Cipher.ENCRYPT_MODE, secretKeySpecEncrypt, ivParameterSpecEncrypt);
		byte[] byteResultEncrypt = cipherEncrypt.doFinal(text.getBytes("UTF-8"));
		Log.v("GET Result from  final:", byteResultEncrypt.toString());
		String strResultEncrypt = Base64.encodeToString(byteResultEncrypt, 1);
		return strResultEncrypt;
	}

}
