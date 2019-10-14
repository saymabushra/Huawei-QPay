package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class KycParentsAndSpouseInfo extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;


    // initialize all ui components
    private EditText mEditTextFatherName, mEditTextMotherName, mEditTextSpouseTitle, mEditTextSpouseName;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrMasterKey, mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptFatherName,
            mStrEncryptMotherName, mStrEncryptSpouseTitle, mStrEncryptSpouseName,
            mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_parents_and_spouse_info);
        checkOs();
        // initialize all ui components
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mEditTextFatherName = findViewById(R.id.editTextKycParentsAndSpouseInfoFatherName);
        mEditTextMotherName = findViewById(R.id.editTextKycParentsAndSpouseInfoMotherName);
        mEditTextSpouseTitle = findViewById(R.id.editTextKycParentsAndSpouseInfoSpouseTitle);
        mEditTextSpouseName = findViewById(R.id.editTextKycParentsAndSpouseInfoSpouseName);
        mBtnSubmit = findViewById(R.id.btnKycParentsAndSpouseInfoSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewKycParentsAndSpouseInfoServerResponse);
        mStrMasterKey = GlobalData.getStrMasterKey();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSubmit) {
            if (mEditTextFatherName.getText().toString().length() == 0) {
                mEditTextFatherName.setError("Field cannot be empty");
            } else if (mEditTextMotherName.getText().toString().length() == 0) {
                mEditTextMotherName.setError("Field cannot be empty");
            } else if (mEditTextSpouseName.getText().toString().length() == 0) {
                mEditTextSpouseName.setError("Field cannot be empty");
            } else if (mEditTextSpouseName.getText().toString().length() == 0) {
                mEditTextSpouseName.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();
                try {
                    mStrEncryptAccountNumber = encryption.Encrypt(GlobalData.getStrAccountNumber(), mStrMasterKey);
                    mStrEncryptPin = encryption.Encrypt(GlobalData.getStrPin(), mStrMasterKey);
                    mStrEncryptFatherName = encryption.Encrypt(mEditTextFatherName.getText().toString(), mStrMasterKey);
                    mStrEncryptMotherName = encryption.Encrypt(mEditTextMotherName.getText().toString(), mStrMasterKey);
                    mStrEncryptSpouseTitle = encryption.Encrypt(mEditTextSpouseTitle.getText().toString(), mStrMasterKey);
                    mStrEncryptSpouseName = encryption.Encrypt(mEditTextSpouseName.getText().toString(), mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycParentsAndSpouseInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                            insertParentsAndSpouseInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptFatherName,
                                    mStrEncryptMotherName,
                                    mStrEncryptSpouseTitle,
                                    mStrEncryptSpouseName,
                                    mStrMasterKey
                            );
//                            SystemClock.sleep(10000);
//                    } else {
//                        mTextViewShowServerResponse.setText("Session Expire, Please Login Again");
//                    }
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            //####################### Show Dialog ####################
                                            //####################### Show Dialog ####################
                                            //####################### Show Dialog ####################
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(KycParentsAndSpouseInfo.this);
                                            myAlert.setMessage(mStrServerResponse);
                                            myAlert.setNegativeButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                        }
                                                    });
                                            AlertDialog alertDialog = myAlert.create();
                                            alertDialog.show();
                                        }
                                    } catch (Exception e) {
                                        // TODO: handle exception
                                    }
                                }
                            });
                        }
                    });

                    t.start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

    }

    // method for Change PIN
    public void insertParentsAndSpouseInfo(
            String strEncryptAccountNumber,
            String strEncryptPin,
            String strEncryptFatherName,
            String strEncryptMotherName,
            String strEncryptSpouseTitle,
            String strEncryptSpouseName,
            String strMasterKey) {
        METHOD_NAME = "QPAY_KYC_Parent_Spouse_Info ";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_KYC_Parent_Spouse_Info ";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

//        QPAY_KYC_Parent_Spouse_Info
//        AccountNo
//        PIN
//        Fathername
//        Mothername
//        SpouseTitle
//        SpouseName
//        strMasterKey

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptAccountNumber);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PIN");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo encryptFatherName = new PropertyInfo();
        encryptFatherName.setName("Fathername");
        encryptFatherName.setValue(strEncryptFatherName);
        encryptFatherName.setType(String.class);
        request.addProperty(encryptFatherName);

        PropertyInfo encryptMotherName = new PropertyInfo();
        encryptMotherName.setName("Mothername");
        encryptMotherName.setValue(strEncryptMotherName);
        encryptMotherName.setType(String.class);
        request.addProperty(encryptMotherName);

        PropertyInfo encryptSpouseTitle = new PropertyInfo();
        encryptSpouseTitle.setName("SpouseTitle");
        encryptSpouseTitle.setValue(strEncryptSpouseTitle);
        encryptSpouseTitle.setType(String.class);
        request.addProperty(encryptSpouseTitle);

        PropertyInfo encryptSpouseName = new PropertyInfo();
        encryptSpouseName.setName("SpouseName");
        encryptSpouseName.setValue(strEncryptSpouseName);
        encryptSpouseName.setType(String.class);
        request.addProperty(encryptSpouseName);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objKycParentsAndSpouseInfo = null;
        String strKycParentsAndSpouseInfoReponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objKycParentsAndSpouseInfo = envelope.getResponse();
            strKycParentsAndSpouseInfoReponse = objKycParentsAndSpouseInfo.toString();
            mStrServerResponse = strKycParentsAndSpouseInfoReponse;
            if (mStrServerResponse.equalsIgnoreCase("Update")) {
                mStrServerResponse = "Parents/Spouse Info update succesfully.";
            } else {
                mStrServerResponse = strKycParentsAndSpouseInfoReponse;
            }
        } catch (Exception exception) {
            mStrServerResponse = strKycParentsAndSpouseInfoReponse;
        }
    }

    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycParentsAndSpouseInfo.this);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void enableUiComponents() {
        mEditTextFatherName.setEnabled(true);
        mEditTextMotherName.setEnabled(true);
        mEditTextSpouseTitle.setEnabled(true);
        mEditTextSpouseName.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextFatherName.setEnabled(false);
        mEditTextMotherName.setEnabled(false);
        mEditTextSpouseTitle.setEnabled(false);
        mEditTextSpouseName.setEnabled(false);
        mBtnSubmit.setEnabled(false);
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
                Intent intent = new Intent(KycParentsAndSpouseInfo.this, Login.class)
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

    private void checkOs() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

}
