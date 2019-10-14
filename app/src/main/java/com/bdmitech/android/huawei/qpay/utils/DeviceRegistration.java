package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DeviceRegistration {
    public static String doDeviceRegistration(String strDeviceName,
                                             String strDeviceId,
                                             String strChannelType,
                                             String strSecurityKey) {
        String METHOD_NAME = "QPAY_GetDeviceCheckAndRegistration";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GetDeviceCheckAndRegistration";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo deviceName = new PropertyInfo();
        deviceName.setName("TerminalName");
        deviceName.setValue(strDeviceName);
        deviceName.setType(String.class);
        request.addProperty(deviceName);

        PropertyInfo deviceId = new PropertyInfo();
        deviceId.setName("TerminalSerial");
        deviceId.setValue(strDeviceId);
        deviceId.setType(String.class);
        request.addProperty(deviceId);

        PropertyInfo channelType = new PropertyInfo();
        channelType.setName("ChannelType");
        channelType.setValue(strChannelType);
        channelType.setType(String.class);
        request.addProperty(channelType);

        PropertyInfo securityKey = new PropertyInfo();
        securityKey.setName("SecurityKey");
        securityKey.setValue(strSecurityKey);
        securityKey.setType(String.class);
        request.addProperty(securityKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objDeviceRegistrationResponse = null;
        String strDeviceRegistrationResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objDeviceRegistrationResponse = envelope.getResponse();
            strDeviceRegistrationResponse = objDeviceRegistrationResponse.toString();
        } catch (Exception exception) {
            Log.e("Session ID: ", "Error!!!");
        }

        return strDeviceRegistrationResponse;

    }
}
