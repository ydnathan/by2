package com.by2.android.client;

public class GpsLocation {
	private Double latitude;
	private Double longitude;	
	
	public GpsLocation(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "GpsLocation [latitude=" + latitude + ", longitude=" + longitude
				+ "]";
	}	
}
