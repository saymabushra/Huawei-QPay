package com.bdmitech.android.huawei.qpay.utils;

public class GetAllCustomerWallet {
    public static String getAllWallets() {
        String strAllWallets = "";
        String strVoucherPay = "VOUCHER PAY" + "-" + GlobalData.getStrAccountNumber() + "1";
        String strCanteenPay = "CANTEEN PAY" + "-" + GlobalData.getStrAccountNumber() + "2";
        String strSimBill = "SIM BILL" + "-" + GlobalData.getStrAccountNumber() + "3";
        strAllWallets = strVoucherPay + "&" + strCanteenPay + "&" + strSimBill;
        return strAllWallets;
    }
}
