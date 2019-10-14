package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DeleteFavoriteList {
	public static String deleteFavoriteList(String strEncryptAccountNumber,String strEncryptCaption,String strMasterKey) {
		String METHOD_NAME = "QPAY_Delete_Fav_List";
		String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Delete_Fav_List";
		SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

		// Declare the version of the SOAP request
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//                                QPAY_Delete_Fav_List
//                                AccountNo
//                                Caption
//                                strMasterKey

		PropertyInfo encryptAccountNumber = new PropertyInfo();
		encryptAccountNumber.setName("AccountNo");
		encryptAccountNumber.setValue(strEncryptAccountNumber);
		encryptAccountNumber.setType(String.class);
		request.addProperty(encryptAccountNumber);

		PropertyInfo encryptCaption = new PropertyInfo();
		encryptCaption.setName("Caption");
		encryptCaption.setValue(strEncryptCaption);
		encryptCaption.setType(String.class);
		request.addProperty(encryptCaption);

		PropertyInfo masterKey = new PropertyInfo();
		masterKey.setName("strMasterKey");
		masterKey.setValue(strMasterKey);
		masterKey.setType(String.class);
		request.addProperty(masterKey);

		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);
		envelope.implicitTypes = true;
		Object objDeleteFavoriteList = null;
		String strDeleteFavoriteListResponse = "";

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			objDeleteFavoriteList = envelope.getResponse();
			strDeleteFavoriteListResponse = objDeleteFavoriteList.toString();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return strDeleteFavoriteListResponse;
	}
}
