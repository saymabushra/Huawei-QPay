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
import com.bdmitech.android.huawei.qpay.utils.GetDistrict;
import com.bdmitech.android.huawei.qpay.utils.GetThana;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;
import com.bdmitech.android.huawei.qpay.utils.InsertKycAddressInfo;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class KycAddressInfo extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;

    ArrayList<String> arrayListDistrictId = new ArrayList<String>();
    ArrayList<String> arrayListDistrict = new ArrayList<String>();
    ArrayList<String> arrayListThanaId = new ArrayList<String>();
    ArrayList<String> arrayListThana = new ArrayList<String>();

    private Spinner mSpinnerDistrict, mSpinnerThana;
    private EditText mEditTextPresentAddress, mEditTextPermanentAddress, mEditTextOfficeAddress;
    private Button mBtnSaveAddress;
    private TextView mTextViewShowServerResponse;
    private String mStrAccountNumber, mStrPin, mStrMasterKey,
            mStrEncryptAccountNumber, mStrEncryptPin,
            mStrDistirctId, mStrEncryptDistrictId, mStrThanaId, mStrEncryptThanaId,
            mStrPresentAddress, mStrPermanentAddress, mStrOfficeAddress,mStrServerResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_address_info);
        checkOs();
        // initialize all ui components
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mSpinnerDistrict = findViewById(R.id.spinnerDistrict);
        mSpinnerThana = findViewById(R.id.spinnerThana);
        mEditTextPresentAddress = findViewById(R.id.editTextKycAddressInfoPresentAddress);
        mEditTextPermanentAddress = findViewById(R.id.editTextKycAddressInfoPermanentAddress);
        mEditTextOfficeAddress = findViewById(R.id.editTextKycAddressInfoOfficeAddress);
        mBtnSaveAddress = findViewById(R.id.btnKycAddressInfoSaveAddress);
        mBtnSaveAddress.setOnClickListener(this);
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
        if (v == mBtnSaveAddress) {
            if (mEditTextPresentAddress.getText().toString().length() == 0) {
                mEditTextPresentAddress.setError("Field cannot be empty");
            } else if (mEditTextPermanentAddress.getText().toString().length() == 0) {
                mEditTextPermanentAddress.setError("Field cannot be empty");
            } else if (mEditTextOfficeAddress.getText().toString().length() == 0) {
                mEditTextOfficeAddress.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();
                try {
                    mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                    mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                    mStrPresentAddress = encryption.Encrypt(mEditTextPresentAddress.getText().toString(), mStrMasterKey);
                    mStrPermanentAddress = encryption.Encrypt(mEditTextPermanentAddress.getText().toString(), mStrMasterKey);
                    mStrOfficeAddress = encryption.Encrypt(mEditTextOfficeAddress.getText().toString(), mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycAddressInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            mStrServerResponse = InsertKycAddressInfo.insertAddressInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptThanaId,
                                    mStrPresentAddress,
                                    mStrPermanentAddress,
                                    mStrOfficeAddress,
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycAddressInfo.this);
                                                myAlert.setMessage("Successfully save address info.");
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(KycAddressInfo.this);
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
                mStrAccountNumber = GlobalData.getStrAccountNumber();
                mStrPin = GlobalData.getStrPin();
                mStrMasterKey = GlobalData.getStrMasterKey();
                mStrEncryptAccountNumber = encryption.Encrypt(mStrAccountNumber, mStrMasterKey);
                mStrEncryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                loadSpinnerDistrict();
                ArrayAdapter<String> adapterDistrictName = new ArrayAdapter<String>(KycAddressInfo.this,
                        android.R.layout.simple_spinner_item, arrayListDistrict);
                adapterDistrictName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerDistrict.setAdapter(adapterDistrictName);
                mSpinnerDistrict.setOnItemSelectedListener(onItemSelectedListenerForDistrict);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycAddressInfo.this);
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

    //######################### Show District #########################
    //######################### Show District #########################
    //######################### Show District #########################
    private void loadSpinnerDistrict() {
        arrayListDistrict.clear();
        arrayListDistrictId.clear();
        String str = GetDistrict.getDistrict(mStrEncryptAccountNumber, mStrEncryptPin, mStrMasterKey);
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
                arrayListDistrictId.add(tokenDistrictIdAndName.nextToken());
                arrayListDistrict.add(tokenDistrictIdAndName.nextToken());
            }
        } else {
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycAddressInfo.this);
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

    //######################### District ID #########################
    //######################### District ID #########################
    //######################### District ID #########################
    AdapterView.OnItemSelectedListener onItemSelectedListenerForDistrict = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mStrDistirctId = String.valueOf(arrayListDistrictId.get(position));
            if (mStrDistirctId != null && !mStrDistirctId.isEmpty()) {
                try {
                    mStrEncryptDistrictId = encryption.Encrypt(mStrDistirctId, mStrMasterKey);
                    loadSpinnerThana();
                    ArrayAdapter<String> adapterThanaName = new ArrayAdapter<String>(KycAddressInfo.this,
                            android.R.layout.simple_spinner_item, arrayListThana);
                    adapterThanaName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinnerThana.setAdapter(adapterThanaName);
                    mSpinnerThana.setOnItemSelectedListener(onItemSelectedListenerForThana);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    //######################### Show Thana #########################
    //######################### Show Thana #########################
    //######################### Show Thana #########################
    private void loadSpinnerThana() {
        arrayListThana.clear();
        arrayListThanaId.clear();
        String strAllThanaIdAndName = GetThana.getThana(mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptDistrictId, mStrMasterKey);
        if (strAllThanaIdAndName != null && !strAllThanaIdAndName.isEmpty()) {
            StringTokenizer strToken = new StringTokenizer(strAllThanaIdAndName, "&");
            ArrayList<String> arrayListThanaIdAndName = new ArrayList<String>();
            for (int j = 0; j <= strToken.countTokens(); j++) {
                while (strToken.hasMoreElements()) {
                    arrayListThanaIdAndName.add(strToken.nextToken());
                }
            }
            for (int i = 0; i <= arrayListThanaIdAndName.size() - 1; i++) {
                StringTokenizer tokenThanaIdAndName = new StringTokenizer(arrayListThanaIdAndName.get(i), "*");
                arrayListThanaId.add(tokenThanaIdAndName.nextToken());
                arrayListThana.add(tokenThanaIdAndName.nextToken());
            }
        } else {
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycAddressInfo.this);
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

    //######################### Thana ID #########################
    //######################### Thana ID #########################
    //######################### Thana ID #########################
    AdapterView.OnItemSelectedListener onItemSelectedListenerForThana = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mStrThanaId = String.valueOf(arrayListThanaId.get(position));
            try{
                mStrEncryptThanaId=encryption.Encrypt(mStrThanaId,mStrMasterKey);
            }catch (Exception e){
                e.printStackTrace();
            }
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
        mSpinnerDistrict.setEnabled(true);
        mSpinnerThana.setEnabled(true);
        mEditTextPresentAddress.setEnabled(true);
        mEditTextPermanentAddress.setEnabled(true);
        mEditTextOfficeAddress.setEnabled(true);
        mBtnSaveAddress.setEnabled(true);
    }

    private void disableUiComponents() {
        mSpinnerDistrict.setEnabled(false);
        mSpinnerThana.setEnabled(false);
        mEditTextPresentAddress.setEnabled(false);
        mEditTextPermanentAddress.setEnabled(false);
        mEditTextOfficeAddress.setEnabled(false);
        mBtnSaveAddress.setEnabled(false);
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
                Intent intent = new Intent(KycAddressInfo.this, Login.class)
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
