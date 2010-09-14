package bro1.utils.regex;

import java.util.regex.Pattern;

public class PatternCompiler {

	/**
	 * Compile regular expression with standard flags (case insensitive and
	 * unicode).
	 **/
	public static Pattern compile(String expression) {
		return Pattern.compile(expression, Pattern.CASE_INSENSITIVE
				+ Pattern.UNICODE_CASE);
	}

	public static Pattern compileMultiline(String expression) {
		return Pattern.compile(expression, Pattern.CASE_INSENSITIVE
				+ Pattern.UNICODE_CASE + Pattern.MULTILINE);
	}

}
