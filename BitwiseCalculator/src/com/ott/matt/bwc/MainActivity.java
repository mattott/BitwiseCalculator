package com.ott.matt.bwc;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends RoboActivity {
	@InjectView(R.id.operator_view)
	GridView oV;
	@InjectView(R.id.numbers_view)
	GridView nV;
	@InjectView(R.id.special_view)
	GridView sV;
	@InjectView(R.id.radix_spinner)
	Spinner radixSpinner;
	@InjectView(R.id.display_view)
	TextView tV;

	private String mCurText = "";

	// initialize the radix to binary
	private int radix = 2;

	// allows new operators to be called on operands
	private boolean hasOperator = false;

	private OperatorAdapter opAdapter;
	private SpecialAdapter spAdapter;
	private NumbersAdapter numAdapter;
	private ArrayAdapter<CharSequence> radixAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// initialize the arrayadapters
		opAdapter = OperatorAdapter.createFromResource(this,
				R.array.operators_array, R.layout.operation_layout);
		spAdapter = SpecialAdapter.createFromResource(this,
				R.array.special_array, R.layout.special_layout);
		numAdapter = NumbersAdapter.createFromResource(this,
				R.array.numbers_array, R.layout.numbers_layout);
		numAdapter.setRadix(radix);
		radixAdapter = ArrayAdapter.createFromResource(this,
				R.array.radix_array, R.layout.spinner_item);

		// initialize the click and select listeners
		OnItemClickListener opClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String selected = oV.getItemAtPosition(position).toString();
				if (!hasOperator
						&& (mCurText.length() > 1 || selected
								.equalsIgnoreCase("~"))) {
					mCurText = onOperationPressed(selected);
				}
				tV.setText(mCurText);
			}
		};
		OnItemClickListener spClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String selected = sV.getItemAtPosition(position).toString();
				if (selected.startsWith("DEL")) {
					mCurText = onDeletePressed();
				} else if (mCurText.length() > 1) {
					mCurText = onCalculatePressed();
				}
				tV.setText(mCurText);
			}
		};
		OnItemClickListener numClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				String selected = nV.getItemAtPosition(position).toString();
				mCurText = onNumberPressed(selected);
				tV.setText(mCurText);
			}
		};

		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				String selected = radixSpinner.getSelectedItem().toString();

				if (selected.startsWith("BIN")) {
					radix = 2;
				} else if (selected.startsWith("OCT")) {
					radix = 8;
				} else if (selected.startsWith("DEC")) {
					radix = 10;
				} else if (selected.startsWith("HEX")) {
					radix = 16;
				}
				updateDataSet(radix);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				radix = 2;
			}

		};

		// bind the adapters to the views
		oV.setAdapter(opAdapter);
		oV.setOnItemClickListener(opClickListener);
		nV.setAdapter(numAdapter);
		nV.setOnItemClickListener(numClickListener);
		sV.setAdapter(spAdapter);
		sV.setOnItemClickListener(spClickListener);
		radixAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		radixSpinner.setAdapter(radixAdapter);
		radixSpinner.setOnItemSelectedListener(spinnerListener);
	}

	/**
	 * method: onSaveInstanceState(Bundle outState) saves user input
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("curText", mCurText);
	}

	/***
	 * method: updateDataSet(Integer[] newResources)
	 * 
	 * @param newResources
	 *            : the resource array to replace in event of a radix change
	 */
	public void updateDataSet(int new_radix) {
		numAdapter.setRadix(new_radix);
		for (int position = 0; position < numAdapter.getCount(); position++) {
			numAdapter.isEnabled(position);
		}
		mCurText = "";
		// allow a new operator to be called
		hasOperator = false;
		tV.setText(mCurText);
		numAdapter.notifyDataSetChanged();
	}

	/***
	 * method: onDeletePressed()
	 * 
	 * 
	 * @return the string that will be put back into the textview
	 */
	public String onDeletePressed() {

		if (mCurText.length() > 1) {
			char lastChar = mCurText.charAt(mCurText.length() - 1);
			if (lastChar == '<' || lastChar == '>') {
				hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 2);
			} else {
				if (lastChar == '|' || lastChar == '&' || lastChar == '^'
						|| lastChar == '~')
					hasOperator = false;
				return (String) mCurText.subSequence(0, mCurText.length() - 1);
			}
		} else {
			hasOperator = false;
			return "";
		}
	}

	/**
	 * method: onCalculatePressed()
	 * 
	 * @return the answer based on the delimiting operator
	 */
	public String onCalculatePressed() {
		int[] operands;
		boolean beginsWithNumber = String.valueOf(mCurText.charAt(0)).matches(
				"[0-9a-zA-Z]");
		boolean endsWithNumber = String.valueOf(
				mCurText.charAt(mCurText.length() - 1)).matches("[0-9a-zA-Z]");

		if (beginsWithNumber && mCurText.contains("<<") && endsWithNumber) {
			operands = findOperands("<<");
			mCurText = Integer.toString(operands[0] << operands[1], radix);
		} else if (beginsWithNumber && mCurText.contains(">>")
				&& endsWithNumber) {
			operands = findOperands(">>");
			mCurText = Integer.toString(operands[0] >> operands[1], radix);
		} else if (beginsWithNumber && mCurText.contains("|") && endsWithNumber) {
			operands = findOperands("|");
			mCurText = Integer.toString(operands[0] | operands[1], radix);
		} else if (beginsWithNumber && mCurText.contains("&") && endsWithNumber) {
			operands = findOperands("&");
			mCurText = Integer.toString(operands[0] & operands[1], radix);
		} else if (beginsWithNumber && mCurText.contains("^") && endsWithNumber) {
			operands = findOperands("\\^");
			mCurText = Integer.toString(operands[0] ^ operands[1], radix);
		} else if (mCurText.startsWith("~") && endsWithNumber) {
			int val = Integer.parseInt(
					mCurText.subSequence(1, mCurText.length()).toString(),
					radix);
			mCurText = Integer.toString(~val, radix);
			hasOperator = false;
		}
		return mCurText;
	}

	/**
	 * method onOperationPressed(String keyText)
	 * 
	 * @param keyText
	 *            - the value of the operator that was pressed
	 * @return the old text plus the operator
	 */
	public String onOperationPressed(String keyText) {
		if (hasOperator) {
			return mCurText;
		} else {
			hasOperator = true;
			return mCurText + keyText;
		}
	}

	/**
	 * method: onNumberPressed(String keyText)
	 * 
	 */
	public String onNumberPressed(String keyText) {
		return mCurText + keyText;
	}

	public int[] findOperands(String operator) {
		String[] parts = mCurText.split(operator);
		int[] operands = { Integer.parseInt(parts[0], radix),
				Integer.parseInt(parts[1], radix) };
		hasOperator = false;
		return operands;
	}
}
