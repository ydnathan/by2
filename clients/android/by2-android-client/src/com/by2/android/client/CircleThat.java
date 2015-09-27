package com.by2.android.client;

import java.util.Arrays;
import java.util.List;
import android.view.View;
import android.view.View.OnClickListener;

public class CircleThat implements OnClickListener {

	List<View> toHideList;

	public CircleThat(View... toHideArray) {
		toHideList = Arrays.asList(toHideArray);
	}

	@Override
	public void onClick(View v) {
		// circle this
		v.setBackgroundResource(R.drawable.circle_that);

		// uncircle others
		for (View view : toHideList) {
			view.setBackgroundResource(0);
		}
	}

}
