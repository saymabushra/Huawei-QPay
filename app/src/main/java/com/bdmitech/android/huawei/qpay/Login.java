package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.CheckDeviceActiveStatus;
import com.bdmitech.android.huawei.qpay.utils.CheckDeviceIdByAccountNumber;
import com.bdmitech.android.huawei.qpay.utils.CheckMasterKey;
import com.bdmitech.android.huawei.qpay.utils.Decryption;
import com.bdmitech.android.huawei.qpay.utils.DeviceRegistration;
import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GetBalance;
import com.bdmitech.android.huawei.qpay.utils.GetMasterKeyByUserId;
import com.bdmitech.android.huawei.qpay.utils.GetQrCodeContent;
import com.bdmitech.android.huawei.qpay.utils.GetSessionId;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class Login extends Activity implements View.OnClickListener {
    private ProgressDialog mProgressDialog = null;

    //#############################################################
    private SharedPreferences mSharedPreferencsLogin;
    private SharedPreferences.Editor mSharedPreferencsLoginEditor;
    private Boolean isRememberMe;
    //#############################################################

    private Decryption decryption = new Decryption();
    private Encryption encryption = new Encryption();

    private ImageButton mImgBtnInfo;
    private EditText mEditTextUserId, mEditTextPin;
    private CheckBox mCheckBoxRememberMe;
    private Button mBtnLogin;
    private TextView mTextViewForgetPin, mTextViewRegistration, mTextViewShowServerResponse;
    private String mStrUserId, mStrPin, mStrMasterKey,
            mStrEncryptUserId, mStrEnycryptPin, mStrEncryptPackage,
            mStrEncryptAccountNumber, mStrEncryptAccountHolderName,
            mStrPackage, mStrAccountNumber, mStrAccountHolderName,
            mStrSessionId, mStrServerResponse, mStrTerminalName,
            mStrQrCodeContent, mStrDeviceId, mStrEnycryptDeviceId,
            mStrVoucherWallet, mStrDeviceRegistrationStatus, mStrEncryptAccountRank,
            mStrAccountRank;

    @SuppressLint({"NewApi", "HandlerLeak"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkOS();
        initUI();
    }

    private void checkOS() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initUI() {
        mEditTextUserId = findViewById(R.id.editTextLoginUserId);
        mImgBtnInfo = findViewById(R.id.imageBtnInfo);
        mImgBtnInfo.setOnClickListener(this);
        mEditTextPin = findViewById(R.id.editTextLoginPin);
        mCheckBoxRememberMe = findViewById(R.id.checkBoxRememberMe);
        mTextViewForgetPin = findViewById(R.id.txtViewForgetPin);
        mTextViewForgetPin.setOnClickListener(this);
        mBtnLogin = findViewById(R.id.btnLoginLogin);
        mBtnLogin.setOnClickListener(this);
        mTextViewRegistration = findViewById(R.id.txtViewRegistration);
        mTextViewRegistration.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.txtViewLoginShowServerResponse);
        //######## Device Model #########
        //######## Device Model #########
        //######## Device Model #########
        mStrTerminalName = Build.MODEL;
        //######## Device ID #########
        //######## Device ID #########
        //######## Device ID #########
        mStrDeviceId = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        //############# Check Internet ##############
        //############# Check Internet ##############
        //############# Check Internet ##############
        checkInternet();

//        //############## Auto Login Check ##############
//        //############## Auto Login Check ##############
//        //############## Auto Login Check ##############
//        mSharedPreferencsLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
//        mSharedPreferencsLoginEditor = mSharedPreferencsLogin.edit();
//        isRememberMe = mSharedPreferencsLogin.getBoolean("rememberCredentials", false);
//
//        //############# Login and Set Login Credentials ##############
//        //############# Login and Set Login Credentials ##############
//        //############# Login and Set Login Credentials ##############
//        if (isRememberMe) {
//            mEditTextUserId.setText(mSharedPreferencsLogin.getString("userid", ""));
//            mEditTextPin.setText(mSharedPreferencsLogin.getString("pin_submission", ""));
//            mCheckBoxRememberMe.setChecked(true);
//            login();
//        }

    }

    @Override
    public void onClick(View v) {
        if (v == mImgBtnInfo) {
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(Login.this);
            mAlertDialogBuilder.setMessage("You can only use one account per device. If you want to use another account from this device, please tag your account.");
            mAlertDialogBuilder.setNegativeButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            startActivity(new Intent(Login.this, TagDevice.class));
                        }
                    });
            AlertDialog mAlertDialog = mAlertDialogBuilder.create();
            mAlertDialog.show();
        }
        if (v == mBtnLogin) {
            login();
        } else if (v == mTextViewForgetPin) {
            startActivity(new Intent(Login.this, ForgetPin.class));
        } else if (v == mTextViewRegistration) {
            startActivity(new Intent(Login.this, SignUp.class));
        }
    }

    private void login() {
        if (mEditTextUserId.getText().toString().length() == 0) {
            mEditTextUserId.setError("Field cannot be empty");
        } else if (mEditTextPin.getText().toString().length() == 0) {
            mEditTextPin.setError("Field cannot be empty");
        } else if (mEditTextPin.getText().toString().length() < 4) {
            mEditTextPin.setError("Must be 4 characters in length");
        } else {
            if (mStrDeviceRegistrationStatus.equalsIgnoreCase("Device is Registered")) {
                // ########## Device Register #############
                // ########## Device Register #############
                // ########## Device Register #############
                String strDeviceActiveStatus = CheckDeviceActiveStatus.checkDeviceActiveStatus(mStrDeviceId);
                if (strDeviceActiveStatus.equals("A")) {
                    // ############################ Active ############################
                    // ############################ Active ############################
                    // ############################ Active ############################
                    mStrUserId = mEditTextUserId.getText().toString();
                    mStrPin = mEditTextPin.getText().toString();

                    //##########################################################################################
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mEditTextUserId.getWindowToken(), 0);
                    //##########################################################################################

                    //#############################################################################################
                    if (mCheckBoxRememberMe.isChecked()) {
                        mSharedPreferencsLoginEditor.putBoolean("rememberCredentials", true);
                        mSharedPreferencsLoginEditor.putString("userid", mStrUserId);
                        mSharedPreferencsLoginEditor.putString("pin_submission", mStrPin);
                        mSharedPreferencsLoginEditor.commit();
                    } else {
                        mSharedPreferencsLoginEditor.clear();
                        mSharedPreferencsLoginEditor.commit();
                    }
                    //##############################################################################################

                    String strMasterKeyAndAccountNumber = GetMasterKeyByUserId.getMasterKeyByUserId(mStrUserId, GlobalData.getStrRegistrationSecurityKey());
                    int intIndexOtp = strMasterKeyAndAccountNumber.indexOf("\\*");
                    if (intIndexOtp == -1) {
                        if (strMasterKeyAndAccountNumber.equalsIgnoreCase("Master key is null")) {
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(Login.this);
                            myAlert.setMessage("Invalid User ID or PIN");
                            myAlert.setNegativeButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alertDialog = myAlert.create();
                            alertDialog.show();
                        } else if (strMasterKeyAndAccountNumber.equalsIgnoreCase("Invalid Key")) {
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(Login.this);
                            myAlert.setMessage("Invalid User ID or PIN");
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
                            String[] parts = strMasterKeyAndAccountNumber.split("\\*");
                            mStrMasterKey = parts[0];//96745482897185504726639965371045
                            mStrEncryptAccountNumber = parts[1];//0021200000007

                            try {
                                mStrEncryptUserId = encryption.Encrypt(mStrUserId, mStrMasterKey);
                                mStrEnycryptPin = encryption.Encrypt(mStrPin, mStrMasterKey);
                                mStrEnycryptDeviceId = encryption.Encrypt(mStrDeviceId, mStrMasterKey);
                                String strCheckDeviceId = CheckDeviceIdByAccountNumber.checkDeviceIdByAccountNumber(mStrEncryptUserId, mStrEnycryptDeviceId, mStrMasterKey);
                                if (strCheckDeviceId.equalsIgnoreCase("Match")) {
                                    String strCheckMasterKey = CheckMasterKey.checkMasterKey(mStrMasterKey, mStrEncryptAccountNumber);
                                    if (strCheckMasterKey.equals("Right key")) {
                                        // Initialize progress dialog
                                        mProgressDialog = ProgressDialog.show(Login.this, null, "Login...", false, true);
                                        // Cancel progress dialog on back key press
                                        mProgressDialog.setCancelable(true);

                                        Thread t = new Thread(new Runnable() {

                                            @Override
                                            public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                                                doLogin(mStrEncryptUserId, mStrEnycryptPin, mStrMasterKey);//QPAY_Login(UserID,PIN,strMasterKey)
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
                                                                mTextViewShowServerResponse.setText(mStrServerResponse);
//                                                        mEditTextUserId.setText("");
                                                                mEditTextPin.setText("");
                                                            }
                                                        } catch (Exception e) {
                                                            // TODO: handle exception
                                                        }
                                                    }
                                                });
                                            }
                                        });

                                        t.start();

                                    } else {
                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(Login.this);
                                        myAlert.setMessage(strCheckMasterKey);
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
                                } else {
                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(Login.this);
                                    myAlert.setMessage(strCheckDeviceId);
                                    myAlert.setPositiveButton(
                                            "Ok",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    startActivity(new Intent(Login.this, TagDevice.class));
                                                }
                                            });
                                    myAlert.setNegativeButton(
                                            "Cancel",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = myAlert.create();
                                    alertDialog.show();
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                } else {
                    // ############################ Inactive ############################
                    // ############################ Inactive ############################
                    // ############################ Inactive ############################
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(Login.this);
                    mAlertDialogBuilder.setMessage("Device is not Active. Please register to use this app.");
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
            } else {
                // ########## Device Not Register #############
                // ########## Device Not Register #############
                // ########## Device Not Register #############
                AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(Login.this);
                mAlertDialogBuilder.setMessage(mStrDeviceRegistrationStatus);
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
    }

    // Login Button
    public void doLogin(String strEncryptUserId,
                        String strEncryptPin,
                        String strMasterKey) {
        String METHOD_NAME = "QPAY_Login";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Login";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //QPAY_Login(UserID,PIN,strMasterKey)
        PropertyInfo encryptUserId = new PropertyInfo();
        encryptUserId.setName("UserID");
        encryptUserId.setValue(strEncryptUserId);
        encryptUserId.setType(String.class);
        request.addProperty(encryptUserId);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PIN");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        envelope.implicitTypes = true;
        Object objLoginResponse = null;

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objLoginResponse = envelope.getResponse();
            String strLoginResponse = objLoginResponse.toString();// Login Response
            int intIndexOtpResponse = strLoginResponse.indexOf("Login Successfully");
            if (intIndexOtpResponse == -1) {
                mStrServerResponse = "Login failed. Please insert correct User ID and PIN.";
            } else {
                String[] parts = strLoginResponse.split("\\&");//Login Successfully&7A8NTA5Z0iFBeBcqhyKSBg==&xXnV1UduuUGrB4sz9/ibzA==
                String strLoginStatus = parts[0];//Login Successfully
                mStrEncryptPackage = parts[1];//7A8NTA5Z0iFBeBcqhyKSBg==
                mStrEncryptAccountNumber = parts[2];//xXnV1UduuUGrB4sz9/ibzA==
                mStrEncryptAccountHolderName = parts[3];
                mStrEncryptAccountRank = parts[4];
                //Decrypt Values
                //Decrypt Values
                //Decrypt Values
                decryptionValues();
                // All Wallet
                // All Wallet
                // All Wallet
                mStrVoucherWallet = mStrAccountNumber + 1;
//                mStrCanteenWallet = mStrAccountNumber + 1;
//                mStrSimBillWallet = mStrAccountNumber + 1;
                //All Encrypt Wallet
                //All Encrypt Wallet
                //All Encrypt Wallet
                String strEncryptPrimaryWallet = encryption.Encrypt(mStrVoucherWallet, mStrMasterKey);
                //Get Session ID
                //Get Session ID
                //Get Session ID
                mStrSessionId = GetSessionId.getSessionId(mStrEncryptAccountNumber, mStrMasterKey);
                //Get All QR Code
                //Get All QR Code
                //Get All QR Code
                mStrQrCodeContent = GetQrCodeContent.getQrCode(strEncryptPrimaryWallet, mStrMasterKey);
                //Set All Values in GlobalData
                //Set All Values in GlobalData
                //Set All Values in GlobalData
                setGlobalValues();
                //All Wallet Balance
                //All Wallet Balance
                //All Wallet Balance
                String strPrimaryWalletBalance = GetBalance.getBalance(mStrEncryptUserId, mStrEnycryptPin, strEncryptPrimaryWallet, mStrMasterKey);
                //set Balance in GlobalData
                //set Balance in GlobalData
                //set Balance in GlobalData
                GlobalData.setStrPrimaryWalletBalance(strPrimaryWalletBalance);
//                startActivity(new Intent(Login.this, QPayMenu.class));
                startActivity(new Intent(Login.this, QPayMenuNew.class));
            }

        } catch (Exception exception) {
            mStrServerResponse = "Login failed. Please insert correct User ID and PIN.";
        }
    }

    private void decryptionValues() {
        try {
            mStrPackage = decryption.Decrypt(mStrEncryptPackage, mStrMasterKey);
            mStrAccountNumber = decryption.Decrypt(mStrEncryptAccountNumber, mStrMasterKey);
            mStrAccountHolderName = decryption.Decrypt(mStrEncryptAccountHolderName, mStrMasterKey);
            mStrAccountRank = decryption.Decrypt(mStrEncryptAccountRank, mStrMasterKey);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void setGlobalValues() {
        GlobalData.setStrDeviceId(mStrDeviceId);
        GlobalData.setStrDeviceName(mStrTerminalName);
        GlobalData.setStrUserId(mStrUserId);
        GlobalData.setStrEncryptUserId(mStrEncryptUserId);
        GlobalData.setStrPin(mStrPin);
        GlobalData.setStrEncryptPin(mStrEnycryptPin);
        GlobalData.setStrMasterKey(mStrMasterKey);
        GlobalData.setStrPackage(mStrPackage);
        GlobalData.setStrEncryptPackage(mStrEncryptPackage);
        GlobalData.setStrAccountNumber(mStrAccountNumber);
        GlobalData.setStrEncryptAccountNumber(mStrEncryptAccountNumber);
        GlobalData.setStrWallet(mStrVoucherWallet);
        GlobalData.setStrCustomerAccountL14(mStrVoucherWallet);
        GlobalData.setStrAccountHolderName(mStrAccountHolderName);
        GlobalData.setStrEncryptAccountRank(mStrEncryptAccountRank);
        GlobalData.setStrAccountRank(mStrAccountRank);
        GlobalData.setStrSessionId(mStrSessionId);
        GlobalData.setStrQrCodeContent(mStrQrCodeContent);
    }

    private void checkInternet() {
        if (isNetworkConnected()) {
            //########################## Online ###########################
            //########################## Online ###########################
            //########################## Online ###########################

            // ########## Device Registration #############
            // ########## Device Registration #############
            // ########## Device Registration #############
            mStrDeviceRegistrationStatus = DeviceRegistration.doDeviceRegistration(mStrTerminalName, mStrDeviceId, GlobalData.getStrChannelType(), GlobalData.getStrSecurityKey());

            //############## Auto Login Check ##############
            //############## Auto Login Check ##############
            //############## Auto Login Check ##############
            mSharedPreferencsLogin = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            mSharedPreferencsLoginEditor = mSharedPreferencsLogin.edit();
            isRememberMe = mSharedPreferencsLogin.getBoolean("rememberCredentials", false);

            //############# Login and Set Login Credentials ##############
            //############# Login and Set Login Credentials ##############
            //############# Login and Set Login Credentials ##############
            if (isRememberMe) {
                mEditTextUserId.setText(mSharedPreferencsLogin.getString("userid", ""));
                mEditTextPin.setText(mSharedPreferencsLogin.getString("pin_submission", ""));
                mCheckBoxRememberMe.setChecked(true);
                login();
            }
        } else {
            //########################## Offline ###########################
            //########################## Offline ###########################
            //########################## Offline ###########################
            disableUiComponents();

            //########################## Dialog ###########################
            //########################## Dialog ###########################
            //########################## Dialog ###########################
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(Login.this);
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
        mEditTextPin.setEnabled(true);
        mCheckBoxRememberMe.setEnabled(true);
        mTextViewRegistration.setEnabled(true);
        mBtnLogin.setEnabled(true);
        mTextViewForgetPin.setEnabled(true);
        mImgBtnInfo.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextUserId.setEnabled(false);
        mEditTextPin.setEnabled(false);
        mCheckBoxRememberMe.setEnabled(false);
        mTextViewRegistration.setEnabled(false);
        mBtnLogin.setEnabled(false);
        mTextViewForgetPin.setEnabled(false);
        mImgBtnInfo.setEnabled(false);
    }

}
