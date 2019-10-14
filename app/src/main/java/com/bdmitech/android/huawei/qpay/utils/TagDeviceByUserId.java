package com.bdmitech.android.huawei.qpay.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class TagDeviceByUserId {
    public static String tagDeviceByUserId(
            String strEncryptUserId,
            String strEncryptMobileNumber,
            String strEncryptDeviceId,
            String strMasterKey) {
        String METHOD_NAME = "QPAY_Tag_DEVICE_WITH_AccountID";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Tag_DEVICE_WITH_AccountID";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//		QPAY_Tag_DEVICE_WITH_AccountID
//		UserID
//		PhoneNo
//		DeviceID
//		MasterKey


        PropertyInfo encryptUserId = new PropertyInfo();
        encryptUserId.setName("UserID");
        encryptUserId.setValue(strEncryptUserId);
        encryptUserId.setType(String.class);
        request.addProperty(encryptUserId);

        PropertyInfo encryptMobileNumber = new PropertyInfo();
        encryptMobileNumber.setName("PhoneNo");
        encryptMobileNumber.setValue(strEncryptMobileNumber);
        encryptMobileNumber.setType(String.class);
        request.addProperty(encryptMobileNumber);

        PropertyInfo encryptDeviceId = new PropertyInfo();
        encryptDeviceId.setName("DeviceID");
        encryptDeviceId.setValue(strEncryptDeviceId);
        encryptDeviceId.setType(String.class);
        request.addProperty(encryptDeviceId);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("MasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        envelope.implicitTypes = true;
        Object objTagDeviceByUserId = null;
        String strTagDeviceByUserIdResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objTagDeviceByUserId = envelope.getResponse();
            strTagDeviceByUserIdResponse = objTagDeviceByUserId.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return strTagDeviceByUserIdResponse;

    }
}
