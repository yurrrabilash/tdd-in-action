package org.yura.bilash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.yura.bilash.exception.NegativeNotAllowedException;

public class Calculator {
	private static final List<String> DEFAULT_DELIMITERS = Arrays.asList(new String[]{",", "\n"});
	private static final String ESCAPE_CHARACTER = "\\";
	private static final Pattern CALCULATOR_EXPRESSION_PATTERN = Pattern.compile("(//(.*)\\n)?(.{2,})");
	private static final Pattern SPECIAL_REGEX_SYMBOLS_PATTERN = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

	public int add(String numbersToken) {
		if (StringUtils.isEmpty(numbersToken)) {
			return 0;
		}

		List<String> delimiters = getDelimiters(numbersToken);
		String numbers = getNumbersStr(numbersToken);
		String[] arr = getNumbers(numbers, delimiters);
		return Arrays
			.stream(arr)
			.mapToInt(Integer::parseInt)
			.filter(el -> el <= 1000)
			.sum();
	}

	private String getNumbersStr(String numbersToken) {
		Matcher matcher = CALCULATOR_EXPRESSION_PATTERN.matcher(numbersToken);
		if (matcher.matches()) {
			final int numbersGroup = 3;
			return matcher.group(numbersGroup);
		}

		return numbersToken;
	}

	private List<String> getDelimiters(String numbersToken) {
		Matcher matcher = CALCULATOR_EXPRESSION_PATTERN.matcher(numbersToken);
		if (matcher.matches()) {
			final int delimitersGroup = 2;
			String delimitersStr = matcher.group(delimitersGroup);
			if (delimitersStr == null) {
				return DEFAULT_DELIMITERS;
			} else if (delimitersStr.length() == 1) {
				return Collections.singletonList(escapeSpecSymbols(delimitersStr));
			}

			char[] delimitersStrChars = delimitersStr.toCharArray();
			ArrayList<String> delimiters = new ArrayList<>();
			StringBuilder currentDelimiter = new StringBuilder();
			for (char ch : delimitersStrChars) {
				if (ch == '[') {
					currentDelimiter.setLength(0);
					continue;
				} else if (ch == ']') {
					delimiters.add(currentDelimiter.toString());
				}

				String escapedChStr = escapeSpecSymbols("" + ch);
				currentDelimiter.append(escapedChStr);
			}

			return delimiters;
		}

		return DEFAULT_DELIMITERS;
	}

	private boolean isSpecialRegexSymbol(String symbol) {
		Matcher matcher = SPECIAL_REGEX_SYMBOLS_PATTERN.matcher(symbol);
		return matcher.find();
	}

	private String[] getNumbers(String numbers, List<String> delimiters) {
		String delimitersPattern = getDelimitersPattern(delimiters);
		String[] numbersArr = numbers.split(delimitersPattern);
		validateNumbers(numbersArr);

		return numbersArr;
	}

	private String getDelimitersPattern(List<String> delimiters) {
		StringBuilder delimitersPattern = new StringBuilder();
		for (int i = 0; i < delimiters.size(); i++) {
			String delimiter = delimiters.get(i);
			delimitersPattern.append(delimiter);

			if (i != (delimiters.size() - 1)) {
				delimitersPattern.append("|");
			}
		}

		return delimitersPattern.toString();
	}

	private String escapeSpecSymbols(String delimiter) {
		StringBuilder escapedDelimiter = new StringBuilder();
		char[] delimiterChars = delimiter.toCharArray();
		for (char delimiterChar : delimiterChars) {
			if (isSpecialRegexSymbol(delimiter)) {
				escapedDelimiter.append(ESCAPE_CHARACTER).append(delimiterChar);
			} else {
				escapedDelimiter.append(delimiterChar);
			}
		}

		return escapedDelimiter.toString();
	}

	private void validateNumbers(String[] numbersStr) {
		ArrayList<String> negatives = new ArrayList<>();
		for (String numberStr : numbersStr) {
			int number = Integer.parseInt(numberStr);
			if (number < 0) {
				negatives.add(numberStr);
			}
		}

		if (!negatives.isEmpty()) {
			throw new NegativeNotAllowedException(negatives);
		}
	}
}
