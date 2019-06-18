package ch.bytecrowd.devtools.test.persistence;

import static org.junit.Assert.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CdiRunner.class)
public class EntityManagerTest {

	@Test
	public void test() {
		try {
			EntityManagerFactory EMF = Persistence.createEntityManagerFactory("pu-test");
			EMF.close();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

}
