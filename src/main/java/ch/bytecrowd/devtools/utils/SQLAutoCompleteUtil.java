package ch.bytecrowd.devtools.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public final class SQLAutoCompleteUtil {
	private static final Logger LOG = Logger.getLogger(SQLAutoCompleteUtil.class);
	
	public  static String fetchJsonAutocompleteGeneric(Connection connection) throws SQLException {
		StringBuilder builder = new StringBuilder("{tables:{");
		try (ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), null, "%", null);) {
			while (tables.next()) {
				try (ResultSet columns = connection.getMetaData().getColumns(connection.getCatalog(), null, tables.getString("TABLE_NAME"), "%");) {
					builder.append(String.format("\"%s\":[", tables.getString("TABLE_NAME")));
					while (columns.next()) {
							builder.append(String.format("\"%s\",", columns.getString("COLUMN_NAME")));
					}
					builder.replace(builder.length() -1, builder.length(), "");
				}
					builder.append("],");
			}
			builder.replace(builder.length() -1, builder.length(), "");
		} catch (Exception e) {
			LOG.error("fetchJsonAutocompleteGeneric failed", e);
		}
		builder.append("}}");
		return builder.toString();
	}

	
	public static String fetchJsonAutocompleteSQL(Connection connection) throws SQLException {
		return fetchJsonAutocompleteGeneric(connection);
	}
}
