/**
 * 
 */
package com.rcg.foundation.fondify.properties;

import java.util.Arrays;
import java.util.List;

import com.rcg.foundation.fondify.utils.helpers.GenericHelper;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class TestPattern {

	private static final String PATTERN1 = "(@\\u007Bpr:)([\\w\\.]+)(\\u007D)";
	private static final String PATTERN2 = "(@\\u007Bpr:)([\\w\\.]+)([:])([\\w\\s\\.]+)(\\u007D)";
	private static final String PATTERN3 = "(@\\u007Bpr:[\\w\\.]+\\u007D)";
	private static final String PATTERN4 = "(@\\u007Bpr:[\\w\\.]+:[\\w\\s\\.]+\\u007D)";
	
	/**
	 * 
	 */
	public TestPattern() {
	}
	
	public static final void main(String[] args) {
//		String s1 = "TheComponent@{pr:com.reference.value}";
//		Pattern pattern1 = Pattern.compile(PATTERN1, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
//		Matcher m1 = pattern1.matcher(s1);
//		if ( m1.find() ) {
//			String g1_1 = m1.group(2);
//			System.out.println(g1_1);
//			
//		} else {
//			System.out.println("s1 doesn't match!!");
//		}
//		String s2 = "TheComponent@{pr:com.reference.value:my text value}";
//		Pattern pattern2 = Pattern.compile(PATTERN2, Pattern.UNICODE_CASE + Pattern.CASE_INSENSITIVE);
//		Matcher m2 = pattern2.matcher(s2);
//		if ( m2.find() ) {
//			String g2_1 = m2.group(2);
//			System.out.println(g2_1);
//			String g2_2 = m2.group(4);
//			System.out.println(g2_2);
//			
//		} else {
//			System.out.println("s1 doesn't match!!");
//		}
		String s1 = "TheComponent@{pr:com.reference.value}To@{pr:com.reference.value2}Stinks";
		String s2 = "TheComponent@{pr:com.reference.value: my default value}To@{pr:com.reference.value2:my default value 2}Stinks";
		
		System.out.println("Text 1 = " + s1);
		if ( GenericHelper.checkMatchIn(PATTERN1, s1) ) {
			List<String> l1 = GenericHelper.findMatchIn(PATTERN1, s1, 2);
			System.out.println("Match 1 = " + Arrays.toString(l1.toArray()));
			System.out.println("Replace 1 = " + GenericHelper.replaceAtGroupsIn(PATTERN3, s1, Arrays.asList("AAA", "BBB")));
		}

		System.out.println("Text 2 = " + s2);
		if ( GenericHelper.checkMatchIn(PATTERN2, s2) ) {
			List<String> l2 = GenericHelper.findMatchIn(PATTERN2, s2, 2, 4);
			System.out.println("Match 2 = " + Arrays.toString(l2.toArray()));
			System.out.println("Replace 2 = " + GenericHelper.replaceAtGroupsIn(PATTERN4, s2, Arrays.asList("CCC", "DDD")));
		}
		
	}

}
