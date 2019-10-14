package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetBalance {

    // Method retrieving Primary Account Balance
    // Method retrieving Primary Account Balance
    // Method retrieving Primary Account Balance
    public static String getBalance(String strEncryptUserId, String strEncryptPin,
                                                  String strEncryptWallet, String strMasterKey) {
        String METHOD_NAME = "QPAY_BalanceEnquiry";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_BalanceEnquiry";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptUserId = new PropertyInfo();
        encryptUserId.setName("UserID");
        encryptUserId.setValue(strEncryptUserId);
        encryptUserId.setType(String.class);
        request.addProperty(encryptUserId);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PIN");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptWallet);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objPrimaryAccountBalance = null;
        String strPrimaryAccountBalance = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objPrimaryAccountBalance = envelope.getResponse();
            strPrimaryAccountBalance = objPrimaryAccountBalance.toString();
        } catch (Exception exception) {
        }

        return strPrimaryAccountBalance;

    }
}
