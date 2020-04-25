package org.yura.bilash.exception;

import java.util.ArrayList;

public class NegativeNotAllowedException extends RuntimeException{
	public static final String ERROR_MSG_PREFIX = "Negatives not allowed: ";

	public NegativeNotAllowedException(ArrayList<String> message) {
		super(ERROR_MSG_PREFIX + message);
	}
}
