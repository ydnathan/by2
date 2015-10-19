package com.by2.android.client;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class SelectDestinationActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener {

	// Google Map
    private GoogleMap googleMap;    
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_destination);
 
        try {
            // Loading map
            initilizeMap();
                        
            
            //double latitude = 17.385044;
            //double longitude = 78.486671;
            
            googleMap.setMyLocationEnabled(true);
            
            buildGoogleApiClient();
            
            
            LatLng myLocation = null;
            //Location location = googleMap.getMyLocation();
            
                        
            if (mLastLocation != null) {
            	System.out.println("GOING IN HERE");
                myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(12).build();            
//            // create marker
//            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Going Here");
//             
//            
//            // adding marker
//            googleMap.addMarker(marker);
//            
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();
    }
    
    
    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap(); 
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            }
        }
    }
 
    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);        
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
