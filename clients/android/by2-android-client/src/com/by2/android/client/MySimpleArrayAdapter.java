package com.by2.android.client;

import com.example.leavingin.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
	private final String[] whereTo;
	private final int[] images;
	private final boolean[] approvalStatus;
	private boolean onlyOneAllowed;
	private String keyName;
	
	
	public MySimpleArrayAdapter(Context context, String[] values, String[] whereTo, boolean[] approvalStatus, int[] images, boolean onlyOneAllowed) {
		super(context, R.layout.approve_line_item, values);
		this.context = context;
		this.values = values;
		this.whereTo = whereTo;
		this.approvalStatus = approvalStatus;
		this.onlyOneAllowed = onlyOneAllowed;
		this.images = images;
		if(onlyOneAllowed) {
			keyName = "ride_givers";
		} else {
			keyName = "ride_seekers";
		}
	}

	public String getApprovedItemsStr() {
		String str = "";
		for(int i=0; i<approvalStatus.length; i++) {
			if(approvalStatus[i] == true) {
				str+=","+i;	
			}			
		}		
		return str.substring(1,  str.length());
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.approve_line_item, parent, false);
		
		((TextView) rowView.findViewById(R.id.firstLine)).setText(values[position]);
		((TextView) rowView.findViewById(R.id.secondLine)).setText(whereTo[position]);
		
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);		
		// change the icon for Windows and iPhone
		imageView.setImageResource(images[position]);		
		
		SharedPreferences prefs = rowView.getContext().getSharedPreferences("leaving.in", rowView.getContext().MODE_PRIVATE); 
				
		final String rideSeekersDecided = prefs.getString(keyName, null);
		System.out.println("rideSeekersDecided="+rideSeekersDecided + "keyName="+keyName);
//		if (restoredText != null) {
//		  String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
//		  int idName = prefs.getInt("idName", 0); //0 is the default value.
//		}
		final ImageView approveStatus = (ImageView) rowView.findViewById(R.id.approve_status);
		if(rideSeekersDecided != null) {
			if(rideSeekersDecided.indexOf(position+"") > -1) {
				approveStatus.setImageResource(R.drawable.approve_after);
			} else {
				approveStatus.setImageResource(R.drawable.approve_before);
			}
		}
		
		
		approveStatus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(rideSeekersDecided == null) {
					if(onlyOneAllowed) {
						if(approvalStatus[position]) {
							approveStatus.setImageResource(R.drawable.approve_before);
							approvalStatus[position] = false;
						} else {
							boolean atleastOneSelected = false;
							for(boolean val : approvalStatus) {
								if(val == true) {
									atleastOneSelected = true;
									break;
								}
							}
							if(!atleastOneSelected) {
								approveStatus.setImageResource(R.drawable.approve_after);
								approvalStatus[position] = true;	
							}
						}
					} else {
						if(approvalStatus[position]) {
							approveStatus.setImageResource(R.drawable.approve_before);
							approvalStatus[position] = false;
						}else {
							approveStatus.setImageResource(R.drawable.approve_after);
							approvalStatus[position] = true;
						}
					}
				}								
			}
		});
		return rowView;
	}
}