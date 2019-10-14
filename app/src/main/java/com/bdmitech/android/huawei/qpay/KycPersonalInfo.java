package com.bdmitech.android.huawei.qpay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bdmitech.android.huawei.qpay.utils.Encryption;
import com.bdmitech.android.huawei.qpay.utils.GlobalData;

import junit.framework.Test;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class KycPersonalInfo extends AppCompatActivity implements View.OnClickListener {
    private String SOAP_ACTION, METHOD_NAME;
    private Encryption encryption = new Encryption();
    private ProgressDialog mProgressDialog = null;

    private Calendar cal;
    private int day;
    private int month;
    private int year;


    // initialize all ui components
    private EditText mEditTextDateOfBirth, mEditTextOccupation, mEditTextOrganizationName;
    private Button mBtnSubmit;
    private TextView mTextViewShowServerResponse;
    private String mStrGender, mStrMasterKey, mStrEncryptAccountNumber, mStrEncryptPin,
            mStrEncryptDateOfBirth, mStrEncryptOccupation, mStrEncryptOrganizationName,
            mStrEncryptGender, mStrServerResponse, mStrCurrentDate;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kyc_personal_info);
        checkOs();
        // initialize all ui components
        initUI();
    }

    // initialize all ui components and enable buttons for click event
    private void initUI() {
        mEditTextDateOfBirth = findViewById(R.id.editTextKycPersonalInfoDateOfBirth);
        mEditTextOccupation = findViewById(R.id.editTextKycPersonalInfoOccupation);
        mEditTextOrganizationName = findViewById(R.id.editTextKycPersonalInfoOrganizationName);
        mBtnSubmit = findViewById(R.id.btnKycPersonalInfoSubmit);
        mBtnSubmit.setOnClickListener(this);
        mTextViewShowServerResponse = findViewById(R.id.textViewKycPersonalInfoServerResponse);
        mStrMasterKey = GlobalData.getStrMasterKey();


        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        mStrCurrentDate = sdf.format(new Date());

//        mEditTextDateOfBirth.setText(mStrCurrentDate);
        mEditTextDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialogFromDate();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        checkInternet();
    }

    public void datePickerDialogFromDate() {

        DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                String strDay = Integer.toString(dayOfMonth);
                String strMonth = Integer.toString(monthOfYear + 1);

                if (strDay.length() == 1) {
                    strDay = "0" + strDay;
                }
                if (strMonth.length() == 1) {
                    strMonth = "0" + strMonth;
                }
                mEditTextDateOfBirth.setText(strDay + "/" + strMonth + "/" + year);
            }
        };

        DatePickerDialog dpDialog = new DatePickerDialog(this, listener, year, month, day);
        dpDialog.show();

    }

    @Override
    public void onClick(View v) {
        if (v == mBtnSubmit) {
            if (mEditTextDateOfBirth.getText().toString().length() == 0) {
                mEditTextDateOfBirth.setError("Field cannot be empty");
            } else if (mEditTextOccupation.getText().toString().length() == 0) {
                mEditTextOccupation.setError("Field cannot be empty");
            } else if (mEditTextOrganizationName.getText().toString().length() == 0) {
                mEditTextOrganizationName.setError("Field cannot be empty");
            } else {
//                disableUiComponentAfterClick();
                try {
                    mStrEncryptAccountNumber = encryption.Encrypt(GlobalData.getStrAccountNumber(), mStrMasterKey);
                    mStrEncryptPin = encryption.Encrypt(GlobalData.getStrPin(), mStrMasterKey);
                    mStrEncryptDateOfBirth = encryption.Encrypt(mEditTextDateOfBirth.getText().toString(), mStrMasterKey);
                    mStrEncryptOccupation = encryption.Encrypt(mEditTextOccupation.getText().toString(), mStrMasterKey);
                    mStrEncryptOrganizationName = encryption.Encrypt(mEditTextOrganizationName.getText().toString(), mStrMasterKey);
                    mStrEncryptGender = encryption.Encrypt(mStrGender, mStrMasterKey);

                    // Initialize progress dialog
                    mProgressDialog = ProgressDialog.show(KycPersonalInfo.this, null, "Processing request...", false, true);
                    // Cancel progress dialog on back key press
                    mProgressDialog.setCancelable(true);

                    Thread t = new Thread(new Runnable() {

                        @Override
                        public void run() {
//                    if (CheckMasterKeyAndSessionId.checkMasterKeyAndSessionId() == true) {
                            insertPersonalInfo(
                                    mStrEncryptAccountNumber,
                                    mStrEncryptPin,
                                    mStrEncryptDateOfBirth,
                                    mStrEncryptOccupation,
                                    mStrEncryptOrganizationName,
                                    mStrEncryptGender,
                                    mStrMasterKey
                            );
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
                                            AlertDialog.Builder myAlert = new AlertDialog.Builder(KycPersonalInfo.this);
                                            myAlert.setMessage(mStrServerResponse);
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

    // method for KYC Update for Personal Info
    public void insertPersonalInfo(
            String strEncryptAccountNumber,
            String strEncryptPin,
            String strEncryptDateOfBirth,
            String strEncryptOccupation,
            String strEncryptOrganizationName,
            String strEncryptGender,
            String strMasterKey) {
        METHOD_NAME = "QPAY_KYC_Personal_Info ";
        SOAP_ACTION = "http://www.bdmitech.com/m2b/QPAY_KYC_Personal_Info ";
        SoapObject request = new SoapObject(GlobalData.getStrNamespace().replaceAll(" ", "%20"), METHOD_NAME);

//        QPAY_KYC_Personal_Info
//        AccountNo
//        PIN
//        DOB
//        Occupation
//        Org_Name
//        Gender
//        strMasterKey

        final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        PropertyInfo encryptAccountNumber = new PropertyInfo();
        encryptAccountNumber.setName("AccountNo");
        encryptAccountNumber.setValue(strEncryptAccountNumber);
        encryptAccountNumber.setType(String.class);
        request.addProperty(encryptAccountNumber);

        PropertyInfo encryptPin = new PropertyInfo();
        encryptPin.setName("PIN");
        encryptPin.setValue(strEncryptPin);
        encryptPin.setType(String.class);
        request.addProperty(encryptPin);

        PropertyInfo encryptDateOfBirth = new PropertyInfo();
        encryptDateOfBirth.setName("DOB");
        encryptDateOfBirth.setValue(strEncryptDateOfBirth);
        encryptDateOfBirth.setType(String.class);
        request.addProperty(encryptDateOfBirth);

        PropertyInfo encryptOccupation = new PropertyInfo();
        encryptOccupation.setName("Occupation");
        encryptOccupation.setValue(strEncryptOccupation);
        encryptOccupation.setType(String.class);
        request.addProperty(encryptOccupation);

        PropertyInfo encryptOrganizationName = new PropertyInfo();
        encryptOrganizationName.setName("Org_Name");
        encryptOrganizationName.setValue(strEncryptOrganizationName);
        encryptOrganizationName.setType(String.class);
        request.addProperty(encryptOrganizationName);

        PropertyInfo encryptGender = new PropertyInfo();
        encryptGender.setName("Gender");
        encryptGender.setValue(strEncryptGender);
        encryptGender.setType(String.class);
        request.addProperty(encryptGender);

        PropertyInfo masterKey = new PropertyInfo();
        masterKey.setName("strMasterKey");
        masterKey.setValue(strMasterKey);
        masterKey.setType(String.class);
        request.addProperty(masterKey);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);
        Log.v("myApp:", request.toString());
        envelope.implicitTypes = true;
        Object objKycPersonalInfo = null;
        String strKycPersonalInfoReponse = "";

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(GlobalData.getStrUrl().replaceAll(" ", "%20"), 1000000);
            androidHttpTransport.call(SOAP_ACTION, envelope);
            objKycPersonalInfo = envelope.getResponse();
            strKycPersonalInfoReponse = objKycPersonalInfo.toString();
            mStrServerResponse = strKycPersonalInfoReponse;
            if (mStrServerResponse.equalsIgnoreCase("Update")) {
                mStrServerResponse = "Personal Info update succesfully.";
            } else {
                mStrServerResponse = strKycPersonalInfoReponse;
            }
        } catch (Exception exception) {
            mStrServerResponse = strKycPersonalInfoReponse;
        }
    }

    private void checkInternet() {
        if (isNetworkConnected()) {
            enableUiComponents();
        } else {
            disableUiComponents();
            AlertDialog.Builder mAlertDialogBuilder = new AlertDialog.Builder(KycPersonalInfo.this);
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
        mEditTextDateOfBirth.setEnabled(true);
        mEditTextOccupation.setEnabled(true);
        mEditTextOrganizationName.setEnabled(true);
        mBtnSubmit.setEnabled(true);
    }

    private void disableUiComponents() {
        mEditTextDateOfBirth.setEnabled(false);
        mEditTextOccupation.setEnabled(false);
        mEditTextOrganizationName.setEnabled(false);
        mBtnSubmit.setEnabled(false);
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
                Intent intent = new Intent(KycPersonalInfo.this, Login.class)
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

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    mStrGender = "M";
                Toast.makeText(KycPersonalInfo.this, "Male", Toast.LENGTH_LONG).show();
                break;
            case R.id.radio_female:
                if (checked)
                    mStrGender = "F";
                Toast.makeText(KycPersonalInfo.this, "Female", Toast.LENGTH_LONG).show();
                break;
        }
    }

}
