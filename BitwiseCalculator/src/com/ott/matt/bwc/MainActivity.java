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
	TextView tV;
	// initialize the radix to binary
	int radix = 2;
	// allows new operators to be called on operands
	boolean hasOperator = false;
	Integer[] buttonResources;
	private ArrayAdapter<String> opAdapter;
	private ArrayAdapter<String> numAdapter;
	// this must be initialized outside of onCreate in order to update
	private ArrayList<String> opList = new ArrayList<String>();
	private ArrayList<String> numList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// inflate the view
		setContentView(R.layout.activity_main);
		// initialize the grid and text views
		GridView oV = (GridView) findViewById(R.id.operator_view);
		GridView nV = (GridView) findViewById(R.id.numbers_view);
		tV = (TextView) findViewById(R.id.display_view);
		// keypad numbers depends on radix
		switch (radix) {
		case 2:
			buttonResources = binResources;
			break;
		case 8:
			buttonResources = octResources;
			break;
		case 16:
			buttonResources = hexResources;
			break;
		default:
			buttonResources = decResources;
		}
		// add the operator strings to the arraylist
		for (int i = 0; i < operators.length; i++) {
			opList.add(getString(operators[i]));
		}
		// add the number keys to the arraylist
		for (int i = 0; i < buttonResources.length; i++) {
			numList.add(getString(buttonResources[i]));
		}
		// bind the arraylist to the arrayadapter
		opAdapter = new ArrayAdapter<String>(this, R.layout.button_layout,
				opList);
		numAdapter = new ArrayAdapter<String>(this, R.layout.button_layout,
				numList);
		// clicklistener calls operator methods
		OnItemClickListener opClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				if (operators[position] == R.string.DELETE) {
					mCurText = onDeletePressed();
				} else if (operators[position] == R.string.CALCULATE
						&& mCurText.length() > 1) {
					mCurText = onCalculatePressed();
				} else if (!hasOperator) {
					mCurText = onOperationPressed(opList.get(position));
				}
				tV.setText(mCurText);
			}
		};
		OnItemClickListener numClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				mCurText = onNumberPressed(numList.get(position));
				tV.setText(mCurText);
			}
		};
		// bind keypad adapter to gridview
		oV.setAdapter(opAdapter);
		oV.setOnItemClickListener(opClickListener);
		nV.setAdapter(numAdapter);
		nV.setOnItemClickListener(numClickListener);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState); // takes care of all of the default
												// saves
		outState.putString("curText", mCurText);
	}

	// update the arraylist and refresh gridview on base change
	public void updateDataSet(Integer[] newResources) {
		numList.clear();
		buttonResources = newResources;
		// add the new base numbers
		for (int j = 0; j < newResources.length; j++) {
			numList.add(getString(newResources[j]));
		}
		mCurText = "";
		// allow a new operator to be called
		hasOperator = false;
		tV.setText(mCurText);
		numAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// options menu allows user to change the number base
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.bin:
			if (item.isChecked())
				item.setChecked(false);
			else {
				item.setChecked(true);
				radix = 2;
				updateDataSet(binResources);
			}
			break;
		case R.id.oct:
			if (item.isChecked())
				break;
			else {
				item.setChecked(true);
				radix = 8;
				updateDataSet(octResources);
			}
			break;
		case R.id.dec:
			if (item.isChecked())
				break;
			else {
				item.setChecked(true);
				radix = 10;
				updateDataSet(decResources);
			}
			break;
		case R.id.hex:
			if (item.isChecked())
				break;
			else {
				item.setChecked(true);
				radix = 16;
				updateDataSet(hexResources);
			}
			break;
		}

		return super.onOptionsItemSelected(item);

	}

	// deletes the last keypad button
	public String onDeletePressed() {
		if (mCurText.length() > 1) {
			if (mCurText.charAt(mCurText.length() - 1) == '<'
					|| mCurText.charAt(mCurText.length() - 1) == '>') {
				hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 2);
			} else {
				if (mCurText.charAt(mCurText.length() - 1) == '|'
						|| mCurText.charAt(mCurText.length() - 1) == '&'
						|| mCurText.charAt(mCurText.length() - 1) == '^'
						|| mCurText.charAt(mCurText.length() - 1) == '~')
					hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 1);
			}
		} else {
			hasOperator = false;
			return "";
		}
	}

	// calls the bitwise operation(s) on the operand(s)
	public String onCalculatePressed() {
		String[] parts;
		int val, bitmask;
		boolean beginsWithNumber = String.valueOf(mCurText.charAt(0)).matches(
				"[0-9a-zA-Z]");
		boolean endsWithNumber = String.valueOf(
				mCurText.charAt(mCurText.length() - 1)).matches("[0-9a-zA-Z]");

		if (beginsWithNumber && mCurText.contains("<<") && endsWithNumber) {
			parts = mCurText.split("<<");
			val = Integer.parseInt(parts[0], radix);
			bitmask = Integer.parseInt(parts[1], radix);
			bitwiseShiftLeft(val, bitmask);
			hasOperator = false;
		} else if (beginsWithNumber && mCurText.contains(">>")
				&& endsWithNumber) {
			parts = mCurText.split(">>");
			val = Integer.parseInt(parts[0], radix);
			bitmask = Integer.parseInt(parts[1], radix);
			bitwiseShiftRight(val, bitmask);
			hasOperator = false;
		} else if (beginsWithNumber && mCurText.contains("|") && endsWithNumber) {
			parts = mCurText.split("\\|");
			val = Integer.parseInt(parts[0], radix);
			bitmask = Integer.parseInt(parts[1], radix);
			bitwiseOr(val, bitmask);
			hasOperator = false;
		} else if (beginsWithNumber && mCurText.contains("&") && endsWithNumber) {
			parts = mCurText.split("&");
			val = Integer.parseInt(parts[0], radix);
			bitmask = Integer.parseInt(parts[1], radix);
			bitwiseAnd(val, bitmask);
			hasOperator = false;
		} else if (beginsWithNumber && mCurText.contains("^") && endsWithNumber) {
			parts = mCurText.split("\\^");
			val = Integer.parseInt(parts[0], radix);
			bitmask = Integer.parseInt(parts[1], radix);
			bitwiseXor(val, bitmask);
			hasOperator = false;
		} else if (mCurText.startsWith("~") && endsWithNumber) {
			val = Integer.parseInt(mCurText.subSequence(1, mCurText.length())
					.toString(), radix);
			bitwiseComplement(val);
			hasOperator = false;
		}
		return mCurText;
	}

	// adds the operation text to the display
	public String onOperationPressed(String keyText) {
		if (hasOperator) {
			return mCurText;
		} else {
			hasOperator = true;
			return mCurText + keyText;
		}
	}

	public String onNumberPressed(String keyText) {
		return mCurText + keyText;
	}

	public void bitwiseAnd(int val, int bitmask) {
		mCurText = Integer.toString(val & bitmask, radix);
	}

	public void bitwiseOr(int val, int bitmask) {
		mCurText = Integer.toString(val | bitmask, radix);
	}

	public void bitwiseXor(int val, int bitmask) {
		mCurText = Integer.toString(val ^ bitmask, radix);
	}

	public void bitwiseShiftLeft(int val, int bitmask) {
		mCurText = Integer.toString(val << bitmask, radix);
	}

	public void bitwiseShiftRight(int val, int bitmask) {
		mCurText = Integer.toString(val >> bitmask, radix);
	}

	public void bitwiseComplement(int val) {
		mCurText = Integer.toString(~val, radix);
	}

	// xml resources for numbers and operators located in res/values/strings.xml
	private final Integer[] operators = { R.string.SHIFT_LEFT,
			R.string.SHIFT_RIGHT, R.string.CALCULATE, R.string.DELETE,
			R.string.OR, R.string.AND, R.string.XOR, R.string.NOT };
	private final Integer[] binResources = { R.string.ZERO, R.string.ONE };
	private final Integer[] octResources = { R.string.ZERO, R.string.ONE,
			R.string.TWO, R.string.THREE, R.string.FOUR, R.string.FIVE,
			R.string.SIX, R.string.SEVEN, };
	private final Integer[] decResources = { R.string.ZERO, R.string.ONE,
			R.string.TWO, R.string.THREE, R.string.FOUR, R.string.FIVE,
			R.string.SIX, R.string.SEVEN, R.string.EIGHT, R.string.NINE };
	private final Integer[] hexResources = { R.string.ZERO, R.string.ONE,
			R.string.TWO, R.string.THREE, R.string.FOUR, R.string.FIVE,
			R.string.SIX, R.string.SEVEN, R.string.EIGHT, R.string.NINE,
			R.string.TEN, R.string.ELEVEN, R.string.TWELVE, R.string.THIRTEEN,
			R.string.FOURTEEN, R.string.FIFTEEN };
}
