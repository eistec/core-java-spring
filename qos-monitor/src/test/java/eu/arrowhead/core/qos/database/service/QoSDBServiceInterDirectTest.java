package eu.arrowhead.core.qos.database.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.HibernateException;
import org.icmp4j.IcmpPingResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.database.entity.Cloud;
import eu.arrowhead.common.database.entity.QoSInterDirectMeasurement;
import eu.arrowhead.common.database.entity.QoSInterDirectPingMeasurement;
import eu.arrowhead.common.database.entity.QoSInterDirectPingMeasurementLog;
import eu.arrowhead.common.database.entity.QoSInterDirectPingMeasurementLogDetails;
import eu.arrowhead.common.database.repository.QoSInterDirectMeasurementPingRepository;
import eu.arrowhead.common.database.repository.QoSInterDirectMeasurementRepository;
import eu.arrowhead.common.database.repository.QoSInterDirectPingMeasurementLogDetailsRepository;
import eu.arrowhead.common.database.repository.QoSInterDirectPingMeasurementLogRepository;
import eu.arrowhead.common.dto.internal.CloudResponseDTO;
import eu.arrowhead.common.dto.internal.DTOConverter;
import eu.arrowhead.common.dto.shared.QoSMeasurementType;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;
import eu.arrowhead.core.qos.dto.PingMeasurementCalculationsDTO;

@RunWith(SpringRunner.class)
public class QoSDBServiceInterDirectTest {
	
	//=================================================================================================
	// members
	@InjectMocks
	private QoSDBService qoSDBService;
	
	@Mock
	private QoSInterDirectMeasurementRepository qoSInterDirectMeasurementRepository;
	
	@Mock
	private QoSInterDirectMeasurementPingRepository qoSInterDirectMeasurementPingRepository;

	@Mock
	private QoSInterDirectPingMeasurementLogRepository qoSInterDirectPingMeasurementLogRepository;
	
	@Mock
	private QoSInterDirectPingMeasurementLogDetailsRepository qoSInterDirectPingMeasurementLogDetailsRepository;
	
	private static final String EMPTY_OR_NULL_ERROR_MESSAGE = " is empty or null";
	private static final String NULL_ERROR_MESSAGE = " is null";
	
	//=================================================================================================
	// methods
	
	//=================================================================================================
	// Tests of updateInterDirectCountStartedAt

	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void updateInterDirectCountStartedAtTest() {
		final ZonedDateTime testStartedAt = ZonedDateTime.now().minusMinutes(1);
		final List<QoSInterDirectPingMeasurement> measurementList = getQosInterDirectPingMeasurementListForTest(3);
		final ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);

		when(qoSInterDirectMeasurementPingRepository.findAll()).thenReturn(measurementList);
		when(qoSInterDirectMeasurementPingRepository.saveAll(valueCapture.capture())).thenReturn(List.of());
		doNothing().when(qoSInterDirectMeasurementPingRepository).flush();

		qoSDBService.updateInterDirectCountStartedAt();

		verify(qoSInterDirectMeasurementPingRepository, times(1)).findAll();
		verify(qoSInterDirectMeasurementPingRepository, times(1)).saveAll(any());
		verify(qoSInterDirectMeasurementPingRepository, times(1)).flush();

		final List<QoSInterDirectPingMeasurement> captured = valueCapture.getValue();
		assertEquals(measurementList.size(), captured.size());
		for (final QoSInterDirectPingMeasurement measurement : captured) {
			assertEquals(0, measurement.getSent());
			assertEquals(0, measurement.getReceived());
			assertTrue(measurement.getCountStartedAt().isAfter(testStartedAt));
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = ArrowheadException.class)
	public void updateInterDirectCountStartedAtFlushThrowDatabaseExceptionTest() {
		final ZonedDateTime testStartedAt = ZonedDateTime.now().minusMinutes(1);;
		final List<QoSInterDirectPingMeasurement> measurementList = getQosInterDirectPingMeasurementListForTest(3);
		final ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);

		when(qoSInterDirectMeasurementPingRepository.findAll()).thenReturn(measurementList);
		when(qoSInterDirectMeasurementPingRepository.saveAll(valueCapture.capture())).thenReturn(List.of());
		doThrow(HibernateException.class).when(qoSInterDirectMeasurementPingRepository).flush();

		try {
			qoSDBService.updateInterDirectCountStartedAt();
		} catch (final Exception ex) {
			assertEquals(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG, ex.getMessage());

			verify(qoSInterDirectMeasurementPingRepository, times(1)).findAll();
			verify(qoSInterDirectMeasurementPingRepository, times(1)).saveAll(any());
			verify(qoSInterDirectMeasurementPingRepository, times(1)).flush();

			final List<QoSInterDirectPingMeasurement> captured = valueCapture.getValue();
			assertEquals(measurementList.size(), captured.size());
			for (final QoSInterDirectPingMeasurement measurement : captured) {
				assertEquals(0, measurement.getSent());
				assertEquals(0, measurement.getReceived());
				assertTrue(measurement.getCountStartedAt().isAfter(testStartedAt));
			}

			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test(expected = ArrowheadException.class)
	public void updateInterDirectCountStartedAtSaveAllThrowDatabaseExceptionTest() {
		final ZonedDateTime testStartedAt = ZonedDateTime.now().minusMinutes(1);;
		final List<QoSInterDirectPingMeasurement> measurementList = getQosInterDirectPingMeasurementListForTest(3);
		final ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);

		when(qoSInterDirectMeasurementPingRepository.findAll()).thenReturn(measurementList);
		when(qoSInterDirectMeasurementPingRepository.saveAll(valueCapture.capture())).thenThrow(HibernateException.class);
		doNothing().when(qoSInterDirectMeasurementPingRepository).flush();

		try {
			qoSDBService.updateInterDirectCountStartedAt();
		} catch (final Exception ex) {
			assertEquals(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG, ex.getMessage());

			verify(qoSInterDirectMeasurementPingRepository, times(1)).findAll();
			verify(qoSInterDirectMeasurementPingRepository, times(1)).saveAll(any());
			verify(qoSInterDirectMeasurementPingRepository, times(0)).flush();

			final List<QoSInterDirectPingMeasurement> captured = valueCapture.getValue();
			assertEquals(measurementList.size(), captured.size());
			for (final QoSInterDirectPingMeasurement measurement : captured) {
				assertEquals(0, measurement.getSent());
				assertEquals(0, measurement.getReceived());
				assertTrue(measurement.getCountStartedAt().isAfter(testStartedAt));
			}

			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void updateInterDirectCountStartedAtFindAllThrowDatabaseExceptionTest() {
		final List<QoSInterDirectPingMeasurement> measurementList = getQosInterDirectPingMeasurementListForTest(3);

		when(qoSInterDirectMeasurementPingRepository.findAll()).thenThrow(HibernateException.class);
		when(qoSInterDirectMeasurementPingRepository.saveAll(any())).thenReturn(measurementList);
		doNothing().when(qoSInterDirectMeasurementPingRepository).flush();

		try {
			qoSDBService.updateInterDirectCountStartedAt();
		} catch (final Exception ex) {
			assertEquals(CoreCommonConstants.DATABASE_OPERATION_EXCEPTION_MSG, ex.getMessage());

			verify(qoSInterDirectMeasurementPingRepository, times(1)).findAll();
			verify(qoSInterDirectMeasurementPingRepository, times(0)).saveAll(any());
			verify(qoSInterDirectMeasurementPingRepository, times(0)).flush();

			throw ex;
		}
	}
	
	//=================================================================================================
	// Tests of createInterDirectMeasurement
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createInterDirectMeasurementTest() {
		final QoSInterDirectMeasurement measurement = getQoSInterDirectMeasurementForTest(QoSMeasurementType.PING);
		
		final ArgumentCaptor<QoSInterDirectMeasurement> valueCapture = ArgumentCaptor.forClass(QoSInterDirectMeasurement.class);
		when(qoSInterDirectMeasurementRepository.saveAndFlush(valueCapture.capture())).thenReturn(measurement);
		
		qoSDBService.createInterDirectMeasurement(measurement.getCloud(), measurement.getAddress(), measurement.getMeasurementType(), measurement.getLastMeasurementAt());
		
		verify(qoSInterDirectMeasurementRepository, times(1)).saveAndFlush(any());
		
		final QoSInterDirectMeasurement captured = valueCapture.getValue();
		assertEquals(measurement.getCloud().getId(), captured.getCloud().getId());
		assertEquals(measurement.getMeasurementType(), captured.getMeasurementType());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void createInterDirectMeasurementSaveAndFlushThrowExceptionTest() {
		final QoSInterDirectMeasurement measurement = getQoSInterDirectMeasurementForTest(QoSMeasurementType.PING);
		final ArgumentCaptor<QoSInterDirectMeasurement> valueCapture = ArgumentCaptor.forClass(QoSInterDirectMeasurement.class);
		when(qoSInterDirectMeasurementRepository.saveAndFlush(valueCapture.capture())).thenThrow(HibernateException.class);
		
		try {
			qoSDBService.createInterDirectMeasurement(measurement.getCloud(), measurement.getAddress(), measurement.getMeasurementType(), measurement.getLastMeasurementAt());			
		} catch (Exception ex) {
			verify(qoSInterDirectMeasurementRepository, times(1)).saveAndFlush(any());
			
			final QoSInterDirectMeasurement captured = valueCapture.getValue();
			assertEquals(measurement.getCloud().getId(), captured.getCloud().getId());
			assertEquals(measurement.getMeasurementType(), captured.getMeasurementType());
			
			throw ex;
		}		
	}
	
	//=================================================================================================
	// Tests of getInterDirectMeasurementByCloud
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void getInterDirectMeasurementByCloudTest() {
		final QoSInterDirectMeasurement measurement = getQoSInterDirectMeasurementForTest(QoSMeasurementType.PING);
		final CloudResponseDTO cloudResponseDTO = DTOConverter.convertCloudToCloudResponseDTO(measurement.getCloud());
		
		when(qoSInterDirectMeasurementRepository.findByCloudAndMeasurementType(any(), any())).thenReturn(List.of(measurement));
		qoSDBService.getInterDirectMeasurementByCloud(cloudResponseDTO, measurement.getMeasurementType());
		
		verify(qoSInterDirectMeasurementRepository, times(1)).findByCloudAndMeasurementType(any(), any());
	}
	
	//=================================================================================================
	// Tests of getInterDirectPingMeasurementByMeasurement

	//-------------------------------------------------------------------------------------------------
	@Test
	public void getInterDirectPingMeasurementByMeasurementTest() {
		final QoSInterDirectPingMeasurement measurement = getQosInterDirectPingMeasurementForTest();
		
		when(qoSInterDirectMeasurementPingRepository.findByMeasurement(any())).thenReturn(Optional.of(measurement));
		qoSDBService.getInterDirectPingMeasurementByMeasurement(measurement.getMeasurement());
		
		verify(qoSInterDirectMeasurementPingRepository, times(1)).findByMeasurement(any());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void getInterDirectPingMeasurementByMeasurementWithNullMeasurementTest() {
		try {
			qoSDBService.getInterDirectPingMeasurementByMeasurement(null);					
		} catch (final Exception ex) {
			verify(qoSInterDirectMeasurementPingRepository, times(0)).findByMeasurement(any());
			throw ex;
		}
	}
	
	//=================================================================================================
	// Tests of createInterDirectPingMeasurement
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void createInterDirectPingMeasurementTest() {
		final QoSInterDirectPingMeasurement pingMeasurement = getQosInterDirectPingMeasurementForTest();
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();
		
		when(qoSInterDirectMeasurementPingRepository.saveAndFlush(any())).thenReturn(pingMeasurement);
		
		qoSDBService.createInterDirectPingMeasurement(pingMeasurement.getMeasurement(), calculations, pingMeasurement.getLastAccessAt());
		verify(qoSInterDirectMeasurementPingRepository, times(1)).saveAndFlush(any());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void createInterDirectPingMeasurementSaveAndFlushThrowDatabaseExceptionTest() {
		final QoSInterDirectPingMeasurement pingMeasurement = getQosInterDirectPingMeasurementForTest();
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectMeasurementPingRepository.saveAndFlush(any())).thenThrow(HibernateException.class);
		
		try {
			qoSDBService.createInterDirectPingMeasurement(pingMeasurement.getMeasurement(), calculations, pingMeasurement.getLastAccessAt());			
		} catch (final Exception ex) {
			verify(qoSInterDirectMeasurementPingRepository, times(1)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createInterDirectPingMeasurementWithNullMeasurementTest() {
		final QoSInterDirectPingMeasurement pingMeasurement = getQosInterDirectPingMeasurementForTest();
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();
		when(qoSInterDirectMeasurementPingRepository.saveAndFlush(any())).thenReturn(pingMeasurement);
		
		try {
			qoSDBService.createInterDirectPingMeasurement(null, calculations, pingMeasurement.getLastAccessAt());			
		} catch (final Exception ex) {
			verify(qoSInterDirectMeasurementPingRepository, times(0)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createInterDirectPingMeasurementWithNullCalculationsTest() {
		final QoSInterDirectPingMeasurement pingMeasurement = getQosInterDirectPingMeasurementForTest();		
		when(qoSInterDirectMeasurementPingRepository.saveAndFlush(any())).thenReturn(pingMeasurement);
		
		try {
			qoSDBService.createInterDirectPingMeasurement(pingMeasurement.getMeasurement(), null, pingMeasurement.getLastAccessAt());			
		} catch (final Exception ex) {
			verify(qoSInterDirectMeasurementPingRepository, times(0)).saveAndFlush(any());
			throw ex;
		}		
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void createInterDirectPingMeasurementWithNullAroundNowTest() {
		final QoSInterDirectPingMeasurement pingMeasurement = getQosInterDirectPingMeasurementForTest();
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectMeasurementPingRepository.saveAndFlush(any())).thenReturn(pingMeasurement);
		
		try {
			qoSDBService.createInterDirectPingMeasurement(pingMeasurement.getMeasurement(), calculations, null);			
		} catch (final Exception ex) {
			verify(qoSInterDirectMeasurementPingRepository, times(0)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//=================================================================================================
	// Tests of logInterDirectMeasurementToDB
	
	//-------------------------------------------------------------------------------------------------
	@Test
	public void logInterDirectMeasurementToDBTest() {
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectPingMeasurementLogRepository.saveAndFlush(any())).thenReturn(new QoSInterDirectPingMeasurementLog());
		
		try {			
			qoSDBService.logInterDirectMeasurementToDB("1.1.1.1", calculations, ZonedDateTime.now());
		} catch (final Exception ex) {
			verify(qoSInterDirectPingMeasurementLogRepository, times(1)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = ArrowheadException.class)
	public void logInterDirectMeasurementToDBSaveAndFlushThrowDatabaseExceptionTest() {
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectPingMeasurementLogRepository.saveAndFlush(any())).thenThrow(HibernateException.class);
		
		try {
			qoSDBService.logInterDirectMeasurementToDB("1.1.1.1", calculations, ZonedDateTime.now());			
		} catch (final Exception ex) {
			verify(qoSInterDirectPingMeasurementLogRepository, times(1)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementToDBWithNullAddressTest() {
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectPingMeasurementLogRepository.saveAndFlush(any())).thenReturn(new QoSInterDirectPingMeasurementLog());
		
		try {
			qoSDBService.logInterDirectMeasurementToDB(null, calculations, ZonedDateTime.now());			
		} catch (final Exception ex) {
			verify(qoSInterDirectPingMeasurementLogRepository, times(0)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementToDBWithNullCalculationsTest() {
		when(qoSInterDirectPingMeasurementLogRepository.saveAndFlush(any())).thenReturn(new QoSInterDirectPingMeasurementLog());
		
		try {
			qoSDBService.logInterDirectMeasurementToDB("1.1.1.1", null, ZonedDateTime.now());			
		} catch (final Exception ex) {
			verify(qoSInterDirectPingMeasurementLogRepository, times(0)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementToDBWithNullAroundNowTest() {
		final PingMeasurementCalculationsDTO calculations = getCalculationsForTest();		
		when(qoSInterDirectPingMeasurementLogRepository.saveAndFlush(any())).thenReturn(new QoSInterDirectPingMeasurementLog());
		
		try {
			qoSDBService.logInterDirectMeasurementToDB("1.1.1.1", calculations, null);			
		} catch (final Exception ex) {
			verify(qoSInterDirectPingMeasurementLogRepository, times(0)).saveAndFlush(any());
			throw ex;
		}
	}
	
	//=================================================================================================
	// Tests of logInterDirectMeasurementDetailsToDB
	
	//-------------------------------------------------------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void logInterDirectMeasurementDetailsToDBTest() {
		final ZonedDateTime aroundNow = ZonedDateTime.now();
		final List<IcmpPingResponse> responseList = getResponseListForTest();
		final QoSInterDirectPingMeasurementLog measurementLogSaved = new QoSInterDirectPingMeasurementLog();

		final ArgumentCaptor<List> valueCapture = ArgumentCaptor.forClass(List.class);
		final List<QoSInterDirectPingMeasurementLogDetails> measurementLogDetailsList = List.of(new QoSInterDirectPingMeasurementLogDetails());

		when(qoSInterDirectPingMeasurementLogDetailsRepository.saveAll(valueCapture.capture())).thenReturn(measurementLogDetailsList);
		doNothing().when(qoSInterDirectPingMeasurementLogDetailsRepository).flush();

		qoSDBService.logInterDirectMeasurementDetailsToDB(measurementLogSaved, responseList, aroundNow);

		verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(1)).saveAll(any());
		verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(1)).flush();

		final List<QoSInterDirectPingMeasurementLogDetails> captured = valueCapture.getValue();
		assertEquals(responseList.size(), captured.size());
		for (final QoSInterDirectPingMeasurementLogDetails measurement : captured) {
			assertNotNull(measurement.getMeasurementLog());
			assertNotNull(measurement.isSuccessFlag());
			assertNotNull(measurement.getMeasuredAt());
			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementDetailsToDBWithNullResponseListTest() {
		final ZonedDateTime aroundNow = ZonedDateTime.now();
		final QoSInterDirectPingMeasurementLog measurementLogSaved = new QoSInterDirectPingMeasurementLog();

		final List<QoSInterDirectPingMeasurementLogDetails> measurementLogDetailsList = List.of(new QoSInterDirectPingMeasurementLogDetails());

		when(qoSInterDirectPingMeasurementLogDetailsRepository.saveAll(any())).thenReturn(measurementLogDetailsList);
		doNothing().when(qoSInterDirectPingMeasurementLogDetailsRepository).flush();

		try {
			qoSDBService.logInterDirectMeasurementDetailsToDB(measurementLogSaved, null, aroundNow);
		} catch (final Exception ex) {
			assertTrue(ex.getMessage().contains("List<IcmpPingResponse>" + EMPTY_OR_NULL_ERROR_MESSAGE));
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).saveAll(any());
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).flush();

			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementDetailsToDBWithEmptyResponseListTest() {
		final ZonedDateTime aroundNow = ZonedDateTime.now();
		final QoSInterDirectPingMeasurementLog measurementLogSaved = new QoSInterDirectPingMeasurementLog();

		final List<QoSInterDirectPingMeasurementLogDetails> measurementLogDetailsList = List.of(new QoSInterDirectPingMeasurementLogDetails());

		when(qoSInterDirectPingMeasurementLogDetailsRepository.saveAll(any())).thenReturn(measurementLogDetailsList);
		doNothing().when(qoSInterDirectPingMeasurementLogDetailsRepository).flush();

		try {
			qoSDBService.logInterDirectMeasurementDetailsToDB(measurementLogSaved, List.of(), aroundNow);
		} catch (final Exception ex) {
			assertTrue(ex.getMessage().contains("List<IcmpPingResponse>" + EMPTY_OR_NULL_ERROR_MESSAGE));
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).saveAll(any());
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).flush();

			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementDetailsToDBWithNullMeasurementLogSavedTest() {
		final ZonedDateTime aroundNow = ZonedDateTime.now();
		final List<IcmpPingResponse> responseList = getResponseListForTest();

		final List<QoSInterDirectPingMeasurementLogDetails> measurementLogDetailsList = List.of(new QoSInterDirectPingMeasurementLogDetails());

		when(qoSInterDirectPingMeasurementLogDetailsRepository.saveAll(any())).thenReturn(measurementLogDetailsList);
		doNothing().when(qoSInterDirectPingMeasurementLogDetailsRepository).flush();

		try {
			qoSDBService.logInterDirectMeasurementDetailsToDB(null, responseList, aroundNow);
		} catch (final Exception ex) {
			assertTrue(ex.getMessage().contains("QoSInterPingMeasurementLog" + NULL_ERROR_MESSAGE));
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).saveAll(any());
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).flush();

			throw ex;
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@Test(expected = InvalidParameterException.class)
	public void logInterDirectMeasurementDetailsToDBWithNullAroundNowTest() {
		final List<IcmpPingResponse> responseList = getResponseListForTest();
		final QoSInterDirectPingMeasurementLog measurementLogSaved = new QoSInterDirectPingMeasurementLog();

		final List<QoSInterDirectPingMeasurementLogDetails> measurementLogDetailsList = List.of(new QoSInterDirectPingMeasurementLogDetails());

		when(qoSInterDirectPingMeasurementLogDetailsRepository.saveAll(any())).thenReturn(measurementLogDetailsList);
		doNothing().when(qoSInterDirectPingMeasurementLogDetailsRepository).flush();

		try {
			qoSDBService.logInterDirectMeasurementDetailsToDB(measurementLogSaved, responseList, null);
		} catch (final Exception ex) {
			assertTrue(ex.getMessage().contains("ZonedDateTime" + NULL_ERROR_MESSAGE));
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).saveAll(any());
			verify(qoSInterDirectPingMeasurementLogDetailsRepository, times(0)).flush();

			throw ex;
		}
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private List<QoSInterDirectPingMeasurement> getQosInterDirectPingMeasurementListForTest(final int size) {
		final List<QoSInterDirectPingMeasurement> measurementList = new ArrayList<>(size);

		for (int i = 0; i < size; i++) {

			measurementList.add(getQosInterDirectPingMeasurementForTest());
		}

		return measurementList;
	}

	//-------------------------------------------------------------------------------------------------
	private QoSInterDirectPingMeasurement getQosInterDirectPingMeasurementForTest() {
		final QoSInterDirectMeasurement measurement = getQoSInterDirectMeasurementForTest(QoSMeasurementType.PING);

		final QoSInterDirectPingMeasurement pingMeasurement = new QoSInterDirectPingMeasurement();

		pingMeasurement.setMeasurement(measurement);
		pingMeasurement.setAvailable(true);
		pingMeasurement.setMaxResponseTime(1);
		pingMeasurement.setMinResponseTime(1);
		pingMeasurement.setMeanResponseTimeWithoutTimeout(1);
		pingMeasurement.setMeanResponseTimeWithTimeout(1);
		pingMeasurement.setJitterWithoutTimeout(1);
		pingMeasurement.setJitterWithTimeout(1);
		pingMeasurement.setLostPerMeasurementPercent(0);
		pingMeasurement.setCountStartedAt(ZonedDateTime.now());
		pingMeasurement.setLastAccessAt(ZonedDateTime.now());
		pingMeasurement.setSent(35);
		pingMeasurement.setSentAll(35);
		pingMeasurement.setReceived(35);
		pingMeasurement.setReceivedAll(35);

		return pingMeasurement;
	}

	//-------------------------------------------------------------------------------------------------
	private QoSInterDirectMeasurement getQoSInterDirectMeasurementForTest(final QoSMeasurementType type) {
		final Cloud cloud = new Cloud("test-operator", "test-name", true, true, false, "dakjsfug");
		cloud.setCreatedAt(ZonedDateTime.now());
		cloud.setUpdatedAt(ZonedDateTime.now());
		return new QoSInterDirectMeasurement(cloud, "1.1.1.1", type, ZonedDateTime.now());
	}
	
	//-------------------------------------------------------------------------------------------------
	private PingMeasurementCalculationsDTO getCalculationsForTest() {
		return new PingMeasurementCalculationsDTO(true,//available,
												  1,//maxResponseTime,
												  1,//minResponseTime,
												  1,//meanResponseTimeWithTimeout,
												  1,//meanResponseTimeWithoutTimeout,
												  1,//jitterWithTimeout,
												  1,//jitterWithoutTimeout,
												  35,//sentInThisPing,
												  35,//receivedInThisPing,
												  0,
												  ZonedDateTime.now());//lostPerMeasurementPercent)
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<IcmpPingResponse> getResponseListForTest() {
		final int sizeOfList = 3;
		final List<IcmpPingResponse> responseList = new ArrayList<>(3);

		for (int i = 0; i < sizeOfList; i++) {
			responseList.add(getIcmpResponseForTest());
		}
		return responseList;
	}

	//-------------------------------------------------------------------------------------------------
	private IcmpPingResponse getIcmpResponseForTest() {
		final IcmpPingResponse response = new IcmpPingResponse();
		response.setDuration(1);
		response.setRtt(1);
		response.setSuccessFlag(true);
		response.setTimeoutFlag(false);
		response.setSize(32);
		response.setTtl(64);
		return response;
	}
}