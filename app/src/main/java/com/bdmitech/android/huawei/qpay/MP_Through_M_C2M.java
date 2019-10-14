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
import com.bdmitech.android.huawei.qpay.utils.GetMerchantName;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.StringTokenizer;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")

public class MP_Through_M_C2M extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;

    ArrayList<String> arrayListWalletType = new ArrayList<String>();
    ArrayList<String> arrayListWallet = new ArrayList<String>();

    private Spinner mSpinnerMerchantWallet;
    private EditText mEditTextAmount, mEditTextCustomerOtp,
            mEditTextCustomerReference, mEditTextCustomerWallet;
    private ImageButton mImgBtnScanQr;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrEncryptMerchantWallet,
            mStrCustomerMasterKeyByQrCode, mStrCustomerWallet, mStrCustomerWalletByQrCode,
            strUrlForQrCode, mStrEncryptCustomerWallet,
            strMethodName, strBankBin, mStrServerResponse,
            mStrEncryptMerchantPin, mStrEncryptAmount,
            mStrEncryptCustomerOtp, mStrEncryptCustomerReference,
            mStrEncryptCustomerWalletByQrCode,
            mStrEncryptMerchatWalletForOtp, mStrSourceWallet, mStrMerchantRank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_by_merchant);
        checkOs();
        initUI();

    }

    private void initUI() {
        mSpinnerMerchantWallet = findViewById(R.id.spinnerPaymentByMerchantMerchantWallet);
        mEditTextAmount = findViewById(R.id.editTextPaymentByMerchantAmount);
        mEditTextAmount.requestFocus();
        mEditTextCustomerOtp = findViewById(R.id.editTextPaymentByMerchantCustomerOtp);
        mEditTextCustomerReference = findViewById(R.id.editTextPaymentByMerchantCustomerReference);
        mImgBtnScanQr = findViewById(R.id.imgBtnMerchantPaymentByMerchantScanQrCustomerWallet);
        mImgBtnScanQr.setOnClickListener(this);
        mEditTextCustomerWallet = findViewById(R.id.editTextPaymentByMerchantCustomerWallet);
        mBtnSubmit = findViewById(R.id.btnPaymentByMerchantSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewPaymentByMerchantServerResponse);
        mStrMerchantRank = GlobalData.getStrAccountRank();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mImgBtnScanQr) {
//            mEditTextCustomerWallet.setFocusable(false);
//            mEditTextCustomerWallet.setEnabled(false);
            try {
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                showDialog(MP_Through_M_C2M.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
                        "No").show();
            }
        }
        if (v == mBtnSubmit) {
            if (mEditTextAmount.getText().toString().length() == 0) {
                mEditTextAmount.setError("Field cannot be empty");
            } else if (mEditTextCustomerOtp.getText().toString().length() == 0) {
                mEditTextCustomerOtp.setError("Field cannot be empty");
            } else if (mEditTextCustomerOtp.getText().toString().length() < 5) {
                mEditTextCustomerOtp.setError("Must be 5 characters in length");
            } else if (mEditTextCustomerWallet.getText().toString().length() == 0) {
                mEditTextCustomerWallet.setError("Field cannot be empty");
            } else {
                try {
                    mStrEncryptMerchantWallet = encryptionDecryption.Encrypt(mStrSourceWallet, GlobalData.getStrMasterKey());
                    mStrEncryptMerchantPin = encryptionDecryption.Encrypt(GlobalData.getStrPin(), GlobalData.getStrMasterKey());
                    mStrEncryptAmount = encryptionDecryption.Encrypt(mEditTextAmount.getText().toString(), GlobalData.getStrMasterKey());
                    mStrEncryptCustomerOtp = encryptionDecryption.Encrypt(mEditTextCustomerOtp.getText().toString(), GlobalData.getStrMasterKey());
                    mStrEncryptCustomerReference = encryptionDecryption.Encrypt(mEditTextCustomerReference.getText().toString(), GlobalData.getStrMasterKey());
                    mStrEncryptCustomerWallet = encryptionDecryption.Encrypt(mEditTextCustomerWallet.getText().toString(), GlobalData.getStrMasterKey());


                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(MP_Through_M_C2M.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            doMakePaymentByMerchant(
                                    mStrEncryptMerchantWallet,
                                    mStrEncryptMerchantPin,
                                    mStrEncryptAmount,
                                    mStrEncryptCustomerOtp,
                                    mStrEncryptCustomerReference,
                                    mStrEncryptCustomerWallet,
                                    GlobalData.getStrMasterKey());
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                            mProgressDialog.dismiss();
                                            //####################### Show Dialog ####################
                                            //####################### Show Dialog ####################
                                            //####################### Show Dialog ####################
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                                            myAlert.setNegativeButton(
                                                    "Close",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                            startActivity(new Intent(MP_Through_M_C2M.this, QPayMenuNew.class));
                                                        }
                                                    });
                                            AlertDialog alertDialog = myAlert.create();
                                            alertDialog.show();

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
        }
    }

    public void doMakePaymentByMerchant(String strEncryptMerchantWallet,
                                        String strEncryptMerchantPin,
                                        String strEncryptAmount,
                                        String strEncryptCustomerOtp,
                                        String strEncryptCustomerReference,
                                        String strEncryptCustomerWallet,
                                        String strMasterKey) {


        METHOD_NAME = "QPAY_Merchant_Payment_Through_Agent";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Merchant_Payment_Through_Agent";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//        QPAY_Merchant_Payment_Through_Agent
//        MerchantAccNo:
//        MerchantPIN:
//        Amount:
//        CustomerOTP:
//        CustomerRefID:
//        CustomerAccount:
//        strMasterKey:

        PropertyInfo encryptMerchantWallet = new PropertyInfo();
        encryptMerchantWallet.setName("MerchantAccNo");
        encryptMerchantWallet.setValue(strEncryptMerchantWallet);
        encryptMerchantWallet.setType(String.class);
        request.addProperty(encryptMerchantWallet);

        PropertyInfo encryptMerchantPin = new PropertyInfo();
        encryptMerchantPin.setName("MerchantPIN");
        encryptMerchantPin.setValue(strEncryptMerchantPin);
        encryptMerchantPin.setType(String.class);
        request.addProperty(encryptMerchantPin);

        PropertyInfo encryptAmount = new PropertyInfo();
        encryptAmount.setName("Amount");
        encryptAmount.setValue(strEncryptAmount);
        encryptAmount.setType(String.class);
        request.addProperty(encryptAmount);

        PropertyInfo encryptCustomerOtp = new PropertyInfo();
        encryptCustomerOtp.setName("CustomerOTP");
        encryptCustomerOtp.setValue(strEncryptCustomerOtp);
        encryptCustomerOtp.setType(String.class);
        request.addProperty(encryptCustomerOtp);

        PropertyInfo encryptCustomerReference = new PropertyInfo();
        encryptCustomerReference.setName("CustomerRefID");
        encryptCustomerReference.setValue(strEncryptCustomerReference);
        encryptCustomerReference.setType(String.class);
        request.addProperty(encryptCustomerReference);

        PropertyInfo encryptCustomerWallet = new PropertyInfo();
        encryptCustomerWallet.setName("CustomerAccount");
        encryptCustomerWallet.setValue(strEncryptCustomerWallet);
        encryptCustomerWallet.setType(String.class);
        request.addProperty(encryptCustomerWallet);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objPaymentByMerchant = null;
        String strPaymentByMerchantResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objPaymentByMerchant = envelope.getResponse();
            strPaymentByMerchantResponse = objPaymentByMerchant.toString();
            mStrServerResponse = strPaymentByMerchantResponse;

//            Merchant Payment successful on 20-Mar-18 04:21:10Sent to: 00611000000561 Amount Tk.180.00
//            Balance: Tk.92,300.00
//            TXN ID: 18032000000098
//            QPAY//Merchant Payment Received From: 00612000000532 on 20-Mar-18 04:21:10
//            Amount: Tk.180.00
//            Balance: Tk.7,700.00
//            TXN ID: 18032000000098
//            QPAY//*AP180320162409


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
                String strExtra01 = parts[1];
                String strExtra02 = parts[2];
                mStrServerResponse = strResponse;
            }
        } catch (Exception exception) {
            mStrServerResponse = strPaymentByMerchantResponse;
        }
    }

    public void sendCustomerOtp(String strEncryptMerchantWallet,
                                String strEncryptMerchantPin,
                                String strEncryptCustomerWallet,
                                String strMasterKey) {
        METHOD_NAME = "QPAY_GenerateOTP_Res";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GenerateOTP_Res";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

//        QPAY_GenerateOTP_Res
//        MerchantNo:
//        MerchantPIN:
//        CustomerNo:
//        strMasterKey:
        PropertyInfo encryptMerchantWallet = new PropertyInfo();
        encryptMerchantWallet.setName("MerchantNo");
        encryptMerchantWallet.setValue(strEncryptMerchantWallet);
        encryptMerchantWallet.setType(String.class);
        request.addProperty(encryptMerchantWallet);

        PropertyInfo encryptMerchantPin = new PropertyInfo();
        encryptMerchantPin.setName("MerchantPIN");
        encryptMerchantPin.setValue(strEncryptMerchantPin);
        encryptMerchantPin.setType(String.class);
        request.addProperty(encryptMerchantPin);

        PropertyInfo encryptCustomerWallet = new PropertyInfo();
        encryptCustomerWallet.setName("CustomerNo");
        encryptCustomerWallet.setValue(strEncryptCustomerWallet);
        encryptCustomerWallet.setType(String.class);
        request.addProperty(encryptCustomerWallet);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objSendCustomerOtp = null;
        String strSendCustomerWalletResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objSendCustomerOtp = envelope.getResponse();
            strSendCustomerWalletResponse = objSendCustomerOtp.toString();
//            mStrServerResponse = strSendCustomerWalletResponse;
        } catch (Exception exception) {
//            mStrServerResponse = strSendCustomerWalletResponse;
        }
    }


    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            // Initialize progress dialog
            mProgressDialog = ProgressDialog.show(MP_Through_M_C2M.this, null, "Loading Bank...", false, true);
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
                                    ArrayAdapter<String> adapterWallet = new ArrayAdapter<String>(MP_Through_M_C2M.this,
                                            android.R.layout.simple_spinner_item, arrayListWalletType);
                                    adapterWallet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mSpinnerMerchantWallet.setAdapter(adapterWallet);
                                    mSpinnerMerchantWallet.setOnItemSelectedListener(onItemSelectedListenerForWallet);
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
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
        ArrayList<String> arrayListWalletTypeAndNumber = new ArrayList<String>();
        if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
            arrayListWalletTypeAndNumber.add("VOUCHER PAY" + "-" + GlobalData.getStrAccountNumber() + "1");
        } else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
            arrayListWalletTypeAndNumber.add("CANTEEN PAY" + "-" + GlobalData.getStrAccountNumber() + "1");
        } else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
            arrayListWalletTypeAndNumber.add("SIM BILL" + "-" + GlobalData.getStrAccountNumber() + "1");
        }
        for (int i = 0; i <= arrayListWalletTypeAndNumber.size() - 1; i++) {
            StringTokenizer tokenWalletTypeAndAccount = new StringTokenizer(arrayListWalletTypeAndNumber.get(i), "-");
            arrayListWalletType.add(tokenWalletTypeAndAccount.nextToken());
            arrayListWallet.add(tokenWalletTypeAndAccount.nextToken());
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

    private void enableUiComponents() {
        mSpinnerMerchantWallet.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mEditTextCustomerOtp.setEnabled(true);
        mEditTextCustomerReference.setEnabled(true);
        mImgBtnScanQr.setEnabled(true);
        mEditTextCustomerWallet.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mSpinnerMerchantWallet.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextCustomerOtp.setEnabled(false);
        mEditTextCustomerReference.setEnabled(false);
        mImgBtnScanQr.setEnabled(false);
        mEditTextCustomerWallet.setEnabled(false);
        mBtnSubmit.setEnabled(false);
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
                Intent intent = new Intent(MP_Through_M_C2M.this, Login.class)
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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_M_C2M.this);
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

                    String strEncryptDestinationWalletAndMasterKey = GetEncryptAccountNumberAndMasterKeyByQrCode.getEncryptAccountNumberAndMasterKeyByQrCode(mStrQrCodeContents);
                    if (!strEncryptDestinationWalletAndMasterKey.equalsIgnoreCase("")) {
                        int intIndex02 = strEncryptDestinationWalletAndMasterKey.indexOf("*");
                        if (intIndex02 == -1) {
                            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                            String[] parts = strEncryptDestinationWalletAndMasterKey.split("\\*");
                            mStrEncryptCustomerWallet = parts[0];//
                            mStrCustomerMasterKeyByQrCode = parts[1];//96745482897185504726639965371045

                            try {
                                mStrEncryptMerchatWalletForOtp = encryptionDecryption.Encrypt(GlobalData.getStrAccountNumber() + 1, GlobalData.getStrMasterKey());
                                mStrCustomerWalletByQrCode = encryptionDecryption.Decrypt(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                mStrEncryptCustomerWalletByQrCode = encryptionDecryption.Encrypt(mStrCustomerWalletByQrCode, GlobalData.getStrMasterKey());

                                String strAccountTypeCode = mStrCustomerWalletByQrCode.substring(3, 5);
                                if (strAccountTypeCode.equalsIgnoreCase("12")) {
                                    String strDestinationRank = mStrCustomerWalletByQrCode.substring(13, 14);
                                    //---------------------------------
                                    if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                                        if (strDestinationRank.equalsIgnoreCase("1")) {
                                            //--------------------------------
//                                    sendCustomerOtp(
//                                            mStrEncryptMerchatWalletForOtp,
//                                            GlobalData.getStrEncryptPin(),
//                                            mStrEncryptCustomerWalletByQrCode,
//                                            GlobalData.getStrMasterKey());

                                            //################ Customer Wallet ####################
                                            //################ Customer Wallet ####################
                                            //################ Customer Wallet ####################
                                            mStrCustomerWallet = encryptionDecryption.Decrypt(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            String strCustomerName = GetMerchantName.getMerchantName(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
                                            myAlert.setTitle("CUSTOMER INFO");
                                            myAlert.setMessage("CUSTOMER NAME" + "\n" + strCustomerName + "\n" + "CUSTOMER WALLET" + "\n" + mStrCustomerWallet);
                                            myAlert.setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            mEditTextCustomerWallet.setText(mStrCustomerWallet);
//                                                    mEditTextAmount.setSelection(1);
                                                            dialog.cancel();
                                                            sendCustomerOtp(
                                                                    mStrEncryptMerchatWalletForOtp,
                                                                    GlobalData.getStrEncryptPin(),
                                                                    mStrEncryptCustomerWalletByQrCode,
                                                                    GlobalData.getStrMasterKey());
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
                                        }
                                        else {
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                                    else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                                        if (strDestinationRank.equalsIgnoreCase("2")) {

                                            mStrCustomerWallet = encryptionDecryption.Decrypt(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            String strCustomerName = GetMerchantName.getMerchantName(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
                                            myAlert.setTitle("CUSTOMER INFO");
                                            myAlert.setMessage("CUSTOMER NAME" + "\n" + strCustomerName + "\n" + "CUSTOMER WALLET" + "\n" + mStrCustomerWallet);
                                            myAlert.setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            mEditTextCustomerWallet.setText(mStrCustomerWallet);
//                                                    mEditTextAmount.setSelection(1);
                                                            dialog.cancel();
                                                            sendCustomerOtp(
                                                                    mStrEncryptMerchatWalletForOtp,
                                                                    GlobalData.getStrEncryptPin(),
                                                                    mStrEncryptCustomerWalletByQrCode,
                                                                    GlobalData.getStrMasterKey());
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

                                        }
                                        else {
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                                    else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                                        if (strDestinationRank.equalsIgnoreCase("3")) {
                                            mStrCustomerWallet = encryptionDecryption.Decrypt(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            //################ Customer Name ####################
                                            String strCustomerName = GetMerchantName.getMerchantName(mStrEncryptCustomerWallet, mStrCustomerMasterKeyByQrCode);
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            //################ Show Dialog ####################
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
                                            myAlert.setTitle("CUSTOMER INFO");
                                            myAlert.setMessage("CUSTOMER NAME" + "\n" + strCustomerName + "\n" + "CUSTOMER WALLET" + "\n" + mStrCustomerWallet);
                                            myAlert.setPositiveButton(
                                                    "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            mEditTextCustomerWallet.setText(mStrCustomerWallet);
//                                                    mEditTextAmount.setSelection(1);
                                                            dialog.cancel();
                                                            sendCustomerOtp(
                                                                    mStrEncryptMerchatWalletForOtp,
                                                                    GlobalData.getStrEncryptPin(),
                                                                    mStrEncryptCustomerWalletByQrCode,
                                                                    GlobalData.getStrMasterKey());
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
                                        }
                                        else {
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                                } else {
                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
                                mEditTextCustomerWallet.setText(mStrCustomerWallet);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    } else {
                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(MP_Through_M_C2M.this);
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
        mEditTextCustomerReference.setText("");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(MP_Through_M_C2M.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
