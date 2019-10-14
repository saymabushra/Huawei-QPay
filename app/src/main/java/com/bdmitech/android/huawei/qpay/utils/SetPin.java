package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SetPin {
	public static String setPin(String strEncryptPin,String strEncryptAccountNumber,String strMasterKey) {
		String METHOD_NAME = "QPAY_SetPIN";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_SetPIN";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo encryptPin = new PropertyInfo();
		encryptPin.setName("PIN");
		encryptPin.setValue(strEncryptPin);
		encryptPin.setType(String.class);
		request.addProperty(encryptPin);

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
		Object objSetPinResponse = null;
		String strSetPin = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objSetPinResponse = envelope.getResponse();
			strSetPin = objSetPinResponse.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return strSetPin;

	}
}
