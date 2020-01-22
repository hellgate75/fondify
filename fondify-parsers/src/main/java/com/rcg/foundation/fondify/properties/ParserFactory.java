/**
 * 
 */
package com.rcg.foundation.fondify.properties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rcg.foundation.fondify.properties.typings.JSONParser;
import com.rcg.foundation.fondify.properties.typings.Parser;
import com.rcg.foundation.fondify.properties.typings.ParserType;
import com.rcg.foundation.fondify.properties.typings.XMLParser;
import com.rcg.foundation.fondify.properties.typings.YAMLParser;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public final class ParserFactory {
	
	private static final Map<TypeIdentifier<?>, Parser<?>> cacheMap = new ConcurrentHashMap<>(0);

	private static ParserFactory instance = null;
	
	/**
	 * 
	 */
	private ParserFactory() {
		super();
	}
	
	/**
	 * Creates a new instance of the required parser or throws  IllegalStateException in case parse type is not available
	 * @param <T> Template Parameter
	 * @param type Required Parser identified by {@link ParserType} 
	 * @return Required Parser
	 */
	public <T> Parser<T> newParserByType(ParserType type) {
		if ( type == null )
			throw new NullPointerException("Unable to create Parser from null type");
		switch ( type ) {
		case JSON:
			return JSONParser.newParser();
		case XML:
			return XMLParser.newParser();
		case YAML:
			return YAMLParser.newParser();
		default:
			throw new IllegalStateException("NOT IMPLEMENTED TYPE: " + type);
		}
	}
	
	/**
	 * Creates in cache or retrieve from cache an instance of the required parser or throws IllegalStateException in case parse type is not available
	 * @param <T> Template Parameter
	 * @param type Required Parser identified by {@link ParserType} 
	 * @return Required Parser
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized <T> Parser<T> getReusableParserByType(ParserType type) {
		if ( type == null )
			throw new NullPointerException("Unable to create/recover Parser from null type");
		Parser<T> parser;
		switch ( type ) {
		case JSON:
			parser = JSONParser.newParser();
			break;
		case XML:
			parser = XMLParser.newParser();
			break;
		case YAML:
			parser = YAMLParser.newParser();
			break;
		default:
			throw new IllegalStateException("NOT IMPLEMENTED TYPE: " + type);
		}
		TypeIdentifier<T> identifier = new TypeIdentifier(type, parser.getClass());
		if ( ! cacheMap.containsKey(identifier) ) {
			cacheMap.put(identifier, parser);
		} else {
			parser = (Parser<T>)cacheMap.get(identifier);
		}
		return parser;
	}
	/**
	 * @return
	 */
	public static final ParserFactory getInstance() {
		if ( instance == null )
			instance = new ParserFactory();
		return instance;
	}
	
	private static final class TypeIdentifier<T> {
		private ParserType type;
		private Class<? extends T> clazz;
		/**
		 * @param type
		 * @param clazz
		 */
		public TypeIdentifier(ParserType type, Class<? extends T> clazz) {
			super();
			this.type = type;
			this.clazz = clazz;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			if ( ! TypeIdentifier.class.isAssignableFrom(obj.getClass()) ) {
				return false;
			}
			@SuppressWarnings("rawtypes")
			TypeIdentifier other = (TypeIdentifier) obj;
			if (clazz == null) {
				if (other.clazz != null)
					return false;
			} else if (!clazz.equals(other.clazz))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "TypeIdentifier [type=" + type + ", clazz=" + clazz + "]";
		}
		
		
	}
}
