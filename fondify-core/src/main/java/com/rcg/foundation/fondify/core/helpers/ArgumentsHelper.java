/**
 * 
 */
package com.rcg.foundation.fondify.core.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rcg.foundation.fondify.core.arguments.ArgumentDescriptor;
import com.rcg.foundation.fondify.core.arguments.Feature;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ArgumentsHelper {
	private static final Queue<ArgumentDescriptor> argumentDescriptors = new ConcurrentLinkedQueue<ArgumentDescriptor>();
	private static final Queue<Feature> features = new ConcurrentLinkedQueue<Feature>();
	private static final Properties arguments = new Properties();

	public static boolean debug = false;

	public static boolean useLogger = false;

	public static boolean debugArguments = false;

	/**
	 * Denied access constructor
	 */
	private ArgumentsHelper() {
		throw new IllegalStateException("ArgumentsHelper::constructor - unable to instantiate utility class!!");
	}

	/**
	 * @param argumentDescriptor
	 */
	public static final void addArgumentDescriptor(ArgumentDescriptor argumentDescriptor) {
		if (argumentDescriptor != null && !argumentDescriptors.contains(argumentDescriptor)) {
			argumentDescriptors.add(argumentDescriptor);
		}
	}

	/**
	 * @param argumentName
	 * @return
	 */
	public static final boolean hasArgumentDescriptor(String argumentName) {
		return argumentDescriptors.stream().filter(arg -> arg.getArgumentName().equalsIgnoreCase(argumentName))
				.count() > 0;
	}

	/**
	 * @param argumentName
	 * @return
	 */
	public static final Optional<ArgumentDescriptor> getArgumentDescriptor(String argumentName) {
		return argumentDescriptors.stream().filter(arg -> arg.getArgumentName().equalsIgnoreCase(argumentName))
				.findFirst();
	}

	/**
	 * @param argumentName
	 * @return
	 */
	public static final boolean hasArgument(String argumentName) {
		return arguments.containsKey(argumentName);
	}

	/**
	 * @param argumentName
	 * @return
	 */
	public static final String getArgument(String argumentName) {
		return arguments.getProperty(argumentName);
	}

	/**
	 * @param feature
	 */
	public static final void addFeature(Feature feature) {
		if (feature != null && !features.contains(feature)) {
			features.add(feature);
		}
	}

	/**
	 * Check and report feature application
	 */
	protected static final void checkFeatures() {
		features.forEach(feature -> {
			if (feature.getMatchFunction().match(argumentDescriptors, arguments))
				LoggerHelper.logDebug("ArgumentsHelper::checkFeatures",
						String.format("Feature %s can be applied", feature.getName()));
			else
				LoggerHelper.logDebug("ArgumentsHelper::checkFeatures", String
						.format("Feature %s cannot be applied, requred parameters are not applied", feature.getName()));
		});
	}

	/**
	 * 
	 */
	protected static final void executeFeatures() {
		features.forEach(feature -> {
			if (feature.getMatchFunction().match(argumentDescriptors, arguments)) {
				LoggerHelper.logInfo("ArgumentsHelper::executeFeatures",
						String.format("Applying feature %s ...", feature.getName()));
				feature.getApplyFunction().apply(arguments);
				LoggerHelper.logInfo("ArgumentsHelper::executeFeatures",
						String.format("Feature %s applied!!", feature.getName()));
			}
		});
	}

	/**
	 * Process Application Arguments
	 * 
	 * @param args
	 */
	public static final void storeArguments(String[] args) {
//		String currentArgument = "";
		List<String> discarded = new ArrayList<String>(0);
		List<String> messages = new ArrayList<String>(0);
		// Note: Argument kind is -{argument_name}={argument_value}
		for (String argument : args) {
			if (!argument.startsWith("-")) {
				messages.add(String.format("Argument '%s' doesn't start with a minus", argument));
				discarded.add(argument);
			} else {
				String currentArgument = argument.substring(1);
				if (currentArgument.contains("=")) {
					// Argument with value
					String key = currentArgument.substring(0, currentArgument.indexOf("="));
					String value = currentArgument.indexOf("=") < currentArgument.length() - 1
							? currentArgument.substring(currentArgument.indexOf("=") + 1)
							: "";
					arguments.put(key, value);
				} else {
					// Argument without value
					arguments.put(currentArgument, "");
				}
			}
		}
		String debugStr = System.getProperty("debug.arguments");
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty("debug.arguments");
		}
		debugArguments = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			debugArguments = true;
		}
		debugStr = System.getProperty("debug");
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty("debug");
		}
		debug = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			debug = true;
		}

		debugStr = System.getProperty("use.logger");

		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty("use.logger");
		}

		useLogger = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			useLogger = true;
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Enabled Application using logger!!!");
		}
		
		messages.forEach(message -> LoggerHelper.logWarn("AgumentsHelper::storeArguments", message, null) );

		if ( debugArguments ) {
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Enabled Arguments Debug Mode!!!");
		}
		if ( debug ) {
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Enabled Application Debug Mode!!!");
		}


		if (debug || debugArguments)
			checkFeatures();
		
		if (discarded.size() > 0) {
			String message = String.format("Some arguments has been discarded : %s",
					Arrays.toString(discarded.toArray()));
			LoggerHelper.logWarn("StreamIOApplication::processArguments", message, null);
		}
	}

	/**
	 * Process Application Arguments
	 * 
	 * @param args
	 */
	public static final void processArguments() {
		executeFeatures();
	}

}
