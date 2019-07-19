package eu.arrowhead.core.orchestrator.database.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.database.entity.Cloud;
import eu.arrowhead.common.database.entity.OrchestratorStore;
import eu.arrowhead.common.database.entity.ServiceDefinition;
import eu.arrowhead.common.database.entity.ServiceInterface;
import eu.arrowhead.common.database.entity.System;
import eu.arrowhead.common.database.repository.CloudRepository;
import eu.arrowhead.common.database.repository.OrchestratorStoreRepository;
import eu.arrowhead.common.database.repository.ServiceDefinitionRepository;
import eu.arrowhead.common.database.repository.ServiceInterfaceRepository;
import eu.arrowhead.common.database.repository.SystemRepository;
import eu.arrowhead.common.dto.CloudRequestDTO;
import eu.arrowhead.common.dto.OrchestratorStoreModifyPriorityRequestDTO;
import eu.arrowhead.common.dto.OrchestratorStoreRequestDTO;
import eu.arrowhead.common.dto.SystemRequestDTO;
import eu.arrowhead.common.exception.InvalidParameterException;

@RunWith (SpringRunner.class)
public class OrchestratorStoreDBServiceTest {

	//=================================================================================================
	// members
	
	@InjectMocks
	OrchestratorStoreDBService orchestratorStoreDBService; 
	
	@Mock
	OrchestratorStoreRepository orchestratorStoreRepository;
	
	@Mock
	SystemRepository systemRepository;
	
	@Mock
	ServiceDefinitionRepository serviceDefinitionRepository;
	
	@Mock
	CloudRepository cloudRepository;
	
	@Mock
	ServiceInterfaceRepository serviceInterfaceRepository;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	// Test getOrchestratorStoreById
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getOrchestratorStoreByIdTest() {
		
		final Optional<OrchestratorStore> orchestratorStoreOptional = Optional.of(getOrchestratorStore());
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(orchestratorStoreOptional);
		
		orchestratorStoreDBService.getOrchestratorStoreById( 1);		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoreByIdWithInvalidIdTest() {
		
		final Optional<OrchestratorStore> orchestratorStoreOptional = Optional.of(getOrchestratorStore());
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(orchestratorStoreOptional);
		
		orchestratorStoreDBService.getOrchestratorStoreById( -1);		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoreByIdNotInDBTest() {
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.getOrchestratorStoreById( 1);		
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test getOrchestratorStoreById
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getOrchestratorStoreEntriesResponseOKTest() {
		
		when(orchestratorStoreRepository.findAll(any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoreEntriesResponse(0, 10, Direction.ASC, "id");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoreEntriesResponseWithNotValidSortFieldTest() {
		
		when(orchestratorStoreRepository.findAll(any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoreEntriesResponse(0, 10, Direction.ASC, "notValid");
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test getAllTopPriorityOrchestratorStoreEntriesResponse
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getAllTopPriorityOrchestratorStoreEntriesResponseOKTest() {
		
		when(orchestratorStoreRepository.findAllByPriority(anyInt(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getAllTopPriorityOrchestratorStoreEntriesResponse(0, 10, Direction.ASC, "id");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getAllTopPriorityOrchestratorStoreEntriesResponseWithNotValidSortFieldTest() {
		
		when(orchestratorStoreRepository.findAllByPriority(anyInt(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getAllTopPriorityOrchestratorStoreEntriesResponse(0, 10, Direction.ASC, "notValid");
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test getOrchestratorStoresByConsumerResponse
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getOrchestratorStoresByConsumerResponseOKTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", 1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoresByConsumerResponseWithNotValidSortFieldTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "notValid", 1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoresByConsumerResponseWithNotValidSystemIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", -1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoresByConsumerResponseWithNotValidServiceDefinitionIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", 1L, " ");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoresByConsumerResponseWithSystemIdNotInDBTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", 1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getOrchestratorStoresByConsumerResponseWithServiceDefinitionIdNotInDBTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", 1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getOrchestratorStoresByConsumerResponseNotInDBTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreListNotInDB());
		
		orchestratorStoreDBService.getOrchestratorStoresByConsumerResponse(0, 10, Direction.ASC, "id", 1L, "serviceDefinition");
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test createOrchestratorStoresResponse
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoresResponseOKTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoresResponse(getOrchestratorStoreRequestDTOListForTest(3));
	}

	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoresResponseWithNullRequestTest() {
		
		orchestratorStoreDBService.createOrchestratorStoresResponse(null);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoresResponseWithEmptyRequestTest() {
		
		orchestratorStoreDBService.createOrchestratorStoresResponse(getOrchestratorStoreRequestDTOEmptyListForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoresResponseWithSomeEmptyOrchestratoreStoreTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoresResponse(getOrchestratorStoreRequestDTOListWithSomeEmptyOrchestratoreStoreForTest(3));
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test createOrchestratorStoreEntity
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidConsumerSystemIdTest() {
				
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidConsumerSystemIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidProviderSystemTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidProviderSystemForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidServiceDefinitionIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidServiceDefinitionIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidPriorityTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidPriorityForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidCloudIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidCloudForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithInvalidServiceInterfaceIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithInvalidServiceInterfaceNameForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNullServiceInterfaceIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullServiceInterfaceIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNullServiceDefinitionIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullServiceDefinitionIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNullConsumerSystemIdTest() {

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullConsumerSystemIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNullProviderSystemIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullProviderSystemForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNullPriorityTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullPriorityForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoreEntityWithNullCloudIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithNullCloudIdForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNotInDBServiceDefinitionIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNotInDBConsumerSystemIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNotInDBProviderSystemIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithNotInDBCloudIdTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findById(anyLong())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createOrchestratorStoreEntityWithUniqueConstraintTest() {
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.of(getOrchestratorStore()));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoreEntityWithPriorityCombinationNotPresentInDBTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoreEntityWithPriorityNotPresentInDBEntitiesTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOWithPriorityNotPresentInDBForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createOrchestratorStoreEntityWithPriorityPresentInDBEntitiesTest() {
		
		when(systemRepository.findById(anyLong())).thenReturn(Optional.of(getSystemForTest()));
		when(cloudRepository.findByOperatorAndName(any(), any())).thenReturn(Optional.of(getProviderCloudForTest()));		
		when(systemRepository.findBySystemNameAndAddressAndPort(any(), any(), anyInt())).thenReturn(Optional.of(getSystemForTest()));
		when(serviceDefinitionRepository.findByServiceDefinition(any())).thenReturn(Optional.of(getServiceDefinitionForTest()));
		when(serviceInterfaceRepository.findByInterfaceName(any())).thenReturn(Optional.of(getInterfaceForTest()));
		when(orchestratorStoreRepository.findByConsumerSystemAndServiceDefinitionAndProviderSystemIdAndServiceInterfaceAndForeign(any(), any(), anyLong(), any(), anyBoolean())).thenReturn(Optional.ofNullable(null));		
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any(), any(PageRequest.class))).thenReturn(getPageOfOrchestratorStoreList());
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(3));
		when(orchestratorStoreRepository.saveAndFlush(any())).thenReturn(getOrchestratorStore());
		
		orchestratorStoreDBService.createOrchestratorStoreEntity(getOrchestratorStoreRequestDTOForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test removeOrchestratorStoreById
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void removeOrchestratorStoreByIdWithLeftPriorityCombinationPresentInDBOkTest() {
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.of(getOrchestratorStore()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForRemoveOrchestratorStoreTest(3));
		
		orchestratorStoreDBService.removeOrchestratorStoreById(getIdForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void removeOrchestratorStoreByIdWithLeftPriorityCombinationNotPresentInDBOkTest() {
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.of(getOrchestratorStore()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(new ArrayList<OrchestratorStore>());
		
		orchestratorStoreDBService.removeOrchestratorStoreById(getIdForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void removeOrchestratorStoreByIdWithInvalidIdTest() {
		
		orchestratorStoreDBService.removeOrchestratorStoreById(getInvalidIdForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void removeOrchestratorStoreByIdWithIdNotInDBTest() {
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.removeOrchestratorStoreById(getIdForTest());
		
	}
	
	//-------------------------------------------------------------------------------------------------
	// Test modifyOrchestratorStorePriorityResponse
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void modifyOrchestratorStorePriorityResponseOKTest() {
		
		final int testMapSize = 3;
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.of(getOrchestratorStore()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(getOrchestratorStoreListForTest(testMapSize));

		orchestratorStoreDBService.modifyOrchestratorStorePriorityResponse(getOrchestratorStoreModifyPriorityRequestDTO(testMapSize));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void modifyOrchestratorStorePriorityResponseNullRequestTest() {

		orchestratorStoreDBService.modifyOrchestratorStorePriorityResponse(null);
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void modifyOrchestratorStorePriorityResponseEmptyRequestTest() {

		orchestratorStoreDBService.modifyOrchestratorStorePriorityResponse(getOrchestratorStoreModifyPriorityRequestDTOWithEmptyRequest());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void modifyOrchestratorStorePriorityResponseMapKeyStoreIDNotInDBTest() {

		final int testMapSize = 3;
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));
		
		orchestratorStoreDBService.modifyOrchestratorStorePriorityResponse(getOrchestratorStoreModifyPriorityRequestDTO(testMapSize));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void modifyOrchestratorStorePriorityResponseMapKeyConsumerAndServiceDefinitionCombinationNotInDBTest() {

		final int testMapSize = 3;
		
		when(orchestratorStoreRepository.findById(anyLong())).thenReturn(Optional.of(getOrchestratorStore()));
		when(orchestratorStoreRepository.findAllByConsumerSystemAndServiceDefinition(any(), any())).thenReturn(new ArrayList<OrchestratorStore>());
		
		orchestratorStoreDBService.modifyOrchestratorStorePriorityResponse(getOrchestratorStoreModifyPriorityRequestDTO(testMapSize));
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOForTest() {
		
		return getLocalOrchestratorStoreRequestDTOForTest();
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getLocalOrchestratorStoreRequestDTOForTest() {
		
		final String serviceDefinitionName = "serviceDefinitionNameForTest";
		final Long consumerSystemId = 1L;
		final SystemRequestDTO providerSystemDTO = getProviderSystemRequestDTOForTest();
		final CloudRequestDTO cloudDTO = null;
		final String serviceInterfaceName = "serviceIntrfaceNameForTest";
		final Integer priority = 1;	
		final Map<String,String> attribute = new HashMap<String, String>();
		
		return new OrchestratorStoreRequestDTO(
				serviceDefinitionName, 
				consumerSystemId, 
				providerSystemDTO, 
				cloudDTO, 
				serviceInterfaceName, 
				priority, 
				attribute);
	}
	
	//-------------------------------------------------------------------------------------------------
	private CloudRequestDTO getLocalProviderCloudRequestDTOForTest() {
		
		final String operator = "operatorForTest";
		final String name = "cloudName";
		final String address = "localhost";
		final Integer port = 12345;
		final Boolean ownCloud = true;	
		
		final CloudRequestDTO cloudRequestDTO = new CloudRequestDTO();
		
		cloudRequestDTO.setOperator(operator);
		cloudRequestDTO.setName(name);
		cloudRequestDTO.setAddress(address);
		cloudRequestDTO.setPort(port);
		cloudRequestDTO.setOwnCloud(ownCloud);
		
		return cloudRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private SystemRequestDTO getProviderSystemRequestDTOForTest() {
		
		final SystemRequestDTO systemRequestDTO = new SystemRequestDTO();
		systemRequestDTO.setAddress("localhost");
		systemRequestDTO.setSystemName("systemNameForTest");
		systemRequestDTO.setPort(12345);
		
		return systemRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidServiceDefinitionIdForTest() {
		
		final String serviceDefinitionName = " ";

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setServiceDefinitionName(serviceDefinitionName);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidConsumerSystemIdForTest() {
		
		final Long consumerSystemId = -1L;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setConsumerSystemId(consumerSystemId);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidProviderSystemForTest() {
		
		final SystemRequestDTO providerSystemRequestDTO = getProviderSystemRequestDTOForTest();
		providerSystemRequestDTO.setAddress("");
		
		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setProviderSystemDTO(providerSystemRequestDTO);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidCloudForTest() {
		
		final CloudRequestDTO providerCloudRequestDTO = getLocalProviderCloudRequestDTOForTest();
		providerCloudRequestDTO.setName(" ");
		
		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setCloudDTO(providerCloudRequestDTO);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidServiceInterfaceNameForTest() {
		
		final String serviceInterfaceName = " ";

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setServiceInterfaceName(serviceInterfaceName);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithInvalidPriorityForTest() {

		final Integer priority = -1;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setPriority(priority);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullServiceDefinitionIdForTest() {
		
		final String serviceDefinitionName = null;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setServiceDefinitionName(serviceDefinitionName);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullConsumerSystemIdForTest() {
		
		final Long consumerSystemId = null;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setConsumerSystemId(consumerSystemId);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullProviderSystemForTest() {
		
		final CloudRequestDTO providerCloudRequestDTO = null;
		
		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setCloudDTO(providerCloudRequestDTO);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullCloudIdForTest() {
		
		final CloudRequestDTO providerCloudRequestDTO = null;
		
		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setCloudDTO(providerCloudRequestDTO);
		
		return orchestratorStoreRequestDTO;

	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullServiceInterfaceIdForTest() {
		
		final String serviceInterfaceName = null;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setServiceInterfaceName(serviceInterfaceName);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithNullPriorityForTest() {
		
		final Integer priority = null;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setPriority(priority);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreRequestDTO getOrchestratorStoreRequestDTOWithPriorityNotPresentInDBForTest() {
		
		final Integer priority = 1;

		final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
		orchestratorStoreRequestDTO.setPriority(priority);
		
		return orchestratorStoreRequestDTO;
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<OrchestratorStoreRequestDTO> getOrchestratorStoreRequestDTOListForTest(final int listSize) {
		
		final List<OrchestratorStoreRequestDTO> orchestratorStoreRequestDTOList = new ArrayList<OrchestratorStoreRequestDTO>(listSize);
		
		for (int i = 0; i < listSize; i++) {
			
			final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
			orchestratorStoreRequestDTOList.add(orchestratorStoreRequestDTO);
		}
		return orchestratorStoreRequestDTOList;
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<OrchestratorStoreRequestDTO> getOrchestratorStoreRequestDTOEmptyListForTest() {
		
		final List<OrchestratorStoreRequestDTO> orchestratorStoreRequestDTOList = new ArrayList<>();
		
		return orchestratorStoreRequestDTOList;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStore getOrchestratorStore() {
		
		final OrchestratorStore orchestratorStore = new OrchestratorStore(
				getServiceDefinitionForTest(),
				getConsumerSystemForTest(),
				false,
				1L,
				getServiceIntefaceForTest(),
				getPriorityForTest(),
				getAttributeStringForTest(),
				getCreatedAtForTest(),
				getUpdatedAtForTest()
				);
	
		orchestratorStore.setId(getIdForTest());
		
		return orchestratorStore;
	}

	
	//-------------------------------------------------------------------------------------------------
	private List<OrchestratorStore> getOrchestratorStoreListForTest(final int listSize) {
		
		final List<OrchestratorStore> orchestratorStoreList = new ArrayList<>(listSize);
		
		for (int i = 0; i < listSize; i++) {
			
			final OrchestratorStore orchestratorStore = getOrchestratorStore();
			orchestratorStore.setId(i + 1L);
			orchestratorStore.setProviderSystemId(i + 1L);
			orchestratorStore.setPriority(i + 1);
			orchestratorStoreList.add(orchestratorStore);
		}
		return orchestratorStoreList;
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<OrchestratorStore> getOrchestratorStoreListForRemoveOrchestratorStoreTest(final int listSize) {
		
		final List<OrchestratorStore> orchestratorStoreList = new ArrayList<>(listSize);
		
		for (int i = 1; i < listSize+1; i++) {
			
			final OrchestratorStore orchestratorStore = getOrchestratorStore();
			orchestratorStore.setId(i);
			orchestratorStore.setProviderSystemId(i + 1L);
			orchestratorStore.setPriority(i + 1);
			orchestratorStoreList.add(orchestratorStore);
		}
		return orchestratorStoreList;
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<OrchestratorStoreRequestDTO> getOrchestratorStoreRequestDTOListWithSomeEmptyOrchestratoreStoreForTest(final int listSize) {
		
		final List<OrchestratorStoreRequestDTO> orchestratorStoreRequestDTOList = new ArrayList<OrchestratorStoreRequestDTO>(listSize);
		
		for (int i = 0; i < listSize; i++) {
			
			final OrchestratorStoreRequestDTO orchestratorStoreRequestDTO = getOrchestratorStoreRequestDTOForTest();
			orchestratorStoreRequestDTOList.add(orchestratorStoreRequestDTO);
		}
		
		for (int i = 0; i < listSize; i++) {
			
			orchestratorStoreRequestDTOList.add(new OrchestratorStoreRequestDTO());
		}
		
		return orchestratorStoreRequestDTOList;
	}
	//-------------------------------------------------------------------------------------------------
	private PageImpl<OrchestratorStore> getPageOfOrchestratorStoreList() {
		final List<OrchestratorStore>	orchestratorStoreList = List.of(getOrchestratorStore());
		
		return new PageImpl<OrchestratorStore>(orchestratorStoreList);
	}
	
	//-------------------------------------------------------------------------------------------------
	private PageImpl<OrchestratorStore> getPageOfOrchestratorStoreListNotInDB() {
		final List<OrchestratorStore>	orchestratorStoreList = new ArrayList<OrchestratorStore>();
		
		return new PageImpl<OrchestratorStore>(orchestratorStoreList);
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreModifyPriorityRequestDTO getOrchestratorStoreModifyPriorityRequestDTO(final int size) {
		final OrchestratorStoreModifyPriorityRequestDTO request = new OrchestratorStoreModifyPriorityRequestDTO();
		
		final Map<Long, Integer> priorityMap = new HashMap<Long, Integer>(size); 
		for (int i = 0; i < size; i++) {
			priorityMap.put(i + 1L, i + 1);
		}		
		request.setPriorityMap(priorityMap);
		
		return request;
	}
	
	//-------------------------------------------------------------------------------------------------
	private OrchestratorStoreModifyPriorityRequestDTO getOrchestratorStoreModifyPriorityRequestDTOWithEmptyRequest() {
		final OrchestratorStoreModifyPriorityRequestDTO request = new OrchestratorStoreModifyPriorityRequestDTO();
		
		final Map<Long, Integer> priorityMap = new HashMap<>(0); 
		request.setPriorityMap(priorityMap);
		
		return request;
	}
	//-------------------------------------------------------------------------------------------------
	private ZonedDateTime getUpdatedAtForTest() {
			
		return Utilities.parseUTCStringToLocalZonedDateTime("2019-07-04 14:43:19");
	}
	
	//-------------------------------------------------------------------------------------------------
	private ZonedDateTime getCreatedAtForTest() {
			
		return Utilities.parseUTCStringToLocalZonedDateTime("2019-07-04 14:43:19");
	}
	
	//-------------------------------------------------------------------------------------------------
	private String getAttributeStringForTest() {
			
		return null;
	}
	
	//-------------------------------------------------------------------------------------------------
	private Integer getPriorityForTest() {
			
		return 1;
	}

	//-------------------------------------------------------------------------------------------------
	private Cloud getProviderCloudForTest() {
			
		final Cloud cloud =  new Cloud(
					"operator",
					"name", 
					"address", 
					1234, 
					"gatekeeperServiceUri", 
					"authenticationInfo", 
					true, 
					true, 
					true);
		cloud.setId(getIdForTest());
		cloud.setCreatedAt(getCreatedAtForTest());
		cloud.setUpdatedAt(getUpdatedAtForTest());
		
		return cloud;
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceInterface getServiceIntefaceForTest() {
		
		final ServiceInterface serviceInterface = new ServiceInterface("HTTP-SECURE-XML");
		
		return serviceInterface;
	}
	
	//-------------------------------------------------------------------------------------------------
	private System getSystemForTest() {
			
		final System system = new System(
				"systemName",
				"address", 
				1234, 
				null);
		
		system.setId(getIdForTest());
		system.setCreatedAt(getCreatedAtForTest());
		system.setUpdatedAt(getUpdatedAtForTest());
		
		return system;
	}
	
	//-------------------------------------------------------------------------------------------------
	private System getConsumerSystemForTest() {
			
		return getSystemForTest();
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceDefinition getServiceDefinitionForTest() {
			
		final ServiceDefinition serviceDefinition = new ServiceDefinition("serviceDefinition");
		serviceDefinition.setId(getIdForTest());
		serviceDefinition.setCreatedAt(getCreatedAtForTest());
		serviceDefinition.setUpdatedAt(getUpdatedAtForTest());
		
		return serviceDefinition;
	}

	//-------------------------------------------------------------------------------------------------
	private long getIdForTest() {
		
		return 1L;
	}
	
	//-------------------------------------------------------------------------------------------------
	private long getInvalidIdForTest() {
		
		return -1L;
	}
	
	//-------------------------------------------------------------------------------------------------
	private ServiceInterface getInterfaceForTest() {
		
		final ServiceInterface serviceInterface = new ServiceInterface("interfaceName");
		serviceInterface.setId(getIdForTest());
		serviceInterface.setCreatedAt(getCreatedAtForTest());
		
		return serviceInterface;
	}
}