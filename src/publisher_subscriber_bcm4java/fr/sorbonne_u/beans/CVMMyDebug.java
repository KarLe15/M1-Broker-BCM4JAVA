package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import fr.sorbonne_u.components.helpers.CVMDebugModesI;
/**
 * Contains all the enum useful for debugging
 */
public enum CVMMyDebug implements CVMDebugModesI {
	/** trace the actions done by registries.							*/
	REGISTRY,
	/** trace the actions done for plug-in management.					*/
	PLUGIN,
	/** trace the actions done by the distributed CVM cyclic barrier.		*/
	CYCLIC_BARRIER,
	/** trace the actions made to manage the life cycle of the component
	 *  virtual machine.													*/
	LIFE_CYCLE,
	/** trace the actions made to manage the deployment of components on
	 *  the current component virtual machine.							*/
	COMPONENT_DEPLOYMENT,
	/** trace the actions made for the publication of ports.				*/
	PUBLIHSING,
	/** trace the actions pertaining to the connections of ports.			*/
	CONNECTING,
	/** trace the actions made when calling component services through
	 *  ports and connectors.											*/
	CALLING,

	PRINTING_INTENSE,
	SIMPLE_LOGGING,
	INTENSE_LOGGING
}
