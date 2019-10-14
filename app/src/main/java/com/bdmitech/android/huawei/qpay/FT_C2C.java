package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.EncryptionDecryption;
import com.bdmitech.android.huawei.qpay.utils.GetEncryptAccountNumberAndMasterKeyByQrCode;
import com.bdmitech.android.huawei.qpay.utils.GetAllCustomerWallet;
import com.bdmitech.android.huawei.qpay.utils.GetMerchantName;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class FT_C2C extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;

    //#############################################################
    private SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    private Spinner mSpinnerWallet;
    private EditText mEditTextOtp, mEditTextAmount, mEditTextBeneficiaryAccountNumber;
    private ImageButton mImgBtnScanQr;
    private Button mBtnSubmit, mBtnAddToFav;
    private TextView mTextViewShowServerResponse;
    private String strBeneficiaryAccountNumber, mStrEncryptBeneficiaryAccountNumber, strMerchantMasterKey,
            strUrlForQrCode, strEncryptMerchantAccountNumber,
            strMethodName, strBankBin, mStrServerResponse,
            mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptAmount,
            mStrEncryptOtp, mStrDestinationAccountHolderName, mStrSourceWallet, mStrEncryptDestinationWallet,
            mStrEncryptDestinationName, mStrEncryptCaption, mStrEncryptFunctionType, mStrSourcePin,strSourcePin,
    strAmount,strDestinationWallet,strDestinationAccountName,strFunctionType;

    ArrayList<String> arrayListWalletType = new ArrayList<String>();
    ArrayList<String> arrayListWallet = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_transfer);
        checkOs();
        initUI();
    }

    private void initUI() {
        mSpinnerWallet = findViewById(R.id.spinnerSendMoneyWallet);
        mImgBtnScanQr = findViewById(R.id.imgBtnFundTransferScanQrCustomerWallet);
        mImgBtnScanQr.setOnClickListener(this);
        mEditTextOtp = findViewById(R.id.editTextSendMoneyOtp);
        mEditTextAmount = findViewById(R.id.editTextSendMoneyAmount);
        mEditTextAmount.requestFocus();
        mEditTextBeneficiaryAccountNumber = findViewById(R.id.editTextSendMoneyBeneficiaryAccountNumber);
        mBtnSubmit = findViewById(R.id.btnSendMoneySubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewSendMoneyServerResponse);

        try {
            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
            //############################################### OTP ###############################################
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
                // if valid
                mEditTextOtp.setText(strOtp);
            } else {
                // if expire
                AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                myAlert.setMessage("OTP is expired. Generate a new OTP?");
                myAlert.setPositiveButton(
                        "Generate OTP",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(FT_C2C.this, GenerateOtp.class));
                            }
                        });
                myAlert.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(FT_C2C.this, QPayMenuNew.class));
                            }
                        });
                AlertDialog alertDialog = myAlert.create();
                alertDialog.show();
            }
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
        if (v == mImgBtnScanQr) {
//            mEditTextBeneficiaryAccountNumber.setFocusable(false);
//            mEditTextBeneficiaryAccountNumber.setEnabled(false);
            try {
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                showDialog(FT_C2C.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
                        "No").show();
            }
        }
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

                    //########################################
                    if (mEditTextOtp.getText().toString().length() == 0) {
                        mEditTextOtp.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() < 5) {
                        mEditTextOtp.setError("Must be 5 characters in length");
                    } else if (mEditTextAmount.getText().toString().length() == 0) {
                        mEditTextAmount.setError("Field cannot be empty");
                    } else if (mEditTextBeneficiaryAccountNumber.getText().toString().length() == 0) {
                        mEditTextBeneficiaryAccountNumber.setError("Field cannot be empty");
                    } else {
                        strSourcePin = GlobalData.getStrPin();
                        strAmount = mEditTextAmount.getText().toString();
                        strDestinationWallet = mEditTextBeneficiaryAccountNumber.getText().toString();
                        strDestinationAccountName = mStrDestinationAccountHolderName;
                        strFunctionType = "CFT";
                        try {
                            mStrEncryptAccountNumber = encryptionDecryption.Encrypt(mStrSourceWallet, GlobalData.getStrMasterKey());
                            mStrEncryptPin = encryptionDecryption.Encrypt(strSourcePin, GlobalData.getStrMasterKey());
                            mStrEncryptOtp = encryptionDecryption.Encrypt(mEditTextOtp.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptAmount = encryptionDecryption.Encrypt(strAmount, GlobalData.getStrMasterKey());
                            mStrEncryptBeneficiaryAccountNumber = encryptionDecryption.Encrypt(strDestinationWallet, GlobalData.getStrMasterKey());

                            // Initialize progress dialog
                            mProgressDialog = ProgressDialog.show(FT_C2C.this, null, "Processing request...", false, true);
                            // Cancel progress dialog on back key press
                            mProgressDialog.setCancelable(true);

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                                    doSendMoney(mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptOtp, mStrEncryptAmount, mStrEncryptBeneficiaryAccountNumber, GlobalData.getStrMasterKey());
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
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    } else {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
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
                                                                        GlobalData.setStrFavSourceWallet(mStrSourceWallet);
                                                                        GlobalData.setStrFavSourcePin(strSourcePin);
                                                                        GlobalData.setStrFavAmount(strAmount);
                                                                        GlobalData.setStrFavDestinationWallet(strDestinationWallet);
                                                                        GlobalData.setStrFavDestinationWalletAccountHolderName(strDestinationAccountName);
                                                                        GlobalData.setStrFavFunctionType(strFunctionType);
                                                                        startActivity(new Intent(FT_C2C.this, FM_M2M_AddToFav.class));
                                                                    }
                                                                });
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FT_C2C.this, QPayMenuNew.class));
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

                    //#####################################################
                } else {
                    // if expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C.this, GenerateOtp.class));
                                }
                            });
                    myAlert.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FT_C2C.this, QPayMenuNew.class));
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
//            if (mEditTextAmount.getText().toString().length() == 0) {
//                mEditTextAmount.setError("Field cannot be empty");
//            } else if (mEditTextBeneficiaryAccountNumber.getText().toString().length() == 0) {
//                mEditTextBeneficiaryAccountNumber.setError("Field cannot be empty");
//            } else {
//                String strSourceAllWallet = mSpinnerWallet.getSelectedItem().toString();
//                String[] parts = strSourceAllWallet.split("\\-");
//                String strSourceWalletType = parts[0];
//                String strSourceWallet = parts[1];
//                String strSourcePin = GlobalData.getStrPin();
//                String strAmount = mEditTextAmount.getText().toString();
//                String strDestinationWallet = mEditTextBeneficiaryAccountNumber.getText().toString();
//                String strDestinationAccountName = mStrDestinationAccountHolderName;
//                String strFunctionType = "CFT";
//
//                GlobalData.setStrFavSourceWallet(strSourceWallet);
//                GlobalData.setStrFavSourcePin(strSourcePin);
//                GlobalData.setStrFavAmount(strAmount);
//                GlobalData.setStrFavDestinationWallet(strDestinationWallet);
//                GlobalData.setStrFavDestinationWalletAccountHolderName(strDestinationAccountName);
//                GlobalData.setStrFavFunctionType(strFunctionType);
//
//                startActivity(new Intent(FT_C2C.this, FT_C2C_FromFav.class));
//            }
//
//        }
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
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            // Initialize progress dialog
            mProgressDialog = ProgressDialog.show(FT_C2C.this, null, "Loading Bank...", false, true);
            // Cancel progress dialog on back key press
            mProgressDialog.setCancelable(true);

            Thread t = new Thread(new Runnable() {

                @Override
                public void run() {
                    // Background code should be in here
                    loadSpinnerWallet();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                    mProgressDialog.dismiss();
                                    ArrayAdapter<String> adapterWallet = new ArrayAdapter<String>(FT_C2C.this,
                                            android.R.layout.simple_spinner_item, arrayListWalletType);
                                    adapterWallet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mSpinnerWallet.setAdapter(adapterWallet);
                                    mSpinnerWallet.setOnItemSelectedListener(onItemSelectedListenerForWallet);
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
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C.this);
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

    //######################### Show Account Type #########################
    //######################### Show Account Type #########################
    //######################### Show Account Type #########################
    private void loadSpinnerWallet() {
        String strAllWalletTypeAndNumber = GetAllCustomerWallet.getAllWallets();
        if (strAllWalletTypeAndNumber != null && !strAllWalletTypeAndNumber.isEmpty()) {
            StringTokenizer strToken = new StringTokenizer(strAllWalletTypeAndNumber, "&");
            ArrayList<String> arrayListWalletTypeAndNumber = new ArrayList<String>();
            for (int j = 0; j <= strToken.countTokens(); j++) {
                while (strToken.hasMoreElements()) {
                    arrayListWalletTypeAndNumber.add(strToken.nextToken());
                }
            }
            for (int i = 0; i <= arrayListWalletTypeAndNumber.size() - 1; i++) {
                StringTokenizer tokenWalletTypeAndAccount = new StringTokenizer(arrayListWalletTypeAndNumber.get(i), "-");
                arrayListWalletType.add(tokenWalletTypeAndAccount.nextToken());
                arrayListWallet.add(tokenWalletTypeAndAccount.nextToken());
            }
        } else {
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C.this);
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

    //######################### Account Number #########################
    //######################### Account Number #########################
    //######################### Account Number #########################
    AdapterView.OnItemSelectedListener onItemSelectedListenerForWallet = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mStrSourceWallet = String.valueOf(arrayListWallet.get(position));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void initSourceWallet() {
        String strAllWalletTypeAndNumber = GetAllCustomerWallet.getAllWallets();
        String[] parts = strAllWalletTypeAndNumber.split("\\&");
        String strPrimaryWallet = parts[0];
        String strSalaryWallet = parts[1];
        String strCreditWallet = parts[2];
        String[] strArrayAllWallet = {strPrimaryWallet, strSalaryWallet, strCreditWallet};

        ArrayAdapter<String> arrayAdapterWallet = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,
                        strArrayAllWallet);
        arrayAdapterWallet.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mSpinnerWallet.setAdapter(arrayAdapterWallet);
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
                Intent intent = new Intent(FT_C2C.this, Login.class)
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
        mSpinnerWallet.setEnabled(true);
        mEditTextOtp.setEnabled(true);
        mImgBtnScanQr.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mEditTextBeneficiaryAccountNumber.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mSpinnerWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mImgBtnScanQr.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextBeneficiaryAccountNumber.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }

    private void disableUiComponentAfterClick() {
        mSpinnerWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mImgBtnScanQr.setEnabled(false);
        mEditTextBeneficiaryAccountNumber.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }

    private void enableUiComponentAfterClick() {
        mSpinnerWallet.setEnabled(true);
        mEditTextOtp.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mImgBtnScanQr.setEnabled(true);
        mEditTextBeneficiaryAccountNumber.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message,
                                          CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {
                    Log.v("Tracing  Value: ", "Error!!!");
                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //Encrypt QR Code
                String mStrQrCodeContents = intent.getStringExtra("SCAN_RESULT");
                int intIndex01 = mStrQrCodeContents.indexOf(":");
                if (intIndex01 == -1) {
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C.this);
                    mAlertDialogBuilder.setMessage("QR Scan fail. Please try again.");
                    mAlertDialogBuilder.setNegativeButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog mAlertDialog = mAlertDialogBuilder.create();
                    mAlertDialog.show();
                } else {
                    String[] arrayQrCodeContents = mStrQrCodeContents.split(":");
                    strBankBin = arrayQrCodeContents[0]; //003 bank bin for Merchant
                    String strEncryptQrCode = arrayQrCodeContents[1]; //78HnkqX7uY2ebpIuhaThVDQJy83m2/WM0EROhKDzgd1P0M0dZlhQuSmUQmYDJb6pmE4RKhHjdHltEMD0FJ16mjPu3kRxwSeVkuz7hT32BCjpUOtwFvy6ygDDParmnhwN/zzcUOd7Kr4bPagq4EPnfA==

                    if (strBankBin.equalsIgnoreCase("006")) {//For Huawei
                        strUrlForQrCode = GlobalData.getStrUrl();
                        strMethodName = "QPAY_Get_Account_BY_QR_CARD";
                    } else if (strBankBin.equalsIgnoreCase("002")) {//For QPay
                        strUrlForQrCode = GlobalData.getStrUrl();
                        strMethodName = "QPAY_Get_Account_BY_QR_CARD";
                    }

                    String strEncryptDestinationAccountNumberAndMasterKey = GetEncryptAccountNumberAndMasterKeyByQrCode.getEncryptAccountNumberAndMasterKeyByQrCode(mStrQrCodeContents);
                    if (!strEncryptDestinationAccountNumberAndMasterKey.equalsIgnoreCase("")) {
                        int intIndex02 = strEncryptDestinationAccountNumberAndMasterKey.indexOf("*");
                        if (intIndex02 == -1) {
                            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C.this);
                            mAlertDialogBuilder.setMessage("QR Scan fail. Please try again.");
                            mAlertDialogBuilder.setNegativeButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog mAlertDialog = mAlertDialogBuilder.create();
                            mAlertDialog.show();
                        } else {
                            String[] parts = strEncryptDestinationAccountNumberAndMasterKey.split("\\*");
                            strEncryptMerchantAccountNumber = parts[0];//96745482897185504726639965371045
                            strMerchantMasterKey = parts[1];//0021200000007

                            try {
                                //################ Merchant Account Number ####################
                                //################ Merchant Account Number ####################
                                //################ Merchant Account Number ####################
                                strBeneficiaryAccountNumber = encryptionDecryption.Decrypt(strEncryptMerchantAccountNumber, strMerchantMasterKey);
                                String strAccountTypeCode = strBeneficiaryAccountNumber.substring(3, 5);
                                if (strAccountTypeCode.equalsIgnoreCase("12")) {
                                    //######################### Source Rank #####################
                                    //######################### Source Rank #####################
                                    //######################### Source Rank #####################
                                    String strSourceRank = mStrSourceWallet.substring(13, 14);
                                    //######################### Benificiary Rank #####################
                                    //######################### Benificiary Rank #####################
                                    //######################### Benificiary Rank #####################
                                    String strBenificaityRank = strBeneficiaryAccountNumber.substring(13, 14);
                                    if (strSourceRank.equalsIgnoreCase(strBenificaityRank)) {
                                        //################ Merchant Name ####################
                                        //################ Merchant Name ####################
                                        //################ Merchant Name ####################
                                        mStrDestinationAccountHolderName = GetMerchantName.getMerchantName(strEncryptMerchantAccountNumber, strMerchantMasterKey);
                                        //################ Show Dialog ####################
                                        //################ Show Dialog ####################
                                        //################ Show Dialog ####################
                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                                        myAlert.setTitle("BENEFICIARY INFO");
                                        myAlert.setMessage("BENEFICIARY NAME" + "\n" + mStrDestinationAccountHolderName + "\n" + "BENEFICIARY ACCOUNT NUMBER" + "\n" + strBeneficiaryAccountNumber);
                                        myAlert.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        mEditTextBeneficiaryAccountNumber.setText(strBeneficiaryAccountNumber);
                                                    }
                                                });
                                        myAlert.setNegativeButton(
                                                "CANCEL",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                        AlertDialog alertDialog = myAlert.create();
                                        alertDialog.show();
                                    } else {
                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                                        myAlert.setMessage("Account Type not match. Please scan corrent account type.");
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
                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FT_C2C.this);
                                    myAlert.setMessage("Please Scan Customer QR");
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


                            } catch (Exception exception) {
                                exception.printStackTrace();
                            }
                        }


                    } else {
                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FT_C2C.this);
                        mAlertDialogBuilder.setMessage("QR Scan fail. Please try again.");
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
        }
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
            startActivity(new Intent(FT_C2C.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
