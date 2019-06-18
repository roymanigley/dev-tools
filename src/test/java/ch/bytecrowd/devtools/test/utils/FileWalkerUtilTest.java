package ch.bytecrowd.devtools.test.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.primefaces.model.TreeNode;

import ch.bytecrowd.devtools.models.view.DocumentModel;
import ch.bytecrowd.devtools.utils.FileWalkerUtil;

public class FileWalkerUtilTest {

	@Test
	public void tesFileWalker() {
		try {
			String tempDir = System.getProperty("java.io.tmpdir");
			File file = new File(tempDir + "/AAAAAAAAAAAAA_TEST/AAAAAAAAAAAAA_TEST_01/AAAAAAAAAAAAA_TEST_02/AAAAAAAAAAAAA_TEST_03");
			Files.createDirectories(Paths.get(file.toURI().getPath()));
			TreeNode fileSystemTree = FileWalkerUtil.buildFileSystemTree(tempDir, true);
			boolean found = false;
			for (TreeNode node : fileSystemTree.getChildren()) {
				if (((DocumentModel) node.getData()).getName().equals("AAAAAAAAAAAAA_TEST"));
					for (TreeNode node_01 : node.getChildren()) {
						if (((DocumentModel) node_01.getData()).getName().equals(".AAAAAAAAAAAAA_TEST_01"));
							for (TreeNode node_02 : node_01.getChildren()) {
								if (((DocumentModel) node_02.getData()).getName().equals(".AAAAAAAAAAAAA_TEST_02"));
									for (TreeNode node_03 : node_02.getChildren()) {
										if (((DocumentModel) node_03.getData()).getName().equals(".AAAAAAAAAAAAA_TEST_02"));
											found = true;
									}
							}
					}
			}
			assertThat(found, is(true));
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
