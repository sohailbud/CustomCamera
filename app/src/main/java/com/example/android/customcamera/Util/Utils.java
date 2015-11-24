package com.example.android.customcamera.Util;

import android.os.Build;
import android.os.Build.VERSION_CODES;

/**
 * Created by Sohail on 11/16/15.
 */
public class Utils {

    public Utils() {
    }

//    Android 2.0.1 Eclair (API level 6)
//        Android 2.1 Eclair (API level 7)
//    Android 2.2–2.2.3 Froyo (API level 8)
//    Android 2.3–2.3.2 Gingerbread (API level 9)
//        Android 2.3.3–2.3.7 Gingerbread (API level 10)
//    Android 3.0 Honeycomb (API level 11)
//    	Android 3.1 Honeycomb (API level 12)
//    	Android 3.2–3.2.6 Honeycomb (API level 13)
//    Android 4.0–4.0.2 Ice Cream Sandwich (API level 14)
//    	Android 4.0.3–4.0.4 Ice Cream Sandwich (API level 15)
//    Android 4.1–4.1.2 Jelly Bean (API level 16)
//    	Android 4.2–4.2.2 Jelly Bean (API level 17)
//    	Android 4.3–4.3.1 Jelly Bean (API level 18)
//    Android 4.4–4.4.4 KitKat (API level 19)
//    	Android 4.4W–4.4W.2 KitKat, with wearable extensions (API level 20)
//    Android 5.0–5.0.2 Lollipop (API level 21)
//    	Android 5.1–5.1.1 Lollipop (API level 22)
//    Android 6.0 Marshmallow (API level 23)

    public static boolean hashIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hashKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }

}
