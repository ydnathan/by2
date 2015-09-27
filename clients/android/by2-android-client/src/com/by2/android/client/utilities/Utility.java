package com.by2.android.client.utilities;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
/**
 * Class which has Utility methods
 * 
 */
public class Utility {
    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN = 
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
 
    /**
     * Validate Email with regular expression
     * 
     * @param email
     * @return true for Valid Email and false for Invalid Email
     */
    public static boolean validate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }
    
    /**
     * returns a byte array of a image
     * @param context
     * @param uri
     * @return
     */
    public static byte[] GetFileBytes(Context context, Uri uri)
	{
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(200000);
	    InputStream imageStream;
	    try {
	        imageStream = context.getContentResolver().openInputStream(uri);
	        byte[] buffer;
	        buffer = new byte[100000];
	        while ((imageStream.read(buffer)) != -1) {
	            bos.write(buffer);
	        }
	    } catch (Throwable e) {
	        e.printStackTrace();
	    }
	    return bos.toByteArray();
	}
    
    /**
     * Checks whether play services are present
     * @param activity
     * @return
     */
    public static boolean checkPlayServices(Activity activity) {
    	final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity, "This device doesn't support Play services, App will not work normally", Toast.LENGTH_LONG).show();
            }
            return false;
        } else {
        	return true;
        }        
    }
}