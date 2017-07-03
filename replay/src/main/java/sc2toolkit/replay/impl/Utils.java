/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sc2toolkit.replay.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilities.
 */
public class Utils {

  /** Milliseconds in a minute. */
	public static final long   MS_IN_MIN   = 60L * 1000;
	
	/** Milliseconds in an hour. */
	public static final long   MS_IN_HOUR  = MS_IN_MIN * 60;
	
	/** Milliseconds in a day. */
	public static final long   MS_IN_DAY   = MS_IN_HOUR * 24;
	
	/** Milliseconds in a week. */
	public static final long   MS_IN_WEEK  = MS_IN_DAY * 7;
	
	/**
	 * Milliseconds in a month.<br>
	 * Approximated by 31 days.
	 */
	public static final long   MS_IN_MONTH = MS_IN_DAY * 31;
	
	/**
	 * Milliseconds in a week.<br>
	 * Approximated by 365 days.
	 */
	public static final long   MS_IN_YEAR  = MS_IN_DAY * 365;
  /**
   * Digits used in the hexadecimal representation.
   */
  private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

  /**
   * Returns the minimal capacity of hash-based structures (e.g. {@link HashSet}
   * or {@link HashMap}) calculated from the specified size (number of elements)
   * and the default load factor (which is 0.75).
   *
   * @param size size to calculate the minimal capacity
   * @return the minimal capacity of hash-based structures for the specified
   * size
   */
  public static int hashCapacityForSize(final int size) {
    return size * 4 / 3 + 1;
  }

  /**
   * Returns a new {@link HashSet} initialized with a large enough capacity to
   * allow adding <code>size</code> elements without causing a rehash.
   *
   * @param <E> type of the elements
   * @param size number of elements to accommodate without a rehash
   * @return a new {@link HashMap} with a large enough capacity to accommodate
   * <code>size</code> elements without a rehash
   */
  public static < E> HashSet< E> newHashSet(final int size) {
    return new HashSet<>(hashCapacityForSize(size));
  }

  /**
   * Returns a new {@link HashMap} initialized with a large enough capacity to
   * allow adding <code>size</code> elements without causing a rehash.
   *
   * @param <K> type of the keys
   * @param <V> type of the values
   * @param size number of elements to accommodate without a rehash
   * @return a new {@link HashMap} with a large enough capacity to accommodate
   * <code>size</code> elements without a rehash
   */
  public static < K, V> HashMap< K, V> newHashMap(final int size) {
    return new HashMap<>(hashCapacityForSize(size));
  }

  /**
   * Returns a new, independent {@link Set} which contains all the specified
   * elements.
   *
   * @param <E> type of the elements
   * @param elements elements to be added to the new set
   * @return an independent {@link Set} which does not rely on the
   * <code>elements</code> array
   */
  public static < E> Set< E> asNewSet(@SuppressWarnings("unchecked") final E... elements) {
    final Set< E> set = newHashSet(elements.length);
    Collections.addAll(set, elements);
    return set;
  }

  /**
   * Converts the specified data to hex string.
   *
   * @param data data to be converted
   *
   * @return the specified data converted to hex string
   *
   * @see #toHexString(byte[], int, int)
   */
  public static String toHexString(final byte[] data) {
    return toHexString(data, 0, data.length);
  }

  /**
   * Converts the specified data to hex string.
   *
   * @param data data to be converted
   * @param offset offset of the first byte to be convert
   * @param length number of bytes to be converted
   *
   * @return the specified data converted to hex string
   *
   * @see #toHexString(byte[])
   */
  public static String toHexString(final byte[] data, int offset, int length) {
    final StringBuilder hexBuilder = new StringBuilder(length * 2);

    for (length += offset; offset < length; offset++) {
      final byte b = data[offset];
      hexBuilder.append(HEX_DIGITS[(b & 0xff) >> 4]).append(HEX_DIGITS[b & 0x0f]);
    }

    return hexBuilder.toString();
  }

  /**
   * Strips off leading zero characters from the specified string.
   * <p>
   * Example: <code>"\0\0S2"</code> => <code>"S2"</code>
   * </p>
   *
   * @param s string to strip off leading zero characters from
   * @return a string cleaned up from leading zeros
   */
  public static String stripOffLeadingZeros(final String s) {
    final int lastNullIdx = s.lastIndexOf('\0');
    return lastNullIdx < 0 ? s : s.substring(lastNullIdx + 1);
  }

  /**
   * Reads a full byte array from the specified input stream.
   *
   * <p>
   * The problem with {@link InputStream#read(byte[])} is that it does not
   * guarantee that the passed array will be "fully populated" even if there are
   * enough data in the input stream (the returned number of bytes might be
   * smaller than the array length). This method does guarantee it.
   * </p>
   *
   * @param in input stream to read from
   * @param buffer array to be read
   * @return the buffer
   * @throws IOException if reading from the stream throws {@link IOException}
   */
  public static byte[] readFully(final InputStream in, final byte[] buffer) throws IOException {
    final int size = buffer.length;

    for (int remaining = size; remaining > 0;) {
      remaining -= in.read(buffer, size - remaining, remaining);
    }

    return buffer;
  }
  
	/**
	 * Strips off markup formatting from the specified string.
	 * <p>
	 * Example: <code>"[RA]&lt;sp/&gt;SvnthSyn"</code> => <code>"[RA]SvnthSyn"</code>
	 * </p>
	 * 
	 * @param s string to strip off markup formatting from
	 * @return a string cleaned up from markup formatting
	 */
	public static String stripOffMarkupFormatting( String s ) {
		int start = 0;
		
		while ( ( start = s.indexOf( '<', start ) ) >= 0 )
			s = s.substring( 0, start ) + s.substring( s.indexOf( '>', start ) + 1 );
			
		return s;
	}
	  
	/**
	 * Converts a const name to a normal, human readable format.
	 * 
	 * <p>
	 * <b>Converter rules:</b>
	 * </p>
	 * <ul>
	 * <li>The output will start with an upper-cased letter if input starts with a letter (regardless of word capitalization).
	 * <li>Word boundaries are defined by underscore characters (<code>'_'</code>). Underscores are replaced with spaces.
	 * <li>First letters of words will be capitalized based on the <code>capitalizeWords</code> parameter.
	 * <li>The rest of the characters will be lower-cased.
	 * </ul>
	 * 
	 * <p>
	 * Examples when capitalizing words:
	 * </p>
	 * <ul>
	 * <li><code>"DETAILS"</code> = &gt; <code>"Details"</code></p>
	 * <li><code>"INIT_DATA"</code> = &gt; <code>"Init Data"</code></p>
	 * <li><code>"sT_raN_Ge"</code> = &gt; <code>"St Ran Ge"</code></p>
	 * </ul>
	 * 
	 * <p>
	 * Examples when not capitalizing words:
	 * </p>
	 * <ul>
	 * <li><code>"DETAILS"</code> = &gt; <code>"Details"</code></p>
	 * <li><code>"INIT_DATA"</code> = &gt; <code>"Init data"</code></p>
	 * <li><code>"sT_raN_Ge"</code> = &gt; <code>"St ran ge"</code></p>
	 * </ul>
	 * 
	 * @param text text to be converted
	 * @param capitalizeWords if true, all words will start with capital letters
	 * @return the converted text
	 */
	public static String constNameToNormal( final String text, final boolean capitalizeWords ) {
		final StringBuilder sb = new StringBuilder( text.length() );
		
		boolean nextCapital = true;
		for ( final char ch : text.toCharArray() ) {
			if ( ch == '_' ) {
				sb.append( ' ' );
				nextCapital = capitalizeWords;
			} else if ( nextCapital ) {
				sb.append( ch );
				nextCapital = false;
			} else
				sb.append( Character.toLowerCase( ch ) );
		}
		
		return sb.toString();
	};

	/**
	 * Converts the specified data to base64 encoded string.
	 * 
	 * @param data data to be converted
	 * 		   
	 * @return the specified data converted to base64 encoded string
	 */
	public static String toBase64String( final byte[] data ) {
		return javax.xml.bind.DatatypeConverter.printBase64Binary( data );
	}  
}
