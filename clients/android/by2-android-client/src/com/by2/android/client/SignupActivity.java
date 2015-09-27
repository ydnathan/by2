package com.by2.android.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.by2.android.client.constants.Constants;
import com.by2.android.client.external.UserService;
import com.by2.android.client.helpers.SharedPreferencesDB;
import com.by2.android.client.utilities.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SignupActivity extends Activity {

	private ProgressDialog prgDialog;    
	private ImageView profilePic;
	private TextView uploadSelfieMsg;
	private EditText secretCode;
	private Spinner availableOffices;
	private EditText email;
	private EditText name;
	private EditText phoneNumber;
	private Button signup;
	private RequestParams params;
	private RadioGroup genderSelector;
	private Button verifyButton;
	private String selectedGender = MALE;
	
	private GoogleCloudMessaging gcmObj;
	private Context applicationContext; 
        
    private final static int GALLERY_RETURN_CODE = 54;
    private final static String KEY_VERIFICATION_CODE = "verificationCode";
    private final static String KEY_USER_ID = "userID";
    private final static String KEY_VERIFIED = "verified";
    private final static String KEY_REG_ID = "regId";
    
    private final static String MALE = "M";
    private final static String FEMALE = "F";
    private final static String UNVERIFIED = "0";
    private final static String VERIFIED = "0";
    
    private SharedPreferencesDB db;
    
    private UserService userService;
    
    AsyncTask<Void, Void, String> createRegIdTask;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		applicationContext = getApplicationContext();							
		params = new RequestParams();
		db = new SharedPreferencesDB(this);
		userService = new UserService(this);
		
		setTitle(getResources().getText(R.string.sign_up_text));
		
		
		// If verified, go directly
		if(isVerifiedUser()) {
			Intent intent = new Intent(SignupActivity.this, MainActivity.class);
			openApp(intent);
		}
		
		
		// Initializing all the views
		
		availableOffices = (Spinner) findViewById(R.id.availableOffices);
		email = (EditText) findViewById(R.id.email);
		name = (EditText) findViewById(R.id.name);
		phoneNumber = (EditText) findViewById(R.id.phoneNumber);		
		secretCode = (EditText) findViewById(R.id.secretCode);
		signup = (Button) findViewById(R.id.signup);
		profilePic = (ImageView) findViewById(R.id.profile_pic);
		uploadSelfieMsg = (TextView) findViewById(R.id.upload_selfie_msg);
		genderSelector = (RadioGroup) findViewById(R.id.gender);
		verifyButton = (Button) findViewById(R.id.verify_button);
		prgDialog = new ProgressDialog(this);
		
		// TODO: Populating the companies - should be done from Server call.
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.available_companies, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		availableOffices.setAdapter(adapter);		
		
		// TODO: Populate the second drop-down using the data from first dropdown. 
		
		signup.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				
				db.set(Constants.USER_NAME, name.getText().toString());
				showProgressBar();
				registerUser();
				// TODO:
				
				//saveGCMStuff(email.getText().toString()+"@flipkart.com");
			}
		});
				
		genderSelector.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(uploadSelfieMsg.getVisibility() == View.VISIBLE) {
					if(checkedId == R.id.male) {
						selectedGender = MALE;
						profilePic.setImageResource(R.drawable.m);
					} else {
						selectedGender = FEMALE;
						profilePic.setImageResource(R.drawable.f);
					}	
				}				
			}
		});
	
		profilePic.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), GALLERY_RETURN_CODE);
			}
		});
		
		verifyButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SignupActivity.this, MainActivity.class);
				if(secretCode.getText().toString().equals(db.get(KEY_VERIFICATION_CODE))) {
					db.set(KEY_VERIFIED, VERIFIED);
					openApp(intent);
				}								
			}
		});			
	}
	
	
	
	/**
	 * 
	 * @param Open the main app after closing this current actiity.
	 */
	protected void openApp(Intent intent) {
		SignupActivity.this.startActivity(intent);
		this.finish();
	}
	
	/**
	 * API to register/sign-up the new user.
	 */
	protected void registerUser() {
		new AsyncTask<Void, Void, JSONObject>() {								
            @Override
            protected JSONObject doInBackground(Void... params) {
            	return userService.addUser("1", selectedGender, phoneNumber.getText().toString(), name.getText().toString(), email.getText().toString()+"@flipkart.com", UNVERIFIED);            	
            }
 
            @Override
            protected void onPostExecute(JSONObject msgJSON) {
            	hideProgressBar();
            	String userId = null; 
            	String verificationCode = null;
				try {					
					verificationCode = msgJSON.getString(KEY_VERIFICATION_CODE);
					userId = msgJSON.getString(KEY_USER_ID);
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	            	
                // Store the verification id in order to validate from corp email.				
				db.set(KEY_VERIFICATION_CODE, verificationCode);                
                db.set(KEY_USER_ID, userId);

                if(verificationCode!=null && userId!= null) {
                	Toast.makeText(applicationContext, getResources().getString(R.string.successfully_registered_msg), Toast.LENGTH_LONG).show();	
                } else {
                	Toast.makeText(applicationContext, getResources().getString(R.string.generic_connection_issue_msg), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
	}
	
	/**
	 * Handling return from Gallery (to select the profile photo)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (resultCode != RESULT_CANCELED) {
	        try {
	            if (requestCode == GALLERY_RETURN_CODE && resultCode != 0) {
	            	byte[] fileBytes = null;
	                if (intent != null) {
	                    Uri selectedImage = intent.getData();
	                    fileBytes = Utility.GetFileBytes(this, selectedImage);
	                    Bitmap bitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length);
	                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
	                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);	                    
	                    profilePic.setImageBitmap(scaled);
	                    profilePic.setAlpha(1f);
	                    uploadSelfieMsg.setVisibility(View.GONE);
	                } else {
	                    fileBytes = null;
	                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.image_load_error), Toast.LENGTH_SHORT).show();
	                }
	            }
	        } catch (Throwable e) {
	            e.printStackTrace();
	        }
	    }
	}	

	protected void showProgressBar() {		
        prgDialog.setMessage(getResources().getString(R.string.waiting_msg));
        prgDialog.setCancelable(false);
        prgDialog.show();
	}
	
	protected void hideProgressBar() {
		if(prgDialog!=null) {
			prgDialog.hide();
			prgDialog.dismiss();
		}
	}

	protected void saveGCMStuff(String emailID) {
		if (!TextUtils.isEmpty(emailID) && Utility.validate(emailID)) {			 
            // Check if Google Play Service is installed in Device
            // Play services is needed to handle GCM stuffs
            if (Utility.checkPlayServices(this)) {
                registerInBackground(emailID);
            }
        } else {
            Toast.makeText(applicationContext, getResources().getString(R.string.validation_fail_email), Toast.LENGTH_LONG).show();
        }
	}
	
	// AsyncTask to register Device in GCM Server (Google)
    private void registerInBackground(final String emailID) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = null;
                try {
                    if (gcmObj == null) {
                        gcmObj = GoogleCloudMessaging.getInstance(applicationContext);
                    }
                    msg = gcmObj.register(ApplicationConstants.GOOGLE_PROJ_ID);
                } catch (IOException ex) {
                	ex.printStackTrace();
                }
                return msg;
            }
 
            @Override
            protected void onPostExecute(String regId) {
                if ( regId!=null ) {
                    // Store RegId created by GCM Server in SharedPref
                    db.set(KEY_REG_ID, regId);                                    
                    Toast.makeText(applicationContext, getResources().getString(R.string.successfully_registered_gcm_msg) + regId, Toast.LENGTH_SHORT).show();
                    storeRegIdinServer();
                } else { 
                    Toast.makeText(applicationContext, getResources().getString(R.string.gcm_error_msg), Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }
    
    // Share RegID with GCM Server Application on App Server
    private void storeRegIdinServer() {
        prgDialog.show();
        params.put(KEY_REG_ID, db.get(KEY_REG_ID));
        
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(ApplicationConstants.APP_SERVER_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                prgDialog.hide();
                if (prgDialog != null) {
                    prgDialog.dismiss();
                }
                Toast.makeText(applicationContext, getResources().getString(R.string.successfully_stored_gcm_msg), Toast.LENGTH_LONG).show();
            }
 
	        @Override
	        public void onFailure(int statusCode, Throwable error, String content) {
	            prgDialog.hide();
	            if (prgDialog != null) {
	                prgDialog.dismiss();
	            }
                        
                // non 200
                if (statusCode == 404) {
                    Toast.makeText(applicationContext,  getResources().getString(R.string.gcm_404), Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(applicationContext,  getResources().getString(R.string.gcm_500), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(applicationContext, getResources().getString(R.string.gcm_other_errors), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    
    @Override
    protected void onResume() {
        super.onResume();
        Utility.checkPlayServices(this);
    }
        
	protected boolean isVerifiedUser() {
		return db.get(Constants.USER_NAME)!=null && db.get(KEY_VERIFIED)!=null;  
	}
}






//public void onClick(View v)
//{
//    if(fileBytes != null)
//    {
//        MultipartEntity entity = getEntity(fileBytes);
//        PostData(entity);
//    }
//}
//
//private MultipartEntity getEntity(byte[] bytes)
//{
//    MultipartEntity entity = new MultipartEntity();
//    InputStream in = new ByteArrayInputStream(bytes);
//    ContentBody bin = new InputStreamBody(in, "Image_" + c.getTimeInMillis() + ".jpg");
//    entity.addPart("image_query_string_variable_name", bin);
//}
//
//private void PostData(MultipartEntity entity)
//{
//    HttpParams httpParameters = new BasicHttpParams();  
//    HttpConnectionParams.setConnectionTimeout(httpParameters, 20000);
//    HttpConnectionParams.setSoTimeout(httpParameters, 20000);
//    DefaultHttpClient client = new DefaultHttpClient(httpParameters);
//
//    HttpPost request = new HttpPost("http://www.you_server_url.com/someFileName.php");
//    request.setEntity(entity);
//
//    HttpResponse response = null;
//    response = client.execute(request);
//    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//    StringBuffer result = new StringBuffer();
//    String line = "";
//    while ((line = rd.readLine()) != null) {
//        result.append(line);
//    }
//    //
//    // Process the returned result from server...
//    //
//}