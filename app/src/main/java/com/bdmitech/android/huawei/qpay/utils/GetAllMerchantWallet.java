package com.bdmitech.android.huawei.qpay.utils;

public class GetAllMerchantWallet {
    public static String getAllMerchantWallet() {
        String strAllWallets = "";
        String strVoucherPay = "VOUCHER PAY" + "-" + GlobalData.getStrAccountNumber() + "1";
        String strCanteenPay = "CANTEEN PAY" + "-" + GlobalData.getStrAccountNumber() + "1";
        String strSimBill = "SIM BILL" + "-" + GlobalData.getStrAccountNumber() + "1";
        strAllWallets = strVoucherPay + "&" + strCanteenPay + "&" + strSimBill;
        return strAllWallets;
    }
}
