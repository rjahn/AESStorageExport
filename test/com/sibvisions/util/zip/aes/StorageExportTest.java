/*
 * History
 * 
 * 27.08.2013 - [JR] - creation
 */
package com.sibvisions.util.zip.aes;

import java.io.File;
import java.math.BigDecimal;

import javax.rad.io.RemoteFileHandle;
import javax.rad.model.ColumnDefinition;
import javax.rad.model.ColumnView;
import javax.rad.model.ModelException;
import javax.rad.model.RowDefinition;
import javax.rad.model.condition.GreaterEquals;
import javax.rad.model.condition.ICondition;
import javax.rad.model.condition.LessEquals;
import javax.rad.model.datatype.BigDecimalDataType;
import javax.rad.model.datatype.TimestampDataType;
import javax.rad.model.event.DataBookEvent;

import org.junit.Test;

import com.sibvisions.rad.model.mem.MemDataBook;
import com.sibvisions.rad.persist.AbstractMemStorage;
import com.sibvisions.rad.persist.AbstractStorage;
import com.sibvisions.util.type.DateUtil;
import com.sibvisions.util.type.FileUtil;

/**
 * Tests the functionality of <code>StorageExportTest</code>
 * 
 * @author René Jahn
 */
public class StorageExportTest
{
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // User-defined methods
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Creates a new storage with test data.
	 * 
	 * @return the storage
	 * @throws ModelException if creation fails
	 */
	private AbstractStorage createStorage() throws ModelException
	{
		AbstractMemStorage amsLoggings = new AbstractMemStorage()
		{
			@Override
			public RowDefinition getRowDefinition() throws ModelException
			{
				RowDefinition rowdef = new RowDefinition();
				rowdef.addColumnDefinition(new ColumnDefinition("ID", new BigDecimalDataType()));
				rowdef.addColumnDefinition(new ColumnDefinition("TEXT"));
				rowdef.addColumnDefinition(new ColumnDefinition("DATE", new TimestampDataType()));
				rowdef.addColumnDefinition(new ColumnDefinition("VALUE", new BigDecimalDataType()));
				
				rowdef.setColumnView(null, new ColumnView("ID", "TEXT", "DATE", "VALUE"));
				
				rowdef.setPrimaryKeyColumnNames(new String[] {"ID"});
				
				return rowdef;
			}
			
			@Override
			public void update(DataBookEvent pEvent) throws ModelException
			{
			}
			
			@Override
			public void loadData(MemDataBook pBook, ICondition pFilter) throws ModelException
			{
				pBook.setFilter(pFilter);
			}
			
			@Override
			public void insert(DataBookEvent pEvent) throws ModelException
			{
			}
			
			@Override
			public void delete(DataBookEvent pEvent) throws ModelException
			{
			}
		};

		amsLoggings.open();

		for (int i = 1, j = 90; i <= 100; i++, j = j % 4 + 10)
		{
			amsLoggings.getDataBook().insert(false);
			amsLoggings.getDataBook().setValues(new String[] {"ID", "TEXT", "DATE", "VALUE"}, 
					                            new Object[] {BigDecimal.valueOf(i), "Text (" + i + ")", DateUtil.getDate(10, 04, 1950 + i, 10, 00, 45), BigDecimal.valueOf(j)});
		}
		
		amsLoggings.getDataBook().saveAllRows();
		
		return amsLoggings;
	}
	
	/**
	 * Creates a new storage with column names.
	 * 
	 * @return the storage
	 * @throws ModelException if creation fails
	 */
	private AbstractStorage createColumnStorage() throws ModelException
	{
		AbstractMemStorage amsColList = new AbstractMemStorage()
		{
			@Override
			public RowDefinition getRowDefinition() throws ModelException
			{
				RowDefinition rowdef = new RowDefinition();
				rowdef.addColumnDefinition(new ColumnDefinition("ID", new BigDecimalDataType()));
				rowdef.addColumnDefinition(new ColumnDefinition("NAME"));
				
				rowdef.setColumnView(null, new ColumnView("ID", "NAME"));
				
				rowdef.setPrimaryKeyColumnNames(new String[] {"ID"});
				
				return rowdef;
			}
			
			@Override
			public void update(DataBookEvent pEvent) throws ModelException
			{
			}
			
			@Override
			public void loadData(MemDataBook pBook, ICondition pFilter) throws ModelException
			{
				pBook.setFilter(pFilter);
			}
			
			@Override
			public void insert(DataBookEvent pEvent) throws ModelException
			{
			}
			
			@Override
			public void delete(DataBookEvent pEvent) throws ModelException
			{
			}
		};

		amsColList.open();

		amsColList.getDataBook().insert(false);
		amsColList.getDataBook().setValues(new String[] {"ID", "NAME"}, new Object[] {BigDecimal.valueOf(1), "TEXT"});
		amsColList.getDataBook().insert(false);
		amsColList.getDataBook().setValues(new String[] {"ID", "NAME"}, new Object[] {BigDecimal.valueOf(1), "VALUE"});

		amsColList.getDataBook().saveAllRows();
		
		return amsColList;
	}
	
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Test methods
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	
	/**
	 * Tests archive creation with one entry. 
	 */
	@Test
	public void createArchive() throws Exception
	{
		StorageExport export = new StorageExport();
		
		ICondition condFilter = new GreaterEquals("ID", BigDecimal.valueOf(10)).and(new LessEquals("ID", BigDecimal.valueOf(20)));
		
		StorageEntry entryColumns = new StorageEntry("columns.csv", createStorage(), condFilter);
		entryColumns.setColumnNames("ID", "VALUE");
		entryColumns.setShowColumnNames(true);
		
		StorageEntry entryColumnsStorage = new StorageEntry("columns_storage.csv", createStorage(), condFilter);
		entryColumnsStorage.setColumnNames(createColumnStorage(), "NAME");
		
		export.add(new StorageEntry("first.csv", createStorage()));
		export.add(new StorageEntry("filtered.csv", createStorage(), condFilter));
		export.add(entryColumns);
		export.add(entryColumnsStorage);
		export.setPassword("testcase");
		export.setSeparator(",");

		File fiTemp = new File(System.getProperty("java.io.tmpdir"), "aesarchive.zip");
		
		RemoteFileHandle rfh = new RemoteFileHandle();
		export.export(rfh.getOutputStream());
		
		FileUtil.save(fiTemp, rfh.getInputStream());
		
		System.out.println(fiTemp);
	}
	
}	// StorageExportTest
