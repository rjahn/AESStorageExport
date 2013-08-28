Overview
========

A simple CSV exporter for AbstractStorages.

Usage
=====

<pre>
StorageExport export = new StorageExport();

ICondition condFilter = new GreaterEquals("ID", BigDecimal.valueOf(10)).and(new LessEquals("ID", BigDecimal.valueOf(20)));

StorageEntry entryColumns = new StorageEntry("columns.csv", createStorage(), condFilter);
entryColumns.setColumnNames("ID", "VALUE");

StorageEntry entryColumnsStorage = new StorageEntry("columns_storage.csv", createStorage(), condFilter);
entryColumnsStorage.setColumnNames(createColumnStorage(), "NAME");

export.add(new StorageEntry("first.csv", createStorage()));
export.add(new StorageEntry("filtered.csv", createStorage(), condFilter));
export.add(entryColumns);
export.add(entryColumnsStorage);
export.setPassword("testcase");

File fiTemp = new File(System.getProperty("java.io.tmpdir"), "aesarchive.zip");

RemoteFileHandle rfh = new RemoteFileHandle();
export.export(rfh.getOutputStream());

FileUtil.save(fiTemp, rfh.getInputStream());</pre>

License
-------

Apache 2.0 (http://www.apache.org/licenses/)


Have fun!
