package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CheckDeviceIdByAccountNumber {
    public static String checkDeviceIdByAccountNumber(String strUserId,String strDeviceId,String strMasterKey) {
        String METHOD_NAME = "QPAY_CheckDeviceIDWithAccountNo";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_CheckDeviceIDWithAccountNo";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo userId = new PropertyInfo();
        userId.setName("UserID");
        userId.setValue(strUserId);
        userId.setType(String.class);
        request.addProperty(userId);

        PropertyInfo deviceId = new PropertyInfo();
        deviceId.setName("DeviceID");
        deviceId.setValue(strDeviceId);
        deviceId.setType(String.class);
        request.addProperty(deviceId);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("MasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objDeviceActiveStatusResponse = null;
        String strDeviceActiveStatus = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objDeviceActiveStatusResponse = envelope.getResponse();
            strDeviceActiveStatus = objDeviceActiveStatusResponse.toString();
        } catch (Exception exception) {
            Log.e("Session ID: ", "Error!!!");
        }

        return strDeviceActiveStatus;

    }
}
