package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetMasterKeyAndAccountNumberByDeviceId {
    private static String strUrl = GlobalData.getStrUrl().replaceAll(" ", "%20");
    private static String strNamespace = GlobalData.getStrNamespace().replaceAll(" ", "%20");

    // Get Master Key
    // Get Master Key
    // Get Master Key
    public static String getMasterKeyAndAccountNumberByDeviceId(String strDeviceId, String strSecurityKey) {
        String METHOD_NAME = "QPAY_GetMasterKeyAndAcctIDByDeviceID";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GetMasterKeyAndAcctIDByDeviceID";
        SoapObject request = new SoapObject(strNamespace, METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo deviceId = new PropertyInfo();
        deviceId.setName("Device_ID");
        deviceId.setValue(strDeviceId);
        deviceId.setType(String.class);
        request.addProperty(deviceId);

        PropertyInfo securityKey = new PropertyInfo();
        securityKey.setName("Securitykey");
        securityKey.setValue(strSecurityKey);
        securityKey.setType(String.class);
        request.addProperty(securityKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objGetMasterKeyByDeviceId = null;
        String strMasterKey = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(strUrl, 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objGetMasterKeyByDeviceId = envelope.getResponse();
            strMasterKey = objGetMasterKeyByDeviceId.toString();
            Log.v("Master Key: ", strMasterKey);
        } catch (Exception exception) {
            Log.e("Master Key: ", "Error!!!");
        }

        return strMasterKey;
    }
}
