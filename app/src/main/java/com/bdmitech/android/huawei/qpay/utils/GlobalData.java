package com.bdmitech.android.huawei.qpay.utils;

public class GlobalData {
    //    private static String strUrl = "http://27.147.137.179:90/qpay/";//Live Ip Fiber Optic Cable
//    private static String strUrl = "http://203.76.116.170:90/qpay/";//Live Ip Radio
    //    private static String strUrl = "http://203.83.175.154/qpayws/M2BWS_agent_QR.asmx";//Live Huawei Old
//    private static String strUrl = "http://203.83.175.202//qpayws/M2BWS_agent_QR.asmx";//Live Huawei New
    private static String strUrl = "http://203.83.175.202//qpayws/Service.asmx";//Live Huawei Current


    private static String strNamespace = "http://www.bdmitech.com/m2b";
    private static String strChannelType = "MPOS";
    private static String strSecurityKey = "^%$#@!";
    private static String strRegistrationSecurityKey = "Mit%$#21_^&*";
    private static String strRegistrationSecurityKeyEncryptionKey = "MiT^%$#@!";
    private static String strDeviceId;
    private static String strDeviceName;
    private static String strToken;
    private static String strMasterKey;
    private static String strSessionId;
    private static String strUserId;
    private static String strEncryptUserId;
    private static String strAccountHolderName;
    private static String strAccountNumber;
    private static String strEncryptAccountNumber;
    private static String strWallet;
    private static String strPin;
    private static String strEncryptPin;
    private static String strPackage;
    private static String strEncryptPackage;
    private static String strEncryptAccountRank;
    private static String strAccountRank;
    private static String strOtp;
    private static String strEncryptOtp;
    private static String strPrimaryWalletBalance;
    private static String strSalaryWalletBalance;
    private static String strCreditWalletBalance;
    private static String strQrCodeContent;
    private static boolean isLogout;

    private static String strCheckMasterKey;
    private static String strResponseMessage;

    private static String strFavSourceWallet;
    private static String strFavSourcePin;
    private static String strFavAmount;
    private static String strFavDestinationWallet;
    private static String strFavDestinationWalletAccountHolderName;
    private static String strFavCaption;
    private static String strFavReference;
    private static String strFavFunctionType;
    private static String strFavSourceMasterKey;
    private static String strMenuFrom;

    private static String strSourceWallet;
    private static String strDestinationWallet;
    private static String strDestinationWalletName;
    private static String strCustomerAccountL14;


    private static String strAccountTypeCode = "12";//Saving Account, 11 for Current Account
    private static String strMitEncryptionKey = "MitMycash";
    private static String strMitUserName = "mit";
    private static String strMitPin = "1234";

    public static String getStrUrl() {
        return strUrl;
    }

    public static void setStrUrl(String strUrl) {
        GlobalData.strUrl = strUrl;
    }

    public static String getStrNamespace() {
        return strNamespace;
    }

    public static void setStrNamespace(String strNamespace) {
        GlobalData.strNamespace = strNamespace;
    }

    public static String getStrChannelType() {
        return strChannelType;
    }

    public static void setStrChannelType(String strChannelType) {
        GlobalData.strChannelType = strChannelType;
    }

    public static String getStrSecurityKey() {
        return strSecurityKey;
    }

    public static void setStrSecurityKey(String strSecurityKey) {
        GlobalData.strSecurityKey = strSecurityKey;
    }

    public static String getStrRegistrationSecurityKey() {
        return strRegistrationSecurityKey;
    }

    public static void setStrRegistrationSecurityKey(String strRegistrationSecurityKey) {
        GlobalData.strRegistrationSecurityKey = strRegistrationSecurityKey;
    }

    public static String getStrRegistrationSecurityKeyEncryptionKey() {
        return strRegistrationSecurityKeyEncryptionKey;
    }

    public static void setStrRegistrationSecurityKeyEncryptionKey(String strRegistrationSecurityKeyEncryptionKey) {
        GlobalData.strRegistrationSecurityKeyEncryptionKey = strRegistrationSecurityKeyEncryptionKey;
    }

    public static String getStrDeviceId() {
        return strDeviceId;
    }

    public static void setStrDeviceId(String strDeviceId) {
        GlobalData.strDeviceId = strDeviceId;
    }

    public static String getStrDeviceName() {
        return strDeviceName;
    }

    public static void setStrDeviceName(String strDeviceName) {
        GlobalData.strDeviceName = strDeviceName;
    }

    public static String getStrToken() {
        return strToken;
    }

    public static void setStrToken(String strToken) {
        GlobalData.strToken = strToken;
    }

    public static String getStrMasterKey() {
        return strMasterKey;
    }

    public static void setStrMasterKey(String strMasterKey) {
        GlobalData.strMasterKey = strMasterKey;
    }

    public static String getStrSessionId() {
        return strSessionId;
    }

    public static void setStrSessionId(String strSessionId) {
        GlobalData.strSessionId = strSessionId;
    }

    public static String getStrUserId() {
        return strUserId;
    }

    public static void setStrUserId(String strUserId) {
        GlobalData.strUserId = strUserId;
    }

    public static String getStrEncryptUserId() {
        return strEncryptUserId;
    }

    public static void setStrEncryptUserId(String strEncryptUserId) {
        GlobalData.strEncryptUserId = strEncryptUserId;
    }

    public static String getStrAccountHolderName() {
        return strAccountHolderName;
    }

    public static void setStrAccountHolderName(String strAccountHolderName) {
        GlobalData.strAccountHolderName = strAccountHolderName;
    }

    public static String getStrAccountNumber() {
        return strAccountNumber;
    }

    public static void setStrAccountNumber(String strAccountNumber) {
        GlobalData.strAccountNumber = strAccountNumber;
    }

    public static String getStrEncryptAccountNumber() {
        return strEncryptAccountNumber;
    }

    public static void setStrEncryptAccountNumber(String strEncryptAccountNumber) {
        GlobalData.strEncryptAccountNumber = strEncryptAccountNumber;
    }

    public static String getStrWallet() {
        return strWallet;
    }

    public static void setStrWallet(String strWallet) {
        GlobalData.strWallet = strWallet;
    }

    public static String getStrPin() {
        return strPin;
    }

    public static void setStrPin(String strPin) {
        GlobalData.strPin = strPin;
    }

    public static String getStrEncryptPin() {
        return strEncryptPin;
    }

    public static void setStrEncryptPin(String strEncryptPin) {
        GlobalData.strEncryptPin = strEncryptPin;
    }

    public static String getStrPackage() {
        return strPackage;
    }

    public static void setStrPackage(String strPackage) {
        GlobalData.strPackage = strPackage;
    }

    public static String getStrEncryptPackage() {
        return strEncryptPackage;
    }

    public static void setStrEncryptPackage(String strEncryptPackage) {
        GlobalData.strEncryptPackage = strEncryptPackage;
    }

    public static String getStrEncryptAccountRank() {
        return strEncryptAccountRank;
    }

    public static void setStrEncryptAccountRank(String strEncryptAccountRank) {
        GlobalData.strEncryptAccountRank = strEncryptAccountRank;
    }

    public static String getStrAccountRank() {
        return strAccountRank;
    }

    public static void setStrAccountRank(String strAccountRank) {
        GlobalData.strAccountRank = strAccountRank;
    }

    public static String getStrOtp() {
        return strOtp;
    }

    public static void setStrOtp(String strOtp) {
        GlobalData.strOtp = strOtp;
    }

    public static String getStrEncryptOtp() {
        return strEncryptOtp;
    }

    public static void setStrEncryptOtp(String strEncryptOtp) {
        GlobalData.strEncryptOtp = strEncryptOtp;
    }

    public static String getStrPrimaryWalletBalance() {
        return strPrimaryWalletBalance;
    }

    public static void setStrPrimaryWalletBalance(String strPrimaryWalletBalance) {
        GlobalData.strPrimaryWalletBalance = strPrimaryWalletBalance;
    }

    public static String getStrSalaryWalletBalance() {
        return strSalaryWalletBalance;
    }

    public static void setStrSalaryWalletBalance(String strSalaryWalletBalance) {
        GlobalData.strSalaryWalletBalance = strSalaryWalletBalance;
    }

    public static String getStrCreditWalletBalance() {
        return strCreditWalletBalance;
    }

    public static void setStrCreditWalletBalance(String strCreditWalletBalance) {
        GlobalData.strCreditWalletBalance = strCreditWalletBalance;
    }

    public static String getStrQrCodeContent() {
        return strQrCodeContent;
    }

    public static void setStrQrCodeContent(String strQrCodeContent) {
        GlobalData.strQrCodeContent = strQrCodeContent;
    }

    public static boolean isIsLogout() {
        return isLogout;
    }

    public static void setIsLogout(boolean isLogout) {
        GlobalData.isLogout = isLogout;
    }

    public static String getStrCheckMasterKey() {
        return strCheckMasterKey;
    }

    public static void setStrCheckMasterKey(String strCheckMasterKey) {
        GlobalData.strCheckMasterKey = strCheckMasterKey;
    }

    public static String getStrResponseMessage() {
        return strResponseMessage;
    }

    public static void setStrResponseMessage(String strResponseMessage) {
        GlobalData.strResponseMessage = strResponseMessage;
    }

    public static String getStrFavSourceWallet() {
        return strFavSourceWallet;
    }

    public static void setStrFavSourceWallet(String strFavSourceWallet) {
        GlobalData.strFavSourceWallet = strFavSourceWallet;
    }

    public static String getStrFavSourcePin() {
        return strFavSourcePin;
    }

    public static void setStrFavSourcePin(String strFavSourcePin) {
        GlobalData.strFavSourcePin = strFavSourcePin;
    }

    public static String getStrFavAmount() {
        return strFavAmount;
    }

    public static void setStrFavAmount(String strFavAmount) {
        GlobalData.strFavAmount = strFavAmount;
    }

    public static String getStrFavDestinationWallet() {
        return strFavDestinationWallet;
    }

    public static void setStrFavDestinationWallet(String strFavDestinationWallet) {
        GlobalData.strFavDestinationWallet = strFavDestinationWallet;
    }

    public static String getStrFavDestinationWalletAccountHolderName() {
        return strFavDestinationWalletAccountHolderName;
    }

    public static void setStrFavDestinationWalletAccountHolderName(String strFavDestinationWalletAccountHolderName) {
        GlobalData.strFavDestinationWalletAccountHolderName = strFavDestinationWalletAccountHolderName;
    }

    public static String getStrFavCaption() {
        return strFavCaption;
    }

    public static void setStrFavCaption(String strFavCaption) {
        GlobalData.strFavCaption = strFavCaption;
    }

    public static String getStrFavReference() {
        return strFavReference;
    }

    public static void setStrFavReference(String strFavReference) {
        GlobalData.strFavReference = strFavReference;
    }

    public static String getStrFavFunctionType() {
        return strFavFunctionType;
    }

    public static void setStrFavFunctionType(String strFavFunctionType) {
        GlobalData.strFavFunctionType = strFavFunctionType;
    }

    public static String getStrFavSourceMasterKey() {
        return strFavSourceMasterKey;
    }

    public static void setStrFavSourceMasterKey(String strFavSourceMasterKey) {
        GlobalData.strFavSourceMasterKey = strFavSourceMasterKey;
    }

    public static String getStrMenuFrom() {
        return strMenuFrom;
    }

    public static void setStrMenuFrom(String strMenuFrom) {
        GlobalData.strMenuFrom = strMenuFrom;
    }

    public static String getStrSourceWallet() {
        return strSourceWallet;
    }

    public static void setStrSourceWallet(String strSourceWallet) {
        GlobalData.strSourceWallet = strSourceWallet;
    }

    public static String getStrDestinationWallet() {
        return strDestinationWallet;
    }

    public static void setStrDestinationWallet(String strDestinationWallet) {
        GlobalData.strDestinationWallet = strDestinationWallet;
    }

    public static String getStrDestinationWalletName() {
        return strDestinationWalletName;
    }

    public static void setStrDestinationWalletName(String strDestinationWalletName) {
        GlobalData.strDestinationWalletName = strDestinationWalletName;
    }

    public static String getStrCustomerAccountL14() {
        return strCustomerAccountL14;
    }

    public static void setStrCustomerAccountL14(String strCustomerAccountL14) {
        GlobalData.strCustomerAccountL14 = strCustomerAccountL14;
    }

    public static String getStrAccountTypeCode() {
        return strAccountTypeCode;
    }

    public static void setStrAccountTypeCode(String strAccountTypeCode) {
        GlobalData.strAccountTypeCode = strAccountTypeCode;
    }

    public static String getStrMitEncryptionKey() {
        return strMitEncryptionKey;
    }

    public static void setStrMitEncryptionKey(String strMitEncryptionKey) {
        GlobalData.strMitEncryptionKey = strMitEncryptionKey;
    }

    public static String getStrMitUserName() {
        return strMitUserName;
    }

    public static void setStrMitUserName(String strMitUserName) {
        GlobalData.strMitUserName = strMitUserName;
    }

    public static String getStrMitPin() {
        return strMitPin;
    }

    public static void setStrMitPin(String strMitPin) {
        GlobalData.strMitPin = strMitPin;
    }
}
