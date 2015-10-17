package com.by2.android.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
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
import com.by2.android.client.external.LocationService;
import com.by2.android.client.external.UserService;
import com.by2.android.client.helpers.SharedPreferencesDB;
import com.by2.android.client.utilities.Utility;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SignupActivity extends Activity {

	private ProgressDialog prgDialog;    
	private ImageView profilePic;
	private TextView uploadSelfieMsg;
	private TextView companyDomain;
	private EditText secretCode;
	private Spinner availableOffices;
	private Spinner companyLocationDropdown;
	private EditText email;
	private EditText name;
	private EditText phoneNumber;
	private Button signup;
	private RequestParams params;
	private RadioGroup genderSelector;
	private Button verifyButton;
	private String selectedGender = MALE;
	private String selectedCompanyOffice;
	
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
    private LocationService locationService;
    private Map<String, String> companyToIdMap;
    private Map<String, String> companyToDomainMap;
    private Map<String, GpsLocation> companyOfficeToGPSLocationMap;
    private Map<String, String> companyOfficeToIdLocationMap;
    private List<String> companies;
    private List<String> companyOffices;
    private File profilePicImage;
    
    AsyncTask<Void, Void, String> createRegIdTask;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		applicationContext = getApplicationContext();							
		params = new RequestParams();
		db = new SharedPreferencesDB(this);
		userService = new UserService(this);
		locationService = new LocationService(this);
		
		setTitle(getResources().getText(R.string.sign_up_text));
		
		
		// If verified, go directly
		if(isVerifiedUser()) {
			Intent intent = new Intent(SignupActivity.this, MainActivity.class);
			openApp(intent);
		}
		
		
		// Initializing all the views
		
		availableOffices = (Spinner) findViewById(R.id.availableOffices);
		companyLocationDropdown = (Spinner) findViewById(R.id.companyLocationDropdown);
		companyDomain = (TextView) findViewById(R.id.company_domain);
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
		companyToIdMap = new HashMap<String, String>();
		companyToDomainMap = new HashMap<String, String>();
		companyOfficeToGPSLocationMap = new HashMap<String, GpsLocation>();
		companyOfficeToIdLocationMap = new HashMap<String, String>();
		companies = new ArrayList<String>();
		companyOffices = new ArrayList<String>();
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
				} else {
					Toast.makeText(SignupActivity.this, "Uh Oh ! That's not right !", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		availableOffices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				companyDomain.setText("@" + companyToDomainMap.get(companies.get(position)));
				findCompanyOffices(companyToIdMap.get(companies.get(position)));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}					
		});
		
		companyLocationDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				selectedCompanyOffice = companyOfficeToIdLocationMap.get(companyOffices.get(position));
				Toast.makeText(applicationContext, "The co-od for this is " + companyOfficeToGPSLocationMap.get(companyOffices.get(position)), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		// for the locations to load up
		showProgressBar();
		findAllCompanies();
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
	 * API to get all the companies
	 */
	protected void findAllCompanies() {
		new AsyncTask<Void, Void, JSONArray>() {								
            @Override
            protected JSONArray doInBackground(Void... params) {
            	return locationService.findAllCompanies();            	
            }
 
            @Override
            protected void onPostExecute(JSONArray responseArray) {
            	hideProgressBar();            	
				try {
					for(int i=0; i<responseArray.length(); i++) {
						JSONObject companyObj = ((JSONObject)responseArray.get(i));
						String companyName = companyObj.getString("company_name");
						String companyId = String.valueOf(companyObj.getInt("id"));
						String domainName = companyObj.getString("email_domain");
						companies.add(companyName);
						companyToIdMap.put(companyName, companyId);
						companyToDomainMap.put(companyName, domainName);
					}					
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	            					
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter(SignupActivity.this, R.layout.multi_line, companies);
				availableOffices.setAdapter(adapter);
				
            }
        }.execute(null, null, null);
	}
	
	/**
	 * API to get company offices corresponding to company
	 */
	protected void findCompanyOffices(final String selectedCompany) {
		new AsyncTask<Void, Void, JSONArray>() {								
            @Override
            protected JSONArray doInBackground(Void... params) {
            	return locationService.findCompanyOffices(selectedCompany);            	
            }
 
            @Override
            protected void onPostExecute(JSONArray responseArray) {
            	hideProgressBar();
            	companyOffices.clear();
				try {
					for(int i=0; i<responseArray.length(); i++) {
						JSONObject companyObj = ((JSONObject)responseArray.get(i));
						String officeName = companyObj.getString("office_name");
						String addressText = companyObj.getString("address_text");
						String addressLatLon = companyObj.getString("address_lat_lon");
						String officeId = String.valueOf(companyObj.getInt("id"));
						companyOffices.add(officeName + " | " + addressText);
						String[] addressLoc = addressLatLon.split(",");
						
						Double latitude = Double.parseDouble(addressLoc[0]);
						Double longitude = Double.parseDouble(addressLoc[1]);
						
						companyOfficeToGPSLocationMap.put(officeName + " | " + addressText, new GpsLocation(latitude, longitude));
						companyOfficeToIdLocationMap.put(officeName + " | " + addressText, officeId);
					}					
				} catch (JSONException e) {
					e.printStackTrace();
				}
            	            					
				ArrayAdapter<CharSequence> adapter = new ArrayAdapter(SignupActivity.this, R.layout.multi_line, companyOffices);
				companyLocationDropdown.setAdapter(adapter);
				
            }
        }.execute(null, null, null);
	}
	
	
	/**
	 * API to register/sign-up the new user.
	 */
	protected void registerUser() {
		new AsyncTask<Void, Void, JSONObject>() {								
            @Override
            protected JSONObject doInBackground(Void... params) {
            	JSONObject result;            	            
            	result = userService.addUser(selectedCompanyOffice, selectedGender, phoneNumber.getText().toString(), name.getText().toString(), email.getText().toString() + companyDomain.getText(), UNVERIFIED);
            	if(profilePicImage !=null && result != null && result.has(KEY_USER_ID)) {
            		boolean imageUploadResult = false;
					try {
						imageUploadResult = uploadPhoto(profilePicImage, result.getString(KEY_USER_ID));
					} catch (JSONException e) {
						e.printStackTrace();
					}
            		System.out.println("Image upload : " + imageUploadResult);
            	}
            	return result;
            }
 
            @Override
            protected void onPostExecute(JSONObject msgJSON) {
            	hideProgressBar();
            	String userId = null; 
            	String verificationCode = null;
				try {					
					verificationCode = msgJSON.getString(KEY_VERIFICATION_CODE);
					userId = msgJSON.getString(KEY_USER_ID);
					
	                // Store the verification id in order to validate from corp email.				
					db.set(KEY_VERIFICATION_CODE, verificationCode);                
	                db.set(KEY_USER_ID, userId);
	                
				} catch (JSONException e) {
					e.printStackTrace();
				}            	            

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
	                    String actualFilePath = getRealPathFromUri(this, selectedImage);
	                    profilePicImage = new File(saveBitmapToFile(actualFilePath, scaled));
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

	private static String saveBitmapToFile(String fileName, Bitmap bmp) {
		File dir = new File(fileName);
		if (!dir.exists()) {
			dir.mkdirs();
		}			
		File file = new File(fileName+"_small");
		FileOutputStream fOut;
		try {
			fOut = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
			fOut.flush();
			fOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileName+"_small";
	}
	
	private static String getRealPathFromUri(Context context, Uri contentUri) {
	    Cursor cursor = null;
	    try {
	        String[] proj = { MediaStore.Images.Media.DATA };
	        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    } finally {
	        if (cursor != null) {
	            cursor.close();
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

	private boolean uploadPhoto(File image, String fileName) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(this.getResources().getString(R.string.server_base_url) + "/user/add_image");
		try {
			MultipartEntity entity = new MultipartEntity();
			entity.addPart("file_name", new StringBody(fileName + ".png"));
			entity.addPart("file", new FileBody(image));
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}