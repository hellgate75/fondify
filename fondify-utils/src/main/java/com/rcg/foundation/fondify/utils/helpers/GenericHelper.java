package com.rcg.foundation.fondify.utils.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TORELFA (Fabrizio Torelli - fabrizio.torelli@ie.verizon.com)
 */
public class GenericHelper {

	public static final Logger log = LoggerFactory.getLogger(GenericHelper.class);

	private GenericHelper() {
		// No Argument Constructor - Should not be invoked
	}
	
	/**
	 * Remove tailing zeros, creating a valid number format
	 * @param in
	 * @return
	 */
	public static final String removeTrailingZeros(String in) {
		String out = in;
		while (out.endsWith("0") && out.length() > 1) {
			out = out.substring(0, out.length()-2);
		}
		return out;
	}
	
	public static final String initCapBeanName(String name) {
		if (name == null || name.isEmpty())
			return name;
		if (name.length() <= 1)
			return name.toLowerCase();
		return name.charAt(0) + name.substring(1);
	}

	/**
	 * Convert a stack trace to representing string listing all stack of exceptions
	 * @param stackTraceElements
	 * @return
	 */
	public static final String convertStackTrace(StackTraceElement[] stackTraceElements) {
		String stackTraceStr = "";
		if (stackTraceElements==null || stackTraceElements.length==0)
			return stackTraceStr;
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			stackTraceStr += stackTraceElement.toString() + "\n";
		}
		return stackTraceStr;
	}
	
	/**
	 * Remove heading zeros, creating a valid number format
	 * @param in
	 * @return
	 */
	public static final String removeHeadingZeros(String s) {
		while (s != null && s.trim().length() > 1 && s.trim().startsWith("0") ) {
			s = s.trim().substring(1);
		}
		return s;
	}
	
	/**
	 * Sanitize Unicode characters from strings
	 * @param s Input String
	 * @return Unicode characters sanitized output string
	 */
	public static final String fixUnicodeCharacters(String s) {
//		return s.replaceAll("[^\\x00-\\x7F]", "").replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").replaceAll("\\p{C}", "").replaceAll("\\p{Cf}", "").replaceAll("\\", "");
		return s.replaceAll("\\p{C}", "").replaceAll("\\p{Cf}", "");
	}
	
	/**
	 * Method that securely parse a String to integer value, and in case of not null logger, logs eventually the error
	 * @param s Input String
	 * @return (int) Parsed integer value, or default 0 value in case of error.
	 */
	public static final int parseInt(String s) {
		int out = 0;
		// remove heading zeros from input string because they don't allow input string to be 
		// parsed as integer value (prevent unwanted errors (input file data format issues)
		try {
			s = removeHeadingZeros(fixUnicodeCharacters(s));
		} catch (Exception e) {
				LoggerHelper.logError("GenericHelper::parseInt", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		try {
			out = Integer.parseInt(s);
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseInt", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		return out;
	}

	/**
	 * Method that securely parse a String to long value, and in case of not null logger, logs eventually the error
	 * @param s Input String
	 * @return (long) Parsed integer value, or default 0 value in case of error.
	 */
	public static final long parseLong(String s) {
		long out = 0l;
		// remove heading zeros from input string because they don't allow input string to be 
		// parsed as integer value (prevent unwanted errors (input file data format issues)
		try {
			s = removeHeadingZeros(fixUnicodeCharacters(s));
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseLong", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		try {
			out = Long.parseLong(s);
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseLong", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		return out;
	}

	/**
	 * Method that securely parse a String to double value, and in case of not null logger, logs eventually the error
	 * @param s Input String
	 * @return (double) Parsed integer value, or default 0 value in case of error.
	 */
	public static final double parseDouble(String s) {
		double out = 0d;
		// remove heading zeros from input string because they don't allow input string to be 
		// parsed as integer value (prevent unwanted errors (input file data format issues)
		try {
			s = removeHeadingZeros(fixUnicodeCharacters(s));
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseDouble", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		try {
			out = Double.parseDouble(s);
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseDouble", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		return out;
	}

	/**
	 * Parse Date format string
	 * @param s input string
	 * @param format date format see {@link SimpleDateFormat}
	 * @return <java.util.Date> Parsed Date or now() in case of error
	 */
	public static final Date parseDate(String s, String format) {
		Date out = new Date();
		// remove heading zeros from input string because they don't allow input string to be 
		// parsed as integer value (prevent unwanted errors (input file data format issues)
		try {
			s = removeHeadingZeros(fixUnicodeCharacters(s));
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseDate", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		try {
			out = new SimpleDateFormat(format).parse(s);
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseDate", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		return out;
	}
	
	/**
	 * Parse SQL Date format string
	 * @param s input string
	 * @param format date format see {@link SimpleDateFormat}
	 * @return <java.sql.Date> Parsed SQL Date or now() in case of error
	 */
	public static final java.sql.Date parseSQLDate(String s, String format) {
		java.sql.Date out = new java.sql.Date(System.currentTimeMillis());
		// remove heading zeros from input string because they don't allow input string to be 
		// parsed as integer value (prevent unwanted errors (input file data format issues)
		try {
			s = removeHeadingZeros(fixUnicodeCharacters(s));
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseSQLDate", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		try {
			out = new java.sql.Date(new SimpleDateFormat(format).parse(s).getTime());
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::parseSQLDate", String.format("string cleaning error : %s -> $s", e.getMessage(), convertStackTrace(e.getStackTrace())), null);
		}
		return out;
	}
	
	/**
	 * @return
	 */
	public static final String getHostNameAndIpAddressString() {
		try {
			InetAddress ip = InetAddress.getLocalHost();
			return String.format("%s / %s", ip.getHostAddress(), ip.getHostName());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public static final String getHostNameString() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public static final String getIpAddressString() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return
	 */
	public static final String getUserNameString() {
		return System.getProperty("user.name");
	}
	
	/**
	 * @return
	 */
	public synchronized static final String getDateToken() {
		return "" + new Date(System.nanoTime()).getTime();
	}

	/**
	 * Sleep current Thread for a certain number of milliseconds
	 * @param milliseconds number of milliseconds of sleep clock
	 */
	public static final void sleepThread(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			//INFO: NOT INPORTANT
		}
	}
	
	/**
	 * Determine a n decimal double string representations
	 * @param d decimal value
	 * @param precision number of decimal approximation
	 * @return (String) representing the double in the required number of decimals format
	 */
	public static final String doubleStringPrecision(double d, int precision) {
		if ( precision < 1 ) {
			precision = 2;
		}
		return String.format("%."+precision+"f", d);
	}
	
	/**
	 * Creates a custom thread name, based on annotation engine standards,
	 * unique and compliant to AE rules, as any other thread. 
	 * @return
	 */
	public static final String getCurrentThreadStandardName() {
		return "ANNOTATION-ENGINE-SESSION-THREAD-" + Thread.currentThread().getId();
	}
	
	/**
	 * Report the base name into the current thread, or in case of null name
	 * collects current thread standard name and associate to current thread,
	 * useful before start up a new session. It's kindly required to execute
	 * this command on the standard name or current thread name on any sub
	 * threads (eg. lambda, new threads, thread pools / executors) or in case
	 * of an overlapping of session, creating a new instance. Thread name makes  
	 * difference in order to create a new thread session bucket or in case to
	 * recover from the ApplicationManager the session Id or session objects.
	 * New sub threads, threads or daemons will report null session objects,
	 * without prior use of this method to change the own thread name with the
	 * main process one.
	 * @param baseName New Thread name or null in case of first thread name generation
	 * @return (String) passed or new generated thread name, assigned to current thread.
	 */
	public static final String fixCurrentThreadStandardName(String baseName) {
		String threadName = baseName;
		if ( threadName == null ) {
			threadName = getCurrentThreadStandardName();
		}
		Thread.currentThread().setName(threadName);
		return threadName;
	}
	
	/**
	 * @param <K>
	 * @param <V>
	 * @param inputMap
	 * @return
	 */
	public static final <K extends Comparable<K>, V> Map<K, V> sortedMapByKey(Map<K,V> inputMap) {
		return inputMap
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	/**
	 * @param <K>
	 * @param <V>
	 * @param inputMap
	 * @return
	 */
	public static final <K extends Comparable<K>, V> Map<K, V> reverseSortedMapByKey(Map<K,V> inputMap) {
		return inputMap
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	/**
	 * Check grouping RegExp find groups for given text value 
	 * @param pattern RegExp pattern with groups
	 * @param value Given input value to test
	 * @return state of find for groups in given sample text
	 */
	public static final boolean checkMatchIn(String pattern, String value) {
		try {
			Pattern compiledPattern = Pattern.compile(pattern, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
			Matcher matcher = compiledPattern.matcher(value);
			return matcher.find();
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::checkMatchIn", 
								String.format("Errors during finding groups for pattern %s, in value %s", pattern, value), e);
		}
		return false;
	}

	/**
	 * Check grouping RegExp find groups for given text value 
	 * @param pattern RegExp pattern with groups
	 * @param value Given input value to test
	 * @return state of find for groups in given sample text
	 */
	public static final int countMatchIn(String pattern, String value) {
		try {
			Pattern compiledPattern = Pattern.compile(pattern, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
			Matcher matcher = compiledPattern.matcher(value);
			if ( matcher.find() ) {
				return matcher.groupCount();
			}
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::checkMatchIn", 
								String.format("Errors during count groups found for pattern %s, in value %s", pattern, value), e);
		}
		return 0;
	}

	/**
	 * Extract from grouping RegExp some given groups by number from given text value 
	 * @param pattern RegExp pattern with groups
	 * @param value Given input value to test
	 * @param groups number of groups to extract if present during matcher find operations 
	 * @return (List&lt;String&gt;) List of results (eventually empty)
	 */
	public static final List<String> findMatchIn(String pattern, String value, int... groups) {
		List<String> response = new ArrayList<String>(0);
		try {
			Pattern compiledPattern = Pattern.compile(pattern, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
			Matcher matcher = compiledPattern.matcher(value);
			while ( matcher.find() ) {
				int count = matcher.groupCount();
				for ( int group: groups ) {
					if ( group < count ) {
						response.add(matcher.group(group));
					}
				}
			}
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::checkMatchIn", 
								String.format("Errors during finding of groups for pattern %s, in value %s, requiring collection of groups: %s", pattern, value, Arrays.toString(groups)), e);
		}
		return response;
	}
	
	/**
	 * Replace values in groups based on the order of the passed list values.
	 * Max replacements depends on the minimum between found groups and list size.
	 * Any null value in the list will be considered as a skip case for the group
	 * replacement and the original group value will be kept in the response.
	 * @param pattern RegExp pattern with groups
	 * @param value Given input value to test
	 * @param replacementsList list of new values to be replaced instead of the found groups ones
	 * @return Replacement string within new values instead of the found data.
	 */
	public static final String replaceAtGroupsIn(String pattern, String value, List<String> replacementsList) {
		int replacements = 0;
		StringBuffer sb = new StringBuffer();
		try {
			Pattern compiledPattern = Pattern.compile(pattern, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
			Matcher matcher = compiledPattern.matcher(value);
			while ( matcher.find() ) {
				if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceUtilsLevel )
					LoggerHelper.logTrace("GenericHelper::replaceFouGroupsIn", "Found group ("+replacementsList.size()+") " + (replacements + 1));
				if ( replacementsList.size() > replacements ) {
					if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceUtilsLevel )
						LoggerHelper.logTrace("GenericHelper::replaceFouGroupsIn", "Try replace group " + (replacements + 1));
					String newText = replacementsList.get(replacements);
					if ( newText != null ) {
						if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceUtilsLevel )
							LoggerHelper.logTrace("GenericHelper::replaceFouGroupsIn", "Replace group " + (replacements + 1) + " value: " + matcher.group(0) + " to value: " + newText);
						matcher.appendReplacement(sb, newText);
						
					}
					else {
						if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceUtilsLevel )
							LoggerHelper.logTrace("GenericHelper::replaceFouGroupsIn", "Replace group " + (replacements + 1) + " NO CHANGES:  Missing data at the index");
						matcher.appendReplacement(sb, matcher.group(0));
					}
				}
				else {
					if ( ArgumentsHelper.traceAllLevels || ArgumentsHelper.traceUtilsLevel )
						LoggerHelper.logTrace("GenericHelper::replaceFouGroupsIn", "Replace group " + (replacements + 1) + " NO CHANGES:  No more data");
					matcher.appendReplacement(sb, matcher.group(0));
				}
				replacements++;
			}
			matcher.appendTail(sb);
		} catch (Exception e) {
			LoggerHelper.logError("GenericHelper::checkMatchIn", 
								String.format("Errors during replacing of groups for pattern %s, in value %s, with replacements: %s", pattern, value, Arrays.toString(replacementsList.toArray())), e);
		}
		String outcome = sb.toString();
		if ( outcome.length() > 0 )
			return outcome;
		return value;
	}
	
	public static final Class<?> getObjectAsClass(Object obj, Class<?> defaultValue) {
		if ( obj != null ) {
			if( Class.class.isAssignableFrom(obj.getClass()) ) {
				return ((Class<?>)obj);
			} else if ( String.class.isAssignableFrom(obj.getClass()) ) {
				try {
					return Class.forName(""+obj);
				} catch (ClassNotFoundException e) {
					LoggerHelper.logError("GenericHelper::getObjectAsClass(Object, Class<?>)", 
												String.format("Unable to create new instances for instance type %s, value %s", obj.getClass().getName(), obj), 
												e);
				}
			}
		}
		return defaultValue;
	}
	
	public static final boolean getObjectAsBoolean(Object obj, boolean defaultValue) {
		if ( obj != null ) {
			if( Boolean.class.isAssignableFrom(obj.getClass()) ) {
				return ((Boolean)obj).booleanValue();
			} else if ( String.class.isAssignableFrom(obj.getClass()) ) {
				return (""+obj).equalsIgnoreCase("true");
			}
		}
		return defaultValue;
	}
	
	public static final int getObjectAsInteger(Object obj, int defaultValue) {
		if ( obj != null ) {
			if( Boolean.class.isAssignableFrom(obj.getClass()) ) {
				return ((Integer)obj).intValue();
			} else if ( String.class.isAssignableFrom(obj.getClass()) ) {
				return Integer.parseInt(""+obj);
			}
		}
		return defaultValue;
	}
	
	public static final long getObjectAsLong(Object obj, long defaultValue) {
		if ( obj != null ) {
			if( Boolean.class.isAssignableFrom(obj.getClass()) ) {
				return ((Long)obj).longValue();
			} else if ( String.class.isAssignableFrom(obj.getClass()) ) {
				return Long.parseLong(""+obj);
			}
		}
		return defaultValue;
	}
	
	public static final double getObjectAsDouble(Object obj, double defaultValue) {
		if ( obj != null ) {
			if( Boolean.class.isAssignableFrom(obj.getClass()) ) {
				return ((Double)obj).doubleValue();
			} else if ( String.class.isAssignableFrom(obj.getClass()) ) {
				return Double.parseDouble(""+obj);
			}
		}
		return defaultValue;
	}

}
