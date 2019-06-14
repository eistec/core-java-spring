package eu.arrowhead.core.serviceregistry.database.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import eu.arrowhead.common.database.entity.ServiceInterface;
import eu.arrowhead.common.database.entity.ServiceRegistry;
import eu.arrowhead.common.database.entity.ServiceRegistryInterfaceConnection;

@RunWith(SpringRunner.class)
public class RegistryUtilsTest {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesNullList() {
		Assert.assertEquals(0, RegistryUtils.normalizeInterfaceNames(null).size());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesEmptyList() {
		Assert.assertEquals(0, RegistryUtils.normalizeInterfaceNames(List.<String>of()).size());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesRemovesNull() {
		Assert.assertEquals(1, RegistryUtils.normalizeInterfaceNames(Arrays.asList("a", null)).size());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesRemovesBlank() {
		Assert.assertEquals(1, RegistryUtils.normalizeInterfaceNames(List.of("a", "", "  ")).size());
	}

	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesUppercaseTrim() {
		final List<String> result = RegistryUtils.normalizeInterfaceNames(List.of(" http-secure-xml "));
		Assert.assertEquals(1, result.size());
		Assert.assertEquals("HTTP-SECURE-XML", result.get(0));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testNormalizeInterfaceNamesAll() {
		final List<String> result = RegistryUtils.normalizeInterfaceNames(getExampleInterfaceNameList());
		Assert.assertEquals(3, result.size());
		Assert.assertEquals("HTTP-SECURE-JSON", result.get(0));
		Assert.assertEquals("HTTP-SECURE-XML", result.get(1));
		Assert.assertEquals("HTTP-INSECURE-JSON", result.get(2));
	}
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings("squid:S2699")
	@Test
	public void testFilterOnInterfacesProvidedServicesNullOrEmpty() {
		RegistryUtils.filterOnInterfaces(null, List.of("HTTP-SECURE-JSON")); // it just shows there is no exception if it called with null first parameter
		RegistryUtils.filterOnInterfaces(List.<ServiceRegistry>of(), List.of("HTTP-SECURE-JSON")); // it just shows there is no exception if it called with empty first parameter
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testFilterOnInterfacesInterfaceNameListNullOrEmpty() {
		final ServiceRegistry sr = new ServiceRegistry();
		final List<ServiceRegistry> providedServices = List.of(sr);
		final List<ServiceRegistry> expectedList = List.of(sr);
		RegistryUtils.filterOnInterfaces(providedServices, null); // it just shows there is no exception and no changes if it called with null second parameter
		Assert.assertEquals(expectedList, providedServices);
		RegistryUtils.filterOnInterfaces(providedServices, List.<String>of()); // it just shows there is no exception and no changes if it called with empty second parameter
		Assert.assertEquals(expectedList, providedServices);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testFilterOnInterfacesUnknownInterface() {
		final List<ServiceRegistry> providedServices = getProvidedServices();
		RegistryUtils.filterOnInterfaces(providedServices, List.of("HTTP-INSECURE-XML"));
		Assert.assertEquals(0, providedServices.size());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testFilterOnInterfacesGood() {
		final List<ServiceRegistry> providedServices = getProvidedServices();
		Assert.assertEquals(3, providedServices.size());
		RegistryUtils.filterOnInterfaces(providedServices, List.of("HTTP-SECURE-JSON"));
		Assert.assertEquals(2, providedServices.size());
		Assert.assertEquals(1, providedServices.get(0).getId());
		Assert.assertEquals(3, providedServices.get(1).getId());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void testFilterOnInterfacesGood2() {
		final List<ServiceRegistry> providedServices = getProvidedServices();
		Assert.assertEquals(3, providedServices.size());
		final List<ServiceRegistry> expectedList = new ArrayList<ServiceRegistry>(providedServices);
		RegistryUtils.filterOnInterfaces(providedServices, List.of("HTTP-SECURE-JSON", "HTTP-INSECURE-JSON"));
		Assert.assertEquals(3, providedServices.size());
		Assert.assertEquals(expectedList, providedServices);
	}
 	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private List<String> getExampleInterfaceNameList() {
		return Arrays.asList("HTTP-SECURE-JSON", null, "", " ", "http-secure-xml", "   HTTP-INSECURE-JSON   ");
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<ServiceRegistry> getProvidedServices() {
		final ServiceInterface intf1 = new ServiceInterface("HTTP-SECURE-JSON");
		final ServiceInterface intf2 = new ServiceInterface("HTTP-SECURE-XML");
		final ServiceInterface intf3 = new ServiceInterface("HTTP-INSECURE-JSON");
		
		final ServiceRegistry srEntry1 = new ServiceRegistry();
		srEntry1.setId(1);
		srEntry1.getInterfaceConnections().add(new ServiceRegistryInterfaceConnection(srEntry1, intf1));
		
		final ServiceRegistry srEntry2 = new ServiceRegistry();
		srEntry2.setId(2);
		srEntry2.getInterfaceConnections().add(new ServiceRegistryInterfaceConnection(srEntry2, intf3));
		
		final ServiceRegistry srEntry3 = new ServiceRegistry();
		srEntry3.setId(3);
		srEntry3.getInterfaceConnections().add(new ServiceRegistryInterfaceConnection(srEntry3, intf1));
		srEntry3.getInterfaceConnections().add(new ServiceRegistryInterfaceConnection(srEntry3, intf2));
		
		return new ArrayList<>(List.of(srEntry1, srEntry2, srEntry3));
	}
}