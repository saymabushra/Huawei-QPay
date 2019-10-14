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

public class FM_M2M extends AppCompatActivity implements OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog = null;

    //#############################################################
    public SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    ArrayList<String> arrayListWalletType = new ArrayList<String>();
    ArrayList<String> arrayListWallet = new ArrayList<String>();

    private Spinner mSpinnerMerchantWallet;
    private EditText mEditTextOtp, mEditTextAmount, mEditTextDestinationWallet;
    private ImageButton mImgBtnScanQr;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrSourceMasterKey,
            mStrSourceWallet, mStrEncryptSourceWallet,
            mStrSourcePin, mStrEncryptSourcePin,
            mStrEncryptSourceOtp,
            strDestinationWallet, mStrEncryptDestinationWallet, strEncryptDestinationWalletFromQr,
            strDestinationMasterKey,
            mStrEncryptAmount, mStrDestinationName,
            mStrUrlForQrCode, mStrMethodName, mStrBankBin,
            mStrServerResponse, mStrMerchantRank,mStrAmount,
            mStrDestinationWallet,mStrDestinationAccountName,mStrFunctionType,strName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fund_management);
        checkOs();
        initUI();
    }

    private void initUI() {
        mSpinnerMerchantWallet = findViewById(R.id.spinnerFundManagementWallet);
        mEditTextOtp = findViewById(R.id.editTextFundManagementOtp);
        mEditTextAmount = findViewById(R.id.editTextFundManagementAmount);
        mEditTextAmount.requestFocus();
        mImgBtnScanQr = findViewById(R.id.imgBtnFundManagementScanQrMerchantWallet);
        mImgBtnScanQr.setOnClickListener(this);
        mEditTextDestinationWallet = findViewById(R.id.editTextFundManagementDestinationAccountWallet);
        mBtnSubmit = findViewById(R.id.btnFundManagementSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewFundManagementServerResponse);
        mStrSourceMasterKey = GlobalData.getStrMasterKey();
        mStrSourcePin = GlobalData.getStrPin();
        mStrMerchantRank = GlobalData.getStrAccountRank();

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
                AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
                myAlert.setMessage("OTP is expired. Generate a new OTP?");
                myAlert.setPositiveButton(
                        "Generate OTP",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(FM_M2M.this, GenerateOtp.class));
                            }
                        });
                myAlert.setNegativeButton(
                        "Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(FM_M2M.this, QPayMenuNew.class));
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    @Override
    public void onClick(View v) {
        if (v == mImgBtnScanQr) {
//            mEditTextDestinationWallet.setFocusable(false);
//            mEditTextDestinationWallet.setEnabled(false);
            try {
                Intent intent = new Intent(ACTION_SCAN);
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, 0);
            } catch (ActivityNotFoundException anfe) {
                showDialog(FM_M2M.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
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
                    if (mEditTextOtp.getText().toString().length() == 0) {
                        mEditTextOtp.setError("Field cannot be empty");
                    } else if (mEditTextOtp.getText().toString().length() < 5) {
                        mEditTextOtp.setError("Must be 5 characters in length");
                    } else if (mEditTextAmount.getText().toString().length() == 0) {
                        mEditTextAmount.setError("Field cannot be empty");
                    } else if (mEditTextDestinationWallet.getText().toString().length() == 0) {
                        mEditTextDestinationWallet.setError("Field cannot be empty");
                    } else {

                        mStrAmount = mEditTextAmount.getText().toString();
                        mStrDestinationWallet = mEditTextDestinationWallet.getText().toString();
                        mStrDestinationAccountName = mStrDestinationName;
                        String[] parts01 = mStrDestinationAccountName.split("\\*");
                        strName = parts01[0];
                        String strType = parts01[1];
                        mStrFunctionType = "MFM";

                        try {
                            mStrEncryptSourceWallet = encryptionDecryption.Encrypt(mStrSourceWallet, mStrSourceMasterKey);
                            mStrEncryptSourcePin = encryptionDecryption.Encrypt(mStrSourcePin, mStrSourceMasterKey);
                            mStrEncryptSourceOtp = encryptionDecryption.Encrypt(mEditTextOtp.getText().toString(), mStrSourceMasterKey);
                            mStrEncryptAmount = encryptionDecryption.Encrypt(mStrAmount, mStrSourceMasterKey);
                            mStrEncryptDestinationWallet = encryptionDecryption.Encrypt(mStrDestinationWallet, mStrSourceMasterKey);

                            // Initialize progress dialog
                            mProgressDialog = ProgressDialog.show(FM_M2M.this, null, "Processing request...", false, true);
                            // Cancel progress dialog on back key press
                            mProgressDialog.setCancelable(true);

                            Thread t = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    doFundManagement(mStrEncryptSourceWallet, mStrEncryptSourcePin, mStrEncryptSourceOtp, mStrEncryptAmount, mStrEncryptDestinationWallet, GlobalData.getStrMasterKey());
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
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
                                                        myAlert.setMessage(mStrServerResponse);
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FM_M2M.this, QPayMenuNew.class));
                                                                    }
                                                                });
                                                        AlertDialog alertDialog = myAlert.create();
                                                        alertDialog.show();
                                                    } else {
                                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
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
                                                                        GlobalData.setStrFavSourceWallet(mStrSourceWallet);
                                                                        GlobalData.setStrFavSourcePin(mStrSourcePin);
                                                                        GlobalData.setStrFavAmount(mStrAmount);
                                                                        GlobalData.setStrFavDestinationWallet(mStrDestinationWallet);
                                                                        GlobalData.setStrFavDestinationWalletAccountHolderName(strName);
                                                                        GlobalData.setStrFavFunctionType(mStrFunctionType);
                                                                        startActivity(new Intent(FM_M2M.this, FM_M2M_AddToFav.class));
                                                                    }
                                                                });
                                                        myAlert.setNegativeButton(
                                                                "Close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        dialog.cancel();
//                                                            enableUiComponentAfterClick();
                                                                        startActivity(new Intent(FM_M2M.this, QPayMenuNew.class));
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

                } else {
                    // if expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(FM_M2M.this, GenerateOtp.class));
                                }
                            });
                    myAlert.setNegativeButton(
                            "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
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
//            } else if (mEditTextDestinationWallet.getText().toString().length() == 0) {
//                mEditTextDestinationWallet.setError("Field cannot be empty");
//            } else {
//                String strSourceAllWallet = mSpinnerMerchantWallet.getSelectedItem().toString();
//                String[] parts = strSourceAllWallet.split("\\-");
//                String strSourceWalletType = parts[0];
//                String strSourceWallet = parts[1];
//                String strSourcePin = GlobalData.getStrPin();
//                String strAmount = mEditTextAmount.getText().toString();
//                String strDestinationWallet = mEditTextDestinationWallet.getText().toString();
//                String strDestinationAccountName = mStrDestinationAccountHolderName;
//                String strFunctionType = "MFM";
//
//                GlobalData.setStrFavSourceWallet(strSourceWallet);
//                GlobalData.setStrFavSourcePin(strSourcePin);
//                GlobalData.setStrFavAmount(strAmount);
//                GlobalData.setStrFavDestinationWallet(strDestinationWallet);
//                GlobalData.setStrFavDestinationWalletAccountHolderName(strDestinationAccountName);
//                GlobalData.setStrFavFunctionType(strFunctionType);
//
//                startActivity(new Intent(FM_M2M.this, FM_M2M_FromFav.class));
//            }
//
//        }
    }

    public void doFundManagement(String strEncryptSourceAccountNumber,
                                 String strEncryptSourcePin,
                                 String strEncryptSourceOtp,
                                 String strEncryptAmount,
                                 String strEncryptDestinationAccountNumber,
                                 String strMasterKey) {
        METHOD_NAME = "QPAY_Fund_Management_Merchant";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_Fund_Management_Merchant";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace(), METHOD_NAME);
        // Declare the version of the SOAP request
        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        //QPAY_Fund_Management_Merchant
        // AccountNo
        // AccountPIN
        //InitiatorOTP
        // Amount
        // DestinationAccount
        // strMasterKey
        PropertyInfo sourceAccountNumber = new PropertyInfo();
        sourceAccountNumber.setName("AccountNo");
        sourceAccountNumber.setValue(strEncryptSourceAccountNumber);
        sourceAccountNumber.setType(String.class);
        request.addProperty(sourceAccountNumber);

        PropertyInfo sourcePin = new PropertyInfo();
        sourcePin.setName("AccountPIN");
        sourcePin.setValue(strEncryptSourcePin);
        sourcePin.setType(String.class);
        request.addProperty(sourcePin);

        PropertyInfo sourceOtp = new PropertyInfo();
        sourceOtp.setName("InitiatorOTP");
        sourceOtp.setValue(strEncryptSourceOtp);
        sourceOtp.setType(String.class);
        request.addProperty(sourceOtp);

        PropertyInfo amount = new PropertyInfo();
        amount.setName("Amount");
        amount.setValue(strEncryptAmount);
        amount.setType(String.class);
        request.addProperty(amount);

        PropertyInfo destinationAccountNumber = new PropertyInfo();
        destinationAccountNumber.setName("DestinationAccount");
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
        Object objFundManagement = null;
        String strFundManagementResponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl(), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objFundManagement = envelope.getResponse();
            strFundManagementResponse = objFundManagement.toString();
            mStrServerResponse = strFundManagementResponse;

//            Fund Transfer  successful on 04-Mar-18 07:54:43 Sent to : 00611000000371Amount Tk.200.00
//            Balance: Tk.0.00
//            TXN ID: 18030400000079
//            QPAY//Fund Transfer  successful on 04-Mar-18 07:54:43
//            Received From: 0061100000040
//            Amount: Tk.200.00
//            Balance: Tk.1,00,295.00
//            TXN ID: 18030400000079
//            QPAY

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
            mStrServerResponse = strFundManagementResponse;
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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FM_M2M.this);
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
                    mStrBankBin = arrayQrCodeContents[0]; //003 bank bin for Merchant
                    String strEncryptQrCode = arrayQrCodeContents[1]; //78HnkqX7uY2ebpIuhaThVDQJy83m2/WM0EROhKDzgd1P0M0dZlhQuSmUQmYDJb6pmE4RKhHjdHltEMD0FJ16mjPu3kRxwSeVkuz7hT32BCjpUOtwFvy6ygDDParmnhwN/zzcUOd7Kr4bPagq4EPnfA==

                    if (mStrBankBin.equalsIgnoreCase("006")) {//For Huawei
                        mStrUrlForQrCode = GlobalData.getStrUrl();
                        mStrMethodName = "QPAY_Get_Account_BY_QR_CARD";
                    } else if (mStrBankBin.equalsIgnoreCase("002")) {//For QPay
                        mStrUrlForQrCode = GlobalData.getStrUrl();
                        mStrMethodName = "QPAY_Get_Account_BY_QR_CARD";
                    }
                }


                String strEncryptDestinationWalletAndDestinationMasterKey = GetEncryptAccountNumberAndMasterKeyByQrCode.getEncryptAccountNumberAndMasterKeyByQrCode(mStrQrCodeContents);
                if (!strEncryptDestinationWalletAndDestinationMasterKey.equalsIgnoreCase("")) {
                    int intIndex02 = strEncryptDestinationWalletAndDestinationMasterKey.indexOf("*");
                    if (intIndex02 == -1) {
                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FM_M2M.this);
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
                        String[] parts = strEncryptDestinationWalletAndDestinationMasterKey.split("\\*");
                        strEncryptDestinationWalletFromQr = parts[0];//96745482897185504726639965371045
                        strDestinationMasterKey = parts[1];//0021200000007

                        try {
                            //################ Merchant Account Number ####################
                            //################ Merchant Account Number ####################
                            //################ Merchant Account Number ####################
                            strDestinationWallet = encryptionDecryption.Decrypt(strEncryptDestinationWalletFromQr, strDestinationMasterKey);
                            String strAccountTypeCode = strDestinationWallet.substring(3, 5);
                            if (strAccountTypeCode.equalsIgnoreCase("11")) {
                                //################ Merchant Name ####################
                                //################ Merchant Name ####################
                                //################ Merchant Name ####################
                                mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationWalletFromQr, strDestinationMasterKey);
                                String[] parts01 = mStrDestinationName.split("\\*");
                                String strName = parts01[0];
                                final String strType = parts01[1];

//                                String strSourceRank = mStrSourceWallet.substring(13, 14);
//
//                                if (strType.equalsIgnoreCase("VOUPAY")) {
//                                    SourceRank = "VOUPAY";
//                                } else if (strType.equalsIgnoreCase("CANPAY")) {
//                                    SourceRank = "CANPAY";
//                                } else if (strType.equalsIgnoreCase("SIMBIL")) {
//                                    SourceRank = "SIMBIL";
//                                }

                                //################ Show Dialog ####################
                                //################ Show Dialog ####################
                                //################ Show Dialog ####################
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
                                myAlert.setTitle("MERCHANT INFO");
                                myAlert.setMessage("MERCHANT NAME" + "\n" + strName + "\n" + "MERCHANT WALLET" + "\n" + strDestinationWallet);
                                myAlert.setPositiveButton(
                                        "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (strType.equalsIgnoreCase(mStrMerchantRank)) {
                                                    mEditTextDestinationWallet.setText(strDestinationWallet);
                                                } else {
                                                    AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
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
                                AlertDialog.Builder myAlert = new AlertDialog.Builder(FM_M2M.this);
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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FM_M2M.this);
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
                Intent intent = new Intent(FM_M2M.this, Login.class)
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
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            //######################### Spinner Account Type#########################
            // Initialize progress dialog
            mProgressDialog = ProgressDialog.show(FM_M2M.this, null, "Loading Bank...", false, true);
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
                                    ArrayAdapter<String> adapterWallet = new ArrayAdapter<String>(FM_M2M.this,
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
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(FM_M2M.this);
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void enableUiComponents() {
        mSpinnerMerchantWallet.setEnabled(true);
        mEditTextOtp.setEnabled(true);
        mImgBtnScanQr.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mEditTextDestinationWallet.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mSpinnerMerchantWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mImgBtnScanQr.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mEditTextDestinationWallet.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }

    private void disableUiComponentAfterClick() {
        mSpinnerMerchantWallet.setEnabled(false);
        mEditTextOtp.setEnabled(false);
        mEditTextAmount.setEnabled(false);
        mImgBtnScanQr.setEnabled(false);
        mEditTextDestinationWallet.setEnabled(false);
        mBtnSubmit.setEnabled(false);
    }

    private void enableUiComponentAfterClick() {
        mSpinnerMerchantWallet.setEnabled(true);
        mEditTextOtp.setEnabled(true);
        mEditTextAmount.setEnabled(true);
        mImgBtnScanQr.setEnabled(true);
        mEditTextDestinationWallet.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void clearEditText() {
        mEditTextAmount.setText("");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(FM_M2M.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initSourceWallet() {
        String strAllWallets = GetAllCustomerWallet.getAllWallets();
        String[] parts = strAllWallets.split("\\&");
        String strPrimaryWallet = parts[0];

        //################### Implemented Later #########################
        //################### Implemented Later #########################
        //################### Implemented Later #########################
//        String strSalaryWallet = parts[1];
//        String strCreditWallet = parts[2];
//        String[] strArrayAllWallet = {strPrimaryWallet, strSalaryWallet, strCreditWallet};
        //################### Implemented Later #########################
        //################### Implemented Later #########################

        String[] strArrayAllWallet = {strPrimaryWallet};

        ArrayAdapter<String> arrayAdapterWallet = new ArrayAdapter<>
                (this, android.R.layout.simple_spinner_item,
                        strArrayAllWallet);
        arrayAdapterWallet.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mSpinnerMerchantWallet.setAdapter(arrayAdapterWallet);
    }

}
