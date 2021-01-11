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

package eu.arrowhead.common.dto.shared;

import java.io.Serializable;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import org.springframework.util.Assert;

import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ConfigurationResponseDTO implements Serializable {

    //=================================================================================================
	// members

    private static final long serialVersionUID = 1231548244663961370L;

    private long id;
    private String createdAt;
	private String updatedAt;
    
    //=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------	
    public ConfigurationResponseDTO() {}

    //-------------------------------------------------------------------------------------------------
	public ConfigurationResponseDTO(final long systemId, final String createdAt, final String upDatedAt) {
		this.id = systemId;
		this.createdAt = createdAt;
		this.updatedAt = upDatedAt;
	}
    
    //-------------------------------------------------------------------------------------------------
    public long getId() { return id; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    
    //-------------------------------------------------------------------------------------------------
    public void setId(final long id) { this.id = id; }
    public void setCreatedAt(final String createdAt) { this.createdAt = createdAt; }
	public void setUpdatedAt(final String updatedAt) { this.updatedAt = updatedAt; }
    
    //-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return new StringJoiner(", ", ConfigurationResponseDTO.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("createdAt='" + createdAt + "'")
				.add("updatedAt='" + updatedAt + "'")
				.toString();
	}
}