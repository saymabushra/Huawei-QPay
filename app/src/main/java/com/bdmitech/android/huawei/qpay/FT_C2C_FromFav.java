package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.EncryptionDecryption;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class FT_C2C_FromFav extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;

    //#############################################################
    private SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    private EditText mEditTextCaption, mEditTextSourceWallet, mEditTextDestinationWallet,
            mEditTextDestinationAccountName, mEditTextAmount, mEditTextSourceOtp;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrEncryptCaption, mStrEncryptSourceWallet, mStrEncryptSourcePin,
            mStrEncryptSourceOtp, mStrEncryptAmount, mStrEncryptDestinationWallet,
            mStrEncryptDestinationAccountName, mStrEncryptFunctionType, mStrServerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_transfer_from_fav);
        checkOs();
        initUI();
    }

    private void checkOs() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initUI() {
        mEditTextCaption = findViewById(R.id.editTextSendMoneyFromFavCaption);
        mEditTextCaption.setText(GlobalData.getStrFavCaption());
        mEditTextSourceWallet = findViewById(R.id.editTextSendMoneyFromFavSourceWallet);
        mEditTextSourceWallet.setText(GlobalData.getStrFavSourceWallet());
        mEditTextDestinationWallet = findViewById(R.id.editTextSendMoneyFromFavDestinationWallet);
        mEditTextDestinationWallet.setText(GlobalData.getStrFavDestinationWallet());
        mEditTextDestinationAccountName = findViewById(R.id.editTextSendMoneyFromFavDestinationName);
        mEditTextDestinationAccountName.setText(GlobalData.getStrFavDestinationWalletAccountHolderName());
        mEditTextAmount = findViewById(R.id.editTextSendMoneyFromFavAmount);
        mEditTextAmount.setText(GlobalData.getStrFavAmount());
        mEditTextAmount.requestFocus();
        mEditTextSourceOtp = findViewById(R.id.editTextSendMoneyFromFavSourceOtp);
        mBtnSubmit = findViewById(R.id.btnSendMoneyFromFavSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewSendMoneyFromFavServerResponse);

        //################### set OTP #########################
        //################### set OTP #########################
        //################### set OTP #########################
        mSharedPreferencsOtp = getSharedPreferences("otpPrefs", MODE_PRIVATE);
        mSharedPreferencsOtpEditor = mSharedPreferencsOtp.edit();
        mEditTextSourceOtp.setText(mSharedPreferencsOtp.getString("generate_otp", ""));
        //#######################################################################################################

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }


    @Override
    public void onClick(View v) {
        if (v == mBtnSubmit) {
            try {
                mSharedPreferencsOtp = getSharedPreferences("otpPrefs", MODE_PRIVATE);
                mSharedPreferencsOtpEditor = mSharedPreferencsOtp.edit();
                String strExpireTime = mSharedPreferencsOtp.getString("otp_expire_time", "");
                String strOtp = mSharedPreferencsOtp.getString("generate_otp", "");

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                // Current Time
                Date currentTime = Calendar.getInstance().getTime();
                String strCurrentTime = df.format(currentTime);
                // Expire Time
                Date timeCurrent = df.parse(strCurrentTime);
                Date timeExpire = df.parse(strExpireTime);

                if (timeCurrent.before(timeExpire)) {
                    //############################
                    if (mEditTextCaption.getText().toString().length() == 0) {
                        mEditTextCaption.setError("Field cannot be empty");
                    } else if (mEditTextSourceWallet.getText().toString().length() == 5) {
                        mEditTextSourceWallet.setError("Field cannot be empty");
                    } else if (mEditTextDestinationWallet.getText().toString().length() == 0) {
                        mEditTextDestinationWallet.setError("Field cannot be empty");
                    } else if (mEditTextDestinationAccountName.getText().toString().length() == 0) {
                        mEditTextDestinationAccountName.setError("Field cannot be empty");
                    } else if (mEditTextAmount.getText().toString().length() == 0) {
                        mEditTextAmount.setError("Field cannot be empty");
                    } else if (mEditTextSourceOtp.getText().toString().length() == 0) {
                        mEditTextSourceOtp.setError("Field cannot be empty");
                    } else {

                        try {
                            mStrEncryptCaption = encryptionDecryption.Encrypt(mEditTextCaption.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptSourceWallet = encryptionDecryption.Encrypt(mEditTextSourceWallet.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptDestinationWallet = encryptionDecryption.Encrypt(mEditTextDestinationWallet.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptDestinationAccountName = encryptionDecryption.Encrypt(mEditTextDestinationAccountName.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptAmount = encryptionDecryption.Encrypt(mEditTextAmount.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptSourceOtp = encryptionDecryption.Encrypt(mEditTextSourceOtp.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptFunctionType = encryptionDecryption.Decrypt(GlobalData.getStrFavFunctionType(), GlobalData.getStrMasterKey());
                            mStrEncryptSourcePin = encryptionDecryption.Encrypt(GlobalData.getStrPin(), GlobalData.getStrMasterKey());

                            // Initialize progress dialog
                            mProgressDialog = ProgressDialog.show(FT_C2C_FromFav.this, null, "Processing request...", false, true);
                            // Cancel progress dialog on back key press
                            mProgressDialog.setCancelable(true);

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                                    doSendMoney(
                                            mStrEncryptSourceWallet,
                                            mStrEncryptSourcePin,
                                            mStrEncryptSourceOtp,
                                            mStrEncryptAmount,
                                            mStrEncryptDestinationWallet,
                                            GlobalData.getStrMasterKey());
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
                                                    int intIndex = mStrServerResponse.indexOf("successful");
                                                    if (intIndex == -1) {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_FromFav.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C_FromFav.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    } else {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_FromFav.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNeutralButton(
                                                                "Continue",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
//                                                                        clearEditText();
                                                                    }
                                                                });
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C_FromFav.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    }

                                                }

                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
//                                            try {
//                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                                    mProgressDialog.dismiss();
//                                                    //####################### Show Dialog ####################
//                                                    //####################### Show Dialog ####################
//                                                    //####################### Show Dialog ####################
//                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_FromFav.this);
//                                                    myAlert.setMessage(mStrServerResponse);
//                                                    myAlert.setNegativeButton(
//                                                            "OK",
//                                                            new DialogInterface.OnClickListener() {
//                                                                public void onClick(DialogInterface dialog, int id) {
//                                                                    dialog.cancel();
//                                                                }
//                                                            });
//                                                    AlertDialog alertDialog = myAlert.create();
//                                                    alertDialog.show();
//
//                                                }
//
//                                            } catch (Exception e) {
//                                                // TODO: handle exception
//                                            }
                                            // update ui info ( show response message )
                                        }
                                    });
                                }
                            });

                            t.start();


                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }


                    }

                    //###########################
                } else {
                    // if expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_FromFav.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C_FromFav.this, GenerateOtp.class));
                                }
                            });
                    myAlert.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C_FromFav.this, QPayMenuNew.class));
                                }
                            });
                    AlertDialog alertDialog = myAlert.create();
                    alertDialog.show();
                }
                //####
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

//        if (v == mBtnAddToFav) {
//            if (mEditTextCaption.getText().toString().length() == 0) {
//                mEditTextCaption.setError("Field cannot be empty");
//            } else if (mEditTextSourceWallet.getText().toString().length() == 5) {
//                mEditTextSourceWallet.setError("Field cannot be empty");
//            } else if (mEditTextDestinationWallet.getText().toString().length() == 0) {
//                mEditTextDestinationWallet.setError("Field cannot be empty");
//            } else if (mEditTextDestinationAccountName.getText().toString().length() == 0) {
//                mEditTextDestinationAccountName.setError("Field cannot be empty");
//            } else if (mEditTextAmount.getText().toString().length() == 0) {
//                mEditTextAmount.setError("Field cannot be empty");
//            } else if (mEditTextSourceOtp.getText().toString().length() == 0) {
//                mEditTextSourceOtp.setError("Field cannot be empty");
//            } else {
//                try {
//                    mStrEncryptCaption = encryptionDecryption.Encrypt(mEditTextCaption.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptSourceWallet = encryptionDecryption.Encrypt(mEditTextSourceWallet.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptDestinationWallet = encryptionDecryption.Encrypt(mEditTextDestinationWallet.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptDestinationAccountName = encryptionDecryption.Encrypt(mEditTextDestinationAccountName.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptAmount = encryptionDecryption.Encrypt(mEditTextAmount.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptSourceOtp = encryptionDecryption.Encrypt(mEditTextSourceOtp.getText().toString(), GlobalData.getStrMasterKey());
//                    mStrEncryptFunctionType = encryptionDecryption.Encrypt(GlobalData.getStrFavFunctionType(), GlobalData.getStrMasterKey());
//                    mStrEncryptSourcePin = encryptionDecryption.Encrypt(GlobalData.getStrPin(), GlobalData.getStrMasterKey());
//                } catch (Exception exception) {
//                    exception.printStackTrace();
//                }
//
//                // Initialize progress dialog
//                mProgressDialog = ProgressDialog.show(FT_C2C_FromFav.this, null, "Processing request...", false, true);
//                // Cancel progress dialog on back key press
//                mProgressDialog.setCancelable(true);
//
//                Thread t = new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
////                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
//                        addToFav(mStrEncryptSourceWallet,
//                                mStrEncryptSourcePin,
//                                mStrEncryptAmount,
//                                mStrEncryptDestinationWallet,
//                                mStrEncryptDestinationAccountName,
//                                mStrEncryptCaption,
//                                mStrEncryptFunctionType,
//                                GlobalData.getStrMasterKey());
////                            SystemClock.sleep(10000);
////                    } else {
////                        mTextViewShowServerResponse.setText("Session Expire, Please Login Again");
////                    }
//                        runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                try {
//                                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
//                                        mProgressDialog.dismiss();
//                                        //####################### Show Dialog ####################
//                                        //####################### Show Dialog ####################
//                                        //####################### Show Dialog ####################
//                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_FromFav.this);
//                                        myAlert.setMessage(mStrServerResponse);
//                                        myAlert.setNegativeButton(
//                                                "OK",
//                                                new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface dialog, int id) {
//                                                        startActivity(new Intent(FT_C2C_FromFav.this, QPayMenu.class));
//                                                        dialog.cancel();
//                                                    }
//                                                });
//                                        AlertDialog alertDialog = myAlert.create();
//                                        alertDialog.show();
//
//                                    }
//
//                                } catch (Exception e) {
//                                    // TODO: handle exception
//                                }
//                                // update ui info ( show response message )
//                            }
//                        });
//                    }
//                });
//
//                t.start();
//
//            }
//
//
//        }
    }

//    public void addToFav(String strEncryptSourceWallet,
//                         String strEncryptSourcePin,
//                         String strEncryptAmount,
//                         String strEncryptDestinationWallet,
//                         String strEncryptDestinationName,
//                         String strEncryptCaption,
//                         String strEncryptFunctionType,
//                         String strMasterKey) {
//        METHOD_NAME = "QPAY_Add_FAV_FundTransfer";
//        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Add_FAV_FundTransfer";
//        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
//        // Declare the version of the SOAP request
//        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
//
////        QPAY_Add_FAV_FundTransfer
////        SourceNo
////        PIN
////        Amount
////        DestinationNo
////        DestinatioName
////        Caption
////        FunctionType
////        strMasterKey
//
//        PropertyInfo encryptSourceWallet = new PropertyInfo();
//        encryptSourceWallet.setName("SourceNo");
//        encryptSourceWallet.setValue(strEncryptSourceWallet);
//        encryptSourceWallet.setType(String.class);
//        request.addProperty(encryptSourceWallet);
//
//        PropertyInfo encryptSourcePin = new PropertyInfo();
//        encryptSourcePin.setName("PIN");
//        encryptSourcePin.setValue(strEncryptSourcePin);
//        encryptSourcePin.setType(String.class);
//        request.addProperty(encryptSourcePin);
//
//        PropertyInfo encryptAmount = new PropertyInfo();
//        encryptAmount.setName("Amount");
//        encryptAmount.setValue(strEncryptAmount);
//        encryptAmount.setType(String.class);
//        request.addProperty(encryptAmount);
//
//        PropertyInfo encryptDestinationWallet = new PropertyInfo();
//        encryptDestinationWallet.setName("DestinationNo");
//        encryptDestinationWallet.setValue(strEncryptDestinationWallet);
//        encryptDestinationWallet.setType(String.class);
//        request.addProperty(encryptDestinationWallet);
//
//        PropertyInfo encryptDestinationName = new PropertyInfo();
//        encryptDestinationName.setName("DestinatioName");
//        encryptDestinationName.setValue(strEncryptDestinationName);
//        encryptDestinationName.setType(String.class);
//        request.addProperty(encryptDestinationName);
//
//        PropertyInfo encryptCaption = new PropertyInfo();
//        encryptCaption.setName("Caption");
//        encryptCaption.setValue(strEncryptCaption);
//        encryptCaption.setType(String.class);
//        request.addProperty(encryptCaption);
//
//        PropertyInfo encryptFunctionType = new PropertyInfo();
//        encryptFunctionType.setName("FunctionType");
//        encryptFunctionType.setValue(strEncryptFunctionType);
//        encryptFunctionType.setType(String.class);
//        request.addProperty(encryptFunctionType);
//
//        PropertyInfo masterKey = new PropertyInfo();
//        masterKey.setName("strMasterKey");
//        masterKey.setValue(strMasterKey);
//        masterKey.setType(String.class);
//        request.addProperty(masterKey);
//
//        envelope.dotNet = true;
//
//        envelope.setOutputSoapObject(request);
//        Log.v("myApp:", request.toString());
//        envelope.implicitTypes = true;
//        Object objAddToFav = null;
//        String strAddToFavResponse = "";
//
//        try {
//            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
//            androidHttpTransport.call(SOAP_ACTION, envelope);
//            objAddToFav = envelope.getResponse();
//            strAddToFavResponse = objAddToFav.toString();
//            if (strAddToFavResponse.equalsIgnoreCase("Submit")) {
//                mStrServerResponse = "Successfully added beneficiary info in favorite list.";
//            } else {
//                mStrServerResponse = strAddToFavResponse;
//            }
//        } catch (
//                Exception exception) {
//            mStrServerResponse = strAddToFavResponse;
//        }
//
//    }


    public void doSendMoney(String strEncryptSourceAccountNumber,
                            String strEncryptSourcePin,
                            String strEncryptSourceOtp,
                            String strEncryptAmount,
                            String strEncryptDestinationAccountNumber,
                            String strMasterKey) {
        METHOD_NAME = "QPAY_FundTransfer";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_FundTransfer";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //QPAY_FundTransfer
        // SourceNo
        // PIN
        // Amount
        // DestinationNo
        // strMasterKey
        PropertyInfo sourceAccountNumber = new PropertyInfo();
        sourceAccountNumber.setName("SourceNo");
        sourceAccountNumber.setValue(strEncryptSourceAccountNumber);
        sourceAccountNumber.setType(String.class);
        request.addProperty(sourceAccountNumber);

        PropertyInfo sourcePin = new PropertyInfo();
        sourcePin.setName("PIN");
        sourcePin.setValue(strEncryptSourcePin);
        sourcePin.setType(String.class);
        request.addProperty(sourcePin);

        PropertyInfo sourceOtp = new PropertyInfo();
        sourceOtp.setName("SourceOTP");
        sourceOtp.setValue(strEncryptSourceOtp);
        sourceOtp.setType(String.class);
        request.addProperty(sourceOtp);

        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setValue(strEncryptAmount);
        amount.setType(String.class);
        request.addProperty(amount);

        PropertyInfo destinationAccountNumber = new PropertyInfo();
        destinationAccountNumber.setName("DestinationNo");
        destinationAccountNumber.setValue(strEncryptDestinationAccountNumber);
        destinationAccountNumber.setType(String.class);
        request.addProperty(destinationAccountNumber);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objSendMoney = null;
        String strSendMoneyResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objSendMoney = envelope.getResponse();
            strSendMoneyResponse = objSendMoney.toString();
            mStrServerResponse = strSendMoneyResponse;


            //            Send money  successful on 20-Mar-18 04:11:07Sent to: 00612000000551Amount Tk.500.00
//            Balance: Tk.97,945.00
//            TXN ID: 18032000000089
//            QPAY//Money Received From: 00612000000531 on 20-Mar-18 04:11:07
//            Amount: Tk.500.00
//            Balance: Tk.2,055.00
//            TXN ID: 18032000000089
//            QPAY,*AP180320161338

//            Merchant are not allow for this transactionQPAY,*AP180320160638

//            The customer OTP is wrong please insert correct OTP.,*AP180320161231

            int intIndex = mStrServerResponse.indexOf("successful");
            if (intIndex == -1) {
                int intIndex1 = mStrServerResponse.indexOf("not allow");
                if (intIndex1 == -1) {

                } else {
                    String[] parts = mStrServerResponse.split(",");
                    String strResponse = parts[0];
                    String strExtra = parts[1];
                    mStrServerResponse = strResponse;
                }
                int intIndex2 = mStrServerResponse.indexOf("wrong");
                if (intIndex2 == -1) {

                } else {
                    String[] parts = mStrServerResponse.split(",");
                    String strResponse = parts[0];
                    String strExtra = parts[1];
                    mStrServerResponse = strResponse;
                }
            } else {
                String[] parts = mStrServerResponse.split("//");
                String strResponse = parts[0];//
                String strExtra = parts[1];
                mStrServerResponse = strResponse;
            }
        } catch (Exception exception) {
            mStrServerResponse = strSendMoneyResponse;
        }
    }


    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C_FromFav.this);
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
        mEditTextCaption.setEnabled(false);
        mEditTextSourceWallet.setEnabled(false);
        mEditTextDestinationWallet.setEnabled(false);
        mEditTextDestinationAccountName.setEnabled(false);
        mEditTextAmount.setEnabled(true);
        mEditTextSourceOtp.setEnabled(true);
        mBtnSubmit.setEnabled(true);
        mBtnSubmit.setEnabled(true);
        mTextViewShowServerResponse.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextCaption.setEnabled(false);
        mEditTextSourceWallet.setEnabled(false);
        mEditTextDestinationWallet.setEnabled(false);
        mEditTextDestinationAccountName.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextSourceOtp.setEnabled(false);
        mBtnSubmit.setEnabled(false);
        mBtnSubmit.setEnabled(false);
        mTextViewShowServerResponse.setEnabled(false);
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
                Intent intent = new Intent(FT_C2C_FromFav.this, Login.class)
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
            startActivity(new Intent(FT_C2C_FromFav.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
