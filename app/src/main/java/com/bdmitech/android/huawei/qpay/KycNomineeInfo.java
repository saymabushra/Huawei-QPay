package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;
import com.bdmitech.android.huawei.qpay.utils.InsertKycIntroducerInfo;
import com.bdmitech.android.huawei.qpay.utils.InsertKycNomineeInfo;

public class KycNomineeInfo extends AppCompatActivity implements View.OnClickListener {
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;

    private EditText mEditTextNomineeName, mEditTextNomineeMobileNumber,
            mEditTextRelation, mEditTextPercent, mEditTextRemark;
    private Button mBtnSaveNomineeInfo;
    private TextView mTextViewShowServerResponse;
    private String mStrAccountNumber, mStrPin, mStrMasterKey,
            mStrEncryptAccountNumber, mStrEncryptPin,
            mStrEncryptNomineeName, mStrEncryptNomineeMobileNumber, mStrEncryptRelation,
            mStrEncryptPercent, mStrEncryptRemark, mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_nominee_info);
        checkOs();
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mEditTextNomineeName = findViewById(R.id.editTextKycNomineeInfoNomineeName);
        mEditTextNomineeMobileNumber = findViewById(R.id.editTextKycNomineeInfoNomineeMobileNumber);
        mEditTextRelation = findViewById(R.id.editTextKycNomineeInfoRelation);
        mEditTextPercent = findViewById(R.id.editTextKycNomineeInfoPercent);
        mEditTextRemark = findViewById(R.id.editTextKycNomineeInfoRemarks);
        mBtnSaveNomineeInfo = findViewById(R.id.btnKycNomineeInfoSaveNomineeInfo);
        mBtnSaveNomineeInfo.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.txtViewKycNomineeInfoShowServerResponse);

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
        if (v == mBtnSaveNomineeInfo) {
            if (mEditTextNomineeName.getText().toString().length() == 0) {
                mEditTextNomineeName.setError("Field cannot be empty");
            } else if (mEditTextNomineeMobileNumber.getText().toString().length() == 0) {
                mEditTextNomineeMobileNumber.setError("Field cannot be empty");
            } else if (mEditTextNomineeMobileNumber.getText().toString().length() < 11) {
                mEditTextNomineeMobileNumber.setError("Must be 11 characters in length");
            } else if (mEditTextRelation.getText().toString().length() == 0) {
                mEditTextRelation.setError("Field cannot be empty");
            } else if (mEditTextPercent.getText().toString().length() == 0) {
                mEditTextPercent.setError("Field cannot be empty");
            } else if (mEditTextRemark.getText().toString().length() == 0) {
                mEditTextRemark.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();
                try {
                    mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                    mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                    mStrEncryptNomineeName = encryption.Encrypt(mEditTextNomineeName.getText().toString(), mStrMasterKey);
                    mStrEncryptNomineeMobileNumber = encryption.Encrypt(mEditTextNomineeMobileNumber.getText().toString(), mStrMasterKey);
                    mStrEncryptRelation = encryption.Encrypt(mEditTextRelation.getText().toString(), mStrMasterKey);
                    mStrEncryptPercent = encryption.Encrypt(mEditTextPercent.getText().toString(), mStrMasterKey);
                    mStrEncryptRemark = encryption.Encrypt(mEditTextRemark.getText().toString(), mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycNomineeInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            mStrServerResponse = InsertKycNomineeInfo.insertNomineeInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptNomineeName,
                                    mStrEncryptNomineeMobileNumber,
                                    mStrEncryptRelation,
                                    mStrEncryptPercent,
                                    mStrEncryptRemark,
                                    mStrMasterKey);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            if (mStrServerResponse.equalsIgnoreCase("Update")) {
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycNomineeInfo.this);
                                                myAlert.setMessage("Successfully save introducer info.");
                                                myAlert.setNegativeButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                AlertDialog alertDialog = myAlert.create();
                                                alertDialog.show();
                                            } else {
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycNomineeInfo.this);
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


    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycNomineeInfo.this);
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
        mEditTextNomineeName.setEnabled(true);
        mEditTextNomineeMobileNumber.setEnabled(true);
        mEditTextRelation.setEnabled(true);
        mEditTextPercent.setEnabled(true);
        mEditTextRemark.setEnabled(true);
        mBtnSaveNomineeInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextNomineeName.setEnabled(false);
        mEditTextNomineeMobileNumber.setEnabled(false);
        mEditTextRelation.setEnabled(false);
        mEditTextPercent.setEnabled(false);
        mEditTextRemark.setEnabled(false);
        mBtnSaveNomineeInfo.setEnabled(false);
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
                Intent intent = new Intent(KycNomineeInfo.this, Login.class)
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
