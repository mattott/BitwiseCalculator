package com.ott.matt.bwc;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectView;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;

public class MainActivity extends RoboFragmentActivity {

	@InjectView(R.id.delete_button)
	View deleteBtn;
	@InjectView(R.id.display_view)
	TextView displayView;
	@InjectView(R.id.number_pager)
	ViewPager nPager;

	private Symbols mSymbols = new Symbols();
	private String mCurText = "";
	private int mRadix = 10;
	private boolean hasOperator = false;
	private OnClickListener mListener;

	private static final String STATE_CURRENT_VIEW = "state-current-view";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				String selectedButtonText = ((Button) v).getText().toString();
				mCurText += selectedButtonText;
				displayView.setText(mCurText);
			}
		};

		if (nPager != null) {
			nPager.setAdapter(new NumberPagerAdapter(nPager));
		} else {
			final TypedArray num_buttons = getResources().obtainTypedArray(
					R.array.dec_buttons);
			for (int i = 0; i < num_buttons.length(); i++) {
				setOnClickListener(null, num_buttons.getResourceId(i, 0));
			}
			num_buttons.recycle();
		}

		if (nPager != null) {
			nPager.setCurrentItem(savedInstanceState == null ? 0
					: savedInstanceState.getInt(STATE_CURRENT_VIEW, 0));
		}

		nPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				String error = "PageSelected: " + Integer.toString(position);
				Log.d("Kook", error);
				switch (position) {
				case (0):
					mRadix = 10;
					break;
				case (1):
					mRadix = 16;
					break;
				}
				String error1 = "Radix: " + Integer.toString(mRadix);
				Log.d("Kook", error1);
			}
		});

		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		int windowWidth = metrics.widthPixels;
		int windowHeight = metrics.heightPixels;
		int orientation = getResources().getConfiguration().orientation;

		nPager.setLayoutParams(new LinearLayout.LayoutParams(
				windowWidth * 3 / 5, windowHeight * 15 / 25));
		((View) deleteBtn.getParent())
				.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, windowHeight * 4 / 25));
		displayView.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, windowHeight / 5));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	}

	public void setOnClickListener(View root, int id) {
		final View target = root != null ? root.findViewById(id)
				: findViewById(id);
		target.setOnClickListener(mListener);
	}

	public void clickedOperator(View v) {
		String operator = ((Button) v).getText().toString();
		mCurText += operator;
		displayView.setText(mCurText);
	}

	public void onDelete(View v) {

		if (mCurText.length() > 1) {
			char lastChar = mCurText.charAt(mCurText.length() - 1);
			if (lastChar == '<' || lastChar == '>') {
				hasOperator = false;
				mCurText = (String) mCurText.subSequence(0,
						mCurText.length() - 2);
			} else {
				if (!Character.isDigit(lastChar)
						|| !Character.isLetter(lastChar))
					hasOperator = false;
				mCurText = (String) mCurText.subSequence(0,
						mCurText.length() - 1);
			}
		} else {
			hasOperator = false;
			mCurText = "";
		}
		displayView.setText(mCurText);
	}

	public void onEquate(View v) {
		if (mCurText.length() < 1)
			return;
		String result = "";
		String prefix = "";
		char operator = ' ';
		//int last = 0;
		int end = mCurText.length();
		for (int i = 0; i < end; i++) {
			if (isOperator(mCurText.charAt(i))) {
				if (i == 0) {
					operator = mCurText.charAt(i);
					result = operator + mCurText.substring(i, end);
					Log.d("i==0", "prefix: " + prefix);
				} else if (i != 0) {
					try {
						prefix = evaluateExpression(mCurText.substring(0, i),
								operator);
						operator = mCurText.charAt(i);
						Log.d("i != 0, try.", "prefix: " + prefix + ", i: " + i);
					} catch (SyntaxException e) {
						result = "Error";
					}
					result = prefix + mCurText.substring(i, end);
					// last = i;
					Log.d("i != 0", "result: " + result + ", i: " + i);
				}

			} else if (i == end - 1)
				try {
					Log.d("i == end - 1", "expression: " + result);
					result = evaluateExpression(result, operator);
					// prefix + mCurText.substring(last, end), operator);
					Log.d("i == end - 1", "result: " + result + ", i: " + i);
				} catch (SyntaxException e) {
					result = "Error";
				}
		}

		displayView.setText(result);
		mCurText = "";
	}

	public boolean isOperator(char input) {
		if (Character.isDigit(input) || Character.isLetter(input))
			return false;
		else
			return true;
	}

	public boolean isBitOperator(char input) {
		for (int i = 0; i < bitwise_operators.length; i++) {
			if (input == bitwise_operators[i])
				return true;
		}
		return false;
	}

	public String evaluateExpression(String input, char operator)
			throws SyntaxException {
		Log.d("evaluateExpression", "input: " + input);
		if (isBitOperator(operator))
			return evaluateBitExpression(input, operator);
		else
			return convertToRadix(mSymbols.eval(convertToDecimal(input,
					operator)));
	}

	public String evaluateBitExpression(String input, char operator) {
		String parts[] = { "", "" };
		String result = "";
		switch (operator) {
		case '<':
			parts = input.split("<<");
			result = Integer.toString(
					Integer.parseInt(parts[0], mRadix) << Integer.parseInt(
							parts[1], mRadix), mRadix);
			break;
		case '>':
			parts = input.split(">>");
			result = Integer.toString(
					Integer.parseInt(parts[0], mRadix) >> Integer.parseInt(
							parts[1], mRadix), mRadix);
			break;
		case '|':
			parts = input.split("|");
			result = Integer.toString(Integer.parseInt(parts[0], mRadix)
					| Integer.parseInt(parts[1], mRadix), mRadix);
			break;
		case '&':
			parts = input.split("\\&");
			result = Integer.toString(Integer.parseInt(parts[0], mRadix)
					& Integer.parseInt(parts[1], mRadix), mRadix);
			break;
		case '^':
			parts = input.split("\\^");
			result = Integer.toString(Integer.parseInt(parts[0], mRadix)
					^ Integer.parseInt(parts[1], mRadix), mRadix);
			break;
		case '~':
			parts = input.split("\\~");
			result = Integer.toString(~Integer.parseInt(parts[1], mRadix),
					mRadix);
			break;
		default:
			Log.d("Kook5:", "default");
			break;
		}
		return result;
	}

	public String convertToDecimal(String input, char operator) {
		if (mRadix == 10 || operator == ' ')
			return Integer.toString(Integer.parseInt(input, mRadix), mRadix);
		Log.d("convertToDecimal", "Input1: " + input);

		String parts[] = input.split(Character.toString(operator));
		char operators[] = new char[2];
		// int operands[] = new int[10];
		String result = "";
		// int p = 0;
		// int o = 0;
		boolean firstCharOperator = (isOperator(input.charAt(0))) ? true
				: false;
		if (firstCharOperator) {
			operators[0] = input.charAt(0);
			// o += 1;
			input = input.substring(1, input.length());
			Log.d("convertToDecimal", "Input3: " + input);
		}

		/**
		 * for (int i = 0; i < input.length(); i++) { if
		 * (isOperator(input.charAt(i))) { parts[p] = input.substring(0, i);
		 * operators[o] = input.charAt(i); p += 1; o += 1; input =
		 * input.substring(i + 1, input.length()); i = 0;
		 * Log.d("convertToDecimal", "Input4: " + input);
		 * 
		 * } Log.d("convertToDecimal", "EOS:" + Integer.toString(i));
		 * Log.d("convertToDecimal", "Input: " + input + " Input length: " +
		 * input.length()); } Log.d("convertToDecimal", "Input: " + input);
		 * parts[p] = input; for (int i = 0; i <= p; i++) { if (operators[0] ==
		 * "~") { operands[1] = Integer.parseInt(parts[1], mRadix); break; }
		 * operands[i] = Integer.parseInt(parts[i], mRadix);
		 * Log.d("convertToDecimal", "Operands" +
		 * Integer.toString(operands[i])); }
		 */

		if (firstCharOperator)
			result += operators[0];
		result += parts[0] + operator + parts[1];
		/**
		 * for (int i = firstCharOperator ? 1 : 0; i <= p; i++) { result +=
		 * Integer.toString(operands[i]); if (i <= o) result += operators[i];
		 * 
		 * }
		 **/
		Log.d("convertToDecimal", "result: " + result);
		return result;
	}

	public String convertToRadix(double input) {
		if (mRadix == 10)
			return Double.toString(input);
		int fixed = (int) input;
		String result = Integer.toString(fixed, mRadix);
		Log.d("convertToRadix", "result: " + result);
		return result;
	}

	class NumberPagerAdapter extends PagerAdapter {
		private View mNumber_dec;
		private View mNumber_hex;

		public NumberPagerAdapter(ViewPager parent) {
			final LayoutInflater inflater = LayoutInflater.from(parent
					.getContext());
			final View number_dec = inflater.inflate(R.layout.dec_layout,
					parent, false);
			final View number_hex = inflater.inflate(R.layout.hex_layout,
					parent, false);

			mNumber_dec = number_dec;
			mNumber_hex = number_hex;

			final Resources res = getResources();

			final TypedArray hex_buttons = res
					.obtainTypedArray(R.array.hex_buttons);
			for (int i = 0; i < hex_buttons.length(); i++) {
				setOnClickListener(number_hex, hex_buttons.getResourceId(i, 0));
			}
			hex_buttons.recycle();

			final TypedArray dec_buttons = res
					.obtainTypedArray(R.array.dec_buttons);
			for (int i = 0; i < dec_buttons.length(); i++) {
				setOnClickListener(number_dec, dec_buttons.getResourceId(i, 0));
			}
			dec_buttons.recycle();

		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public void startUpdate(View container) {
		}

		@Override
		public Object instantiateItem(View container, int position) {
			final View page;
			switch (position) {
			case (0):
				page = mNumber_dec;
				break;
			case (1):
				page = mNumber_hex;
				break;
			default:
				page = mNumber_dec;
				break;
			}
			((ViewGroup) container).addView(page);

			return page;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewGroup) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}
	}

	private char bitwise_operators[] = { '>', '<', '~', '^', '&', '|' };
}
