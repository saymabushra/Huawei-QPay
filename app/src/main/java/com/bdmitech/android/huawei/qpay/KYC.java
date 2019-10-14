package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class KYC extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();

    private Button mBtnPersonalInfo, mBtnParentsAndSpouseInfo, mBtnAddress,
            mBtnContact, mBtnIdentificationInfo, mBtnBankInfo, mBtnNominee, mBtnIntroducerInfo, mBtnUpdateKyc;

    private String mStrMasterKey, mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc);
        checkOs();
        initUI();
    }

    private void checkOs() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mBtnPersonalInfo = findViewById(R.id.btnKycPersonalInfo);
        mBtnParentsAndSpouseInfo = findViewById(R.id.btnKycParentAndSpouseInfo);
        mBtnAddress = findViewById(R.id.btnKycAddressInfo);
        mBtnContact = findViewById(R.id.btnKycContactInfo);
        mBtnIdentificationInfo = findViewById(R.id.btnKycIdentificationInfo);
        mBtnBankInfo = findViewById(R.id.btnKycBankInfo);
        mBtnNominee = findViewById(R.id.btnKycNomineeInfo);
        mBtnIntroducerInfo = findViewById(R.id.btnKycIntroducerInfo);
        mBtnUpdateKyc = findViewById(R.id.btnKycUpdateKyc);

        mBtnPersonalInfo.setOnClickListener(this);
        mBtnParentsAndSpouseInfo.setOnClickListener(this);
        mBtnAddress.setOnClickListener(this);
        mBtnContact.setOnClickListener(this);
        mBtnIdentificationInfo.setOnClickListener(this);
        mBtnBankInfo.setOnClickListener(this);
        mBtnNominee.setOnClickListener(this);
        mBtnIntroducerInfo.setOnClickListener(this);
        mBtnUpdateKyc.setOnClickListener(this);

        mStrMasterKey = GlobalData.getStrMasterKey();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnPersonalInfo) {
            startActivity(new Intent(KYC.this, KycPersonalInfo.class));
        }
        if (v == mBtnParentsAndSpouseInfo) {
            startActivity(new Intent(KYC.this, KycParentsAndSpouseInfo.class));
        }
        if (v == mBtnAddress) {
            startActivity(new Intent(KYC.this, KycAddressInfo.class));
        }
        if (v == mBtnContact) {
            startActivity(new Intent(KYC.this, KycContactInfo.class));
        }
        if (v == mBtnIdentificationInfo) {
//            Toast.makeText(KYC.this, "Clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(KYC.this, KycIdentificationInfo.class));
        }
        if (v == mBtnBankInfo) {
            startActivity(new Intent(KYC.this, KycBankInfo.class));
        }
        if (v == mBtnNominee) {
            startActivity(new Intent(KYC.this, KycNomineeInfo.class));
        }
        if (v == mBtnIntroducerInfo) {
            startActivity(new Intent(KYC.this, KycIntroducerInfo.class));
        }

        if (v == mBtnUpdateKyc) {
            try {
                String strEncryptAccountNumber = encryption.Encrypt(GlobalData.getStrAccountNumber(), mStrMasterKey);
                String strEncryptPin = encryption.Encrypt(GlobalData.getStrPin(), mStrMasterKey);
                String strEncryptAccountStatus = encryption.Encrypt("A", mStrMasterKey);
                updateAccountActiveStatus(strEncryptAccountNumber, strEncryptPin, strEncryptAccountStatus, mStrMasterKey);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }


    }

    private void updateAccountActiveStatus(
            String strEncryptAccount,
            String strEncryptPin,
            String strEncryptAccountActiveStatus,
            String strMasterKey) {
        METHOD_NAME = "QPAY_Update_Account_Status ";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Update_Account_Status ";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//        QPAY_Update_Account_Status
//        AccountNo:
//        PIN:
//        Status:
//        strMasterKey:

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptAccount);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PIN");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo encryptAccountActiveStatus = new PropertyInfo();
        encryptAccountActiveStatus.setName("Status");
        encryptAccountActiveStatus.setValue(strEncryptAccountActiveStatus);
        encryptAccountActiveStatus.setType(String.class);
        request.addProperty(encryptAccountActiveStatus);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objUpdateAccountActiveState = null;
        String StrUpdateAccountActiveStateResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objUpdateAccountActiveState = envelope.getResponse();
            StrUpdateAccountActiveStateResponse = objUpdateAccountActiveState.toString();
            mStrServerResponse = StrUpdateAccountActiveStateResponse;
//            ####################### Show Dialog ####################
//            ####################### Show Dialog ####################
//            ####################### Show Dialog ####################
            AlertDialog.Builder myAlert = new AlertDialog.Builder(KYC.this);
            myAlert.setMessage(mStrServerResponse);
            myAlert.setNegativeButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            startActivity(new Intent(KYC.this, QPayMenuNew.class));
                        }
                    });
            AlertDialog alertDialog = myAlert.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KYC.this);
            mAlertDialogBuilder.setTitle("No Internet Connection");
            mAlertDialogBuilder.setMessage("It looks like your internet connection is off. Please turn it on and try again.");
            mAlertDialogBuilder.setNegativeButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.show();
        }
    }

    private void enableUiComponents() {
        mBtnPersonalInfo.setEnabled(true);
        mBtnParentsAndSpouseInfo.setEnabled(true);
        mBtnAddress.setEnabled(true);
        mBtnContact.setEnabled(true);
        mBtnBankInfo.setEnabled(true);
        mBtnNominee.setEnabled(true);
        mBtnIntroducerInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mBtnPersonalInfo.setEnabled(false);
        mBtnParentsAndSpouseInfo.setEnabled(false);
        mBtnAddress.setEnabled(false);
        mBtnContact.setEnabled(false);
        mBtnBankInfo.setEnabled(false);
        mBtnNominee.setEnabled(false);
        mBtnIntroducerInfo.setEnabled(false);
    }

    //########################## Logout ############################
    //########################## Logout ############################
    //########################## Logout ############################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionLogout:
                clearDataFromGlobal();
                Intent intent = new Intent(KYC.this, Login.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clearDataFromGlobal() {
        GlobalData.setStrDeviceId("");
        GlobalData.setStrDeviceName("");
        GlobalData.setStrUserId("");
        GlobalData.setStrEncryptUserId("");
        GlobalData.setStrPin("");
        GlobalData.setStrEncryptPin("");
        GlobalData.setStrMasterKey("");
        GlobalData.setStrPackage("");
        GlobalData.setStrEncryptPackage("");
        GlobalData.setStrAccountNumber("");
        GlobalData.setStrEncryptAccountNumber("");
        GlobalData.setStrWallet("");
        GlobalData.setStrAccountHolderName("");
        GlobalData.setStrSessionId("");
        GlobalData.setStrQrCodeContent("");
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(KYC.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
