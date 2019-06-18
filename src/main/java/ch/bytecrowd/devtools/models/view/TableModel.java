package ch.bytecrowd.devtools.models.view;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableModel implements Serializable {

	private static final long serialVersionUID = 6922811783413586795L;
	private List<List<Object>> data = new ArrayList<>();
	private List<List<Object>> filteredData = null;
	private List<ColumnModel> columns = new ArrayList<>();
	private List<Object> selectedRow = new ArrayList<>();
	private String guessedTableName = "INSERT_TABLE_NAME";
	private String sqlQuery = "";
	
	public TableModel(String sqlQuery) {
		super();
		this.sqlQuery = sqlQuery;
	}

	public TableModel(ResultSet result, String sqlQuery) throws SQLException {
		this(sqlQuery);
		try {
			int columnCount = result.getMetaData().getColumnCount();
			columns = new ArrayList<>(columnCount);
			data = new ArrayList<>(result.getFetchSize());
			List<Object> row = null;
			List<String> columnNames = new ArrayList<>(columnCount);
			while (result.next()) {
				row = new ArrayList<>(columnCount);
				for (int i = 1; i <= columnCount; i++) {
					String columnLabel = result.getMetaData().getColumnLabel(i);
					Object value = result.getObject(i);
					if (!columnNames.contains(columnLabel))
						columnNames.add(columnLabel);
					row.add(value);
				}
				data.add(row);
			}
			for (String string : columnNames) {
				columns.add(new ColumnModel(string, string));
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public void setColumns(List<ColumnModel> columns) {
		this.columns = columns;
	}

	public List<List<Object>> getData() {
		return data;
	}

	public void setData(List<List<Object>> data) {
		this.data = data;
	}

	public List<List<Object>> getFilteredData() {
		return filteredData;
	}

	public void setFilteredData(List<List<Object>> filteredData) {
		this.filteredData = filteredData;
	}

	public List<Object> getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(List<Object> selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getGuessedTableName() {
		return guessedTableName;
	}

	public void setGuessedTableName(String guessedTableName) {
		this.guessedTableName = guessedTableName;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.sqlQuery = sqlQuery;
	}
	
}
