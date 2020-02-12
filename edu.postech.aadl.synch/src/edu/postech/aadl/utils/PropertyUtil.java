package edu.postech.aadl.utils;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.ComponentCategory;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.ConnectionInstance;
import org.osate.aadl2.instance.ConnectionInstanceEnd;
import org.osate.aadl2.instance.ConnectionKind;
import org.osate.aadl2.instance.FeatureCategory;
import org.osate.aadl2.instance.FeatureInstance;
import org.osate.aadl2.properties.PropertyLookupException;
import org.osate.xtext.aadl2.properties.util.CommunicationProperties;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

public class PropertyUtil {

	/*
	 * Data Modeling Annex
	 */
	static public final String DATA_MODEL = "Data_Model";
	static public final String DATA_REPRESENTATION = "Data_Representation" ;
	static public final String INITIAL_VALUE = "Initial_Value" ;

	/*
	 * MR-Synch-AADL property set
	 */
	static public final String HYBRIDSYNCHAADL = "Hybrid_SynchAADL";
	static public final String SYNCHRONOUS = "Synchronous";
	static public final String NONDETERMINISTIC = "Nondeterministic";
	static public final String INPUT_ADAPTOR = "InputAdaptor";
	static public final String SAMPLING_TIME = "Sampling_Time";
	static public final String RESPONSE_TIME = "Response_Time";
	static public final String MAX_CLOCK_DEV = "Max_Clock_Deviation";

	/*
	 * Hybrid-AADL property set
	 */
	static public final String ENVIRONMENT = "isEnvironment";
	static public final String ODE = "ODE";
	static public final String CD = "ContinuousDynamics";

	/*
	 * Thermostat property set
	 */
	static public final String THERMOSTAT = "ThermostatSpec";

	public static ListValue getDataInitialValue(final NamedElement ne) {

		try {
			Property initialValue = GetProperties.lookupPropertyDefinition(ne, DATA_MODEL, INITIAL_VALUE);
			return (ListValue) PropertyUtils.getSimplePropertyValue(ne, initialValue);
		}
		catch (PropertyLookupException e) {
			return null;
		}
	}

	public static boolean hasDataInitValue(final NamedElement ne) {
		ListValue value = getDataInitialValue(ne);
		if (value != null) {
			EList<PropertyExpression> pe = value.getOwnedListElements();
			if (pe == null || pe.isEmpty()) {
				return false;
			}
			return true;
		}
		return false;
	}

	public static boolean isSynchronous(ComponentInstance ci) {
		return getSynchBooleanProp(ci, HYBRIDSYNCHAADL, SYNCHRONOUS, false);
	}

	public static boolean isTopComponent(ComponentInstance ci) {
		return ci.getContainingComponentImpl() == null;
	}

	public static boolean hasContinuousDynamics(final NamedElement ne, final String propertySet,
			final String propertyName) {
		try {
			Property prop = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			if (prop == null) {
				return false;
			}
			String cd = PropertyUtils.getStringValue(ne, prop);
			if (cd == null) {
				return false;
			}
		} catch (PropertyLookupException e) {
			return false;
		}
		return true;
	}

	public static boolean hasTimeProperty(final NamedElement ne, final String propertySet, final String propertyName) {
		try {
			Property prop = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			if (prop == null) {
				return false;
			}
			PropertyExpression pe = ne.getSimplePropertyValue(prop);
			if (pe == null) {
				return false;
			}
		} catch (PropertyLookupException e) {
			return false;
		}
		return true;
	}

	public static boolean getSynchBooleanProp(final NamedElement ne, final String propertySet,
			final String propertyName, boolean defaultValue) {
		try {
			Property synchronous = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getBooleanValue(ne, synchronous);
		}
		catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static boolean isEnvironment(ComponentInstance ci) {
		return getSynchBooleanProp(ci, HYBRIDSYNCHAADL, ENVIRONMENT, false);
	}

	public static boolean isController(ComponentInstance ci) {
		for (ComponentInstance child : ci.getComponentInstances()) {
			if (child.getCategory() == ComponentCategory.PROCESS) {
				return true;
			}
		}
		return false;
	}

	public static boolean getEnvBooleanProp(final NamedElement ne, final String propertySet, final String propertyName,
			boolean defaultValue) {
		try {
			Property environment = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getBooleanValue(ne, environment);
		} catch (PropertyLookupException e) {
			return defaultValue;
		}
	}

	public static String getEnvContinuousDynamics(final NamedElement ne, final String propertySet,
			final String propertyName) {
		try {
			Property environment = GetProperties.lookupPropertyDefinition(ne, propertySet, propertyName);
			return PropertyUtils.getStringValue(ne, environment);
		} catch (PropertyLookupException e) {
			return null;
		}
	}

	public static boolean isPortConnection(final ConnectionInstance conn) {
		return conn.getKind() == ConnectionKind.PORT_CONNECTION;
	}

	public static boolean isDataPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.DATA_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.DATA_PORT;
		}
		return false;
	}

	public static boolean isEventDataPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.EVENT_DATA_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.EVENT_DATA_PORT;
		}
		return false;
	}

	public static boolean isEventPortConnection(final ConnectionInstance conn) {
		ConnectionInstanceEnd src = conn.getSource();
		if (src instanceof FeatureInstance) {
			return ((FeatureInstance) src).getCategory() == FeatureCategory.EVENT_PORT;
		}
		ConnectionInstanceEnd dst = conn.getDestination();
		if (dst instanceof FeatureInstance) {
			return ((FeatureInstance) dst).getCategory() == FeatureCategory.EVENT_PORT;
		}
		return false;
	}

	public static EnumerationLiteral getConnectionTiming(final ConnectionInstance conn) {
		try {
			Property timing = GetProperties.lookupPropertyDefinition(conn, CommunicationProperties._NAME,
					CommunicationProperties.TIMING);
			return PropertyUtils.getEnumLiteral(conn, timing);
		}
		catch (PropertyLookupException e) {
			return null;
		}
	}

	public static boolean isDelayedPortConnection(final ConnectionInstance conn) {
		if (isPortConnection(conn)) {
			EnumerationLiteral el = getConnectionTiming(conn);
			final String name = el.getName();
			return name.equalsIgnoreCase(CommunicationProperties.DELAYED);
		}
		return false;
	}

	public static boolean isImmediatePortConnection(final ConnectionInstance conn) {
		if (isPortConnection(conn)) {
			EnumerationLiteral el = getConnectionTiming(conn);
			final String name = el.getName();
			return name.equalsIgnoreCase(CommunicationProperties.IMMEDIATE);
		}
		return false;
	}

	public static ComponentInstance getSrcComponent(final ConnectionInstance conni) {
		ConnectionInstanceEnd srcEnd = conni.getSource();
		if (srcEnd instanceof ComponentInstance) {
			if (((ComponentInstance) srcEnd).getCategory() == ComponentCategory.DATA) {
				return srcEnd.getContainingComponentInstance();
			}
			return (ComponentInstance) srcEnd;
		}
		if (srcEnd instanceof FeatureInstance) {
			return srcEnd.getContainingComponentInstance();
		}
		return null;
	}

	public static FeatureInstance getSrcFeaeture(final ConnectionInstance conni) {
		ConnectionInstanceEnd srcEnd = conni.getSource();
		if (srcEnd instanceof FeatureInstance) {
			return (FeatureInstance) srcEnd;
		}
		return null;
	}

	public static ComponentInstance getDstComponent(final ConnectionInstance conni) {
		ConnectionInstanceEnd dstEnd = conni.getDestination();
		if (dstEnd instanceof ComponentInstance) {
			if (((ComponentInstance) dstEnd).getCategory() == ComponentCategory.DATA) {
				return dstEnd.getContainingComponentInstance();
			}
			return (ComponentInstance) dstEnd;
		}
		if (dstEnd instanceof FeatureInstance) {
			return dstEnd.getContainingComponentInstance();
		}
		return null;
	}

	public static FeatureInstance getDstFeaeture(final ConnectionInstance conni) {
		ConnectionInstanceEnd dstEnd = conni.getSource();
		if (dstEnd instanceof FeatureInstance) {
			return (FeatureInstance) dstEnd;
		}
		return null;
	}
}
