package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetMasterKeyByUserId {
	private static String strUrl = GlobalData.getStrUrl().replaceAll(" ","%20");
	private static String strNamespace = GlobalData.getStrNamespace().replaceAll(" ","%20");

	// Get Master Key
	// Get Master Key
	// Get Master Key
	public static String getMasterKeyByUserId(String strUserId, String strKey) {
		String METHOD_NAME = "QPAY_GetMasterKey_ByUserID";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GetMasterKey_ByUserID";
		SoapObject request = new SoapObject(strNamespace, METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo userId = new PropertyInfo();
		userId.setName("UserID");
		userId.setValue(strUserId);
		userId.setType(String.class);
		request.addProperty(userId);

		PropertyInfo key = new PropertyInfo();
		key.setName("key");
		key.setValue(strKey);
		key.setType(String.class);
		request.addProperty(key);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		Log.v("myApp:", request.toString());
		envelope.implicitTypes = true;
		Object objGetMasterKeyByUserId = null;
		String strMasterKey = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(strUrl, 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objGetMasterKeyByUserId = envelope.getResponse();
			strMasterKey = objGetMasterKeyByUserId.toString();
			Log.v("Master Key: ", strMasterKey);
		} catch (Exception exception) {
			Log.e("Master Key: ", "Error!!!");
		}

		return strMasterKey;
	}
}
