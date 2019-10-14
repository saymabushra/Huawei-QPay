package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetSessionId {

	// Method retrieving Session ID
	// Method retrieving Session ID
	// Method retrieving Session ID
	public static String getSessionId(String strEncryptAccountNumber, String strEncryptMasterKey) {
		String METHOD_NAME = "QPAY_GetSessionID";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GetSessionID";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("AccountNO");
		encryptAccountNumber.setValue(strEncryptAccountNumber);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);
		
		PropertyInfo encryptMasterKey = new PropertyInfo();
		encryptMasterKey.setName("MasterKey");
		encryptMasterKey.setValue(strEncryptMasterKey);
		encryptMasterKey.setType(String.class);
		request.addProperty(encryptMasterKey);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		Log.v("myApp:", request.toString());
		envelope.implicitTypes = true;
		Object objSessionId = null;
		String strSessionId = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objSessionId = envelope.getResponse();
			strSessionId = objSessionId.toString();
			Log.v("Session ID: ", strSessionId);
		} catch (Exception exception) {
			Log.e("Session ID: ", "Error!!!");
		}

		return strSessionId;

	}
}
