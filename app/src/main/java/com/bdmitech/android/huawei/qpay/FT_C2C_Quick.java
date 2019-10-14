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

public class FT_C2C_Quick extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;

    //#############################################################
    private SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    private EditText mEditTextSourceWallet, mEditTextOtp, mEditTextAmount, mEditTextBeneficiaryAccountNumber;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrEncryptBeneficiaryAccountNumber, mStrServerResponse,
            mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptAmount,
            mStrEncryptOtp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_transfer_quick);
        checkOs();
        initUI();
    }

    private void initUI() {
        mEditTextSourceWallet = findViewById(R.id.editTextFundTransferQuickSourceWallet);
        mEditTextSourceWallet.setText(GlobalData.getStrSourceWallet());
        mEditTextOtp = findViewById(R.id.editTextFundTransferQuickSoruceOtp);
        mEditTextAmount = findViewById(R.id.editTextFundTransferQuickAmount);
        mEditTextAmount.requestFocus();
        mEditTextBeneficiaryAccountNumber = findViewById(R.id.editTextFundTransferQuickBeneficiaryAccountNumber);
        mEditTextBeneficiaryAccountNumber.setText(GlobalData.getStrDestinationWallet());
        mBtnSubmit = findViewById(R.id.btnFundTransferQuickSendMoney);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewFundTransferQuickServerResponse);

        try {
            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
            mSharedPreferencsOtp = getSharedPreferences("otpPrefs", MODE_PRIVATE);
            mSharedPreferencsOtpEditor = mSharedPreferencsOtp.edit();
            String strExpireTime = mSharedPreferencsOtp.getString("otp_expire_time", "");
            String strOtp = mSharedPreferencsOtp.getString("generate_otp", "");
            if (strExpireTime != null && !strExpireTime.isEmpty() && strOtp != null && !strOtp.isEmpty()) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    // Current Time
                    Date currentTime = Calendar.getInstance().getTime();
                    String strCurrentTime = df.format(currentTime);
                    // Expire Time
                    Date timeCurrent = df.parse(strCurrentTime);
                    Date timeExpire = df.parse(strExpireTime);
                    if (timeCurrent.before(timeExpire)) {
                        mEditTextOtp.setText(strOtp);
                    } else {
                        // if expire
                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_Quick.this);
                        myAlert.setMessage("OTP is expired. Generate a new OTP?");
                        myAlert.setPositiveButton(
                                "Generate OTP",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        startActivity(new Intent(FT_C2C_Quick.this, GenerateOtp.class));
                                    }
                                });
                        myAlert.setNegativeButton(
                                "Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                        startActivity(new Intent(FT_C2C_Quick.this, QPayMenuNew.class));
                                    }
                                });
                        AlertDialog alertDialog = myAlert.create();
                        alertDialog.show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
//            mSharedPreferencsOtp = getSharedPreferences("otpPrefs", MODE_PRIVATE);
//            mSharedPreferencsOtpEditor = mSharedPreferencsOtp.edit();
//            String strExpireTime = mSharedPreferencsOtp.getString("otp_expire_time", "");
//            String strOtp = mSharedPreferencsOtp.getString("generate_otp", "");
//
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//            // Current Time
//            Date currentTime = Calendar.getInstance().getTime();
//            String strCurrentTime = df.format(currentTime);
//            // Expire Time
//            Date timeCurrent = df.parse(strCurrentTime);
//            Date timeExpire = df.parse(strExpireTime);
//            if (timeCurrent.before(timeExpire)) {
//                // if valid
//                mEditTextOtp.setText(strOtp);
//            } else {
//                // if expire
//                AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_Quick.this);
//                myAlert.setMessage("OTP is expired. Generate a new OTP?");
//                myAlert.setPositiveButton(
//                        "Generate OTP",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                                startActivity(new Intent(FT_C2C_Quick.this, GenerateOtp.class));
//                            }
//                        });
//                myAlert.setNegativeButton(
//                        "Close",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//                AlertDialog alertDialog = myAlert.create();
//                alertDialog.show();
//            }
            //#######################################################################################################
            //#######################################################################################################
            //#######################################################################################################
        } catch (Exception exception) {
            exception.printStackTrace();
        }

//        initSourceWallet();

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
                    //###################################
                    if (mEditTextSourceWallet.getText().toString().length() == 0) {
                        mEditTextSourceWallet.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() == 0) {
                        mEditTextOtp.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() < 5) {
                        mEditTextOtp.setError("Must be 5 characters in length");
                    } else if (mEditTextAmount.getText().toString().length() == 0) {
                        mEditTextAmount.setError("Field cannot be empty");
                    } else if (mEditTextBeneficiaryAccountNumber.getText().toString().length() == 0) {
                        mEditTextBeneficiaryAccountNumber.setError("Field cannot be empty");
                    } else {
                        String strSourceWallet = mEditTextSourceWallet.getText().toString();
                        String strSourcePin = GlobalData.getStrPin();
                        String strAmount = mEditTextAmount.getText().toString();
                        String strDestinationWallet = mEditTextBeneficiaryAccountNumber.getText().toString();
                        String strDestinationAccountName = GlobalData.getStrDestinationWalletName();
                        String strFunctionType = "CFT";

                        GlobalData.setStrFavSourceWallet(strSourceWallet);
                        GlobalData.setStrFavSourcePin(strSourcePin);
                        GlobalData.setStrFavAmount(strAmount);
                        GlobalData.setStrFavDestinationWallet(strDestinationWallet);
                        GlobalData.setStrFavDestinationWalletAccountHolderName(strDestinationAccountName);
                        GlobalData.setStrFavFunctionType(strFunctionType);

                        try {
                            mStrEncryptAccountNumber = encryptionDecryption.Encrypt(strSourceWallet, GlobalData.getStrMasterKey());
                            mStrEncryptPin = encryptionDecryption.Encrypt(strSourcePin, GlobalData.getStrMasterKey());
                            mStrEncryptOtp = encryptionDecryption.Encrypt(mEditTextOtp.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptAmount = encryptionDecryption.Encrypt(strAmount, GlobalData.getStrMasterKey());
                            mStrEncryptBeneficiaryAccountNumber = encryptionDecryption.Encrypt(strDestinationWallet, GlobalData.getStrMasterKey());

                            // Initialize progress dialog
                            mProgressDialog = ProgressDialog.show(FT_C2C_Quick.this, null, "Processing request...", false, true);
                            // Cancel progress dialog on back key press
                            mProgressDialog.setCancelable(true);

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    doSendMoney(mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptOtp, mStrEncryptAmount, mStrEncryptBeneficiaryAccountNumber, GlobalData.getStrMasterKey());
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
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_Quick.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C_Quick.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    } else {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_Quick.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNeutralButton(
                                                                "Continue",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        clearEditText();
                                                                    }
                                                                });
                                                        myAlert.setPositiveButton(
                                                                "Add Favorite",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C_Quick.this, FM_M2M_AddToFav.class));
                                                                    }
                                                                });
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C_Quick.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    }

                                                }

                                            } catch (Exception e) {
                                                // TODO: handle exception
                                            }
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

                    //#############################
                } else {
                    // if expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C_Quick.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C_Quick.this, GenerateOtp.class));
                                }
                            });
                    myAlert.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C_Quick.this, QPayMenuNew.class));
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

    }

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


    //######################### Check Internet #########################
    //######################### Check Internet #########################
    //######################### Check Internet #########################
    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C_Quick.this);
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


    //########################## Back ############################
    //########################## Back ############################
    //########################## Back ############################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //########################## Logout ############################
    //########################## Logout ############################
    //########################## Logout ############################
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionLogout:
                clearDataFromGlobal();
                Intent intent = new Intent(FT_C2C_Quick.this, Login.class)
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

    private void enableUiComponents() {
        mEditTextSourceWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mEditTextAmount.setEnabled(true);
        mEditTextBeneficiaryAccountNumber.setEnabled(false);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextSourceWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextBeneficiaryAccountNumber.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }


    private void checkOs() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void clearEditText() {
        mEditTextAmount.setText("");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(FT_C2C_Quick.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
