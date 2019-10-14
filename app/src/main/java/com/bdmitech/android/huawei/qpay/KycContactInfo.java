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
import com.bdmitech.android.huawei.qpay.utils.InsertKycBankInfo;
import com.bdmitech.android.huawei.qpay.utils.InsertKycContactInfo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class KycContactInfo extends AppCompatActivity implements View.OnClickListener {
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;


    // initialize all ui components
    private EditText mEditTextEmail, mEditTextMobileNumber;
    private Button mBtnUpdateContactInfo;
    private TextView mTextViewShowServerResponse;
    private String mStrMasterKey, mStrAccountNumber, mStrPin, mStrEncryptAccountNumber, mStrEncryptPin, mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_contact_info);
        checkOs();
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mEditTextEmail = findViewById(R.id.editTextKycContactInfoEmail);
        mEditTextMobileNumber = findViewById(R.id.editTextKycContactInfoMobileNumber);
        mBtnUpdateContactInfo = findViewById(R.id.btnKycContactInfoUpdateContactInfo);
        mBtnUpdateContactInfo.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.txtViewKycContactInfoShowServerResponse);

        mStrMasterKey = GlobalData.getStrMasterKey();
        mStrAccountNumber = GlobalData.getStrAccountNumber();
        mStrPin = GlobalData.getStrPin();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnUpdateContactInfo) {
            if (mEditTextEmail.getText().toString().length() == 0) {
                mEditTextEmail.setError("Field cannot be empty");
            } else if (mEditTextMobileNumber.getText().toString().length() == 0) {
                mEditTextMobileNumber.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();

//                try {
//                    mStrEncryptAccountNumber = encryption.Encrypt(GlobalData.getStrAccountNumber(), GlobalData.getStrMasterKey());
//                    mStrEncryptPin = encryption.Encrypt(mEditTextPin.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptNewPin = encryption.Encrypt(mEditTextNewPin.getText().toString(), GlobalData.getStrMasterKey());
//
//                    // Initialize progress dialog
//                    mProgressDialog = ProgressDialog.show(KycContactInfo.this, null, "Processing request...", false, true);
//                    // Cancel progress dialog on back key press
//                    mProgressDialog.setCancelable(true);
//
//                    Thread t = new Thread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            changePin(mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptNewPin, GlobalData.getStrMasterKey());
//                            runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    try {
//                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                            mProgressDialog.dismiss();
//                                            //####################### Show Dialog ####################
//                                            //####################### Show Dialog ####################
//                                            //####################### Show Dialog ####################
//                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(KycContactInfo.this);
//                                            myAlert.setMessage(mStrServerResponse);
//                                            myAlert.setNegativeButton(
//                                                    "OK",
//                                                    new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int id) {
//                                                            dialog.cancel();
//                                                            enableUiComponentAfterClick();
//                                                            startActivity(new Intent(KycContactInfo.this, Login.class));
//                                                        }
//                                                    });
//                                            AlertDialog alertDialog = myAlert.create();
//                                            alertDialog.show();
//                                        }
//                                    } catch (Exception e) {
//                                        // TODO: handle exception
//                                    }
//                                }
//                            });
//                        }
//                    });
//
//                    t.start();
//                } catch (Exception exception) {
//                    exception.printStackTrace();
//                }
            }
        }

    }


    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
            try {
                mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                mStrServerResponse = InsertKycContactInfo.updateContactInfo(
                        mStrEncryptAccountNumber,
                        mStrEncryptPin,
                        mStrMasterKey);
                String[] parts = mStrServerResponse.split("\\*");
                String strEmail = parts[0];
                String strMobileNumber = parts[1];
                mEditTextEmail.setText(strEmail);
                mEditTextMobileNumber.setText(strMobileNumber);

//                AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycContactInfo.this);
//                mAlertDialogBuilder.setMessage(mStrServerResponse);
//                mAlertDialogBuilder.setNegativeButton(
//                        "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog mAlertDialog = mAlertDialogBuilder.create();
//                mAlertDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycContactInfo.this);
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
        mEditTextEmail.setEnabled(true);
        mEditTextMobileNumber.setEnabled(true);
        mBtnUpdateContactInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextEmail.setEnabled(false);
        mEditTextMobileNumber.setEnabled(false);
        mBtnUpdateContactInfo.setEnabled(false);
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
                Intent intent = new Intent(KycContactInfo.this, Login.class)
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
