package ch.bytecrowd.devtools.test.utils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import ch.bytecrowd.devtools.utils.DBUnitUtils;
import ch.bytecrowd.devtools.utils.SQLUtil;

public class DBUnitUtilTest {

	private static final String jdbcUrl = "jdbc:mysql://localhost:3006/employees";
	private static final String username = "test";
	private static final String password = "test";
		
	@Test
	@Ignore
	public void testDbUnitExport() {
		StringBuilder dbUnitExport = null;
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);) {
			//String sql = "SELECT * FROM information_schema.COLUMNS WHERE COLUMN_NAME = 'COLUMN_NAME'  AND TABLE_NAME = 'COLUMNS'";
			//String sql = "SELECT * FROM employees LIMIT 0,1";
			//String sql = "SELECT * FROM dept_emp LIMIT 0,1";
			String sql = "SELECT * FROM dept_emp WHERE emp_no=10011 AND dept_no='d009' AND dept_no='d009'";
			Statement statement = conn.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString(0));
			}
			statement.close();
			dbUnitExport = DBUnitUtils.toDbUnit(conn, sql);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		try (Connection conn = SQLUtil.openConnection(jdbcUrl, username, password);
				InputStream inputStream = IOUtils.toInputStream(dbUnitExport);) {
			System.out.println(dbUnitExport);
			DBUnitUtils.fromDbUnitToDatabase(conn, inputStream);
		} catch (Exception e) {
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
	
	@Test
	public void testLoadToEntityManager() {
		EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu-test");
		EntityManager em = EMF.createEntityManager();
		try (InputStream dbunitStream = DBUnitUtilTest.class.getClassLoader().getResourceAsStream("dbunit/ANREDE.xml");) {
			
			em.getTransaction().begin();
			em.createNativeQuery("DROP TABLE IF EXISTS ANREDE").executeUpdate();
			em.createNativeQuery("CREATE TABLE ANREDE (ID_ANREDE INTEGER PRIMARY KEY, TEXT VARCHAR(25))").executeUpdate();
			em.getTransaction().commit();
						
			DBUnitUtils.immportToEntityManager(em, dbunitStream);

			List resultList = em.createNativeQuery("SELECT * FROM ANREDE").getResultList();
			assertThat(resultList.isEmpty(), is(false));
			
			Object[] result = (Object[]) em.createNativeQuery("SELECT ID_ANREDE, TEXT FROM ANREDE WHERE ID_ANREDE=3").getSingleResult();
			assertThat(result, notNullValue());
			assertThat(result[0], is(3));
			assertThat(result[1], is("Frau / Madame"));

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		em.close();
		EMF.close();
	}
}
