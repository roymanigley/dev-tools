package ch.bytecrowd.devtools.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import ch.bytecrowd.devtools.models.view.ColumnModel;
import ch.bytecrowd.devtools.models.view.TableModel;
import ch.bytecrowd.devtools.utils.QueryBuilderUtil;

public class QueryBuilderUtilTest {
	
	@Test
	public void tesConnectiont() {
		TableModel tableModel = new TableModel("");
		ArrayList<Object> selectedRow = new ArrayList<>();
		String name = new BigInteger(50, new SecureRandom()).toString(32);
		selectedRow.add(name);
		Integer number = (int) (Math.random() * 9999);
		selectedRow.add(number);
		Date date = new Date();
		selectedRow.add(date);
		ArrayList<ColumnModel> columns = new ArrayList<>();
		ColumnModel nameColumn = new ColumnModel("name", "name");
		ColumnModel numberColumn = new ColumnModel("number", "number");
		ColumnModel dateColumn = new ColumnModel("date", "date");
		columns.add(nameColumn);
		columns.add(numberColumn);
		columns.add(dateColumn);
		tableModel.setSelectedRow(selectedRow);
		tableModel.setColumns(columns);
		
		String deleteQuery = QueryBuilderUtil.toDeleteQuery(tableModel);
		String insertQuery = QueryBuilderUtil.toInsertQuery(tableModel);
		String updateQuery = QueryBuilderUtil.toUpdateQuery(tableModel);
		String expectedDeleteQuery = String .format("DELETE FROM\n\tINSERT_TABLE_NAME\nWHERE\n\tname = '%1$s'\n\tAND number = %2$s\n\tAND date = '%3$s'", name, number, date);
		assertThat(deleteQuery, is(expectedDeleteQuery));
		
		String expectedInsertQuery = String .format("INSERT INTO INSERT_TABLE_NAME (\n\tname\n\t,number\n\t,date\n)\nVALUES (\n\t'%1$s'\n\t,%2$s\n\t,'%3$s'\n)", name, number, date);
		assertThat(insertQuery, is(expectedInsertQuery));
		
		String expectedUpdateQuery = String .format("UPDATE INSERT_TABLE_NAME\nSET\n\tname = '%1$s'\n\t,number = %2$s\n\t,date = '%3$s'\nWHERE\n\tname = '%1$s'\n\tAND number = %2$s\n\tAND date = '%3$s'", name, number, date);
		assertThat(updateQuery, is(expectedUpdateQuery));
	}
}
