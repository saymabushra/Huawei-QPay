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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GetKycIdentificationInfo;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;
import com.bdmitech.android.huawei.qpay.utils.InsertKycIdentificationInfo;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class KycIdentificationInfo extends AppCompatActivity implements View.OnClickListener {
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;

    ArrayList<String> arrayListIdentificationId = new ArrayList<String>();
    ArrayList<String> arrayListIdentification = new ArrayList<String>();

    private Spinner mSpinnerIdentificationDocType;
    private EditText mEditTextIdentificationNumber, mEditTextRemark;
    private Button mBtnSaveIdentificationInfo;
    private TextView mTextViewShowServerResponse;
    private String mStrMasterKey, mStrAccountNumber, mStrPin,
            mStrEncryptAccountNumber, mStrEncryptPin, mStrIdentificationId,
            mStrEncryptIdentificationId, mStrEncryptIdentificationNumber, mStrEncryptRemark,
            mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_identification_info);
        checkOs();
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mSpinnerIdentificationDocType = findViewById(R.id.spinnerKycIdentificationInfoIdentificationDocType);
        mEditTextIdentificationNumber = findViewById(R.id.editTextKycIdentificationInfoIdentification);
        mEditTextRemark = findViewById(R.id.editTextKycIdentificationInfoRemarks);
        mBtnSaveIdentificationInfo = findViewById(R.id.btnKycIdentificationInfoSaveIdentificationInfo);
        mBtnSaveIdentificationInfo.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.txtViewKycIdentificationInfoShowServerResponse);

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
        if (v == mBtnSaveIdentificationInfo) {
            if (mEditTextIdentificationNumber.getText().toString().length() == 0) {
                mEditTextIdentificationNumber.setError("Field cannot be empty");
            } else if (mEditTextRemark.getText().toString().length() == 0) {
                mEditTextRemark.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();

                try {
                    mStrEncryptIdentificationId = encryption.Encrypt(mStrIdentificationId, mStrMasterKey);
                    mStrEncryptIdentificationNumber = encryption.Encrypt(mEditTextIdentificationNumber.getText().toString(), mStrMasterKey);
                    mStrEncryptRemark = encryption.Encrypt(mEditTextRemark.getText().toString(), mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycIdentificationInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            mStrServerResponse = InsertKycIdentificationInfo.insertIndentificationInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptIdentificationId,
                                    mStrEncryptIdentificationNumber,
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycIdentificationInfo.this);
                                                myAlert.setMessage("Successfully save identification info.");
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycIdentificationInfo.this);
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
            try {
                mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                mStrServerResponse = GetKycIdentificationInfo.getIdentificationInfo(
                        mStrEncryptAccountNumber,
                        mStrEncryptPin,
                        mStrMasterKey);
                loadSpinner();
                ArrayAdapter<String> adapterDistrictName = new ArrayAdapter<String>(KycIdentificationInfo.this,
                        android.R.layout.simple_spinner_item, arrayListIdentification);
                adapterDistrictName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerIdentificationDocType.setAdapter(adapterDistrictName);
                mSpinnerIdentificationDocType.setOnItemSelectedListener(onItemSelectedListenerForIdentificationDocType);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycIdentificationInfo.this);
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

    //######################### Show Identification Doc Type #########################
    //######################### Show Identification Doc Type #########################
    //######################### Show Identification Doc Type #########################
    private void loadSpinner() {
        arrayListIdentificationId.clear();
        arrayListIdentification.clear();
        String str = GetKycIdentificationInfo.getIdentificationInfo(mStrEncryptAccountNumber, mStrEncryptPin, mStrMasterKey);
        if (str != null && !str.isEmpty()) {
            StringTokenizer strToken = new StringTokenizer(str, "&");
            ArrayList<String> arrayListDistrictIdAndName = new ArrayList<String>();
            for (int j = 0; j <= strToken.countTokens(); j++) {
                while (strToken.hasMoreElements()) {
                    arrayListDistrictIdAndName.add(strToken.nextToken());
                }
            }
            for (int i = 0; i <= arrayListDistrictIdAndName.size() - 1; i++) {
                StringTokenizer tokenDistrictIdAndName = new StringTokenizer(arrayListDistrictIdAndName.get(i), "*");
                arrayListIdentificationId.add(tokenDistrictIdAndName.nextToken());
                arrayListIdentification.add(tokenDistrictIdAndName.nextToken());
            }
        } else {
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycIdentificationInfo.this);
            mAlertDialogBuilder.setMessage("No Account Found.");
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

    //######################### Identification ID #########################
    //######################### Identification ID #########################
    //######################### Identification ID #########################
    AdapterView.OnItemSelectedListener onItemSelectedListenerForIdentificationDocType = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mStrIdentificationId = String.valueOf(arrayListIdentificationId.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void enableUiComponents() {
        mEditTextIdentificationNumber.setEnabled(true);
        mEditTextRemark.setEnabled(true);
        mBtnSaveIdentificationInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextIdentificationNumber.setEnabled(false);
        mEditTextRemark.setEnabled(false);
        mBtnSaveIdentificationInfo.setEnabled(false);
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
                Intent intent = new Intent(KycIdentificationInfo.this, Login.class)
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
