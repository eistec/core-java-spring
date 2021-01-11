/********************************************************************************
 * Copyright (c) 2021 {Lulea University of Technology}
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 
 *
 * Contributors: 
 *   {Lulea University of Technology} - implementation
 *   Arrowhead Consortia - conceptualization 
 ********************************************************************************/
package eu.arrowhead.core.configuration.database.service;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList; 
import java.util.Iterator; 
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.CoreCommonConstants;
import eu.arrowhead.common.dto.shared.ConfigurationResponseDTO;
import eu.arrowhead.common.Utilities;
import eu.arrowhead.common.dto.internal.DTOConverter;
import eu.arrowhead.common.exception.ArrowheadException;
import eu.arrowhead.common.exception.InvalidParameterException;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Array;
import java.sql.SQLException;


@Service
public class ConfigurationDBService {
	//=================================================================================================
	// members
	
	@Value("${spring.datasource.url}")
	private String url;
	@Value("${spring.datasource.username}")
	private String user;
	@Value("${spring.datasource.password}")
	private String password;

	private static final Logger logger = LogManager.getLogger(ConfigurationDBService.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private Connection getConnection() throws SQLException {
	  return DriverManager.getConnection(url, user, password);
	}


	//-------------------------------------------------------------------------------------------------
	private void closeConnection(final Connection conn) throws SQLException {
	  conn.close();
	}

	
	//-------------------------------------------------------------------------------------------------
	public ConfigurationResponseDTO getConfigForSystem(final String systemName) {
		logger.debug("getConfigForSystem:");

		ConfigurationResponseDTO ret = null;
		
		Connection conn = null;
	
		try {
			conn = getConnection();
			String sql = "SELECT id, dataType, data, created_at, updated_at FROM configuration_data WHERE systemName=? ORDER BY id DESC LIMIT 1;";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, systemName);
	
			ResultSet rs = stmt.executeQuery();
			ret = new ConfigurationResponseDTO();
			
			// fetch the information
			rs.next();
			ret.setId(rs.getLong(1));
			ret.setSystemName(systemName);
			ret.setType(rs.getString(2));
			ret.setData(rs.getString(3));
			ret.setCreatedAt(rs.getString(4));
			ret.setUpdatedAt(rs.getString(5));
			
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			logger.debug(e.toString());
		} finally {
			try {
				closeConnection(conn);
			} catch (SQLException e) {
				logger.debug(e.toString());
			}
		}
	
		return ret;
	}
	

}
