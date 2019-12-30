package com.vjtechsolution.kurir.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {

    public PrefUtil() {
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("APP_PREF", Context.MODE_PRIVATE);
    }

    public static void storeCustomerPreference(Context context, String city, String email, String name, String phone) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("CUSTOMER_CITY", city);
        editor.putString("CUSTOMER_EMAIL", email);
        editor.putString("CUSTOMER_NAME", name);
        editor.putString("CUSTOMER_PHONE", phone);
        editor.apply();
    }

    public static String getCustomerPref(Context context, String keyword) {
        return getSharedPreferences(context).getString(keyword, null);
    }
}
