/*
 * Copyright 2013 SIB Visions GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 *
 * History
 *
 * 27.08.2013 - [JR] - creation
 */
package com.sibvisions.util.zip.aes;

import java.util.List;

import javax.rad.model.SortDefinition;
import javax.rad.model.condition.ICondition;
import javax.rad.persist.DataSourceException;
import javax.rad.type.bean.IBean;

import com.sibvisions.rad.persist.AbstractStorage;
import com.sibvisions.util.ArrayUtil;
import com.sibvisions.util.type.StringUtil;

/**
 * The <code>StorageEntry</code> defines an entry for a zip archive.
 * 
 * @author René Jahn
 */
public class StorageEntry
{
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Class members
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/** the entry name. */
	private String name;
	
	/** the storage. */
	private AbstractStorage storage;

	/** the filter condition. */
	private ICondition condFilter;
	
	/** the columns to use. */
	private String[] saColumns;

	/** whether to show column names as first record. */
	private boolean bShowColumnNames = false;
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Initialization
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Creates a new <code>StorageEntry</code>.
	 * 
	 * @param pName the entry name
	 * @param pStorage the data store
	 */
	public StorageEntry(String pName, AbstractStorage pStorage)
	{
		this(pName, pStorage, null);
	}

	/**
	 * Creates a new <code>StorageEntry</code>.
	 * 
	 * @param pName the entry name
	 * @param pStorage the data store
	 * @param pFilter the filter condition
	 */
	public StorageEntry(String pName, AbstractStorage pStorage, ICondition pFilter)
	{
		name = pName;
		storage = pStorage;
		condFilter = pFilter;
	}
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// User-defined methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Gets the entry name.
	 * 
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the data storage.
	 * 
	 * @return the storage
	 */
	public AbstractStorage getStorage()
	{
		return storage;
	}

	/**
	 * Sets the column names which should be used for retrieving data from the storage.
	 * 
	 * @param pColumnNames the column names
	 */
	public void setColumnNames(String...pColumnNames)
	{
		saColumns = pColumnNames;
	}

	/**
	 * Sets the column names fetched via storage.
	 * 
	 * @param pStorage the storage that contains column names
	 * @param pColumName the column that contains the column name
	 * @throws DataSourceException if fetching data fails
	 */
	public void setColumnNames(AbstractStorage pStorage, String pColumName) throws DataSourceException
	{
		setColumnNames(pStorage.fetchBean(null, null, 0, -1), pColumName);
	}
	
	/**
	 * Sets the column names fetched via storage.
	 * 
	 * @param pStorage the storage that contains column names
	 * @param pCondition the fetch condition
	 * @param pSort the sort definition
	 * @param pColumName the column that contains the column name
	 * @throws DataSourceException if fetching data fails
	 */
	public void setColumnNames(AbstractStorage pStorage, ICondition pCondition, SortDefinition pSort, String pColumName) throws DataSourceException
	{
		setColumnNames(pStorage.fetchBean(pCondition, pSort, 0, -1), pColumName);
	}
	
	/**
	 * Sets the column names.
	 * 
	 * @param pBeans the column names
	 * @param pColumName the column that contains the column name
	 */
	public void setColumnNames(List<IBean> pBeans, String pColumName)
	{	
		ArrayUtil<String> auColumns = new ArrayUtil<String>();
		
		String sValue;
		
		for (IBean bean : pBeans)
		{
			sValue = (String)bean.get(pColumName);
			
			if (!StringUtil.isEmpty(sValue))
			{
				auColumns.add(sValue);
			}
		}
		
		if (!auColumns.isEmpty())
		{
			saColumns = new String[auColumns.size()];
			
			auColumns.toArray(saColumns);
		}
	}
	
	/**
	 * Gets the column names which should be used for retrieving data from the storage.
	 * 
	 * @return the column names
	 */
	public String[] getColumnNames()
	{
		return saColumns;
	}
	
	/**
	 * Gets the filter condition.
	 * 
	 * @return the filter condition
	 */
	public ICondition getCondition()
	{
		return condFilter;
	}

	/**
	 * Sets whether column names should be shown as first record.
	 * 
	 * @param pShow <code>true</code> to show column names
	 */
	public void setShowColumnNames(boolean pShow)
	{
		bShowColumnNames = pShow;
	}
	
	/**
	 * Gets whether column names should be shown as first record.
	 * 
	 * @return <code>true</code> if column names should be shown
	 */
	public boolean isShowColumnNames()
	{
		return bShowColumnNames;
	}
	
}	// StorageEntry
