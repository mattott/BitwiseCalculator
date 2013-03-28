package com.ott.matt.bwc;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class OperationsFragment extends Fragment {
	// container activity must implement this
	public interface OperationsListener {
		public void onOperationPressed();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
	}

}
