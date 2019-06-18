package ch.bytecrowd.devtools.models.view;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;

import org.apache.log4j.Logger;

import ch.bytecrowd.devtools.beans.view.FileWalkerView;

public class DocumentModel implements Serializable, Comparable<DocumentModel> {

	private static final Logger LOG = Logger.getLogger(DocumentModel.class);
	private static final long serialVersionUID = 6548563688375134732L;

	private String name;

	private String size;

	private String type;

	private String fileContent;

	private File file;
	
	private boolean toBigToEdit = false;
	
	private static final long MAX_BITE_SIZE_TO_EDIT = 1024 * 254;
	
	public DocumentModel(File file) {
		super();
		this.file = file;
		this.name = file.getName();
		this.size = new Long(file.length()).toString();
		this.type = file.isDirectory() ? "Folder" : "Word Document";
		if (file.length() > MAX_BITE_SIZE_TO_EDIT)
			toBigToEdit = true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	
	public boolean isToBigToEdit() {
		return toBigToEdit;
	}

	public String getFileContent() {
		if (fileContent != null)
			return fileContent;
		try {
			if (!file.isDirectory())
				if (!toBigToEdit)
					return new String(Files.readAllBytes(file.toPath()));
				else
					return String.format("Filesize to big (%s bytes) max. size is %s bytes", file.length(), MAX_BITE_SIZE_TO_EDIT);
			return "Could not read file: Is a directory";
		} catch (Exception e) {
			LOG.error("getFileContent failed", e);
			return String.format("Could not read file: %s", e.getMessage());
		}
	}
	
	// Eclipse Generated hashCode and equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		DocumentModel other = (DocumentModel) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	public int compareTo(DocumentModel document) {
		return this.getName().compareTo(document.getName());
	}

}
