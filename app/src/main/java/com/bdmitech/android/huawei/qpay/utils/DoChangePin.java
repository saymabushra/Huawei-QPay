package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class DoChangePin {
    public static String doChangePin(String strEncryptAccountNumber,
                                   String strEncryptCurrentPin,
                                   String strEncryptNewPin,
                                   String strMasterKey) {
        String METHOD_NAME = "QPAY_AccountPINChange ";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_AccountPINChange ";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptAccountNumber);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo encryptCurrentPin = new PropertyInfo();
        encryptCurrentPin.setName("ACcountOldPIN");
        encryptCurrentPin.setValue(strEncryptCurrentPin);
        encryptCurrentPin.setType(String.class);
        request.addProperty(encryptCurrentPin);

        PropertyInfo encryptNewPin = new PropertyInfo();
        encryptNewPin.setName("NewPIN");
        encryptNewPin.setValue(strEncryptNewPin);
        encryptNewPin.setType(String.class);
        request.addProperty(encryptNewPin);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objResponse = null;
        String strServerResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objResponse = envelope.getResponse();
            strServerResponse = objResponse.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return strServerResponse;
    }
}
