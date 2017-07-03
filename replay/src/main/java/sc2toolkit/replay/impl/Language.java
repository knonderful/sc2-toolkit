/*
 * Project Scelight
 * 
 * Copyright (c) 2013 Andras Belicza <iczaaa@gmail.com>
 * 
 * This software is the property of Andras Belicza.
 * Copying, modifying, distributing, refactoring without the author's permission
 * is prohibited and protected by Law.
 */
package sc2toolkit.replay.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Language and locale specific utilities.
 *
 * @author Andras Belicza
 */
public class Language {

  private static final int MAX_FRACTION_DIGITS = 5;
  private static final String DATE = "yyyy-MM-dd";
  private static final String TIME = "HH:mm:ss";

  /**
   * Date format.
   */
  private final DateFormat dateFormat = new SimpleDateFormat(DATE);

  /**
   * Time format.
   */
  private final DateFormat timeFormat = new SimpleDateFormat(TIME);

  /**
   * Date+time format.
   */
  private final DateFormat dateTimeFormat = new SimpleDateFormat(DATE + " " + TIME);

  /**
   * Date+time format which includes milliseconds.
   */
  private final DateFormat dateTimeFormatMs = new SimpleDateFormat(DATE + " " + TIME + ".S");
  ;
	
	/** Decimal format used to format numbers. */
	private final DecimalFormat decimalFormat;

  /**
   * Decimal formats used to format numbers with different fraction digits. The
   * decimal format instance at index <code>i</code> will use a number of
   * fraction digits <code>i</code>.
   */
  private final DecimalFormat[] fractionDecimalFormats;

  /**
   * Creates a new {@link Language}.
   */
  public Language() {
    decimalFormat = new DecimalFormat();
    fractionDecimalFormats = new DecimalFormat[MAX_FRACTION_DIGITS + 1]; // +1 for zero digits
    for (int i = 0; i < fractionDecimalFormats.length; i++) {
      fractionDecimalFormats[i] = (DecimalFormat) decimalFormat.clone();
      fractionDecimalFormats[i].setMinimumFractionDigits(i);
      fractionDecimalFormats[i].setMaximumFractionDigits(i);
    }
  }

  public String formatDate(final Date date) {
    synchronized (dateFormat) {
      try {
        return dateFormat.format(date);
      } catch (final Exception e) {
        return null;
      }
    }
  }

  public String formatTime(final Date time) {
    synchronized (timeFormat) {
      try {
        return timeFormat.format(time);
      } catch (final Exception e) {
        return null;
      }
    }
  }

  public String formatDateTime(final Date dateTime, final boolean ms) {
    return ms ? formatDateTimeMs(dateTime) : formatDateTime(dateTime);
  }

  public String formatDateTime(final Date dateTime) {
    synchronized (dateTimeFormat) {
      try {
        return dateTimeFormat.format(dateTime);
      } catch (final Exception e) {
        return null;
      }
    }
  }

  public String formatDateTimeMs(final Date dateTime) {
    synchronized (dateTimeFormatMs) {
      try {
        return dateTimeFormatMs.format(dateTime);
      } catch (final Exception e) {
        return null;
      }
    }
  }

  public String formatNumber(final long n) {
    synchronized (decimalFormat) {
      return decimalFormat.format(n);
    }
  }

  public String formatNumber(final double n, int fractionDigits) {
    if (fractionDigits < 0) {
      throw new IllegalArgumentException("Fraction digits cannot be negative!");
    }
    if (fractionDigits > MAX_FRACTION_DIGITS) {
      throw new IllegalArgumentException("Fraction digits cannot be greater than " + MAX_FRACTION_DIGITS + " (provided: " + fractionDigits + ")!");
    }

    synchronized (fractionDecimalFormats[fractionDigits]) {
      return fractionDecimalFormats[fractionDigits].format(n);
    }
  }
}
