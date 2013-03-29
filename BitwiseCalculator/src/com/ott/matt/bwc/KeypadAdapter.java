package com.ott.matt.bwc;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class KeypadAdapter extends BaseAdapter {
	private Context mContext;
	private OnClickListener mListener;

	// bind the Main activities context to each button
	public KeypadAdapter(Context c) {
		mContext = c;
	}
	
	public void setOnButtonClickListener(View.OnClickListener listener) {
		mListener = listener;
	}
	

	public int getCount() {
		return mKeys.length;
	}

	public Object getItem(int position) {
		return mKeys[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	// called for each button created
	public View getView(int position, View convertView, ViewGroup parent) {
		// initialize the Button
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.button_layout, parent,
					false);
		}
		Resources res = parent.getResources();
		String text = String.format(res.getString(mKeys[position]));
		CharSequence styledText = Html.fromHtml(text);
		final Button buttonView = (Button) convertView.findViewById(R.id.button_view);
		buttonView.setOnClickListener(mListener);
		buttonView.setText(styledText);
		return convertView;
	}

	private static Integer[] mKeys = { R.string.SHIFT_LEFT,
			R.string.SHIFT_RIGHT, R.string.CALCULATE, R.string.DELETE,
			R.string.OR, R.string.AND, R.string.XOR, R.string.NOT,
			R.string.ZERO, R.string.ONE, R.string.TWO, R.string.THREE,
			R.string.FOUR, R.string.FIVE, R.string.SIX, R.string.SEVEN,
			R.string.EIGHT, R.string.NINE, R.string.TEN, R.string.ELEVEN,
			R.string.TWELVE, R.string.THIRTEEN, R.string.FOURTEEN,
			R.string.FIFTEEN };

}
