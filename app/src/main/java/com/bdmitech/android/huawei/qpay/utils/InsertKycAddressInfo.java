package com.bdmitech.android.huawei.qpay.utils;

import junit.framework.Test;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class InsertKycAddressInfo {
	public static String insertAddressInfo(
			String strEncryptAccountNumber,
			String strEncryptPin,
			String strEncryptThanaId,
			String strEncryptPresentAddress,
			String strEncryptPermanentAddress,
			String strEncryptOfficeAddress,
			String strMasterKey) {
		String METHOD_NAME = "QPAY_KYC_Address_Info";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_KYC_Address_Info";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//		QPAY_KYC_Address_Info
//		AccountNo
//		PIN
//		ThanaId
//		PresentAddress
//		ParmanetAddress
//		OfficeAddress
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

		PropertyInfo encryptThanaId = new PropertyInfo();
		encryptThanaId.setName("ThanaId");
		encryptThanaId.setValue(strEncryptThanaId);
		encryptThanaId.setType(String.class);
		request.addProperty(encryptThanaId);

		PropertyInfo encryptPresenetAddress = new PropertyInfo();
		encryptPresenetAddress.setName("PresentAddress");
		encryptPresenetAddress.setValue(strEncryptPresentAddress);
		encryptPresenetAddress.setType(String.class);
		request.addProperty(encryptPresenetAddress);

		PropertyInfo encryptPermanentAddress = new PropertyInfo();
		encryptPermanentAddress.setName("ParmanetAddress");
		encryptPermanentAddress.setValue(strEncryptPermanentAddress);
		encryptPermanentAddress.setType(String.class);
		request.addProperty(encryptPermanentAddress);

		PropertyInfo encryptOfficeAddress = new PropertyInfo();
		encryptOfficeAddress.setName("OfficeAddress");
		encryptOfficeAddress.setValue(strEncryptOfficeAddress);
		encryptOfficeAddress.setType(String.class);
		request.addProperty(encryptOfficeAddress);

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
