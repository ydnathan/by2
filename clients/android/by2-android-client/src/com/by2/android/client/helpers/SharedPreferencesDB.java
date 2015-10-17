package com.by2.android.client.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesDB {
	
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;
	private final String DB_NAME="buzkashi";
	
	public SharedPreferencesDB(Context context) {
		this.prefs = context.getSharedPreferences(DB_NAME, context.MODE_PRIVATE);
		this.editor = this.prefs.edit();
	}

	public String get(String key) {		  	
		return prefs.getString(key, null);
	}
	
	public boolean set(String key, String value) {		
		editor.putString(key, value);
		return editor.commit();
	}
	
	public boolean delete(String key) {		
		editor.remove(key);
		return editor.commit();
	}
}
