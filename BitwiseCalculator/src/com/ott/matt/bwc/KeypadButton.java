package com.ott.matt.bwc;

public enum KeypadButton {
	BACKSPACE("DELETE", KeypadButtonCategory.CLEAR),
	SHIFT_LEFT("<<", KeypadButtonCategory.OPERATOR),
	SHIFT_RIGHT(">>", KeypadButtonCategory.OPERATOR),
	XOR("^", KeypadButtonCategory.OPERATOR),
	OR("|", KeypadButtonCategory.OPERATOR),
	AND("&", KeypadButtonCategory.OPERATOR),
	NOT("~", KeypadButtonCategory.OPERATOR),
	CALCULATE("=", KeypadButtonCategory.RESULT),
	DUMMY("", KeypadButtonCategory.DUMMY),
	ZERO("0", KeypadButtonCategory.NUMBER),
	ONE("1", KeypadButtonCategory.NUMBER),
	TWO("2", KeypadButtonCategory.NUMBER),
	THREE("3", KeypadButtonCategory.NUMBER),
	FOUR("4", KeypadButtonCategory.NUMBER),
	FIVE("5", KeypadButtonCategory.NUMBER),
	SIX("6", KeypadButtonCategory.NUMBER),
	SEVEN("7", KeypadButtonCategory.NUMBER),
	EIGHT("8", KeypadButtonCategory.NUMBER),
	NINE("9", KeypadButtonCategory.NUMBER),
	TEN("A", KeypadButtonCategory.NUMBER),
	ELEVEN("B", KeypadButtonCategory.NUMBER),
	TWELVE("C", KeypadButtonCategory.NUMBER),
	THIRTEEN("D", KeypadButtonCategory.NUMBER),
	FOURTEEN("E", KeypadButtonCategory.NUMBER),
	FIFTEEN("F", KeypadButtonCategory.NUMBER)
	;
	
	
	CharSequence mText; // display the text
	KeypadButtonCategory mCategory;
	
	KeypadButton(CharSequence text, KeypadButtonCategory category) {
		mText = text;
		mCategory = category;
	}
	
	public CharSequence getText() {
		return mText;
	}
}
