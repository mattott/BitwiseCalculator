package com.ott.matt.bwc;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DisplayFragment extends Fragment{
	public static DisplayFragment newInstance(String input) {
        DisplayFragment f = new DisplayFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("text", input);
        f.setArguments(args);

        return f;
    }
	// inflate the view
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText(appendOperations());
		return textView;
	}
	
	public String appendOperations() {
		return getArguments().getString("text");
	}
}
