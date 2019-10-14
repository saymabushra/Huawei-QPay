package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class AccountActiveStatus {
	public static String checkAccountActiveStatus(String strEncryptAccountNumber,String strMasterKey) {
		String METHOD_NAME = "QPAY_Account_Status_Check";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Account_Status_Check";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//		QPAY_Account_Status_Check
//		Account_No:
//		MasterKey:

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("Account_No");
		encryptAccountNumber.setValue(strEncryptAccountNumber);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);

		PropertyInfo masterKey = new PropertyInfo();
		masterKey.setName("MasterKey");
		masterKey.setValue(strMasterKey);
		masterKey.setType(String.class);
		request.addProperty(masterKey);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objCheckAccountActiveStatus = null;
		String strCheckAccountActiveStatusResponse = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objCheckAccountActiveStatus = envelope.getResponse();
			strCheckAccountActiveStatusResponse = objCheckAccountActiveStatus.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return strCheckAccountActiveStatusResponse;

	}
}
