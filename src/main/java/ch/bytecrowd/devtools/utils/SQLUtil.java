package ch.bytecrowd.devtools.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ch.bytecrowd.devtools.models.view.TableModel;

public final class SQLUtil {

	public static Connection openConnection(String jdbcUrl, String username, String password) throws SQLException {
		try {
			return DriverManager.getConnection(jdbcUrl, username, password);
		} catch (SQLException e) {
			throw e;
		}
	}

	public static TableModel executeSelectQuery(Connection conn, String sql) throws SQLException {
		try (Statement statement = conn.createStatement(); ResultSet result = statement.executeQuery(sql);) {
			return toTableModel(result, sql);
		} catch (SQLException e) {
			throw e;
		}
	}

	public static int executeDMLQuery(Connection conn, String sql) throws SQLException {
		try (Statement statement = conn.createStatement();) {
			return statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			throw e;
		}
	}

	public static TableModel toTableModel(ResultSet result, String sql) throws SQLException {
		TableModel tableModel = new TableModel(result, sql);
		tableModel.setGuessedTableName(guesTableName(sql));
		return tableModel;
	}

	/**
	 * @param conn
	 * @param sql
	 * @throws Exception
	 * @see http://dbunit.sourceforge.net/faq.html#extract
	 */
	public static class ColumnFkModel {
		
		private String referencedTable;
		private String primaryKey;
		private String foreignKey;
		private String originQuery;
		
		public ColumnFkModel(String referencedTable, String primaryKey, String foreignKey,
				String originQuery) {
			super();
			this.referencedTable = referencedTable;
			this.primaryKey = primaryKey;
			this.foreignKey = foreignKey;
			this.originQuery = originQuery;
		}
		
		public ColumnFkModel() {
			super();
		}

		public String getReferencedTable() {
			return referencedTable;
		}
		public void setReferencedTable(String referencedTable) {
			this.referencedTable = referencedTable;
		}

		public String getPrimaryKey() {
			return primaryKey;
		}
		public void setPrimaryKey(String primaryKey) {
			this.primaryKey = primaryKey;
		}
		public String getForeignKey() {
			return foreignKey;
		}
		public void setForeignKey(String foreignKey) {
			this.foreignKey = foreignKey;
		}
		
		public String getOriginQuery() {
			return originQuery;
		}
		public void setOriginQuery(String originQuery) {
			this.originQuery = originQuery;
		}
		public String toSelectQuery() {
			return String.format("SELECT * FROM %s WHERE %s IN (SELECT %s %s)", referencedTable, primaryKey, foreignKey, originQuery.replaceAll("(?i)^((?!FROM).)+", ""));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((foreignKey == null) ? 0 : foreignKey.hashCode());
			result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
			result = prime * result + ((referencedTable == null) ? 0 : referencedTable.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ColumnFkModel other = (ColumnFkModel) obj;
			if (foreignKey == null) {
				if (other.foreignKey != null)
					return false;
			} else if (!foreignKey.equals(other.foreignKey))
				return false;
			if (primaryKey == null) {
				if (other.primaryKey != null)
					return false;
			} else if (!primaryKey.equals(other.primaryKey))
				return false;
			if (referencedTable == null) {
				if (other.referencedTable != null)
					return false;
			} else if (!referencedTable.equals(other.referencedTable))
				return false;
			return true;
		}
	}
	

	public static String guesTableName(String sql) {
		return sql.replaceAll("--.*", "").replaceAll("\\s", " ").replaceAll("/\\\\*((?!\\\\*/).)+\\\\*/", "").replaceAll("(?i)^((?!FROM).)+FROM\\s*([^\\s]+)\\s*.*", "$2");
	}
	
	
}
