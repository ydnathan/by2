package com.by2.android.client.external;

import java.io.IOException;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Toast;

import com.by2.android.client.R;
import com.by2.android.client.constants.Constants;

public class UserService {

	private Context context;
	public UserService(Context context) {
		this.context = context;
	}
	
	public JSONObject addUser(String companyId, String gender, String contactNumber, String name, String companyEmail, String verified) {
		HttpClient httpclient = new DefaultHttpClient();
		JSONObject responseJSON = null;
		
		HttpPost httppost = new HttpPost(context.getResources().getString(R.string.server_base_url) + Constants.USER_ADD_API);
		
		
		try {
			List<BasicNameValuePair> nameValuePairs = new ArrayList<BasicNameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("company_id", "1"));
			nameValuePairs.add(new BasicNameValuePair("gender", gender));
			nameValuePairs.add(new BasicNameValuePair("contact_number", contactNumber));
			nameValuePairs.add(new BasicNameValuePair("name", name));
			nameValuePairs.add(new BasicNameValuePair("company_email", companyEmail));
			nameValuePairs.add(new BasicNameValuePair("verified", verified));
						
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity, HTTP.UTF_8);
			System.out.println("RESPONSE from server : " + responseString);
			responseJSON = new JSONObject(responseString);
			
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
