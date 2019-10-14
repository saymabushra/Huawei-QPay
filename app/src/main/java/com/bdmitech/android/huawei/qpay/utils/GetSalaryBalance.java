package com.bdmitech.android.huawei.qpay.utils;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetSalaryBalance {

    // Method retrieving Salary Balance
    // Method retrieving Salary Balance
    // Method retrieving Salary Balance
    public static String getSalaryBalance(String strEncryptUserId, String strEncryptPin, String strMasterKey) {
        String METHOD_NAME = "SalaryBalance";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/SalaryBalance";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptUserId = new PropertyInfo();
        encryptUserId.setName("UserIdSubmission");
        encryptUserId.setValue(strEncryptUserId);
        encryptUserId.setType(String.class);
        request.addProperty(encryptUserId);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PinSubmission");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("MasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objSalaryBalance = null;
        String strSalaryBalance = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objSalaryBalance = envelope.getResponse();
            strSalaryBalance = objSalaryBalance.toString();
        } catch (Exception exception) {
        }

        return strSalaryBalance;

    }
}
