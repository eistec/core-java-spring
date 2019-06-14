package eu.arrowhead.core.serviceregistry.database.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.arrowhead.common.database.entity.ServiceRegistry;
import eu.arrowhead.common.database.entity.ServiceRegistryInterfaceConnection;

public class RegistryUtils {
	
	//=================================================================================================
	// members
	
	private static Logger logger = LogManager.getLogger(RegistryUtils.class);
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public static List<String> normalizeInterfaceNames(final List<String> interfaceNames) { 
		logger.debug("normalizeInterfaceNames started...");
		if (interfaceNames == null) {
			return List.<String>of();
		}
		
		return interfaceNames.stream().filter(Objects::nonNull).filter(e -> !e.isBlank()).map(e -> e.toUpperCase().trim()).collect(Collectors.toList());
	}
	
	//-------------------------------------------------------------------------------------------------
	// This method may CHANGE the content of providedServices
	public static void filterOnInterfaces(final List<ServiceRegistry> providedServices, final List<String> interfaceRequirements) {
		logger.debug("filterOnInterfaces started...");
		if (providedServices == null || providedServices.isEmpty() || interfaceRequirements == null || interfaceRequirements.isEmpty()) {
			return;
		}
		
		final List<ServiceRegistry> toBeRemoved = new ArrayList<>();
		for (final ServiceRegistry srEntry : providedServices) {
			boolean remove = true;
			for (final ServiceRegistryInterfaceConnection conn : srEntry.getInterfaceConnections()) {
				if (interfaceRequirements.contains(conn.getServiceInterface().getInterfaceName())) {
					remove = false;
					break;
				}
			}
			
			if (remove) {
				toBeRemoved.add(srEntry);
			}
		}
		
		providedServices.removeAll(toBeRemoved);
	}

	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private RegistryUtils() {
		throw new UnsupportedOperationException();
	}
}