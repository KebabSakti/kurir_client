package com.vjtechsolution.kurir.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    public PrefUtil() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    public static void storeCustomerCity(Context context, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("CUSTOMER_CITY", value);
        editor.apply();
    }

    public static String getCustomerCity(Context context) {
        return getSharedPreferences(context).getString("CUSTOMER_CITY", null);
    }
}
