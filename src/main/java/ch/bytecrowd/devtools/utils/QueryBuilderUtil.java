package ch.bytecrowd.devtools.utils;

import ch.bytecrowd.devtools.models.view.TableModel;

public final class QueryBuilderUtil {
	
	public static String toDeleteQuery(TableModel tableModel) {
		StringBuilder builder = new StringBuilder(String.format("DELETE FROM\n\t%s\nWHERE\n\t", tableModel.getGuessedTableName()));
		for (int i = 0; i < tableModel.getColumns().size(); i++) {
			if (i > 0) 
				builder.append("\n\tAND ");
			builder.append(tableModel.getColumns().get(i).getProperty());
			builder.append(" = ");
			if (tableModel.getSelectedRow().get(i) != null && tableModel.getSelectedRow().get(i) instanceof Number)
				builder.append(tableModel.getSelectedRow().get(i));
			else
				builder.append("'").append(tableModel.getSelectedRow().get(i)).append("'");
		}
		return builder.toString().replaceAll(" AND\\s*$", "");
	}
	
	public static String toInsertQuery(TableModel tableModel) {
		StringBuilder builder = new StringBuilder(String.format("INSERT INTO %s (\n\t", tableModel.getGuessedTableName()));
		for (int i = 0; i < tableModel.getColumns().size(); i++) {
			if (i > 0) 
				builder.append("\n\t,");
			builder.append(tableModel.getColumns().get(i).getProperty());
		}
		builder.append("\n)\nVALUES (\n\t");
		for (int i = 0; i < tableModel.getColumns().size(); i++) {
			if (i > 0) 
				builder.append("\n\t,");
			if (tableModel.getSelectedRow().get(i) != null && tableModel.getSelectedRow().get(i) instanceof Number)
				builder.append(tableModel.getSelectedRow().get(i));
			else
				builder.append("'").append(tableModel.getSelectedRow().get(i)).append("'");
		}
		return builder.append("\n)").toString();
	}
	
	public static String toUpdateQuery(TableModel tableModel) {
		StringBuilder builder = new StringBuilder(String.format("UPDATE %s\nSET\n\t", tableModel.getGuessedTableName()));
		for (int i = 0; i < tableModel.getColumns().size(); i++) {
			if (i > 0) 
				builder.append("\n\t,");
			builder.append(tableModel.getColumns().get(i).getProperty());
			builder.append(" = ");
			if (tableModel.getSelectedRow().get(i) != null && tableModel.getSelectedRow().get(i) instanceof Number)
				builder.append(tableModel.getSelectedRow().get(i));
			else
				builder.append("'").append(tableModel.getSelectedRow().get(i)).append("'");
		}
		builder.append("\nWHERE\n\t");
		for (int i = 0; i < tableModel.getColumns().size(); i++) {
			if (i > 0) 
				builder.append("\n\tAND ");
			builder.append(tableModel.getColumns().get(i).getProperty());
			builder.append(" = ");
			if (tableModel.getSelectedRow().get(i) instanceof Number)
				builder.append(tableModel.getSelectedRow().get(i));
			else
				builder.append("'").append(tableModel.getSelectedRow().get(i)).append("'");
		}
		return builder.toString();
	}
}
