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
import com.bdmitech.android.huawei.qpay.utils.InsertKycBankInfo;


public class KycBankInfo extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;

    private EditText mEditTextBankName, mEditTextBankBranch, mEditTextBankAccountNumber, mEditTextRemark;
    private Button mBtnSaveBankInfo;
    private TextView mTextViewShowServerResponse;
    private String mStrAccountNumber, mStrPin, mStrMasterKey,
            mStrEncryptAccountNumber, mStrEncryptPin,
            mStrEncryptBankName, mStrEncryptBankBranch, mStrEncryptBankAccountNumber,mStrEncryptRemark,
            mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_bank_info);
        checkOs();
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mEditTextBankName = findViewById(R.id.editTextKycBankInfoBankName);
        mEditTextBankBranch = findViewById(R.id.editTextKycBankInfoBankBranch);
        mEditTextBankAccountNumber = findViewById(R.id.editTextKycBankInfoBankAccountNumber);
        mEditTextRemark = findViewById(R.id.editTextKycBankInfoRemarks);
        mBtnSaveBankInfo = findViewById(R.id.btnKycBankInfoSaveBankInfo);
        mBtnSaveBankInfo.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.txtViewKycAddressInfoShowServerResponse);

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
        if (v == mBtnSaveBankInfo) {
            if (mEditTextBankName.getText().toString().length() == 0) {
                mEditTextBankName.setError("Field cannot be empty");
            } else if (mEditTextBankBranch.getText().toString().length() == 0) {
                mEditTextBankBranch.setError("Field cannot be empty");
            } else if (mEditTextBankAccountNumber.getText().toString().length() == 0) {
                mEditTextBankAccountNumber.setError("Field cannot be empty");
            }else if (mEditTextRemark.getText().toString().length() == 0) {
                mEditTextRemark.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();
                try {
                    mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                    mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                    mStrEncryptBankName = encryption.Encrypt(mEditTextBankName.getText().toString(), mStrMasterKey);
                    mStrEncryptBankBranch = encryption.Encrypt(mEditTextBankBranch.getText().toString(), mStrMasterKey);
                    mStrEncryptBankAccountNumber = encryption.Encrypt(mEditTextBankAccountNumber.getText().toString(), mStrMasterKey);
                    mStrEncryptRemark = encryption.Encrypt(mEditTextRemark.getText().toString(), mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycBankInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            mStrServerResponse = InsertKycBankInfo.insertBankInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptBankName,
                                    mStrEncryptBankBranch,
                                    mStrEncryptBankAccountNumber,
                                    mStrEncryptRemark,
                                    mStrMasterKey);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            if(mStrServerResponse.equalsIgnoreCase("Update")){
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycBankInfo.this);
                                                myAlert.setMessage("Successfully save bank info.");
                                                myAlert.setNegativeButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                dialog.cancel();
                                                            }
                                                        });
                                                AlertDialog alertDialog = myAlert.create();
                                                alertDialog.show();
                                            }else{
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                //####################### Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycBankInfo.this);
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
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycBankInfo.this);
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
        mEditTextBankName.setEnabled(true);
        mEditTextBankBranch.setEnabled(true);
        mEditTextBankAccountNumber.setEnabled(true);
        mEditTextRemark.setEnabled(true);
        mBtnSaveBankInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextBankName.setEnabled(false);
        mEditTextBankBranch.setEnabled(false);
        mEditTextBankAccountNumber.setEnabled(false);
        mEditTextRemark.setEnabled(false);
        mBtnSaveBankInfo.setEnabled(false);
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
                Intent intent = new Intent(KycBankInfo.this, Login.class)
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

