package ch.bytecrowd.devtools.utils;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.AmbiguousTableNameException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.HibernateException;
import org.hibernate.internal.SessionImpl;

import ch.bytecrowd.devtools.models.view.DocumentModel;
import ch.bytecrowd.devtools.utils.SQLUtil.ColumnFkModel;


public class DBUnitUtils {

	private static final Logger LOG = Logger.getLogger(DBUnitUtils.class);

	public static StringBuilder toDbUnit(Connection conn, String sql) throws Exception {

		IDatabaseConnection connection = null;
		HashSet<ColumnFkModel>  registeredData = new HashSet<>();
		HashSet<String> xmlLines = new HashSet<>();
		try {
			connection = new DatabaseConnection(conn);
			QueryDataSet partialDataSet = new QueryDataSet(connection);
			appendFkTablesRecursive(sql, partialDataSet, conn, registeredData);
			StringWriter xmlDump = new StringWriter();
			FlatXmlDataSet.write(partialDataSet, xmlDump);
			StringBuilder stringBuilder = new StringBuilder();
			String[] nodes = xmlDump.toString().split("<");
			for (String string : nodes) {
				if (stringBuilder.indexOf(string) == -1) {
					String xmlLine = string.replaceAll("___(-)?\\d*", "");
					if (!xmlLines.contains(xmlLine)) {
						stringBuilder.append("<").append(xmlLine);
						xmlLines.add(xmlLine);
					}
				}
			}
			return stringBuilder;
		} catch (DataSetException | SQLException | IOException e) {
			throw e;
		} finally {
			if (connection != null)
				connection.close();
		}
	}
	
	private static void appendFkTablesRecursive(String sql, QueryDataSet partialDataSet, Connection conn, HashSet<ColumnFkModel> registeredData)
			throws SQLException, AmbiguousTableNameException {
		if (sql != null && !sql.trim().isEmpty()) {
			String guessedTableName = SQLUtil.guesTableName(sql);
			ResultSet importedKeys = conn.getMetaData().getImportedKeys(null, null, guessedTableName);
			final List<ColumnFkModel> columnFkModels = new ArrayList<>();
			while (!importedKeys.isClosed() && importedKeys.next()) {
				final ColumnFkModel columnFkModel = new ColumnFkModel(importedKeys.getString("PKTABLE_NAME"), importedKeys.getString("PKCOLUMN_NAME"), importedKeys.getString("FKCOLUMN_NAME"), sql);
				if (!registeredData.contains(columnFkModel))
					columnFkModels.add(columnFkModel);
			}
			for (ColumnFkModel columnFkModel : columnFkModels) {
				final String selectQuery = columnFkModel.toSelectQuery();
				try {
					if (!registeredData.contains(columnFkModel)) {
						registeredData.add(columnFkModel);
						appendFkTablesRecursive(selectQuery, partialDataSet, conn, registeredData);
						partialDataSet.addTable(columnFkModel.getReferencedTable()  + "___" +  new Random().nextInt() , selectQuery);	
					}
				} catch (AmbiguousTableNameException e) {
					LOG.warn(String.format("Table \"%s\" Already in dataSet", columnFkModel.getReferencedTable()));
				}catch (Exception e) {
					LOG.error("adding table to dataset failed \n" + columnFkModel.toSelectQuery(), e);
				}
			}
			importedKeys.close();
			try {
				partialDataSet.addTable(guessedTableName + "___" + new Random().nextInt(), sql);
			} catch (Exception e) {
				LOG.error("adding table to dataset failed \n" + sql, e);
			}
		}
	}
    

	public static void fromDbUnitToDatabase(Connection conn, InputStream dbunitXml) throws Exception {
		DatabaseConnection connection = null;
        try {
        	connection = new DatabaseConnection(conn);
			importFromDBUnit(dbunitXml, connection);
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static void immportToEntityManager(EntityManager em, InputStream dbunitXml) throws HibernateException, DatabaseUnitException, SQLException {
		DatabaseConnection connection = null;
        try {
        	connection = new DatabaseConnection(((SessionImpl)(em.getDelegate())).connection());
			importFromDBUnit(dbunitXml, connection);
		} catch (Exception e) {
			throw e;
		}
	}

	private static void importFromDBUnit(InputStream dbunitXml, DatabaseConnection connection)
			throws DataSetException, DatabaseUnitException, SQLException {
		connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
		connection.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
		connection.getConfig().setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);

		FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
		flatXmlDataSetBuilder.setColumnSensing(true);
		FlatXmlDataSet dataset = flatXmlDataSetBuilder.build(dbunitXml);
		DatabaseOperation.CLEAN_INSERT.execute(connection, dataset);
	}
}
