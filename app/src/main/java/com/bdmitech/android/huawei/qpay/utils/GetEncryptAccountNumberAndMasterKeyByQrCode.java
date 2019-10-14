package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetEncryptAccountNumberAndMasterKeyByQrCode {
	public static String getEncryptAccountNumberAndMasterKeyByQrCode(String strEncryptQrCodeContent) {
		String METHOD_NAME = "QPAY_Get_Account_BY_QR_CARD";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Get_Account_BY_QR_CARD";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		PropertyInfo encryptQrCodeContent = new PropertyInfo();
		encryptQrCodeContent.setName("strRequest");
		encryptQrCodeContent.setValue(strEncryptQrCodeContent);
		encryptQrCodeContent.setType(String.class);
		request.addProperty(encryptQrCodeContent);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objAccountNumberByQrCode = null;
		String strAccountNumberByQrCodeResponse = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objAccountNumberByQrCode = envelope.getResponse();
			strAccountNumberByQrCodeResponse = objAccountNumberByQrCode.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return strAccountNumberByQrCodeResponse;

	}
}
