package ch.bytecrowd.devtools.utils;

import java.io.File;
import java.util.Collections;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import ch.bytecrowd.devtools.models.view.DocumentModel;

public final class FileWalkerUtil {

	public static TreeNode buildFileSystemTree(String rootPath, boolean walkRecursive) {
		TreeNode root = null;
		File rootFile = new File(rootPath);
		if (rootFile.exists()) {
			root = new DefaultTreeNode(new DocumentModel(rootFile), null);
			buildFileSystemTreeRecursive(root, rootFile, walkRecursive);
		}
		if (root != null)
			Collections.sort(root.getChildren(), (a,b) -> { return a.toString().compareTo(b.toString()); });
		return root;
	}

	private static void buildFileSystemTreeRecursive(TreeNode root, File rootFile, boolean walkRecursive) {
		if (rootFile.listFiles() != null && rootFile.listFiles().length > 0)
			for (File f : rootFile.listFiles()) {
				if (f.isDirectory() && walkRecursive)
					buildFileSystemTreeRecursive(new DefaultTreeNode(new DocumentModel(f), root), f, true);
				else
					new DefaultTreeNode((f.isDirectory() ? DefaultTreeNode.DEFAULT_TYPE : "document"), new DocumentModel(f), root);
			}
	}

}
