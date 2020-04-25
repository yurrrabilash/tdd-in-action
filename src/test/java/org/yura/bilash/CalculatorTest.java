package org.yura.bilash;

import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.yura.bilash.exception.NegativeNotAllowedException;
import org.yura.bilash.params.provider.LambdaSeparatorValueSource;

@DisplayName("String calculator")
public class CalculatorTest {

	// kind of static but it avoids using reflection
	private static final AtomicInteger ADD_NEGATIVE_NOT_ALLOWED_EXCEPTION_COUNTER = new AtomicInteger(0);
	private static final String [] ADD_NEGATIVE_NOT_ALLOWED_EXCEPTION_NEGATIVES = {
		"[-1]",
		"[-1, -2, -3]",
		"[-1, -2, -4]"
	};

	@ParameterizedTest
	@CsvSource(value = {
		"'' -> 0",
		"1 -> 1",
		"1,2 -> 3",
		"2,4 -> 6"
	}, delimiterString = "->")
	public void addUpToTwoNumbersWhenStringIsValid(String numbers, Integer expected) {
		// arrange
		var calculator = new Calculator();

		// act
		int result = calculator.add(numbers);

		// assert
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@CsvSource(value = {
		"1,2,3 -> 6",
		"10,20,10,10 -> 50"
	}, delimiterString = "->")
	public void addUpToUnknownAmountOfNumbersWhenStringIsValid(String numbers, Integer expected) {
		var calculator = new Calculator();
		int result = calculator.add(numbers);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@LambdaSeparatorValueSource(value = {
		"1,3\n2 -> 6",
		"10\n20\n10,10 -> 50"
	})
	public void addHandleNewLinesBetweenNumbers(String numbers, Integer expected) {
		var calculator = new Calculator();
		int result = calculator.add(numbers);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@LambdaSeparatorValueSource(value = {
		"//;\n1;2 -> 3",
		"//-\n1-2-3 -> 6",
		"//*\n1*2*3*4 -> 10",
		"//|\n1|2|3|4|5 -> 15",
		"//->\n1->2->3->4->5 -> 15"
	})
	public void addSupportDifferentDelimiters(String numbers, Integer expected) {
		var calculator = new Calculator();
		int result = calculator.add(numbers);
		assertEquals(expected, result);
	}

	@ParameterizedTest
	@ValueSource(strings = {
		"//;\n-1;2",
		"//;\n-1;-2;-3",
		"-1,-2,3,-4"
	})
	public void addNegativeNotAllowedException(String numbers) {
		var calculator = new Calculator();
		NegativeNotAllowedException exc = assertThrows(NegativeNotAllowedException.class, () -> {
			calculator.add(numbers);
		});

		int i = ADD_NEGATIVE_NOT_ALLOWED_EXCEPTION_COUNTER.getAndAdd(1);
		String valueSourceNegatives = ADD_NEGATIVE_NOT_ALLOWED_EXCEPTION_NEGATIVES[i];
		assertEquals(NegativeNotAllowedException.ERROR_MSG_PREFIX + valueSourceNegatives, exc.getMessage());
	}
}
