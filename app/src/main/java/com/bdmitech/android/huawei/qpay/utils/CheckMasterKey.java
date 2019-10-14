package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CheckMasterKey {
	public static String strUrl = GlobalData.getStrUrl().replaceAll(" ","%20");
	public static String strNamespace = GlobalData.getStrNamespace().replaceAll(" ","%20");

	// Check Master Key
	// Check Master Key
	// Check Master Key
	public static String checkMasterKey(String strEncryptMasterKey, String strEncryptUserId) {
		String METHOD_NAME = "QPAY_CheckMasterKey";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_CheckMasterKey";
		SoapObject request = new SoapObject(strNamespace, METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo masterKey = new PropertyInfo();
		masterKey.setName("MasterKey");
		masterKey.setValue(strEncryptMasterKey);
		masterKey.setType(String.class);
		request.addProperty(masterKey);

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("AccountNO");
		encryptAccountNumber.setValue(strEncryptUserId);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		Log.v("myApp:", request.toString());
		envelope.implicitTypes = true;
		Object objCheckMasterKey = null;
		String strCheckMasterKey = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(strUrl, 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objCheckMasterKey = envelope.getResponse();
			strCheckMasterKey = objCheckMasterKey.toString();
			Log.v("Check Master Key: ", strCheckMasterKey);
		} catch (Exception exception) {
			Log.e("Check Master Key: ", "Error!!!");
		}

		return strCheckMasterKey;

	}
}
