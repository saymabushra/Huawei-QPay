package com.bdmitech.android.huawei.qpay;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bdmitech.android.huawei.qpay.utils.GenerateQrCodeNew;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;


public class QrScan extends AppCompatActivity {
    private ProgressDialog mProgressDialog = null;
    private TextView mTextViewAccountHolderFullName, mTextViewAccountNumber,
            mTextViewAccountHolderFullNameReverse, mTextViewAccountNumberReverse;
    private ImageView mImgView;
    private Bitmap mBitmapQrCode;
    private String mStrQrCodeContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr);
        checkOs();
        initUI();
    }

    private void initUI() {
        mImgView = findViewById(R.id.imageViewPreviewQrCode);
        mTextViewAccountHolderFullName = findViewById(R.id.textViewAccountHolderFullName);
        mTextViewAccountNumber = findViewById(R.id.textViewAccountNumber);
        mTextViewAccountHolderFullNameReverse = findViewById(R.id.textViewAccountHolderFullNameReverse);
        mTextViewAccountNumberReverse = findViewById(R.id.textViewAccountNumberReverse);
        mStrQrCodeContent = GlobalData.getStrQrCodeContent();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize progress dialog
        mProgressDialog = ProgressDialog.show(QrScan.this, null, "Loading...", false, true);
        // Cancel progress dialog on back key press
        mProgressDialog.setCancelable(true);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                mBitmapQrCode = GenerateQrCodeNew.generateQrCode(mStrQrCodeContent);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mImgView.setImageBitmap(mBitmapQrCode);
                                mTextViewAccountHolderFullName.setText(GlobalData.getStrAccountHolderName());
                                mTextViewAccountNumber.setText(GlobalData.getStrWallet());
                                mTextViewAccountHolderFullNameReverse.setText(GlobalData.getStrAccountHolderName());
                                mTextViewAccountNumberReverse.setText(GlobalData.getStrWallet());
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

    }

    private void checkOs() {
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }

    //########################## Logout ############################
    //########################## Logout ############################
    //########################## Logout ############################
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionLogout:
                clearDataFromGlobal();
                Intent intent = new Intent(QrScan.this, Login.class)
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

    private void checkInternet() {
        if (isNetworkConnected()) {
            //online
        } else {
            //offline
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(QrScan.this, QPayMenuNew.class));
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}
