package com.tinyappsdev.tinypos.helper;

import android.content.Context;
import android.widget.Toast;

import com.tinyappsdev.tinypos.R;

/**
 * Created by pk on 11/20/2016.
 */

public class TinyUtils {

    public static double toPrecision(double v, int precision) {
        double factor = Math.pow(10, precision);
        return Math.round(v * factor) / factor;
    }

    public static void showMsgBox(Context context, int resId) {
        Toast.makeText(
                context,
                context.getString(resId),
                Toast.LENGTH_LONG
        ).show();
    }

}
