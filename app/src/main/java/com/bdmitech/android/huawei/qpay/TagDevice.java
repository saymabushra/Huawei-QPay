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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GetMasterKeyByUserId;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;
import com.bdmitech.android.huawei.qpay.utils.TagDeviceByUserId;

public class TagDevice extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;


    // initialize all ui components
    private EditText mEditTextUserId, mEditTextMobileNumber;
    private Button mBtnTagDevice;
    private TextView mTextViewShowServerResponse;
    private String mStrMasterKey, mStrDeviceId, mStrEncryptUserId, mStrEncryptMobileNumber,
            mStrEncryptDeviceId, mStrTagDeviceResponse;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_device);
        checkOs();
        initUI();
    }

    private void initUI() {
        mEditTextUserId = findViewById(R.id.editTextDeviceTaggingUserId);
        mEditTextMobileNumber = findViewById(R.id.editTextDeviceTaggingMobileNumber);
        mBtnTagDevice = findViewById(R.id.btnDeviceTaggingTagDevice);
        mBtnTagDevice.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewDeviceTaggingServerResponse);
        mStrDeviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnTagDevice) {
            if (mEditTextUserId.getText().toString().length() == 0) {
                mEditTextUserId.setError("Field cannot be empty");
            } else if (mEditTextMobileNumber.getText().toString().length() == 0) {
                mEditTextMobileNumber.setError("Field cannot be empty");
            } else if (mEditTextMobileNumber.getText().toString().length() < 11) {
                mEditTextMobileNumber.setError("Must be 11 characters in length");
            } else {
                String strMasterKeyAndAccountNumberByUserId = GetMasterKeyByUserId.getMasterKeyByUserId(mEditTextUserId.getText().toString(), GlobalData.getStrRegistrationSecurityKey());
                int intIndex = strMasterKeyAndAccountNumberByUserId.indexOf("*");
                if (intIndex == -1) {
                    //####################### Show Dialog ####################
                    //####################### Show Dialog ####################
                    //####################### Show Dialog ####################
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(TagDevice.this);
                    myAlert.setMessage("Successfully tag device.");
                    myAlert.setNegativeButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(TagDevice.this, Login.class));
                                }
                            });
                    AlertDialog alertDialog = myAlert.create();
                    alertDialog.show();
                } else {
                    try {
                        String[] partResponse = strMasterKeyAndAccountNumberByUserId.split("\\*");
                        mStrMasterKey = partResponse[0];//REQUEST_ID:18022800000014
                        String strEncryptAccountNumber = partResponse[1];//RESPONSE_ID :18022800000019
                        mStrEncryptUserId = encryption.Encrypt(mEditTextUserId.getText().toString(), mStrMasterKey);
                        mStrEncryptMobileNumber = encryption.Encrypt(mEditTextMobileNumber.getText().toString(), mStrMasterKey);
                        mStrEncryptDeviceId = encryption.Encrypt(mStrDeviceId, mStrMasterKey);

                        // Initialize progress dialog
                        mProgressDialog = ProgressDialog.show(TagDevice.this, null, "Processing request...", false, true);
                        // Cancel progress dialog on back key press
                        mProgressDialog.setCancelable(true);

                        Thread t = new Thread(new Runnable() {

                            @Override
                            public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                                mStrTagDeviceResponse = TagDeviceByUserId.tagDeviceByUserId(
                                        mStrEncryptUserId,
                                        mStrEncryptMobileNumber,
                                        mStrEncryptDeviceId,
                                        mStrMasterKey);
//                    } else {
//                        mTextViewShowServerResponse.setText("Session Expire, Please Login Again");
//                    }
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        try {
                                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                mProgressDialog.dismiss();
                                                if (mStrTagDeviceResponse.equalsIgnoreCase("Update")) {
                                                    //####################### Show Dialog ####################
                                                    //####################### Show Dialog ####################
                                                    //####################### Show Dialog ####################
                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(TagDevice.this);
                                                    myAlert.setMessage("Successfully tag device.");
                                                    myAlert.setNegativeButton(
                                                            "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    dialog.cancel();
                                                                    startActivity(new Intent(TagDevice.this, Login.class));
                                                                }
                                                            });
                                                    AlertDialog alertDialog = myAlert.create();
                                                    alertDialog.show();
                                                } else {
                                                    //####################### Show Dialog ####################
                                                    //####################### Show Dialog ####################
                                                    //####################### Show Dialog ####################
                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(TagDevice.this);
                                                    myAlert.setMessage(mStrTagDeviceResponse);
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

    }

    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(TagDevice.this);
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
        mEditTextUserId.setEnabled(true);
        mEditTextMobileNumber.setEnabled(true);
        mBtnTagDevice.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextUserId.setEnabled(false);
        mEditTextMobileNumber.setEnabled(false);
        mBtnTagDevice.setEnabled(false);
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
                Intent intent = new Intent(TagDevice.this, Login.class)
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
