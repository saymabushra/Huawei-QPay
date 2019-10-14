package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CheckDeviceActiveStatus {
    public static String checkDeviceActiveStatus(String strDeviceId) {
        String METHOD_NAME = "QPAY_DeviceActiveStatusCheck";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_DeviceActiveStatusCheck";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo deviceId = new PropertyInfo();
        deviceId.setName("strTernimalSerial");
        deviceId.setValue(strDeviceId);
        deviceId.setType(String.class);
        request.addProperty(deviceId);

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
