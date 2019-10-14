package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CheckToken {
	public static String checkToken(String strEncryptAccountNumber,String strEncryptToken,String strMasterKey) {
		String METHOD_NAME = "QPAY_CheckToken";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_CheckToken";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("AccounNO");
		encryptAccountNumber.setValue(strEncryptAccountNumber);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);

		PropertyInfo encryptToken = new PropertyInfo();
		encryptToken.setName("Token");
		encryptToken.setValue(strEncryptToken);
		encryptToken.setType(String.class);
		request.addProperty(encryptToken);

		PropertyInfo masterKey = new PropertyInfo();
		masterKey.setName("MasterKey");
		masterKey.setValue(strMasterKey);
		masterKey.setType(String.class);
		request.addProperty(masterKey);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objCheckToken = null;
		String strCheckToken = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objCheckToken = envelope.getResponse();
			strCheckToken = objCheckToken.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return strCheckToken;

	}
}
