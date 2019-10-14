package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CheckSessionId {
	public static String checkSessionId(String strSessionId) {
		String METHOD_NAME = "QPAY_CheckSessionID";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_CheckSessionID";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo sessionId = new PropertyInfo();
		sessionId.setName("Session");
		sessionId.setValue(strSessionId);
		sessionId.setType(String.class);
		request.addProperty(sessionId);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objCheckSessionId = null;
		String strCheckSessionId = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objCheckSessionId = envelope.getResponse();
			strCheckSessionId = objCheckSessionId.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return strCheckSessionId;

	}
}
