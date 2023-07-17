package com.developer.silverheavens.exceptions;

import java.time.LocalDate;

@SuppressWarnings("serial")
public class DateOutOfRangeException extends RuntimeException {
	public DateOutOfRangeException(LocalDate from, LocalDate to) {
		super("Cannot find active prices for range "+from+" - "+to);
	}
}
