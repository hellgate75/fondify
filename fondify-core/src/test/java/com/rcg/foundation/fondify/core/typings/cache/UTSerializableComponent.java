/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.sql.Date;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class UTSerializableComponent {
	
	private static final long defaultNanoTime = 743858223495200L;

	private SampleSerializableComponent getSample() {
		return new SampleSerializableComponent(true, "text", new Date(defaultNanoTime));
	}
	
	private static void testReadElement(String descr, String name, Object expected, Map<String, Object> theMap) {
		assertTrue("Export Map must contain " + descr + " placeholder", theMap.containsKey(name) );
		assertEquals(descr + " must match from export map", expected, theMap.get(name));
	}
	
	@Test
	public void testCompontExport() {
		SampleSerializableComponent sample = getSample();
		Map<String, Object> exportMap = sample.exportToMap();
		testReadElement("Class name", SerializableComponent.CLASS_MAP_KEY, 
				SampleSerializableComponent.class.getName(), exportMap);
		testReadElement("Original Object Boolean Field", "booleanField", 
				sample.getBooleanField(), exportMap);
		testReadElement("Original Object String Field", "stringField", 
				sample.getStringField(), exportMap);
		testReadElement("Original Object Date Field", "dateField", 
				sample.getDateField(), exportMap);
		testReadElement("Exact Value of Boolean Field", "booleanField", 
				true, exportMap);
		testReadElement("Exact Value of String Field", "stringField", 
				"text", exportMap);
		testReadElement("Exact Value of Date Field", "dateField", 
				new Date(defaultNanoTime), exportMap);
	}
	
	@Test
	public void testCompontImport() {
		SampleSerializableComponent expected = getSample();
		Map<String, Object> exportMap = expected.exportToMap();
		SampleSerializableComponent sample = new SampleSerializableComponent();
		sample.importFromMap(exportMap);
		
		assertEquals("Sample class end new imported from his data must match: booleanField", expected.getBooleanField(), sample.getBooleanField());
		assertEquals("Sample class end new imported from his data must match: stringField", expected.getStringField(), sample.getStringField());
		assertEquals("Sample class end new imported from his data must match: dateField", expected.getDateField(), sample.getDateField());
		assertEquals("Sample class end new imported from his data must match", expected, sample);
	}
	

}
