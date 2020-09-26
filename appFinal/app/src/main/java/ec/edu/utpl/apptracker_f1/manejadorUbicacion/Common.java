package ec.edu.utpl.apptracker_f1.manejadorUbicacion;

import android.content.Context;
import android.location.Location;

import java.text.DateFormat;
import java.util.Date;

import androidx.preference.PreferenceManager;

public class Common {
    private static final String KEY_REQUESTING_LOCATION_UPDATES = "LocationUpdateEnable";

    public static String getLocationText(Location mLocation) {
        return mLocation == null ? "No se conoce la ubicación" : new StringBuilder().append(mLocation.getLatitude()).toString();
    }

    public static CharSequence getLocationTitle(MyBackgroundService myBackgroundService) {
        return String.format("Location Updated: %1$s", DateFormat.getDateInstance().format(new Date()));
    }

    public static void setRequestLocationUpdates(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES,value).apply();
    }

    public static boolean requestLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }
}
