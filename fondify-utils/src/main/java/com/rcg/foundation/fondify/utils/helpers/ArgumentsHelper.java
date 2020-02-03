/**
 * 
 */
package com.rcg.foundation.fondify.utils.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.rcg.foundation.fondify.core.arguments.ArgumentDescriptor;
import com.rcg.foundation.fondify.core.arguments.Feature;
import com.rcg.foundation.fondify.utils.constants.ArgumentsConstants;

/**
 * @author Fabrizio Torelli (hellgate75@gmail.com)
 *
 */
public class ArgumentsHelper {
	private static final Queue<ArgumentDescriptor> argumentDescriptors = new ConcurrentLinkedQueue<ArgumentDescriptor>();
	private static final Queue<Feature> features = new ConcurrentLinkedQueue<Feature>();
	private static final Properties arguments = new Properties();

	public static boolean traceAllLevels = false;
	public static boolean traceReflectionsLevel = false;
	public static boolean traceComponentsLevel = false;
	public static boolean traceAnnotationsLevel = false;
	public static boolean traceCoreLevel = false;
	public static boolean traceUtilsLevel = false;
	public static boolean tracePropertiesLevel = false;
	public static boolean traceContextLevel = false;
	public static boolean traceCacheLevel = false;

	public static boolean debugApplication = false;

	public static boolean useLoggerInsteadConsole = false;

	public static boolean debugArgumentsLoad = false;

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
	 * @return
	 */
	public static final Properties getArguments() {
		return arguments;
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
					String[] keyVal =  currentArgument.split("=");
					String key = keyVal[0];
					String value = keyVal[1];
					arguments.put(key, value);
				} else {
					// Argument without value
					arguments.put(currentArgument, "");
				}
			}
		}
		String debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_DEBUG_ARGUMENTS);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_DEBUG_ARGUMENTS);
		}
		debugArgumentsLoad = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			debugArgumentsLoad = true;
		}
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_DEBUG_APPLICATION);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_DEBUG_APPLICATION);
		}
		debugApplication = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			debugApplication = true;
		}

		debugStr = System.getProperty(ArgumentsConstants.FORMAT_APPLICATION_OUTPUT_FOR_LOGGER);

		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.FORMAT_APPLICATION_OUTPUT_FOR_LOGGER);
		}

		useLoggerInsteadConsole = true;
		if (debugStr != null && !debugStr.trim().isEmpty() && ! debugStr.equalsIgnoreCase("true")) {
			useLoggerInsteadConsole = false;
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Disable Application using logger!!!");
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_REFLECTION_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_REFLECTION_LEVEL);
		}
		traceReflectionsLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceReflectionsLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_COMPONENTS_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_COMPONENTS_LEVEL);
		}
		traceComponentsLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceComponentsLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_ANNOTATIONS_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_ANNOTATIONS_LEVEL);
		}
		traceAnnotationsLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceAnnotationsLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CORE_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CORE_LEVEL);
		}
		traceCoreLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceCoreLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_UTILS_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_UTILS_LEVEL);
		}
		traceUtilsLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceUtilsLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_PROPERTIES_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_PROPERTIES_LEVEL);
		}
		tracePropertiesLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			tracePropertiesLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CONTEXT_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CONTEXT_LEVEL);
		}
		traceContextLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceContextLevel = true;
		}

		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CACHE_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_CACHE_LEVEL);
		}
		traceCacheLevel = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceCacheLevel = true;
		}
		
		debugStr = System.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_LOW_LEVEL);
		if (debugStr == null || debugStr.trim().isEmpty()) {
			debugStr = arguments.getProperty(ArgumentsConstants.ARGUMENTS_HELPER_TRACE_LOW_LEVEL);
		}
		traceAllLevels = false;
		if (debugStr != null && !debugStr.trim().isEmpty() && debugStr.equalsIgnoreCase("true")) {
			traceAllLevels = true;
			debugApplication = true;
			debugArgumentsLoad = true;
		}


		if ( debugArgumentsLoad ) {
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Enabled Arguments Debug Mode!!!");
		}

		messages.forEach(message -> LoggerHelper.logWarn("AgumentsHelper::storeArguments", message, null) );

		if (debugApplication || debugArgumentsLoad) {
			LoggerHelper.logTrace("ArgumentsHelper::storeArguments", "Enabled Application Debug Mode!!!");
			checkFeatures();
		}


		
		if (discarded.size() > 0 && (debugArgumentsLoad || debugApplication ) ) {
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
