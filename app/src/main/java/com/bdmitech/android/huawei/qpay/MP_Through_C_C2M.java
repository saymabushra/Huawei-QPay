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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;


import com.bdmitech.android.huawei.qpay.utils.EncryptionDecryption;
import com.bdmitech.android.huawei.qpay.utils.GetEncryptAccountNumberAndMasterKeyByQrCode;
import com.bdmitech.android.huawei.qpay.utils.GetAllMerchant;
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

public class MP_Through_C_C2M extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;
    private StringTokenizer tokensMerchantList;

    //#############################################################
    private SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    ArrayList<String> arrayListWalletType = new ArrayList<String>();
    ArrayList<String> arrayListWallet = new ArrayList<String>();

    private Spinner mSpinnerWallet;
    private EditText mEditTextMerchantAccount,
            mEditTextAmount, mEditTextOtp, mEditTextReference;
    private CheckBox mCheckBoxQrScan, mCheckBoxGetAllMerchant;
    private ImageButton mImgBtnScanQr, mImgBtnMerchantList;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String strMerchantAccountNumber, strMerchantMasterKey,
            strUrlForQrCode, strEncryptMerchantAccountNumber,
            strMethodName, strBankBin, mStrServerResponse,
            mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptMerchantAccountNumber,
            mStrEncryptAmount, mStrEncryptOtp, mStrEncryptReference,
            mStrEncryptMerchantName, mStrEncryptCaption, mStrEncryptFunctionType,
            mStrSourceWallet, mStrDestinationAccountHolderName, SourceRank, strName,
            strReference, strFunctionType, strDestinationWallet1, strAmount, mStrSourcePin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_by_customer);
        checkOs();
        initUI();
    }

    private void initUI() {
        mSpinnerWallet = findViewById(R.id.spinnerMakePaymentWallet);
        mEditTextMerchantAccount = findViewById(R.id.editTextMerchantPaymentMerchantAccountNumber);
        mImgBtnScanQr = findViewById(R.id.imgBtnScanQr);
        mImgBtnScanQr.setOnClickListener(this);
        mImgBtnMerchantList = findViewById(R.id.imgBtnList);
        mImgBtnMerchantList.setOnClickListener(this);
        mEditTextAmount = findViewById(R.id.editTextMerchantPaymentAmount);
        mEditTextAmount.requestFocus();
        mEditTextOtp = findViewById(R.id.editTextMerchantPaymentCustomerOtp);
        mEditTextReference = findViewById(R.id.editTextMerchantPaymentReference);
        mBtnSubmit = findViewById(R.id.btnMerchantPaymentSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewMerchantPaymentServerResponse);
        mStrSourcePin = GlobalData.getStrPin();
        //mStrMerchantRank=GlobalData.getStrAccountRank();

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
                AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                myAlert.setMessage("OTP is expired. Generate a new OTP?");
                myAlert.setPositiveButton(
                        "Generate OTP",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(MP_Through_C_C2M.this, GenerateOtp.class));
                            }
                        });
                myAlert.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(MP_Through_C_C2M.this, QPayMenuNew.class));
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
//            mEditTextMerchantAccount.setFocusable(false);
//            mEditTextMerchantAccount.setEnabled(false);
            try {
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                showDialog(MP_Through_C_C2M.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
                        "No").show();
            }
        }
        if (v == mImgBtnMerchantList) {
            try {
                String strEncryptAccountNumber = encryptionDecryption.Encrypt(GlobalData.getStrCustomerAccountL14(), GlobalData.getStrMasterKey());
                String strAllMerchant = GetAllMerchant.getAllMerchant(strEncryptAccountNumber, GlobalData.getStrMasterKey());
//                                String strAllMerchant = "Merchant 01>>12345678900001&Merchant 02>>12345678900002&Merchant 03>>12345678900003&Merchant 01>>12345678900001&Merchant 02>>12345678900002&Merchant 03>>12345678900003&Merchant 01>>12345678900001&Merchant 02>>12345678900002&Merchant 03>>12345678900003";

                int intIndex = strAllMerchant.indexOf(">>");
                if (intIndex == -1) {
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
                    mAlertDialogBuilder.setMessage(strAllMerchant);
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
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MP_Through_C_C2M.this, android.R.layout.simple_list_item_1);
                    tokensMerchantList = new StringTokenizer(strAllMerchant, "&");
                    for (int j = 0; j <= tokensMerchantList.countTokens(); j++) {
                        while (tokensMerchantList.hasMoreElements()) {
                            String strMerchantNameAndWallet = tokensMerchantList.nextToken();
                            arrayAdapter.add(strMerchantNameAndWallet);
                        }
                    }

                    // ############################# Alert Dialog ###########################
                    // ############################# Alert Dialog ###########################
                    // ############################# Alert Dialog ###########################
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MP_Through_C_C2M.this);
                    alertDialog.setTitle("Choose Merchant");
                    alertDialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int position) {
                            String strMerchantNameAndWallet = arrayAdapter.getItem(position);
                            String[] parts = strMerchantNameAndWallet.split(">>");
                            String strMerchantName = parts[0];
                            String strMerchantWallet = parts[1];
                            mStrDestinationAccountHolderName = strMerchantName;
                            mEditTextMerchantAccount.setText(strMerchantWallet);
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }

            } catch (Exception exception) {
                exception.printStackTrace();
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
                    //#############################
                    if (mEditTextMerchantAccount.getText().toString().length() == 0) {
                        mEditTextMerchantAccount.setError("Field cannot be empty");
                    } else if (mEditTextAmount.getText().toString().length() == 0) {
                        mEditTextAmount.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() == 0) {
                        mEditTextOtp.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() < 5) {
                        mEditTextOtp.setError("Must be 5 characters in length");
                    } else {
                        String strSourcePin = GlobalData.getStrPin();
                        strAmount = mEditTextAmount.getText().toString();
                        strDestinationWallet1 = mEditTextMerchantAccount.getText().toString();
                        int intIndex02 = mStrDestinationAccountHolderName.indexOf("*");
                        if (intIndex02 == -1) {

                        } else {
                            String strDestinationAccountName = mStrDestinationAccountHolderName;
                            String[] parts01 = strDestinationAccountName.split("\\*");
                            strName = parts01[0];
                            String strType = parts01[1];
                        }

                        strReference = mEditTextReference.getText().toString();
                        strFunctionType = "CMP";

                        try {
                            mStrEncryptAccountNumber = encryptionDecryption.Encrypt(mStrSourceWallet, GlobalData.getStrMasterKey());
                            mStrEncryptPin = encryptionDecryption.Encrypt(strSourcePin, GlobalData.getStrMasterKey());
                            mStrEncryptMerchantAccountNumber = encryptionDecryption.Encrypt(strDestinationWallet1, GlobalData.getStrMasterKey());
                            mStrEncryptAmount = encryptionDecryption.Encrypt(strAmount, GlobalData.getStrMasterKey());
                            mStrEncryptOtp = encryptionDecryption.Encrypt(mEditTextOtp.getText().toString(), GlobalData.getStrMasterKey());
                            mStrEncryptReference = encryptionDecryption.Encrypt(strReference, GlobalData.getStrMasterKey());

                            // Initialize progress dialog
                            mProgressDialog = ProgressDialog.show(MP_Through_C_C2M.this, null, "Processing request...", false, true);
                            // Cancel progress dialog on back key press
                            mProgressDialog.setCancelable(true);

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                                    doMakePayment(mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptMerchantAccountNumber,
                                            mStrEncryptAmount, mStrEncryptOtp, mStrEncryptReference, GlobalData.getStrMasterKey());
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
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(MP_Through_C_C2M.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    } else {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
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
                                                                        GlobalData.setStrFavSourcePin(mStrSourcePin);
                                                                        GlobalData.setStrFavAmount(strAmount);
                                                                        GlobalData.setStrFavDestinationWallet(strDestinationWallet1);
                                                                        GlobalData.setStrFavDestinationWalletAccountHolderName(strName);
                                                                        GlobalData.setStrFavReference(strReference);
                                                                        GlobalData.setStrFavFunctionType(strFunctionType);
                                                                        startActivity(new Intent(MP_Through_C_C2M.this, FM_M2M_AddToFav.class));
                                                                    }
                                                                });
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(MP_Through_C_C2M.this, QPayMenuNew.class));
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

                    //##############################
                } else {
                    // if expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(MP_Through_C_C2M.this, GenerateOtp.class));
                                }
                            });
                    myAlert.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(MP_Through_C_C2M.this, QPayMenuNew.class));
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
//            if (mEditTextMerchantAccount.getText().toString().length() == 0) {
//                mEditTextMerchantAccount.setError("Field cannot be empty");
//            } else if (mEditTextAmount.getText().toString().length() == 0) {
//                mEditTextAmount.setError("Field cannot be empty");
//            } else if (mEditTextReference.getText().toString().length() == 0) {
//                mEditTextReference.setError("Field cannot be empty");
//            } else {
//                String strSourceAllWallet = mSpinnerWallet.getSelectedItem().toString();
//                String[] parts = strSourceAllWallet.split("\\-");
//                String strSourceWalletType = parts[0];
//                String strSourceWallet = parts[1];
//                String strSourcePin = GlobalData.getStrPin();
//                String strAmount = mEditTextAmount.getText().toString();
//                String strDestinationWallet = mEditTextMerchantAccount.getText().toString();
//                String strDestinationAccountName = mStrDestinationAccountHolderName;
//                String strReference = mEditTextReference.getText().toString();
//                String strFunctionType = "CMP";
//
//                GlobalData.setStrFavSourceWallet(strSourceWallet);
//                GlobalData.setStrFavSourcePin(strSourcePin);
//                GlobalData.setStrFavAmount(strAmount);
//                GlobalData.setStrFavDestinationWallet(strDestinationWallet);
//                GlobalData.setStrFavDestinationWalletAccountHolderName(strDestinationAccountName);
//                GlobalData.setStrFavReference(strReference);
//                GlobalData.setStrFavFunctionType(strFunctionType);
//
//                startActivity(new Intent(MP_Through_C_C2M.this, MP_Through_C_C2M_FromFav.class));
//
//
//            }
//
//
//        }
    }

    public void doMakePayment(String strEncryptAccountNumber,
                              String strEncryptPin,
                              String strEncryptMerchantAccountNumber,
                              String strEncryptAmount,
                              String strEncryptOtp,
                              String strEncryptReference,
                              String strMasterKey) {
        METHOD_NAME = "QPAY_Merchant_Payment_Customer";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Merchant_Payment_Customer";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //QPAY_Merchant_Payment_Customer
        // CustomerAccount
        // CustomerPIN
        // MerchantAccountno
        // Amount
        // CustomerOTP
        // CustomerRefID
        // strMasterKey
        PropertyInfo customerAccountNumber = new PropertyInfo();
        customerAccountNumber.setName("CustomerAccount");
        customerAccountNumber.setValue(strEncryptAccountNumber);
        customerAccountNumber.setType(String.class);
        request.addProperty(customerAccountNumber);

        PropertyInfo customerPin = new PropertyInfo();
        customerPin.setName("CustomerPIN");
        customerPin.setValue(strEncryptPin);
        customerPin.setType(String.class);
        request.addProperty(customerPin);

        PropertyInfo merchantAccountNumber = new PropertyInfo();
        merchantAccountNumber.setName("MerchantAccountno");
        merchantAccountNumber.setValue(strEncryptMerchantAccountNumber);
        merchantAccountNumber.setType(String.class);
        request.addProperty(merchantAccountNumber);

        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setValue(strEncryptAmount);
        amount.setType(String.class);
        request.addProperty(amount);

        PropertyInfo otp = new PropertyInfo();
        otp.setName("CustomerOTP");
        otp.setValue(strEncryptOtp);
        otp.setType(String.class);
        request.addProperty(otp);

        PropertyInfo reference = new PropertyInfo();
        reference.setName("CustomerRefID");
        reference.setValue(strEncryptReference);
        reference.setType(String.class);
        request.addProperty(reference);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objMakePayment = null;
        String strMakePaymentResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objMakePayment = envelope.getResponse();
            strMakePaymentResponse = objMakePayment.toString();
            mStrServerResponse = strMakePaymentResponse;

//            Merchant Payment successful on 20-Mar-18 04:06:03Sent to: 00611000000561 Amount Tk.5,320.00
//            Balance: Tk.92,480.00
//            TXN ID: 18032000000086
//            QPAY//Merchant Payment Received From: 00612000000532 on 20-Mar-18 04:06:03
//            Amount: Tk.5,320.00
//            Balance: Tk.7,520.00
//            TXN ID: 18032000000086
//            QPAY,*AP180320160903

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
            mStrServerResponse = strMakePaymentResponse;
        }
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
                Intent intent = new Intent(MP_Through_C_C2M.this, Login.class)
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

    private void checkOs() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
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

                }
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
                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
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
                            strMerchantAccountNumber = encryptionDecryption.Decrypt(strEncryptMerchantAccountNumber, strMerchantMasterKey);
                            String strAccountTypeCode = strMerchantAccountNumber.substring(3, 5);
                            if (strAccountTypeCode.equalsIgnoreCase("11")) {
                                //################ Merchant Name ####################
                                //################ Merchant Name ####################
                                //################ Merchant Name ####################
                                mStrDestinationAccountHolderName = GetMerchantName.getMerchantName(strEncryptMerchantAccountNumber, strMerchantMasterKey);
                                String[] parts01 = mStrDestinationAccountHolderName.split("\\*");
                                String strName = parts01[0];
                                final String strType = parts01[1];

                                String strDestinationRank = mStrSourceWallet.substring(13, 14);
                                if (strDestinationRank.equalsIgnoreCase("1")) {
                                    SourceRank = "VOUPAY";
                                } else if (strDestinationRank.equalsIgnoreCase("2")) {
                                    SourceRank = "CANPAY";
                                } else if (strDestinationRank.equalsIgnoreCase("3")) {
                                    SourceRank = "SIMBIL";
                                }
                                //################ Show Dialog ####################
                                //################ Show Dialog ####################
                                //################ Show Dialog ####################
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                                myAlert.setTitle("MERCHANT INFO");
                                myAlert.setMessage("MERCHANT NAME" + "\n" + strName + "\n" + "MERCHANT WALLET" + "\n" + strMerchantAccountNumber);
                                myAlert.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (strType.equalsIgnoreCase(SourceRank)) {
                                                    mEditTextMerchantAccount.setText(strMerchantAccountNumber);
                                                } else {
                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                                                    myAlert.setMessage("Account Type not match. Please scan correct account type.");
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
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_C_C2M.this);
                                myAlert.setMessage("Please Scan Merchant QR");
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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
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

    private void checkInternet() {

        if (isNetworkConnected()) {
            enableUiComponents();
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            // Initialize progress dialog
            mProgressDialog = ProgressDialog.show(MP_Through_C_C2M.this, null, "Loading Bank...", false, true);
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
                                    ArrayAdapter<String> adapterWallet = new ArrayAdapter<String>(MP_Through_C_C2M.this,
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
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
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
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_C_C2M.this);
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
            GlobalData.setStrCustomerAccountL14(mStrSourceWallet);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private void enableUiComponents() {
        mSpinnerWallet.setEnabled(true);
        mEditTextMerchantAccount.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mEditTextOtp.setEnabled(true);
        mEditTextReference.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mSpinnerWallet.setEnabled(false);
        mEditTextMerchantAccount.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mEditTextReference.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }

    private void clearEditText() {
        mEditTextAmount.setText("");
        mEditTextReference.setText("");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(MP_Through_C_C2M.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initSourceWallet() {
        String strAllWallets = GetAllCustomerWallet.getAllWallets();
        String[] parts = strAllWallets.split("\\&");
        String strPrimaryWallet = parts[0];
        String strSalaryWallet = parts[1];
        String strCreditWallet = parts[2];
        String[] strArrayAllWallet = {strPrimaryWallet, strSalaryWallet, strCreditWallet};

        ArrayAdapter<String> arrayAdapterWallet = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        strArrayAllWallet);
        arrayAdapterWallet.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mSpinnerWallet.setAdapter(arrayAdapterWallet);
    }

}
