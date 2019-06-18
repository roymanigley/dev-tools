package ch.bytecrowd.devtools.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.nio.file.Files;

import javax.enterprise.context.RequestScoped;

import org.apache.poi.util.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;
import org.primefaces.model.UploadedFile;

import ch.bytecrowd.devtools.models.view.DocumentModel;
import ch.bytecrowd.devtools.utils.FileWalkerUtil;

@RequestScoped
public class FileService implements Serializable {

	private static final long serialVersionUID = 9174911611487984802L;

	public TreeNode createDocuments(String rootPath, boolean walkRecursive) {
		return FileWalkerUtil.buildFileSystemTree(rootPath, walkRecursive);
	}
	
	public void saveFile(DocumentModel documentModel) throws IOException {
		try {
			Files.write(documentModel.getFile().toPath(), documentModel.getFileContent().getBytes());
		} catch (IOException e) {
			throw e;
		}
	}
	
	public void saveFile(UploadedFile uploadedFile, String path) throws IOException {
		File file = new File(path.replaceAll("(.+)*" + System.getProperty("file.separator") + "*$", "$1/") + uploadedFile.getFileName());
		try (InputStream inputstream = uploadedFile.getInputstream();) {
			byte[] byteArray = IOUtils.toByteArray(inputstream);
			bytesToFile(file, byteArray);
		} catch (IOException e) {
			throw e;
		}
	}

	public void bytesToFile(File file, byte[] byteArray) throws IOException {
		Files.write(file.toPath(), byteArray);
	}

	public void deleteDocumentModel(DocumentModel documentModel) throws SecurityException {
		try {
			documentModel.getFile().delete();
		} catch (SecurityException e) {
			throw e;
		}
	}
	
	public StreamedContent downloadDocumentModel(DocumentModel documentModel) throws IOException {
		try {
			InputStream inputStream = Files.newInputStream(documentModel.getFile().toPath());
			String guessedContentType = URLConnection.guessContentTypeFromStream(inputStream);
			return new DefaultStreamedContent(inputStream, guessedContentType, documentModel.getFile().getName());
		} catch (IOException e) {
			throw e;
		}
	}
}
