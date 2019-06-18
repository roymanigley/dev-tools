package ch.bytecrowd.devtools.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import ch.bytecrowd.devtools.models.view.TableModel;
import ch.bytecrowd.devtools.utils.SQLAutoCompleteUtil;
import ch.bytecrowd.devtools.utils.SQLUtil;

public class SQLUtilTest {

	private static final String jdbcUrl = "jdbc:mysql://localhost:3006/employees";
	private static final String username = "test";
	private static final String password = "test";
	
	@Test
	@Ignore
	public void tesConnectiont() {
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);) {
			assertThat(conn, notNullValue());
			assertThat(conn.isClosed(), is(false));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	public void tesAutocomplete() {
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);) {
		System.out.println(SQLAutoCompleteUtil.fetchJsonAutocompleteGeneric(conn));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore
	// OnlyMySQL
	public void tesQuery() {
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);) {
			TableModel resultMap = SQLUtil.executeSelectQuery(conn, "SELECT user();");
			assertThat(resultMap.getData().isEmpty(), is(false));
			assertThat(resultMap.getData().get(0).get(0).toString().startsWith("test@"), is(true));
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@Ignore 
	// OnlyMySQL
	public void tesDML() {
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);) {
			SQLUtil.executeDMLQuery(conn, "DROP TABLE IF EXISTS TEST;");
			SQLUtil.executeDMLQuery(conn, "CREATE TABLE TEST (ID INT PRIMARY KEY AUTO_INCREMENT, TEXT VARCHAR(100));");
			int i = SQLUtil.executeDMLQuery(conn, "INSERT INTO TEST (text) VALUES ('lala');");
			assertThat(i, is(not(0)));
			i = SQLUtil.executeDMLQuery(conn, String.format("DELETE FROM TEST WHERE ID = %s", i));
			assertThat(i, is(not(0)));
			SQLUtil.executeDMLQuery(conn, "DROP TABLE IF EXISTS TEST;");
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGuesTablename() {
		assertThat(SQLUtil.guesTableName("\nSELECT * FROM Person"), is("Person"));
		assertThat(SQLUtil.guesTableName("SELECT * FROM Person p WHERE p.name LIKE '%lslsls%'"), is("Person"));
		assertThat(SQLUtil.guesTableName("SELECT * FROM Person UNION ALL SELECT OtherTable p p WHERE p.name LIKE '%lslsls%'"), is("Person"));
		assertThat(SQLUtil.guesTableName("SELECT * FrOM Person UNION ALL SELECT OtherTable p p WHERE p.name LIKE '%lslsls%'"), is("Person"));
	}
}
