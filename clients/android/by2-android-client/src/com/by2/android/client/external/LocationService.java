package com.by2.android.client.external;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.by2.android.client.R;
import com.by2.android.client.constants.Constants;

public class LocationService {

	private Context context;
	public LocationService(Context context) {
		this.context = context;
	}
	
	public JSONArray findAllCompanies() {
		HttpClient httpclient = new DefaultHttpClient();
		JSONArray responseJSON = null;
		
		HttpGet request = new HttpGet(context.getResources().getString(R.string.server_base_url) + Constants.ALL_LOCATIONS_API);
		
		
		try {			
			HttpResponse response = httpclient.execute(request);
			
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, HTTP.UTF_8);
			
			System.out.println("RESPONSE from server : " + responseString);
			responseJSON = new JSONArray(responseString);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return responseJSON;
	}
	
	public JSONArray findCompanyOffices(String companyId) {
		HttpClient httpclient = new DefaultHttpClient();
		JSONArray responseJSON = null;
		System.out.println("Calling : " + context.getResources().getString(R.string.server_base_url) + Constants.OFFICE_LOCATIONS_API + companyId);
		HttpGet request = new HttpGet(context.getResources().getString(R.string.server_base_url) + Constants.OFFICE_LOCATIONS_API + companyId);		
		
		try {			
			HttpResponse response = httpclient.execute(request);
			
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, HTTP.UTF_8);
			
			System.out.println("RESPONSE from server : " + responseString);
			responseJSON = new JSONArray(responseString);
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		return responseJSON;
	}
}
