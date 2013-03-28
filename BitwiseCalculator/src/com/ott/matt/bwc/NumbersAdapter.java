package com.ott.matt.bwc;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class NumbersAdapter extends BaseAdapter{
	private Context mContext;
	private OnClickListener mOnButtonClick;
	
	public NumbersAdapter(Context c) {
		mContext = c;
	}
	
	public void setOnButtonClickListener(OnClickListener listener) {
		mOnButtonClick = listener;
	}
	
	public int getCount() {
		return mNums.length;
	}
	
	public Object getItem(int position) {
		return mNums[position];
	}
	
	public long getItemId(int position) {
		return 0;
	}
	
	// creates a new ButtonView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		Button numView;
		if (convertView == null) { // if it is not recycled, then initialize its attributes
			numView = new Button(mContext);
			KeypadButton keypadButton = mNums[position];
			// give the button a click listener
			numView.setOnClickListener(mOnButtonClick);
			
			// set the button tag as the KeypadButton enum
			// which we can use as a reference
			numView.setTag(keypadButton);
		} else {	// if the view is recycled
			numView = (Button) convertView;
		}
		numView.setText(mNums[position].getText());
		return numView;
	}
	// Create and populate number buttons array with KeypadButton values
	private KeypadButton[] mNums = {KeypadButton.SHIFT_LEFT, KeypadButton.SHIFT_RIGHT, KeypadButton.CALCULATE,
			KeypadButton.BACKSPACE, KeypadButton.OR, KeypadButton.AND, KeypadButton.XOR, KeypadButton.NOT,
			KeypadButton.ZERO, KeypadButton.ONE, KeypadButton.TWO,
	 KeypadButton.THREE, KeypadButton.FOUR, KeypadButton.FIVE,KeypadButton.SIX,
	 KeypadButton.SEVEN, KeypadButton.EIGHT, KeypadButton.NINE, KeypadButton.TEN,KeypadButton.ELEVEN, 
	 KeypadButton.TWELVE, KeypadButton.THIRTEEN,KeypadButton.FOURTEEN, KeypadButton.FIFTEEN};
}
