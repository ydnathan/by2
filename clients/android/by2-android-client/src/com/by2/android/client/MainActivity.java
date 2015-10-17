package com.by2.android.client;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.by2.android.client.constants.Constants;
import com.by2.android.client.helpers.SharedPreferencesDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class MainActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	
	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private SharedPreferencesDB db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		db = new SharedPreferencesDB(this);
		
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout));
		
		Toast.makeText(this, "Welcome " + db.get(Constants.USER_NAME), Toast.LENGTH_SHORT).show();
		
		String str = getIntent().getStringExtra("msg");
		if(str!=null) {
			Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		}
	}

	
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		switch (position + 1) {
		case 1:
			fragmentManager.beginTransaction()
					.replace(R.id.container, AskForARideFragment.newInstance(1))
					.commit();
			break;
		case 2:
			fragmentManager.beginTransaction()
					.replace(R.id.container, GiveARideFragment.newInstance(2))
					.commit();
			break;
		case 3:
			fragmentManager.beginTransaction()
					.replace(R.id.container, AllRidesFragment.newInstance(3))
					.commit();
			break;
		case 4:
			fragmentManager.beginTransaction()
					.replace(R.id.container, AllRequestsFragment.newInstance(4))
					.commit();
			break;
		case 5:
			Toast.makeText(this, "coming soon", Toast.LENGTH_LONG).show();
			break;
			
		case 6:
			Toast.makeText(this, "coming soon", Toast.LENGTH_LONG).show();
			break;
			
		case 7:
			logout();
			break;
		}

	}

	private void logout() {
		SharedPreferencesDB db = new SharedPreferencesDB(this);
		db.delete("verificationCode");
		db.delete("userID");
		db.delete("verified");
		db.delete("regId");
		
		Intent intent = new Intent(MainActivity.this, SignupActivity.class);
		MainActivity.this.startActivity(intent);
		this.finish();
	}



	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		case 4:
			mTitle = getString(R.string.title_section4);
			break;
		case 5:
			mTitle = getString(R.string.title_section5);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class GiveARideFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static GiveARideFragment newInstance(int sectionNumber) {
			GiveARideFragment fragment = new GiveARideFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public GiveARideFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			final View rootView = inflater.inflate(
					R.layout.fragment_give_a_ride, container, false);
			final ImageView bike = (ImageView) rootView.findViewById(R.id.bike);
			final ImageView taxi = (ImageView) rootView.findViewById(R.id.taxi);
			final ImageView car = (ImageView) rootView.findViewById(R.id.car);
			final ImageView suv = (ImageView) rootView.findViewById(R.id.suv);

			bike.setOnClickListener(new CircleThat(taxi, car, suv));
			taxi.setOnClickListener(new CircleThat(bike, car, suv));
			car.setOnClickListener(new CircleThat(taxi, bike, suv));
			suv.setOnClickListener(new CircleThat(taxi, bike, car));

			final ImageView now = (ImageView) rootView.findViewById(R.id.now);
			final ImageView min5 = (ImageView) rootView
					.findViewById(R.id.min_5);
			final ImageView min10 = (ImageView) rootView
					.findViewById(R.id.min_10);
			final ImageView min15 = (ImageView) rootView
					.findViewById(R.id.min_15);

			now.setOnClickListener(new CircleThat(min5, min10, min15));
			min5.setOnClickListener(new CircleThat(now, min10, min15));
			min10.setOnClickListener(new CircleThat(now, min5, min15));
			min15.setOnClickListener(new CircleThat(now, min5, min10));

			Spinner availableOffices = (Spinner) rootView
					.findViewById(R.id.fromLocations);
			ArrayAdapter<CharSequence> adapter1 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.from_locations,
							android.R.layout.simple_spinner_item);
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			availableOffices.setAdapter(adapter1);

			Spinner locations = (Spinner) rootView
					.findViewById(R.id.toLocations);
			ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			locations.setAdapter(adapter2);

			Spinner via1 = (Spinner) rootView.findViewById(R.id.via_1);
			ArrayAdapter<CharSequence> adapter3 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			via1.setAdapter(adapter3);

			Spinner via2 = (Spinner) rootView.findViewById(R.id.via_2);
			ArrayAdapter<CharSequence> adapter4 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			via2.setAdapter(adapter4);

			Spinner via3 = (Spinner) rootView.findViewById(R.id.via_3);
			ArrayAdapter<CharSequence> adapter5 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			via3.setAdapter(adapter5);

			Spinner via4 = (Spinner) rootView.findViewById(R.id.via_4);
			ArrayAdapter<CharSequence> adapter6 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			via4.setAdapter(adapter6);

			Spinner via5 = (Spinner) rootView.findViewById(R.id.via_5);
			ArrayAdapter<CharSequence> adapter7 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			via5.setAdapter(adapter7);

			((Button) rootView.findViewById(R.id.add_more_via))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							Spinner via4 = (Spinner) rootView
									.findViewById(R.id.via_4);
							if (via4.getVisibility() == View.GONE) {
								via4.setVisibility(View.VISIBLE);
							} else {
								Spinner via5 = (Spinner) rootView
										.findViewById(R.id.via_5);
								via5.setVisibility(View.VISIBLE);
							}
						}
					});

			final View updateView = rootView.findViewById(R.id.update_pane);
			((Button) rootView.findViewById(R.id.create_my_ride))
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							v.setVisibility(View.GONE);
							updateView.setVisibility(View.VISIBLE);
							Toast.makeText(getActivity(), "ride created !",
									Toast.LENGTH_LONG).show();
						}
					});
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class AskForARideFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static AskForARideFragment newInstance(int sectionNumber) {
			AskForARideFragment fragment = new AskForARideFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public AskForARideFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_ask_for_a_ride,
					container, false);

			Spinner availableOffices = (Spinner) rootView
					.findViewById(R.id.fromLocations);
			ArrayAdapter<CharSequence> adapter1 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.from_locations,
							android.R.layout.simple_spinner_item);
			adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			availableOffices.setAdapter(adapter1);

			Spinner locations = (Spinner) rootView
					.findViewById(R.id.toLocations);
			ArrayAdapter<CharSequence> adapter2 = ArrayAdapter
					.createFromResource(this.getActivity(),
							R.array.to_locations,
							android.R.layout.simple_spinner_item);
			adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			locations.setAdapter(adapter2);

			Button createRequest = (Button) rootView
					.findViewById(R.id.create_my_request);
			createRequest.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "request added !",
							Toast.LENGTH_LONG).show();
					((TextView) v).setText("REQUEST ANOTHER RIDE");
				}
			});
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class AllRidesFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static AllRidesFragment newInstance(int sectionNumber) {
			AllRidesFragment fragment = new AllRidesFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public AllRidesFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_all_rides,
					container, false);

			final ListView listview = (ListView) rootView
					.findViewById(R.id.listview);
			String[] values = new String[] { "Harini", "Raghu", "Sachin",
					"Amod", "Binny" };
			String[] whereTo = new String[] { "Jayanagar", "HSR Layout",
					"Silk Board", "JP Nagar phase 2", "JP Nagar phase 1" };
			int[] images = { R.drawable.user_1, R.drawable.user_2,
					R.drawable.user_3, R.drawable.user_4, R.drawable.user_5 };
			boolean[] statusArr = new boolean[] { false, false, false, false,
					false };

			final ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < values.length; ++i) {
				list.add(values[i]);
			}
			final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
					this.getActivity(), values, whereTo, statusArr, images,
					false);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final String item = (String) parent
							.getItemAtPosition(position);
					// parent.findViewById(R.id.icon).setBackgroundResource(R.drawable.circle_that);
					// view.animate().setDuration(2000).alpha(0)
					// .withEndAction(new Runnable() {
					// @Override
					// public void run() {
					// list.remove(item);
					// adapter.notifyDataSetChanged();
					// view.setAlpha(1);
					// }
					// });
				}

			});

			final TextView info = (TextView) rootView.findViewById(R.id.label_6);
			Button letsGo = (Button) rootView.findViewById(R.id.lets_roll);
			final Button reload = (Button) rootView.findViewById(R.id.reload);
			letsGo.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "confirming the same !",
							Toast.LENGTH_LONG).show();
					saveInfo(adapter);
					v.setVisibility(View.GONE);
					reload.setVisibility(View.GONE);
					info.setVisibility(View.VISIBLE);
				}
			});
						
			if(getInfo() != null) {
				letsGo.setVisibility(View.GONE);
				info.setVisibility(View.VISIBLE);
				reload.setVisibility(View.GONE);
			}
			
			return rootView;
		}

		protected void saveInfo(MySimpleArrayAdapter adapter) {
			SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("leaving.in", MODE_PRIVATE).edit();
			editor.putString("ride_seekers", adapter.getApprovedItemsStr());
			//editor.putInt("idName", 12);
			editor.commit();
		}
		
		protected String getInfo() {
			SharedPreferences prefs = this.getActivity().getSharedPreferences("leaving.in", MODE_PRIVATE); 
			return prefs.getString("ride_seekers", null);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	public static class AllRequestsFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static AllRequestsFragment newInstance(int sectionNumber) {
			AllRequestsFragment fragment = new AllRequestsFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public AllRequestsFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_all_requests,
					container, false);

			final ListView listview = (ListView) rootView
					.findViewById(R.id.listview);
			String[] values = new String[] { "Vaidyanathan | HSR Layout",
					"Prasanth | HSR Layout", "Amit | JP Nagar" };
			String[] whereTo = new String[] { "Leaving right away",
					"Leaving in 15 mins", "Leaving in 5 mins" };
			boolean[] statusArr = new boolean[] { false, false, false };
			int[] images = { R.drawable.user_6, R.drawable.user_7,
					R.drawable.user_8 };
			final ArrayList<String> list = new ArrayList<String>();
			for (int i = 0; i < values.length; ++i) {
				list.add(values[i]);
			}
			final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
					this.getActivity(), values, whereTo, statusArr, images,
					true);
			listview.setAdapter(adapter);

			listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, final View view,
						int position, long id) {
					final String item = (String) parent
							.getItemAtPosition(position);
					// parent.findViewById(R.id.icon).setBackgroundResource(R.drawable.circle_that);
					// view.animate().setDuration(2000).alpha(0)
					// .withEndAction(new Runnable() {
					// @Override
					// public void run() {
					// list.remove(item);
					// adapter.notifyDataSetChanged();
					// view.setAlpha(1);
					// }
					// });
				}

			});

			final Button contactButton = (Button) rootView
					.findViewById(R.id.contact_button);
			contactButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String phText = (String) ((Button) v).getText();
					String number = phText.substring(19, phText.length());
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel:" + number));
					startActivity(intent);
				}
			});

			//final TextView info = (TextView) rootView.findViewById(R.id.label_6);
			final Button reload = (Button) rootView.findViewById(R.id.reload);
			
			Button letsGo = (Button) rootView.findViewById(R.id.lets_go);
			letsGo.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(getActivity(), "ride is confirmed !", Toast.LENGTH_LONG).show();
					saveInfo(adapter);
					v.setVisibility(View.GONE);					
					reload.setVisibility(View.GONE);
					//info.setVisibility(View.VISIBLE);
					contactButton.setVisibility(View.VISIBLE);
				}
			});
			
			if(getInfo() != null) {
				letsGo.setVisibility(View.GONE);
				//info.setVisibility(View.VISIBLE);
				reload.setVisibility(View.GONE);
			}
			
			return rootView;
		}

		protected void saveInfo(MySimpleArrayAdapter adapter) {
			SharedPreferences.Editor editor = this.getActivity().getSharedPreferences("leaving.in", MODE_PRIVATE).edit();
			System.out.println("adapter.getApprovedItemsStr()="+adapter.getApprovedItemsStr());
			editor.putString("ride_givers", adapter.getApprovedItemsStr());
			//editor.putInt("idName", 12);
			editor.commit();
		}
		
		protected String getInfo() {
			SharedPreferences prefs = this.getActivity().getSharedPreferences("leaving.in", MODE_PRIVATE); 
			return prefs.getString("ride_givers", null);
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(
					ARG_SECTION_NUMBER));
		}
	}

	
	
	// Check if Google Playservices is installed in Device or not
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        // When Play services not found in device
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                // Show Error dialog to install Play services
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "This device doesn't support Play services, App will not work normally",
                        Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        } else {
//            Toast.makeText(
//                    getApplicationContext(),
//                    "This device supports Play services, App will work normally",
//                    Toast.LENGTH_LONG).show();
        }
        return true;
    }
 
    // When Application is resumed, check for Play services support to make sure
    // app will be running normally
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }
    
    
    
    
	
	
	
}
