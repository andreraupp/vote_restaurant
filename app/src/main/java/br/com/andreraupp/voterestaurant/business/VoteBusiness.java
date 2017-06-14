package br.com.andreraupp.voterestaurant.business;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by andre on 14/06/2017.
 */

public class VoteBusiness {
    static final String MY_KEY = "AIzaSyC7pb_tjhET-rhhdI2ON8kF65JDw15jM4w";
    static final String URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    static final String RADIUS = "1500";
    static final String TYPE = "restaurant";

    public VoteBusiness() {

    }

    public StringBuilder getUrlNearbySearch(double latitude, double longitude) {
        StringBuilder sb = new StringBuilder(URL);
        sb.append("location=" + latitude + "," + longitude);
        sb.append("&radius=" + RADIUS);
        sb.append("&types=" + TYPE);
        sb.append("&sensor=true");
        sb.append("&key=" + MY_KEY);
        Log.d("UrlNearbySearch", "url: " + sb.toString());

        return sb;
    }

    public String getToday() {
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM-dd-yyyy");
        return simpleDate.format(new Date());
    }

    public Date getYesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    public String getYesterdayString() {
        DateFormat dateFormat =  new SimpleDateFormat("MM-dd-yyyy");
        Date myDate = getYesterday();
        return dateFormat.format(myDate);
    }

    public String getDateStartWeek() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM-dd-yyyy");

        String test = simpleDate.format(cal.getTime());
        return test;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
