/**
 * 
 */
package com.rcg.foundation.fondify.properties.runtime;

import java.util.ArrayList;
import java.util.List;

import com.rcg.foundation.fondify.core.properties.PropertyArchive;
import com.rcg.foundation.fondify.core.typings.properties.NamingTrnslationActuator;
import com.rcg.foundation.fondify.reflections.typings.KeyValuePair;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;

/**
 * Default Properties locator and translator. It checks
 * presence in a text of properties and translates reference
 * in each live value present in the {@link PropertyArchive}.
 * 
 *  Expected properties value is :
 *  <code>@{pr:&lt;property name&gt;:&lt;default value&gt;}</code>
 *  
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class DefaultPropertiesNamingTrnslationActuator implements NamingTrnslationActuator {

	private static final String PATTERN_WITHOUT_DEFAULT = "(@\\u007Bpr:)([\\w\\.]+)(\\u007D)";
	private static final String PATTERN_WITHIN_DEFAULT = "(@\\u007Bpr:)([\\w\\.]+)([:])([\\w\\s\\.]+)(\\u007D)";
	private static final String PATTERN_REPLACE_WITHOUT_DEFAULT = "(@\\u007Bpr:[\\w\\.]+\\u007D)";
	private static final String PATTERN_REPLACE_WITHIN_DEFAULT = "(@\\u007Bpr:[\\w\\.]+:[\\w\\s\\.]+\\u007D)";
	
	
	/**
	 * Default constructor
	 */
	public DefaultPropertiesNamingTrnslationActuator() {
		super();
	}

	@Override
	public String translateText(String text, boolean applyDefault) {
		List<String> listOfEntriesWithoutDefault = new ArrayList<String>(0);
		List<KeyValuePair<String, String>> listOfEntriesWithDefault = new ArrayList<KeyValuePair<String,String>>(0);
		String metchingText = text;
		if ( GenericHelper.checkMatchIn(PATTERN_WITHOUT_DEFAULT, metchingText) ) {
			listOfEntriesWithoutDefault.addAll(
					GenericHelper.findMatchIn(PATTERN_WITHOUT_DEFAULT, metchingText, 2)
			); 
		}
		if ( GenericHelper.checkMatchIn(PATTERN_WITHIN_DEFAULT, metchingText) ) {
			List<String> list = GenericHelper.findMatchIn(PATTERN_WITHIN_DEFAULT, metchingText, 2, 4);
			for ( int i=0; i<list.size(); i+=2 ) {
				if ( i + 1 < list.size() ) {
					listOfEntriesWithDefault.add(
							new KeyValuePair<String, String>(list.get(i), list.get(i+1))
					); 
				}
			}
		}
		List<String> valueStringWithoutDefault = new ArrayList<>(0); 
		for ( String key: listOfEntriesWithoutDefault) {
			valueStringWithoutDefault.add(
				(String) PropertyArchive.getInstance().getProperty(key)
			);
		}
		List<String> valueStringWithDefault = new ArrayList<>(0); 
		for ( KeyValuePair<String, String> pair: listOfEntriesWithDefault) {
			Object value = PropertyArchive.getInstance().getProperty(pair.getKey());
			if ( value == null && applyDefault) {
				value = pair.getValue()!=null && ! pair.getValue().isEmpty() ? pair.getValue() : "";
			}
			valueStringWithDefault.add(
				(String) value
			);
		}
		if ( valueStringWithoutDefault.size() > 0 ) {
			metchingText = GenericHelper.replaceAtGroupsIn(PATTERN_REPLACE_WITHOUT_DEFAULT, metchingText, valueStringWithoutDefault);
		}
		if ( valueStringWithDefault.size() > 0 ) {
			metchingText = GenericHelper.replaceAtGroupsIn(PATTERN_REPLACE_WITHIN_DEFAULT, metchingText, valueStringWithDefault);
		}
		return metchingText;
	}

	@Override
	public boolean canTranslateText(String text) {
		return GenericHelper.checkMatchIn(PATTERN_WITHOUT_DEFAULT, text) ||
				GenericHelper.checkMatchIn(PATTERN_WITHIN_DEFAULT, text);
	}

}
