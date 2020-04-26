package org.yura.bilash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.yura.bilash.exception.NegativeNotAllowedException;

public class Calculator {
	private static final String NEGATIVE_SIGN = "-";
	private static final String ESCAPE_CHARACTER = "\\";
	private static final String DEFAULT_DELIMITER = "[,\n]";
	private static final Pattern CUSTOM_DELIMITER_PATTERN = Pattern.compile("//(.*)\\n(.*)");
	private static final Pattern SPECIAL_REGEX_SYMBOLS_PATTERN = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

	public int add(String numbers) {
		if (StringUtils.isEmpty(numbers)) {
			return 0;
		}

		String[] arr = getNumbers(numbers);
		return Arrays
			.stream(arr)
			.mapToInt(Integer::parseInt)
			.filter(el -> el <= 1000)
			.sum();
	}

	private boolean isSpecialRegexSymbol(String symbol) {
		Matcher matcher = SPECIAL_REGEX_SYMBOLS_PATTERN.matcher(symbol);
		return matcher.find();
	}

	private String[] getNumbers(String numbers) {
		return getCustomSeparatedNumbers(numbers)
			.orElseGet(() -> getDefaultSeparatedNumbers(numbers));
	}

	private String[] getDefaultSeparatedNumbers(String numbers) {
		String[] numbersArr = numbers.split(DEFAULT_DELIMITER);
		validateNumbers(numbersArr);
		return numbersArr;
	}

	private Optional<String[]> getCustomSeparatedNumbers(String numbers) {
		Matcher matcher = CUSTOM_DELIMITER_PATTERN.matcher(numbers);
		if (matcher.matches()) {
			String delimiter = matcher.group(1);
			String delimiterEscaped = delimiter;

			if (isSpecialRegexSymbol(delimiter)) {
				delimiterEscaped = ESCAPE_CHARACTER + delimiter;
			}
			String customSeparatedNumbers = matcher.group(2);
			String[] customSeparatedNumbersArr = customSeparatedNumbers.split(delimiterEscaped);
			if (!NEGATIVE_SIGN.equals(delimiter)) {
				validateNumbers(customSeparatedNumbersArr);
			}

			return Optional.of(customSeparatedNumbersArr);
		}

		return Optional.empty();
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
