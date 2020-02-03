/**
 * 
 */
package com.rcg.foundation.fondify.core.typings.cache;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rcg.foundation.fondify.core.exceptions.ProcessException;
import com.rcg.foundation.fondify.utils.helpers.GenericHelper;
import com.rcg.foundation.fondify.utils.helpers.LoggerHelper;

/**
 * Base Cache Serializable component. It provides
 * based serialization feature to any extending artiface. 
 * it's based on java reflection capabilities, and any field 
 * in the artifact will be scanned for extracting or importing 
 * data, from and to the class instance. Serialization process 
 * is provided from the currently active {@link CacheProvider}.
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class SerializableComponent implements CacheSerializable {

	/**
	 * Unique serial version id
	 */
	private static final long serialVersionUID = 5355795054096243289L;

	/**
	 * Default constructor
	 */
	public SerializableComponent() {
		super();
	}

	@Override
	public Map<String, Object> exportToMap() {
		final Object instance = this;
		
		List<Field> fields = new ArrayList<Field>(0);
		fields.addAll(
				Arrays.asList(getClass().getDeclaredFields())
		);
		Map<String, Object> fieldsMap = new HashMap<>();
		fieldsMap.put(CLASS_MAP_KEY, getClass().getName());
		fieldsMap.putAll(
				fields
				.stream()
				.filter( field -> {
					field.setAccessible(true);
					int mod0 = Modifier.STATIC + Modifier.FINAL + Modifier.PRIVATE;
					int mod1 = Modifier.STATIC + Modifier.FINAL + Modifier.PUBLIC;
					int mod2 = Modifier.STATIC + Modifier.PRIVATE;
					int mod3 = Modifier.STATIC + Modifier.PUBLIC;
					int mod = field.getModifiers();
					return mod != mod0 && mod != mod1 && mod != mod2 && mod != mod3;
				})
				.collect(Collectors.toMap(field -> field.getName(), field ->  { 
					try {
						return field.get(instance);
					} catch (Exception e) {
						LoggerHelper.logError("SerializableComponent::exportToMap", 
								String.format("Errors during value extractor in type %s, from field: %s", 
										getClass().getName(),
										field.getName()), 
								e);
						return null;
					} 
				} ))
		);
		
		return GenericHelper.sortedMapByKey(fieldsMap);
	}

	@Override
	public void importFromMap(Map<String, Object> importMap) {
		if ( importMap == null ) {
			throw new ProcessException("Unable to import nullable import map in type: " + getClass().getName());
		}
		final Object instance = this;
		final Class<?> cls = instance.getClass();
		String className = (String)importMap.get(CLASS_MAP_KEY);
		if ( className == null || className.isEmpty() || ! getClass().getName().equals(className) ) {
			throw new ProcessException(
						String.format("Problems importing data into type: %s, empty or unmatching import class: %s", getClass().getName(), className)
					);
		}
		importMap
			.entrySet()
			.stream()
			.filter( entry -> ! entry.getKey().equals(CLASS_MAP_KEY) )
			.forEach(entry -> {
				try {
					String fieldName = entry.getKey();
					Object value = entry.getValue();
					Field field = cls.getDeclaredField(fieldName);
					field.setAccessible(true);
					field.set(instance, value);
					LoggerHelper.logTrace("SerializableComponent::importFromMap", 
							String.format("Import successful for type %s and field %s, with value type: %s",
							cls.getName(),
							entry.getKey(),
							field.getType().getName()));
				} catch (Exception e) {
					String message = String.format("Unable to import in type %s persisted field %s, with value type: %s",
							cls.getName(),
							entry.getKey(),
							entry.getValue() != null ? entry.getValue().getClass().getName() : "<UNKNOWN>");
					LoggerHelper.logError("SerializableComponent::importFromMap", 
							message,
							e);
					throw new ProcessException(message,e);
				}
			});
	}
}
