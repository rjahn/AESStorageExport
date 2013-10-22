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
 * 12.09.2013 - [JR] - use sort of entry
 */
package com.sibvisions.util.zip.aes;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.rad.model.datatype.IDataType;
import javax.rad.persist.ColumnMetaData;
import javax.rad.persist.MetaData;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.io.StaticEntry;
import net.lingala.zip4j.io.ZipOutputStream;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import com.sibvisions.rad.model.DataBookUtil;
import com.sibvisions.rad.persist.AbstractStorage;
import com.sibvisions.util.ArrayUtil;
import com.sibvisions.util.type.StringUtil;

/**
 * The <code>StorageExport</code> exports data of {@link AbstractStorage}s.
 * 
 * @author René Jahn
 */
public class StorageExport
{
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Class members
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/** the separator character. */ 
	private String sSeparator = ";";
	
	/** the password. */
	private String sPassword;

	/** the list of export entries. */
	private List<StorageEntry> liEntries = new ArrayUtil<StorageEntry>();
	
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// User-defined methods
	//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Sets the separator character.
	 * 
	 * @param pSeparator the separator
	 */
	public void setSeparator(String pSeparator)
	{
		sSeparator = pSeparator;
	}
	
	/**
	 * Gets the separator character.
	 * 
	 * @return the separator
	 */
	public String getSeparator()
	{
		return sSeparator;
	}
	
	/**
	 * Sets the archive password.
	 * 
	 * @param pPassword the password
	 */
	public void setPassword(String pPassword)
	{
		sPassword = pPassword;
	}
	
	/**
	 * Gets the archive password.
	 * 
	 * @return the password
	 */
	public String getPassword()
	{
		return sPassword;
	}
	
	/**
	 * Adds an export entry to the archive.
	 * 
	 * @param pEntry the entry
	 */
	public void add(StorageEntry pEntry)
	{
		liEntries.add(pEntry);
	}

	/**
	 * Removes an export entry from the archive.
	 * 
	 * @param pEntry the entry
	 * @return <code>true</code> if removal was successful, <code>false</code> otherwise
	 */
	public boolean remove(StorageEntry pEntry)
	{
		return liEntries.remove(pEntry);
	}
	
	/**
	 * Gets all archive entries.
	 * 
	 * @return the entry list
	 */
	public StorageEntry[] getEntries()
	{
		StorageEntry[] entries = new StorageEntry[liEntries.size()];
		
		return liEntries.toArray(entries);
	}
	
	/**
	 * Creates an AES zip archive with UTF-8, CSV data from all entries.
	 * 
	 * @param pStream the output stream
	 * @throws ZipException if zip creation fails or data access fails
	 */
	public void export(OutputStream pStream) throws Exception
	{
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		
		if (sPassword != null)
		{
			parameters.setEncryptFiles(true);
			parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_AES);
			parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
			parameters.setPassword(sPassword);
		}
		
		ZipOutputStream zos = new ZipOutputStream(pStream);

		try
		{
			AbstractStorage storage;
			
			MetaData mdata;
			
			ColumnMetaData[] cmdata;
			
			IDataType[] dataType;
			
			List<Object[]> lResult;
			
			String sLabel;

			String[] sEntryColumnNames;
			String[] sEntryColumnLabels;
			Object[] oData;
	
			OutputStreamWriter oswStream;
			
			ByteArrayOutputStream baos;
	
			int iStart;
			int[] iColumnNameIndex;
			
			boolean bAllFetched;
			
			for (StorageEntry entry : liEntries)
			{
				bAllFetched = false;
				
				iStart = 0;
				
				storage = entry.getStorage();
				
				mdata = storage.getMetaData();
	
				cmdata = mdata.getColumnMetaData();
				
				sEntryColumnNames = entry.getColumnNames();
				
				if (sEntryColumnNames == null)
				{
					sEntryColumnNames = mdata.getColumnNames();
				}

				sEntryColumnLabels = entry.getColumnLabels();
				
				if (sEntryColumnLabels == null)
				{
					sEntryColumnLabels = null;
				}				
				
				iColumnNameIndex = new int[sEntryColumnNames.length];
				
				dataType = new IDataType[sEntryColumnNames.length];
				
				for (int i = 0, idx; i < sEntryColumnNames.length; i++)
				{
					idx = mdata.getColumnMetaDataIndex(sEntryColumnNames[i]);
					
					iColumnNameIndex[i] = idx;
	
					if (idx >= 0)
					{
						dataType[i] = ColumnMetaData.createDataType(cmdata[idx]);
					}
				}
				
				baos = new ByteArrayOutputStream();
				
				oswStream = new OutputStreamWriter(baos, "UTF-8");
				
				if (entry.isShowColumnNames())
				{
					for (int i = 0; i < sEntryColumnNames.length; i++)
					{
						if (i > 0)
						{
							oswStream.write(sSeparator);
						}
						
						if (sEntryColumnLabels != null && sEntryColumnLabels.length > i)
						{
							sLabel = sEntryColumnLabels[i];
						}
						else
						{
							sLabel = null;
						}
						
						
						if (StringUtil.isEmpty(sLabel))
						{
							sLabel = ColumnMetaData.getDefaultLabel(sEntryColumnNames[i]);
						}
						
						oswStream.write(sLabel);
					}

					oswStream.write("\n");
				}
				
				while (!bAllFetched)
				{
					lResult = storage.fetch(entry.getCondition(), entry.getSortDefinition(), iStart, 1000);
					
					//continue fetching
					iStart += lResult.size();
					
					//write rows
					for (int i = 0, anz = lResult.size(); i < anz; i++)
					{
						oData = lResult.get(i);
						
						if (oData != null)
						{
							for (int j = 0; j < iColumnNameIndex.length; j++)
							{
								if (j > 0)
								{
									oswStream.write(sSeparator);
								}
								
								DataBookUtil.writeQuoted(oswStream, dataType[j], oData[iColumnNameIndex[j]], sSeparator);
							}		
							
							oswStream.write("\n");
						}
						else
						{
							bAllFetched = true;
						}
					}
				}
				
				oswStream.flush();
				oswStream.close();
				
				baos.close();
				
				zos.putNextEntry(new StaticEntry(entry.getName(), baos.size()), parameters);
				zos.write(baos.toByteArray());
				zos.flush();
				zos.closeEntry();
			}
		}
		finally
		{
			zos.finish();
			zos.close();
		}
	}
	
}	// StorageExport
