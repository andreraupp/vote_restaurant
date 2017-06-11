package br.com.andreraupp.voterestaurant.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by andre on 10/06/2017.
 */

public class SharedPreferencesOperations {
    private static final String PREFS_NAME = "ConfigPrefs";

    public static void saveOnPrefs(Context context, String key, String value){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadFromPrefs(Context context, String key){
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, null);
    }
}
