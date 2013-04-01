package com.ott.matt.bwc;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {
	CharSequence mCurText = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// inflate the view
		setContentView(R.layout.activity_main);
		GridView gV = (GridView) findViewById(R.id.keypad_view);
		final TextView tV = (TextView) findViewById(R.id.display_view);
		final ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < buttonResources.length; i++) {
			arrayList.add(getString(buttonResources[i]));
		}
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, arrayList);
		OnItemClickListener mClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String text;
				if (buttonResources[position] == R.string.DELETE) {
						text = onDelete();
				} else if (buttonResources[position] == R.string.CALCULATE) {
					text = onCalculate();
				} else if (buttonResources[position] == R.string.AND
						| buttonResources[position] == R.string.OR
						| buttonResources[position] == R.string.XOR
						| buttonResources[position] == R.string.NOT
						| buttonResources[position] == R.string.SHIFT_RIGHT
						| buttonResources[position] == R.string.SHIFT_LEFT) {
					text = onOperation((CharSequence) (arrayList.get(position))
							.toString());
				} else {
					text = onNumber((CharSequence) (arrayList.get(position))
							.toString());
				}
				mCurText = (CharSequence) text;
				tV.setText(text);
			}
		};
		gV.setAdapter(aa);
		gV.setOnItemClickListener(mClickListener);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); // takes care of all of the default
												// saves
		outState.putCharSequence("curText", mCurText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public String onDelete() {
		if (mCurText.length() > 1) {
			if (mCurText.charAt(mCurText.length() - 1) == ' ') {
				if (mCurText.charAt(mCurText.length() - 2) == '<' | mCurText.charAt(mCurText.length() - 2) == '>') {
					return (String) mCurText.subSequence(0, mCurText.length() - 4);
				} else {
					return (String) mCurText.subSequence(0, mCurText.length() - 3);
				}
			} else {
				return (String) mCurText.subSequence(0, mCurText.length() - 1);
			}
		} else {
			return "";
		}
	}

	public String onCalculate() {
		return null;
	}

	public String onOperation(CharSequence keyText) {
		return mCurText.toString() + " " + keyText.toString() + " ";
	}

	public String onNumber(CharSequence keyText) {
		return mCurText.toString() + keyText.toString();
	}

	private final Integer[] buttonResources = { R.string.SHIFT_LEFT,
			R.string.SHIFT_RIGHT, R.string.CALCULATE, R.string.DELETE,
			R.string.OR, R.string.AND, R.string.XOR, R.string.NOT,
			R.string.ZERO, R.string.ONE, R.string.TWO, R.string.THREE,
			R.string.FOUR, R.string.FIVE, R.string.SIX, R.string.SEVEN,
			R.string.EIGHT, R.string.NINE, R.string.TEN, R.string.ELEVEN,
			R.string.TWELVE, R.string.THIRTEEN, R.string.FOURTEEN,
			R.string.FIFTEEN };
}
