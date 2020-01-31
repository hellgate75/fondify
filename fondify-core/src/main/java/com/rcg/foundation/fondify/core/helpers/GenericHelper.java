package com.rcg.foundation.fondify.core.helpers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
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
	
	public static final <K extends Comparable<K>, V> Map<K, V> sortedMapByKey(Map<K,V> inputMap) {
		return inputMap
				.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	public static final <K extends Comparable<K>, V> Map<K, V> rverseSortedMapByKey(Map<K,V> inputMap) {
		return inputMap
				.entrySet()
				.stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
}
