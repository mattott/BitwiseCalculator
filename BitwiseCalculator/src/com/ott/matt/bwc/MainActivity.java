package com.ott.matt.bwc;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity extends Activity {
	String mCurText = "";
	boolean hasOperator = false;

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
					text = onOperation(arrayList.get(position));
				} else {
					text = onNumber(arrayList.get(position));
				}
				mCurText = text;
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
		outState.putString("curText", mCurText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bin:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			return true;
		case R.id.oct:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			return true;
		case R.id.dec:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			return true;
		case R.id.hex:
			if (item.isChecked())
				item.setChecked(false);
			else
				item.setChecked(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public String onDelete() {
		if (mCurText.length() > 1) {
			if (mCurText.charAt(mCurText.length() - 1) == '<'
					| mCurText.charAt(mCurText.length() - 1) == '>') {
				hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 2);
			} else {
				if (mCurText.charAt(mCurText.length() - 1) == '|'
						| mCurText.charAt(mCurText.length() - 1) == '&'
						| mCurText.charAt(mCurText.length() - 1) == '^'
						| mCurText.charAt(mCurText.length() - 1) == '~')
					hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 1);
			}
		} else {
			return "";
		}
	}

	public String onCalculate() {
		String[] parts;
		int val, bitmask;

		if (mCurText.contains("<<")) {
			parts = mCurText.split("<<");
			val = Integer.parseInt(parts[0]);
			bitmask = Integer.parseInt(parts[1]);
			return bitwiseShiftLeft(val, bitmask);
		} else if (mCurText.contains(">>")) {
			parts = mCurText.split(">>");
			val = Integer.parseInt(parts[0]);
			bitmask = Integer.parseInt(parts[1]);
			return bitwiseShiftRight(val, bitmask);
		} else if (mCurText.contains("|")) {
			parts = mCurText.split("|");
			val = Integer.parseInt(parts[0]);
			bitmask = Integer.parseInt(parts[1]);
			return bitwiseOr(val, bitmask);
		} else if (mCurText.contains("&")) {
			parts = mCurText.split("&");
			val = Integer.parseInt(parts[0]);
			bitmask = Integer.parseInt(parts[1]);
			return bitwiseAnd(val, bitmask);
		} else if (mCurText.contains("^")) {
			parts = mCurText.split("^");
			val = Integer.parseInt(parts[0]);
			bitmask = Integer.parseInt(parts[1]);
			return bitwiseXor(val, bitmask);
		} else if (mCurText.contains("~")) {
			val = Integer.parseInt(mCurText.subSequence(1, mCurText.length())
					.toString());
			return bitwiseComplement(val);
		} else {
			return mCurText;
		}
	}

	public String onOperation(String keyText) {
		if (hasOperator) {
			return mCurText;
		} else {
			hasOperator = true;
			return mCurText + keyText;
		}
	}

	public String onNumber(String keyText) {
		return mCurText + keyText;
	}

	public String bitwiseAnd(int val, int bitmask) {
		return Integer.toString(val & bitmask);
	}

	public String bitwiseOr(int val, int bitmask) {
		return Integer.toString(val | bitmask);
	}

	public String bitwiseXor(int val, int bitmask) {
		return Integer.toString(val ^ bitmask);
	}

	public String bitwiseShiftLeft(int val, int bitmask) {
		return Integer.toString(val << bitmask);
	}

	public String bitwiseShiftRight(int val, int bitmask) {
		return Integer.toString(val >> bitmask);
	}

	public String bitwiseComplement(int val) {
		return Integer.toString(~val);
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
