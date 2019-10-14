package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class InsertKycBankInfo {
	public static String insertBankInfo(
			String strEncryptAccountNumber,
			String strEncryptPin,
			String strEncryptBankName,
			String strEncryptBankBranch,
			String strEncryptBankAccountNumber,
			String strEncryptRemark,
			String strMasterKey) {
		String METHOD_NAME = "QPAY_KYC_bankInfo_Info";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_KYC_bankInfo_Info";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//		QPAY_KYC_bankInfo_Info
//		AccountNo
//		PIN
//		BankName
//		Bankbranch
//		BankAccountNumber
//		Remarks
//		strMasterKey

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("AccountNo");
		encryptAccountNumber.setValue(strEncryptAccountNumber);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);

		PropertyInfo encryptPin = new PropertyInfo();
		encryptPin.setName("PIN");
		encryptPin.setValue(strEncryptPin);
		encryptPin.setType(String.class);
		request.addProperty(encryptPin);

		PropertyInfo encryptBankName = new PropertyInfo();
		encryptBankName.setName("BankName");
		encryptBankName.setValue(strEncryptBankName);
		encryptBankName.setType(String.class);
		request.addProperty(encryptBankName);

		PropertyInfo encryptBankBranch = new PropertyInfo();
		encryptBankBranch.setName("Bankbranch");
		encryptBankBranch.setValue(strEncryptBankBranch);
		encryptBankBranch.setType(String.class);
		request.addProperty(encryptBankBranch);

		PropertyInfo encryptBankAccountNumber = new PropertyInfo();
		encryptBankAccountNumber.setName("BankAccountNumber");
		encryptBankAccountNumber.setValue(strEncryptBankAccountNumber);
		encryptBankAccountNumber.setType(String.class);
		request.addProperty(encryptBankAccountNumber);

		PropertyInfo encryptRemark = new PropertyInfo();
		encryptRemark.setName("Remarks");
		encryptRemark.setValue(strEncryptRemark);
		encryptRemark.setType(String.class);
		request.addProperty(encryptRemark);

		PropertyInfo masterKey = new PropertyInfo();
		masterKey.setName("strMasterKey");
		masterKey.setValue(strMasterKey);
		masterKey.setType(String.class);
		request.addProperty(masterKey);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objServerResponse = null;
		String strServerResponse = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objServerResponse = envelope.getResponse();
			strServerResponse = objServerResponse.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return strServerResponse;
	}
}
