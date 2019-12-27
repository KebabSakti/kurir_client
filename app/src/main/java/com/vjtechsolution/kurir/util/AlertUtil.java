package com.vjtechsolution.kurir.util;

import android.app.AlertDialog;

public class AlertUtil {

    public void alert(AlertDialog.Builder builder, String title, String msg) {
        builder
            .setTitle(title)
            .setMessage(msg);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
