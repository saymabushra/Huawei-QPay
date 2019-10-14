package com.bdmitech.android.huawei.qpay;

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
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.AccountActiveStatus;
import com.bdmitech.android.huawei.qpay.utils.CustomGrid;
import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.EncryptionDecryption;
import com.bdmitech.android.huawei.qpay.utils.GetEncryptAccountNumberAndMasterKeyByQrCode;
import com.bdmitech.android.huawei.qpay.utils.GetBalance;
import com.bdmitech.android.huawei.qpay.utils.GetLastTransaction;
import com.bdmitech.android.huawei.qpay.utils.GetMerchantName;
import com.bdmitech.android.huawei.qpay.utils.GetQrCodeContent;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;
import com.bdmitech.android.huawei.qpay.utils.MySpannable;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QPayMenuNew extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {
    private static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    EncryptionDecryption encryptionDecryption = new EncryptionDecryption();
    private ProgressDialog mProgressDialog;
    private Encryption encryption = new Encryption();

    //#############################################################
    private SharedPreferences mSharedPreferencsOtp;
    private SharedPreferences.Editor mSharedPreferencsOtpEditor;
    //#############################################################

    private Animation anim;
    private Calendar cal;
    private int day;
    private int month;
    private int year;

    Boolean isSingleLine = true;
    private LinearLayout mLinearLayoutLastTransaction, mLinearLayoutRefreshBalance;
    private ScrollView mScrollView;
    private TextView mTextViewAccountHolderName, mTextViewAccountNumber, mTextViewAccountTypeAndStatus,
            mTextViewBalanceAmount, mTextViewBalanceType, mTextViewLastTransaction;
    private ImageView mImgViewProfilePic, mImgBtnQucikPay, mImgBtnQr;
    private Button mBtnVoucherPay, mBtnCanteenPay, mBtnSimBill;
    private ImageButton mImgBtnReload;
    private GridView mGridView;
    private String mStrServicePackage, mStrQrCodeContent, mStrAccountNumber,
            mStrMasterKey, mStrCurrentDate, mStrEncryptAccountNumber,
            mStrEncryptFromDate, mStrEncryptToDate, mStrEncryptPin,
            mStrEncryptAccessCode, mStrEncryptParameter, mStrServerResponse, mStrLastTransaction,
            mStrOtpStatus, mStrMerchantRank, strMsg,
            mStrBankBin, mStrUrlForQrCode, mStrMethodName,
            strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr,
            mStrDestinationWallet, mStrDestinationName, mStrSourceWallet,
            mStrEncryptMerchatWalletForOtp, mStrCustomerWalletByQrCode, mStrEncryptCustomerWalletByQrCode,
            mStrCustomerAccType, mStrAccountType, mStrAccountStatus, mStrAccountTypeAndStaus,
            SourceRankCustomer,strSourceWallet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qpay_menu);
        checkOs();
        initUi();


        mImgBtnQucikPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStrOtpStatus.equalsIgnoreCase("valid")) {
                    // OTP Valid
                    // OTP Valid
                    // OTP Valid
                    try {
                        Intent intent = new Intent(ACTION_SCAN);
                        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        startActivityForResult(intent, 0);
                    } catch (ActivityNotFoundException anfe) {
                        showDialog(QPayMenuNew.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
                                "No").show();
                    }
                } else {
                    // OTP Expire
                    // OTP Expire
                    // OTP Expire
                    AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                    myAlert.setMessage("OTP is expired. Generate a new OTP?");
                    myAlert.setPositiveButton(
                            "Generate OTP",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
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

            }
        });
    }

    //########################## Initialize UI ############################
    //########################## Initialize UI ############################
    //########################## Initialize UI ############################
    private void initUi() {
        mScrollView = findViewById(R.id.scrollView);
        mLinearLayoutRefreshBalance = findViewById(R.id.linearLayoutRefreshBalance);
        mLinearLayoutLastTransaction = findViewById(R.id.linearLayoutLastTransaction);
        mImgViewProfilePic = findViewById(R.id.imgViewProfilePic);
        mTextViewAccountHolderName = findViewById(R.id.txtViewAccountHolderName);
        mTextViewAccountNumber = findViewById(R.id.txtViewAccountNumber);
        mTextViewAccountTypeAndStatus = findViewById(R.id.txtViewAccountTypeAndStatus);
        mImgBtnQucikPay = findViewById(R.id.imgBtnQuickPay);
        mImgBtnQr = findViewById(R.id.imgBtnQr);
        mBtnVoucherPay = findViewById(R.id.btnQPayMenuVoucherPay);
        mBtnCanteenPay = findViewById(R.id.btnQPayMenuCanteenPay);
        mBtnSimBill = findViewById(R.id.btnQPaySimBill);
        mTextViewBalanceAmount = findViewById(R.id.txtViewBalance);
        mTextViewBalanceType = findViewById(R.id.txtViewLabelBalance);
        mImgBtnReload = findViewById(R.id.imgBtnReload);
        mGridView = findViewById(R.id.gridView);
        mTextViewLastTransaction = findViewById(R.id.textViewQPayMenuLastTransaction);
        mTextViewLastTransaction.setSingleLine(true);
        //set onClick event
        mImgViewProfilePic.setOnClickListener(this);
//        mImgBtnQucikPay.setOnClickListener(this);
        mImgBtnQr.setOnClickListener(this);
        mBtnVoucherPay.setOnClickListener(this);
        mBtnCanteenPay.setOnClickListener(this);
        mBtnSimBill.setOnClickListener(this);
        mImgBtnReload.setOnClickListener(this);
        //get Service Package
        mStrMasterKey = GlobalData.getStrMasterKey();
        mStrServicePackage = GlobalData.getStrPackage();
        mStrMerchantRank = GlobalData.getStrAccountRank();
//        mStrServicePackage = "1312050001";// For Merchant
//        mStrServicePackage = "1205190003";// For Customer
        mStrAccountNumber = GlobalData.getStrAccountNumber();
        mStrSourceWallet = mStrAccountNumber + 1;
        //set TextView
        //set TextView
        //set TextView
        mTextViewAccountHolderName.setText(GlobalData.getStrAccountHolderName());
        mTextViewAccountNumber.setText(GlobalData.getStrAccountNumber());
        if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
            mStrAccountType = "Merchant";
        }
        if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
            mStrAccountType = "Customer";
        }

        mLinearLayoutRefreshBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Toast.makeText(QPayMenuNew.this,"Clicked",Toast.LENGTH_LONG).show();
                    if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                        String strAccountNumber = GlobalData.getStrAccountNumber();
                        String strVoucherAcc = strAccountNumber + 1;
                        String strEncVoucherAcc = encryption.Encrypt(strVoucherAcc, mStrMasterKey);
                        String strVoucherBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncVoucherAcc, mStrMasterKey);
                        mTextViewBalanceAmount.setText(strVoucherBalance);
                    }
                    if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                        String strAccountNumber = GlobalData.getStrAccountNumber();
                        if (mStrCustomerAccType.equalsIgnoreCase("VOUPAY")) {
                            mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                            mBtnCanteenPay.setBackgroundColor(0xFFFFFFF);
                            mBtnSimBill.setBackgroundColor(0xFFFFFFF);
                            String strVoucherWallet = strAccountNumber + 1;
                            String strEncryptVoucherWallet = encryption.Encrypt(strVoucherWallet, mStrMasterKey);
                            String strVoucherBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptVoucherWallet, mStrMasterKey);
                            String strQrCodeContentVoucherWallet = GetQrCodeContent.getQrCode(strEncryptVoucherWallet, mStrMasterKey);
                            GlobalData.setStrQrCodeContent(strQrCodeContentVoucherWallet);
                            GlobalData.setStrWallet(strVoucherWallet);
                            mTextViewBalanceAmount.setText(strVoucherBalance);
                        }
                        if (mStrCustomerAccType.equalsIgnoreCase("CANPAY")) {
                            mBtnVoucherPay.setBackgroundColor(0xFFFFFFF);
                            mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                            mBtnSimBill.setBackgroundColor(0xFFFFFFF);
                            String strCanteenWallet = strAccountNumber + 2;
                            String strEncryptCanteenWallet = encryption.Encrypt(strCanteenWallet, mStrMasterKey);
                            String strCanteenBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptCanteenWallet, mStrMasterKey);
                            String strQrCodeContentCanteenWallet = GetQrCodeContent.getQrCode(strEncryptCanteenWallet, mStrMasterKey);
                            GlobalData.setStrQrCodeContent(strQrCodeContentCanteenWallet);
                            GlobalData.setStrWallet(strCanteenWallet);
                            mTextViewBalanceAmount.setText(strCanteenBalance);
                        }
                        if (mStrCustomerAccType.equalsIgnoreCase("SIMBIL")) {
                            mBtnVoucherPay.setBackgroundColor(0xFFFFFFF);
                            mBtnCanteenPay.setBackgroundColor(0xFFFFFFF);
                            mBtnSimBill.setBackgroundColor(0xFFfde0e0);
                            String strSimBillWallet = strAccountNumber + 3;
                            String strEncryptSimBillWallet = encryption.Encrypt(strSimBillWallet, mStrMasterKey);
                            String strSimBillBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptSimBillWallet, mStrMasterKey);
                            String strQrCodeContentSimBillWallet = GetQrCodeContent.getQrCode(strEncryptSimBillWallet, mStrMasterKey);
                            GlobalData.setStrQrCodeContent(strQrCodeContentSimBillWallet);
                            GlobalData.setStrWallet(strEncryptSimBillWallet);
                            mTextViewBalanceAmount.setText(strSimBillBalance);
                        }
                    }

                    loadLastTransactionDetails();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLinearLayoutLastTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSingleLine) {
                    mTextViewLastTransaction.setSingleLine(false);
                    isSingleLine = false;
                } else {
                    mTextViewLastTransaction.setSingleLine(true);
                    isSingleLine = true;
                }
            }
        });

        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        mStrCurrentDate = sdf.format(new Date());


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
                    mStrOtpStatus = "valid";
                } else {
                    mStrOtpStatus = "expire";
                }
            } catch (Exception e) {
                mStrOtpStatus = "expire";
            }
        } else {
            mStrOtpStatus = "expire";
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();

        String strCheckAccountActiveStatus = AccountActiveStatus.checkAccountActiveStatus(GlobalData.getStrEncryptAccountNumber(), GlobalData.getStrMasterKey());
//        String strCheckAccountActiveStatus = "A";
//        String strCheckAccountActiveStatus = "I";
//        String strCheckAccountActiveStatus = "L";
        if (strCheckAccountActiveStatus.equalsIgnoreCase("A")) {
            mStrAccountStatus = "Active";
            mImgViewProfilePic.setEnabled(true);
            mTextViewAccountHolderName.setEnabled(true);
            mTextViewAccountNumber.setEnabled(true);
            mImgBtnQucikPay.setEnabled(true);
            mImgBtnQr.setEnabled(true);
            mBtnVoucherPay.setEnabled(true);
            mBtnCanteenPay.setEnabled(true);
            mBtnSimBill.setEnabled(true);
            mImgBtnReload.setEnabled(true);
            mGridView.setEnabled(true);
        } else if (strCheckAccountActiveStatus.equalsIgnoreCase("I")) {
            mStrAccountStatus = "Inactive";
            mImgViewProfilePic.setEnabled(true);
            mTextViewAccountHolderName.setEnabled(false);
            mTextViewAccountNumber.setEnabled(false);
            mImgBtnQucikPay.setEnabled(false);
            mImgBtnQr.setEnabled(false);
            mBtnVoucherPay.setEnabled(false);
            mBtnCanteenPay.setEnabled(false);
            mBtnSimBill.setEnabled(false);
            mImgBtnReload.setEnabled(false);
            mGridView.setEnabled(false);
        } else if (strCheckAccountActiveStatus.equalsIgnoreCase("L")) {
            mStrAccountStatus = "Locked";
            mImgViewProfilePic.setEnabled(false);
            mTextViewAccountHolderName.setEnabled(false);
            mTextViewAccountNumber.setEnabled(false);
            mImgBtnQucikPay.setEnabled(false);
            mImgBtnQr.setEnabled(false);
            mBtnVoucherPay.setEnabled(false);
            mBtnCanteenPay.setEnabled(false);
            mBtnSimBill.setEnabled(false);
            mImgBtnReload.setEnabled(false);
            mGridView.setEnabled(false);
            //####################### Show Dialog ####################
            //####################### Show Dialog ####################
            //####################### Show Dialog ####################
            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
            myAlert.setMessage("Your Account is locked. Please contact with QPay.");
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
            mImgViewProfilePic.setEnabled(false);
            mTextViewAccountHolderName.setEnabled(false);
            mTextViewAccountNumber.setEnabled(false);
            mImgBtnQr.setEnabled(false);
            mBtnVoucherPay.setEnabled(false);
            mBtnCanteenPay.setEnabled(false);
            mBtnSimBill.setEnabled(false);
            mImgBtnReload.setEnabled(false);
            mGridView.setEnabled(false);
            //####################### Show Dialog ####################
            //####################### Show Dialog ####################
            //####################### Show Dialog ####################
            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
            myAlert.setTitle("Account Status");
            myAlert.setMessage(strCheckAccountActiveStatus);
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

        mStrAccountTypeAndStaus = mStrAccountType + " | " + mStrAccountStatus;
        mTextViewAccountTypeAndStatus.setText(mStrAccountTypeAndStaus);

        try {
            if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                String strAccountNumber = GlobalData.getStrAccountNumber();
                String strVoucherAcc = strAccountNumber + 1;
                String strEncVoucherAcc = encryption.Encrypt(strVoucherAcc, mStrMasterKey);
                String strVoucherBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncVoucherAcc, mStrMasterKey);
                mTextViewBalanceAmount.setText(strVoucherBalance);
            }
            if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                String strAccountNumber = GlobalData.getStrAccountNumber();
                String strVoucherAcc = strAccountNumber + 1;
                String strCanteenAcc = strAccountNumber + 2;
                String strSimBillAcc = strAccountNumber + 3;
                String strEncVoucherAcc = encryption.Encrypt(strVoucherAcc, mStrMasterKey);
                String strEncCanteenAcc = encryption.Encrypt(strCanteenAcc, mStrMasterKey);
                String strEncSimAcc = encryption.Encrypt(strSimBillAcc, mStrMasterKey);
                String strVoucherBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncVoucherAcc, mStrMasterKey);
                String strCanteenBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncCanteenAcc, mStrMasterKey);
                String strSimBillBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncSimAcc, mStrMasterKey);
                mTextViewBalanceAmount.setText(strVoucherBalance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        initServiceMenu();
    }

    //########################## Initialize Menu ############################
    //########################## Initialize Menu ############################
    //########################## Initialize Menu ############################
    private void initServiceMenu() {
        //#################### Merchant #######################
        //#################### Merchant #######################
        if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
            //#################### Voucher Pay #########################
            if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                mBtnVoucherPay.setEnabled(true);
                mBtnCanteenPay.setEnabled(false);
                mBtnSimBill.setEnabled(false);
                mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                mBtnCanteenPay.setBackgroundColor(0xFFFFFFF);
                mBtnSimBill.setBackgroundColor(0xFFFFFFF);
            }
            //#################### Canteen Pay #########################
            else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                mBtnVoucherPay.setEnabled(false);
                mBtnCanteenPay.setEnabled(true);
                mBtnSimBill.setEnabled(false);
                mBtnVoucherPay.setBackgroundColor(0xFFFFFFF);
                mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                mBtnSimBill.setBackgroundColor(0xFFFFFFF);
            }
            //#################### SIM Bill #########################
            else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                mBtnVoucherPay.setEnabled(false);
                mBtnCanteenPay.setEnabled(false);
                mBtnSimBill.setEnabled(true);
                mBtnVoucherPay.setBackgroundColor(0xFFFFFFF);
                mBtnCanteenPay.setBackgroundColor(0xFFFFFFF);
                mBtnSimBill.setBackgroundColor(0xFFfde0e0);
            }

            final String[] mStrArrayGridViewItemLabel = {
                    "Make Payment",
                    "Fund Management",
                    "Transaction History",
                    "Favorite List",
                    "OTP",
                    "Change PIN"};
            int[] mIntGridViewItemImgId = {
                    R.drawable.icon_make_payment,
                    R.drawable.icon_send_money,
                    R.drawable.icon_transaction_history,
                    R.drawable.icon_favorite,
                    R.drawable.icon_otp,
                    R.drawable.icon_change_pin};
            CustomGrid gridAdapter = new CustomGrid(QPayMenuNew.this, mStrArrayGridViewItemLabel,
                    mIntGridViewItemImgId);
            mGridView = findViewById(R.id.gridView);
            mGridView.setAdapter(gridAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mStrArrayGridViewItemLabel[+position].equals("Make Payment")) {
                        startActivity(new Intent(QPayMenuNew.this, MP_Through_M_C2M.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Fund Management")) {
                        if (mStrOtpStatus.equalsIgnoreCase("valid")) {
                            // OTP Valid
                            startActivity(new Intent(QPayMenuNew.this, FM_M2M.class));
                        } else {
                            // OTP Expire
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                            myAlert.setMessage("OTP is expired. Generate a new OTP?");
                            myAlert.setPositiveButton(
                                    "Generate OTP",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
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
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Transaction History")) {
                        startActivity(new Intent(QPayMenuNew.this, TransactionHistory.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Favorite List")) {
                        startActivity(new Intent(QPayMenuNew.this, FavoriteTransaction.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("OTP")) {
                        startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Change PIN")) {
                        startActivity(new Intent(QPayMenuNew.this, ChangePin.class));
                    }

                }

            });
        }
        //#################### Customer #######################
        //#################### Customer #######################
        else if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
            mBtnVoucherPay.setEnabled(true);
            mBtnCanteenPay.setEnabled(true);
            mBtnSimBill.setEnabled(true);
            mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
            mBtnCanteenPay.setBackgroundColor(0xFFFFFFF);
            mBtnSimBill.setBackgroundColor(0xFFFFFFF);

            final String[] mStrArrayGridViewItemLabel = {
                    "Make Payment",
                    "Fund Transfer",
                    "Transaction History",
                    "Favorite List",
                    "OTP",
                    "Change PIN"};
            int[] mIntGridViewItemImgId = {
                    R.drawable.icon_make_payment,
                    R.drawable.icon_send_money,
                    R.drawable.icon_transaction_history,
                    R.drawable.icon_favorite,
                    R.drawable.icon_otp,
                    R.drawable.icon_change_pin};
            CustomGrid gridAdapter = new CustomGrid(QPayMenuNew.this, mStrArrayGridViewItemLabel,
                    mIntGridViewItemImgId);
            mGridView = findViewById(R.id.gridView);
            mGridView.setAdapter(gridAdapter);
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (mStrArrayGridViewItemLabel[+position].equals("Make Payment")) {
                        if (mStrOtpStatus.equalsIgnoreCase("valid")) {
                            // OTP Valid
                            startActivity(new Intent(QPayMenuNew.this, MP_Through_C_C2M.class));
                        } else {
                            // OTP Expire
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                            myAlert.setMessage("OTP is expired. Generate a new OTP?");
                            myAlert.setPositiveButton(
                                    "Generate OTP",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
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
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Fund Transfer")) {
                        if (mStrOtpStatus.equalsIgnoreCase("valid")) {
                            // OTP Valid
                            // OTP Valid
                            // OTP Valid
                            startActivity(new Intent(QPayMenuNew.this, FT_C2C.class));
                        } else {
                            // OTP Expire
                            // OTP Expire
                            // OTP Expire
                            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                            myAlert.setMessage("OTP is expired. Generate a new OTP?");
                            myAlert.setPositiveButton(
                                    "Generate OTP",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
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
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Transaction History")) {
                        startActivity(new Intent(QPayMenuNew.this, TransactionHistory.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Favorite List")) {
                        startActivity(new Intent(QPayMenuNew.this, FavoriteTransaction.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("OTP")) {
                        startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
                    } else if (mStrArrayGridViewItemLabel[+position].equals("Change PIN")) {
                        startActivity(new Intent(QPayMenuNew.this, ChangePin.class));
                    }

                }

            });
        }
    }

    //########################## All Click Events ############################
    //########################## All Click Events ############################
    //########################## All Click Events ############################
    @Override
    public void onClick(View v) {
        if (v == mImgViewProfilePic) {
            startActivity(new Intent(QPayMenuNew.this, KYC.class));
        }
//        if (v == mImgBtnQucikPay) {
//            if (mStrOtpStatus.equalsIgnoreCase("valid")) {
//                // OTP Valid
//                // OTP Valid
//                // OTP Valid
//                try {
//                    Intent intent = new Intent(ACTION_SCAN);
//                    intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
//                    startActivityForResult(intent, 0);
//                } catch (ActivityNotFoundException anfe) {
//                    showDialog(QPayMenuNew.this, "No Scanner Found", "Download a QR Scanner App?", "Yes",
//                            "No").show();
//                }
//            } else {
//                // OTP Expire
//                // OTP Expire
//                // OTP Expire
//                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
//                myAlert.setMessage("OTP is expired. Generate a new OTP?");
//                myAlert.setPositiveButton(
//                        "Generate OTP",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                                startActivity(new Intent(QPayMenuNew.this, GenerateOtp.class));
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
//
//        }
        if (v == mImgBtnQr) {
            startActivity(new Intent(QPayMenuNew.this, QrScan.class));
        }

        if (v == mBtnVoucherPay) {
            try {
                if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                    String strPrimaryWallet = mStrAccountNumber + 1;
                    mStrSourceWallet = strPrimaryWallet;
                    String strEncryptPrimaryWallet = encryption.Encrypt(strPrimaryWallet, mStrMasterKey);
                    String strPrimaryWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptPrimaryWallet, mStrMasterKey);
                    String strQrCodeContentPrimaryWallet = GetQrCodeContent.getQrCode(strEncryptPrimaryWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentPrimaryWallet;
                    GlobalData.setStrWallet(strPrimaryWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strPrimaryWalletBalance);
                    if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                        mBtnVoucherPay.setEnabled(true);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(true);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(true);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFfde0e0);
                    }

                } else if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                    mStrCustomerAccType = "VOUPAY";
                    String strPrimaryWallet = mStrAccountNumber + 1;
                    GlobalData.setStrCustomerAccountL14(strPrimaryWallet);
                    mStrSourceWallet = strPrimaryWallet;
                    String strEncryptPrimaryWallet = encryption.Encrypt(strPrimaryWallet, mStrMasterKey);
                    String strPrimaryWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptPrimaryWallet, mStrMasterKey);
                    String strQrCodeContentPrimaryWallet = GetQrCodeContent.getQrCode(strEncryptPrimaryWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentPrimaryWallet;
                    GlobalData.setStrWallet(strPrimaryWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strPrimaryWalletBalance);
                    mBtnVoucherPay.setEnabled(true);
                    mBtnCanteenPay.setEnabled(true);
                    mBtnSimBill.setEnabled(true);
                    mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                    mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                    mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }


        }
        //########################## Button Canteen Pay ############################
        //########################## Button Canteen Pay ############################
        //########################## Button Canteen Pay ############################
        if (v == mBtnCanteenPay) {
            try {
                if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                    String strSalaryWallet = GlobalData.getStrAccountNumber() + 1;
                    mStrSourceWallet = strSalaryWallet;
                    String strEncryptSalaryWallet = encryption.Encrypt(strSalaryWallet, mStrMasterKey);
                    String strSalaryWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptSalaryWallet, mStrMasterKey);
                    String strQrCodeContentSalaryWallet = GetQrCodeContent.getQrCode(strEncryptSalaryWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentSalaryWallet;
                    GlobalData.setStrWallet(strSalaryWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strSalaryWalletBalance);
                    if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                        mBtnVoucherPay.setEnabled(true);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(true);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(true);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFfde0e0);
                    }
                } else if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                    mStrCustomerAccType = "CANPAY";
                    String strSalaryWallet = GlobalData.getStrAccountNumber() + 2;
                    GlobalData.setStrCustomerAccountL14(strSalaryWallet);
                    mStrSourceWallet = strSalaryWallet;
                    String strEncryptSalaryWallet = encryption.Encrypt(strSalaryWallet, mStrMasterKey);
                    String strSalaryWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptSalaryWallet, mStrMasterKey);
                    String strQrCodeContentSalaryWallet = GetQrCodeContent.getQrCode(strEncryptSalaryWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentSalaryWallet;
                    GlobalData.setStrWallet(strSalaryWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strSalaryWalletBalance);
                    mBtnVoucherPay.setEnabled(true);
                    mBtnCanteenPay.setEnabled(true);
                    mBtnSimBill.setEnabled(true);
                    mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                    mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                    mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }


        }
        //########################## Button Reload ############################
        //########################## Button Reload ############################
        //########################## Button Reload ############################
        if (v == mBtnSimBill) {
            try {
                if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                    String strCreditWallet = GlobalData.getStrAccountNumber() + 1;
                    mStrSourceWallet = strCreditWallet;
                    String strEncryptCreditWallet = encryption.Encrypt(strCreditWallet, mStrMasterKey);
                    String strCreditWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptCreditWallet, mStrMasterKey);
                    String strQrCodeContentCreditWallet = GetQrCodeContent.getQrCode(strEncryptCreditWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentCreditWallet;
                    GlobalData.setStrWallet(strCreditWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strCreditWalletBalance);
                    if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                        mBtnVoucherPay.setEnabled(true);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFfde0e0);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(true);
                        mBtnSimBill.setEnabled(false);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFfde0e0);
                        mBtnSimBill.setBackgroundColor(0xFFFFFFFF);
                    } else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                        mBtnVoucherPay.setEnabled(false);
                        mBtnCanteenPay.setEnabled(false);
                        mBtnSimBill.setEnabled(true);
                        mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                        mBtnSimBill.setBackgroundColor(0xFFfde0e0);
                    }
                } else if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                    mStrCustomerAccType = "SIMBIL";
                    String strCreditWallet = GlobalData.getStrAccountNumber() + 3;
                    GlobalData.setStrCustomerAccountL14(strCreditWallet);
                    mStrSourceWallet = strCreditWallet;
                    String strEncryptCreditWallet = encryption.Encrypt(strCreditWallet, mStrMasterKey);
                    String strCreditWalletBalance = GetBalance.getBalance(GlobalData.getStrEncryptUserId(), GlobalData.getStrEncryptPin(), strEncryptCreditWallet, mStrMasterKey);
                    String strQrCodeContentCreditWallet = GetQrCodeContent.getQrCode(strEncryptCreditWallet, mStrMasterKey);
                    mStrQrCodeContent = strQrCodeContentCreditWallet;
                    GlobalData.setStrWallet(strCreditWallet);
                    GlobalData.setStrQrCodeContent(mStrQrCodeContent);
                    mTextViewBalanceAmount.setText(strCreditWalletBalance);
                    mBtnVoucherPay.setEnabled(true);
                    mBtnCanteenPay.setEnabled(true);
                    mBtnSimBill.setEnabled(true);
                    mBtnVoucherPay.setBackgroundColor(0xFFFFFFFF);
                    mBtnCanteenPay.setBackgroundColor(0xFFFFFFFF);
                    mBtnSimBill.setBackgroundColor(0xFFfde0e0);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

        }
        //########################## Button Reload ############################
        //########################## Button Reload ############################
        //########################## Button Reload ############################
        if (v == mImgBtnReload) {
            anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
            anim.setAnimationListener(this);
            mImgBtnReload.startAnimation(anim);
            loadLastTransactionDetails();
        }
    }

    //########################## Last Transaction ############################
    //########################## Last Transaction ############################
    //########################## Last Transaction ############################
    private void loadLastTransactionDetails() {
        try {
            mTextViewLastTransaction.setText("");
            mStrLastTransaction = "";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String strCurrentDate = sdf.format(new Date());
            mStrEncryptAccountNumber = encryption.Encrypt(GlobalData.getStrAccountNumber(), mStrMasterKey);
            mStrEncryptPin = encryption.Encrypt(GlobalData.getStrPin(), mStrMasterKey);
            mStrEncryptFromDate = encryption.Encrypt(strCurrentDate, mStrMasterKey);
            mStrEncryptToDate = encryption.Encrypt(strCurrentDate, mStrMasterKey);
            mStrEncryptAccessCode = encryption.Encrypt("all", mStrMasterKey);
            mStrEncryptParameter = encryption.Encrypt("1", mStrMasterKey);

            String strLastTransaction = GetLastTransaction.getLastTransaction(
                    mStrEncryptAccountNumber, mStrEncryptPin, mStrEncryptFromDate,
                    mStrEncryptToDate, mStrEncryptAccessCode, mStrEncryptParameter, mStrMasterKey);

            if (strLastTransaction != null && !strLastTransaction.isEmpty()) {
                if (strLastTransaction.equalsIgnoreCase("No data found")) {
                    mStrLastTransaction = "No Transaction Info Found.";
                } else if (strLastTransaction.equalsIgnoreCase("anyType{}")) {
                    mStrLastTransaction = "No Transaction Info Found.";
                } else {
                    mStrLastTransaction = strLastTransaction;
                }
            } else {
                mStrLastTransaction = "No Transaction Info Found.";
            }
            mTextViewLastTransaction.setText(mStrLastTransaction);
            mTextViewLastTransaction.invalidate();
            //  makeTextViewResizable(mTextViewLastTransaction, 1, "View Details...", true);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
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
                Intent intent = new Intent(QPayMenuNew.this, Login.class)
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

    //########################## Internet ############################
    //########################## Internet ############################
    //########################## Internet ############################
    private void checkInternet() {
        if (isNetworkConnected()) {
            //                enableUiComponents();
            loadLastTransactionDetails();
        } else {
            //                disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(QPayMenuNew.this);
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

    //########################## Animation Reload ############################
    //########################## Animation Reload  ############################
    //########################## Animation Reload  ############################
    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    //############################## Check OS ################################
    //############################## Check OS ################################
    //############################## Check OS ################################
    private void checkOs() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    //############################## View Details/Hide ################################
    //############################## View Details/Hide ################################
    //############################## View Details/Hide ################################
    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "Hide", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 1, "View Details...", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

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
                    AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(QPayMenuNew.this);
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

                    String strEncryptAccountNumberAndMasterKeyByQrCode = GetEncryptAccountNumberAndMasterKeyByQrCode.getEncryptAccountNumberAndMasterKeyByQrCode(mStrQrCodeContents);
                    if (!strEncryptAccountNumberAndMasterKeyByQrCode.equalsIgnoreCase("")) {
                        int intIndex = strEncryptAccountNumberAndMasterKeyByQrCode.indexOf("*");
                        if (intIndex == -1) {
                            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(QPayMenuNew.this);
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
                            String[] parts = strEncryptAccountNumberAndMasterKeyByQrCode.split("\\*");
                            strEncryptDestinationAccountNumberFromQr = parts[0];
                            strDestinationMasterKeyFromQr = parts[1];
                            //############################## Source Merchant ##############################
                            //############################## Source Merchant ##############################
                            //############################## Source Merchant ##############################
                            //############################## Source Merchant ##############################
                            //############################## Source Merchant ##############################
                            //############################## Source Merchant ##############################
                            if (mStrServicePackage.equalsIgnoreCase("1312050001")) {
                                try {
                                    //################ Destination Account Number ####################
                                    mStrDestinationWallet = encryptionDecryption.Decrypt(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                    String strAccountTypeCode = mStrDestinationWallet.substring(3, 5);
                                    //####################### Destination Merchant(M2M-FM) ############################
                                    //####################### Destination Merchant(M2M-FM) ############################
                                    //####################### Destination Merchant(M2M-FM) ############################
                                    if (strAccountTypeCode.equalsIgnoreCase("11")) {
                                        //################ Merchant Name ####################
                                        mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);

                                        String[] parts01 = mStrDestinationName.split("\\*");
                                        String strName = parts01[0];
                                        final String strType = parts01[1];


                                        //################ Show Dialog ####################
                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                        myAlert.setTitle("MERCHANT INFO");
                                        myAlert.setMessage("MERCHANT NAME" + "\n" + strName + "\n" + "MERCHANT WALLET" + "\n" + mStrDestinationWallet);
                                        myAlert.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        if (strType.equalsIgnoreCase(mStrMerchantRank)) {
                                                            GlobalData.setStrSourceWallet(mStrSourceWallet);
                                                            GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                            GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                            startActivity(new Intent(QPayMenuNew.this, FM_M2M_Quick.class));
                                                        } else {
                                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
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

                                    }
                                    //####################### Destination Customer(M2C-MP) ############################
                                    //####################### Destination Customer(M2C-MP) ############################
                                    //####################### Destination Customer(M2C-MP) ############################
                                    if (strAccountTypeCode.equalsIgnoreCase("12")) {
                                        String strDestinationRank = mStrDestinationWallet.substring(13, 14);
                                        if (mStrMerchantRank.equalsIgnoreCase("VOUPAY")) {
                                            if (strDestinationRank.equalsIgnoreCase("1")) {
                                                //################ Customer Name ####################
                                                mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                //################ Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                                myAlert.setTitle("CUSTOMER INFO");
                                                myAlert.setMessage("CUSTOMER NAME" + "\n" + mStrDestinationName + "\n" + "CUSTOMER WALLET" + "\n" + mStrDestinationWallet);
                                                myAlert.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                try {
                                                                    GlobalData.setStrSourceWallet(mStrSourceWallet);
                                                                    GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                                    GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                                    mStrEncryptMerchatWalletForOtp = encryptionDecryption.Encrypt(GlobalData.getStrAccountNumber() + 1, mStrMasterKey);
                                                                    mStrCustomerWalletByQrCode = encryptionDecryption.Decrypt(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                                    mStrEncryptCustomerWalletByQrCode = encryptionDecryption.Encrypt(mStrCustomerWalletByQrCode, mStrMasterKey);
                                                                    startActivity(new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class));
                                                                    ///-----------------####################################----------------------------------
//                                                                    Intent i = new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class);
//                                                                    Bundle bundle = new Bundle();
//                                                                    bundle.putString("source", mStrSourceWallet.toString());
//                                                                    bundle.putString("destination", mStrDestinationAccountNumberFromQr);
//                                                                    i.putExtras(bundle);
//                                                                    startActivity(i);

                                                                    ///-------------------=====##############################==================-------------------
                                                                    sendCustomerOtp(
                                                                            mStrEncryptMerchatWalletForOtp,
                                                                            GlobalData.getStrEncryptPin(),
                                                                            mStrEncryptCustomerWalletByQrCode,
                                                                            GlobalData.getStrMasterKey());
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
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
                                        } else if (mStrMerchantRank.equalsIgnoreCase("CANPAY")) {
                                            if (strDestinationRank.equalsIgnoreCase("2")) {
                                                //################ Customer Name ####################
                                                mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                //################ Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                                myAlert.setTitle("CUSTOMER INFO");
                                                myAlert.setMessage("CUSTOMER NAME" + "\n" + mStrDestinationName + "\n" + "CUSTOMER WALLET" + "\n" + mStrDestinationWallet);
                                                myAlert.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                try {
                                                                    GlobalData.setStrSourceWallet(mStrSourceWallet);
                                                                    GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                                    GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                                    mStrEncryptMerchatWalletForOtp = encryptionDecryption.Encrypt(GlobalData.getStrAccountNumber() + 1, mStrMasterKey);
                                                                    mStrCustomerWalletByQrCode = encryptionDecryption.Decrypt(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                                    mStrEncryptCustomerWalletByQrCode = encryptionDecryption.Encrypt(mStrCustomerWalletByQrCode, mStrMasterKey);
                                                                    startActivity(new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class));
                                                                    ///-----------------####################################----------------------------------
//                                                                    Intent i = new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class);
//                                                                    Bundle bundle = new Bundle();
//                                                                    bundle.putString("source", mStrSourceWallet);
//                                                                    bundle.putString("destination", mStrDestinationAccountNumberFromQr);
//                                                                    i.putExtras(bundle);
//                                                                    startActivity(i);
                                                                    ///-------------------=====##############################==================-------------------

                                                                    sendCustomerOtp(
                                                                            mStrEncryptMerchatWalletForOtp,
                                                                            GlobalData.getStrEncryptPin(),
                                                                            mStrEncryptCustomerWalletByQrCode,
                                                                            GlobalData.getStrMasterKey());
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
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
                                        } else if (mStrMerchantRank.equalsIgnoreCase("SIMBIL")) {
                                            if (strDestinationRank.equalsIgnoreCase("3")) {
                                                //################ Customer Name ####################
                                                mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                //################ Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                                myAlert.setTitle("CUSTOMER INFO");
                                                myAlert.setMessage("CUSTOMER NAME" + "\n" + mStrDestinationName + "\n" + "CUSTOMER WALLET" + "\n" + mStrDestinationWallet);
                                                myAlert.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                try {
                                                                    GlobalData.setStrSourceWallet(mStrSourceWallet);
                                                                    GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                                    GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                                    mStrEncryptMerchatWalletForOtp = encryptionDecryption.Encrypt(GlobalData.getStrAccountNumber() + 1, mStrMasterKey);
                                                                    mStrCustomerWalletByQrCode = encryptionDecryption.Decrypt(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                                    mStrEncryptCustomerWalletByQrCode = encryptionDecryption.Encrypt(mStrCustomerWalletByQrCode, mStrMasterKey);
                                                                    ///-----------------####################################----------------------------------
//                                                                    Intent i = new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class);
//                                                                    Bundle bundle = new Bundle();
//                                                                    bundle.putString("source", mStrSourceWallet);
//                                                                    bundle.putString("destination", mStrDestinationAccountNumberFromQr);
//                                                                    i.putExtras(bundle);
//                                                                    startActivity(i);
                                                                    ///-------------------=====##############################==================-------------------
                                                                    startActivity(new Intent(QPayMenuNew.this, MP_Through_M_C2M_Quick.class));
                                                                    sendCustomerOtp(
                                                                            mStrEncryptMerchatWalletForOtp,
                                                                            GlobalData.getStrEncryptPin(),
                                                                            mStrEncryptCustomerWalletByQrCode,
                                                                            GlobalData.getStrMasterKey());
                                                                } catch (Exception e) {
                                                                    e.printStackTrace();
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
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


                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }


                            //############################## Source Customer ##############################
                            //############################## Source Customer ##############################
                            //############################## Source Customer ##############################
                            //############################## Source Customer ##############################
                            //############################## Source Customer ##############################
                            //############################## Source Customer ##############################
                            if (mStrServicePackage.equalsIgnoreCase("1205190003")) {
                                try {
                                    //################ Destination Account Number ####################
                                    mStrDestinationWallet = encryptionDecryption.Decrypt(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                    String strAccountTypeCode = mStrDestinationWallet.substring(3, 5);
                                    //####################### Destination Merchant(C2M-MP) ############################
                                    //####################### Destination Merchant(C2M-MP) ############################
                                    //####################### Destination Merchant(C2M-MP) ############################
                                    if (strAccountTypeCode.equalsIgnoreCase("11")) {
                                        //################ Merchant Name ####################
                                        mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                        String[] parts01 = mStrDestinationName.split("\\*");
                                        String strName = parts01[0];
                                        final String strType = parts01[1];
                                        if (strType.equalsIgnoreCase("VOUPAY")) {
                                            strSourceWallet = GlobalData.getStrAccountNumber() + "1";
                                        } else if (strType.equalsIgnoreCase("CANPAY")) {
                                            strSourceWallet = GlobalData.getStrAccountNumber() + "2";
                                        } else if (strType.equalsIgnoreCase("SIMBIL")) {
                                            strSourceWallet = GlobalData.getStrAccountNumber() + "3";
                                        }

                                        //################ Show Dialog ####################
                                        AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                        myAlert.setTitle("MERCHANT INFO");
                                        myAlert.setMessage("MERCHANT NAME" + "\n" + strName + "\n" + "MERCHANT WALLET" + "\n" + mStrDestinationWallet);
                                        myAlert.setPositiveButton(
                                                "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        GlobalData.setStrSourceWallet(strSourceWallet);
                                                        GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                        GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                        startActivity(new Intent(QPayMenuNew.this, MP_Through_C_C2M_Quick.class));
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
                                    //####################### Destination Customer(C2C-FT) ############################
                                    //####################### Destination Customer(C2C-FT) ############################
                                    //####################### Destination Customer(C2C-FT) ############################
                                    if (strAccountTypeCode.equalsIgnoreCase("12")) {
                                        //####################### Destination if Customer ############################
                                        //####################### Destination if Customer ############################
                                        if (strAccountTypeCode.equalsIgnoreCase("12")) {
                                            //######################### Source Rank #####################
                                            String strSourceRank = mStrSourceWallet.substring(13, 14);
                                            //######################### Destination Rank #####################
                                            String strDestinationRank = mStrDestinationWallet.substring(13, 14);
                                            if (strSourceRank.equalsIgnoreCase(strDestinationRank)) {
                                                //################ Merchant Name ####################
                                                mStrDestinationName = GetMerchantName.getMerchantName(strEncryptDestinationAccountNumberFromQr, strDestinationMasterKeyFromQr);
                                                //################ Show Dialog ####################
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
                                                myAlert.setTitle("BENEFICIARY INFO");
                                                myAlert.setMessage("BENEFICIARY NAME" + "\n" + mStrDestinationName + "\n" + "BENEFICIARY WALLET" + "\n" + mStrDestinationWallet);
                                                myAlert.setPositiveButton(
                                                        "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                GlobalData.setStrSourceWallet(mStrSourceWallet);
                                                                GlobalData.setStrDestinationWallet(mStrDestinationWallet);
                                                                GlobalData.setStrDestinationWalletName(mStrDestinationName);
                                                                startActivity(new Intent(QPayMenuNew.this, FT_C2C_Quick.class));
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
                                                AlertDialog.Builder myAlert = new AlertDialog.Builder(QPayMenuNew.this);
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

                                    }

                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }


                        }


                    } else {
                        AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(QPayMenuNew.this);
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


    public void sendCustomerOtp(String strEncryptMerchantWallet,
                                String strEncryptMerchantPin,
                                String strEncryptCustomerWallet,
                                String strMasterKey) {
        String METHOD_NAME = "QPAY_GenerateOTP_Res";
        String SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_GenerateOTP_Res";
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


}
