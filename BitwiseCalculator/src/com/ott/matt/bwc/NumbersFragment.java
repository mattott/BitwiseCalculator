package com.ott.matt.bwc;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class NumbersFragment extends Fragment{
	OnNumberPressedListener mListener;

	public interface OnNumberPressedListener {
		public void onNumberPressed(String keyText);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnNumberPressedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + "must implement OnNumberPressedListener");
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		GridView gridView = (GridView) inflater.inflate(R.layout.numbers_fragment, container, false);
		final NumbersAdapter na = new NumbersAdapter(getActivity());
		gridView.setAdapter(na);
		
		// Set button click listener of the keypad adapter
		na.setOnButtonClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			KeypadButton keypadButton = (KeypadButton)btn.getTag();
			mListener.onNumberPressed(keypadButton.getText().toString());
		}});
		
		return gridView;
	}
}
