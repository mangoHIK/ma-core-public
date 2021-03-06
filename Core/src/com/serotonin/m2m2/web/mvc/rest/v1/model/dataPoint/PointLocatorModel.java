/**
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.m2m2.web.mvc.rest.v1.model.dataPoint;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.serotonin.json.spi.JsonEntity;
import com.serotonin.m2m2.DataTypes;
import com.serotonin.m2m2.vo.dataSource.PointLocatorVO;
import com.serotonin.m2m2.web.mvc.rest.v1.csv.CSVEntity;
import com.serotonin.m2m2.web.mvc.rest.v1.model.AbstractRestModel;

/**
 * @author Terry Packer
 *
 */
@JsonEntity
@CSVEntity(derived=true)
public abstract class PointLocatorModel<T extends PointLocatorVO> extends AbstractRestModel<PointLocatorVO>{

	@JsonIgnore
	protected T data;
	
	/**
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public PointLocatorModel(PointLocatorVO data) {
		super(data);
		this.data = (T)data;
	}

    @JsonGetter("type")
    public abstract String getTypeName();
   
    @JsonSetter("type")
    public void setTypeName(String typeName){ }
    
    
    @JsonGetter("dataType")
    public String getDataType(){
    	return DataTypes.CODES.getCode(this.data.getDataTypeId());
    }
    
    @JsonSetter("dataType")
    public void setDataType(String dataTypeCode){ }
    
    
    @Override
    public T getData(){
    	return data;
    }
    
}
