package com.bdmitech.android.huawei.qpay.utils;

import android.util.Base64;
import android.util.Log;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Decryption {
	public String Decrypt(String text, String key) throws Exception {
		Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
		byte[] byteKeyDecrypt = new byte[16];
		byte[] byteBDecrypt = key.getBytes("UTF-8");
		int len = byteBDecrypt.length;
		if (len > byteKeyDecrypt.length)
			len = byteKeyDecrypt.length;
		System.arraycopy(byteBDecrypt, 0, byteKeyDecrypt, 0, len);
		SecretKeySpec secretKeySpecDecrypt = new SecretKeySpec(byteKeyDecrypt, "AES");
		IvParameterSpec ivParameterSpecDecrypt = new IvParameterSpec(byteKeyDecrypt);
		cipherDecrypt.init(Cipher.DECRYPT_MODE, secretKeySpecDecrypt, ivParameterSpecDecrypt);
		byte[] byteResultDecrypt = new byte[text.length()];
		try {
			byteResultDecrypt = cipherDecrypt.doFinal(Base64.decode(text, Base64.DEFAULT));
		} catch (Exception e) {
			Log.i("Error in Decryption", e.toString());
		}
		return new String(byteResultDecrypt, "UTF-8");
	}
}
