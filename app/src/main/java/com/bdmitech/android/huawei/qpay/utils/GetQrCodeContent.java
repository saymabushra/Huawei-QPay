package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * @author Mr. Nazmuzzaman, Kowshik Ahmed, Umme Sayma Bushra, Muhammad Sadat
 *         Al-Jony
 * @version 1.0
 * @company Bangladesh Microtechnology Ltd.
 * @since Nov 2015
 */

public class GetQrCodeContent {
    // Method retrieving Encrypt QR Code
    // Method retrieving Encrypt QR Code
    // Method retrieving Encrypt QR Code
    public static String getQrCode(String strEncryptAccountNumber, String strMasterKey) {
        String METHOD_NAME = "QPAY_GetQRCODE";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GetQRCODE";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ","%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptAccountNumber);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("MasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objGetQrCodeResponse = null;
        String strEncryptedQrCode = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ","%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objGetQrCodeResponse = envelope.getResponse();
            strEncryptedQrCode = objGetQrCodeResponse.toString();
            Log.v("Encrypted QR Code: ", strEncryptedQrCode);
        } catch (Exception exception) {
            Log.e("Encrypted QR Code: ", "Error!!!");
        }

        return strEncryptedQrCode;

    }
}
