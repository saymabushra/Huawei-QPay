package com.bdmitech.android.huawei.qpay.utils;

import junit.framework.Test;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class InsertKycIntroducerInfo {
	public static String insertIntroducerInfo(
			String strEncryptAccountNumber,
			String strEncryptPin,
			String strEncryptIntroducerName,
			String strEncryptIntroducerMobileNumber,
			String strEncryptIntroducerAddress,
			String strEncryptIntroducerOccupation,
			String strEncryptRemark,
			String strMasterKey) {
		String METHOD_NAME = "QPAY_KYC_IntroducerInfo_Info";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_KYC_IntroducerInfo_Info";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//		QPAY_KYC_IntroducerInfo_Info
//		AccountNo
//		PIN
//		IntroducerName
//		IntroducerMobile
//		IntroducerAddress
//		IntroducerOccupation
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

		PropertyInfo encryptIntroducerName = new PropertyInfo();
		encryptIntroducerName.setName("IntroducerName");
		encryptIntroducerName.setValue(strEncryptIntroducerName);
		encryptIntroducerName.setType(String.class);
		request.addProperty(encryptIntroducerName);

		PropertyInfo encryptIntroducerMobileNumber = new PropertyInfo();
		encryptIntroducerMobileNumber.setName("IntroducerMobile");
		encryptIntroducerMobileNumber.setValue(strEncryptIntroducerMobileNumber);
		encryptIntroducerMobileNumber.setType(String.class);
		request.addProperty(encryptIntroducerMobileNumber);

		PropertyInfo encryptIntroducerAddress = new PropertyInfo();
		encryptIntroducerAddress.setName("IntroducerAddress");
		encryptIntroducerAddress.setValue(strEncryptIntroducerAddress);
		encryptIntroducerAddress.setType(String.class);
		request.addProperty(encryptIntroducerAddress);

		PropertyInfo encryptIntroducerOccupation = new PropertyInfo();
		encryptIntroducerOccupation.setName("IntroducerOccupation");
		encryptIntroducerOccupation.setValue(strEncryptIntroducerOccupation);
		encryptIntroducerOccupation.setType(String.class);
		request.addProperty(encryptIntroducerOccupation);

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
