package com.bdmitech.android.huawei.qpay.utils;

public class CheckMasterKeyAndSessionId {

	public static Boolean checkMasterKeyAndSessionId() {
		boolean isSessionIdAndMasterKeyOk = false;
		String strCheckMasterKey = CheckMasterKey.checkMasterKey(GlobalData.getStrMasterKey(), GlobalData.getStrEncryptUserId());
		String strCheckSessionId = CheckSessionId.checkSessionId(GlobalData.getStrSessionId());
		if (strCheckMasterKey.equalsIgnoreCase("Right key")
				&& GlobalData.getStrSessionId().equalsIgnoreCase(strCheckSessionId)) {
			isSessionIdAndMasterKeyOk = true;
		} else {
			isSessionIdAndMasterKeyOk = false;
		}

		return isSessionIdAndMasterKeyOk;

	}
}
