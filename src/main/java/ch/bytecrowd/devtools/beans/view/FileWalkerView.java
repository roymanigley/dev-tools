package ch.bytecrowd.devtools.beans.view;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

import ch.bytecrowd.devtools.models.view.DocumentModel;
import ch.bytecrowd.devtools.service.FileService;

@Named("fileWalkerView")
@SessionScoped
public class FileWalkerView implements Serializable {

	private static final Logger LOG = Logger.getLogger(FileWalkerView.class);
	private static final long serialVersionUID = 5969895348296613587L;

	private TreeNode root;

	private TreeNode selectedNode;

	private DocumentModel selectedDocumentModel;

	private boolean walkRecursive = false;

	private String currendDir = System.getProperty("user.home");

	@Inject
	private FileService service;

	@PostConstruct
	public void init() {
		root = service.createDocuments(currendDir, walkRecursive);
		LOG.info("currendDir: " + currendDir);
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
		if (selectedNode != null)
			this.selectedDocumentModel = ((DocumentModel) selectedNode.getData());
		displaySelectedSingle();
	}

	public void goBack() {
		currendDir = currendDir.replaceAll(String.format("[^%1$s]+%1$s?$", System.getProperty("file.separator")), "");
		init();
	}

	public StreamedContent downloadFile() {
		try {
			return service.downloadDocumentModel(selectedDocumentModel);
		} catch (IOException e) {
			LOG.error("downloadFile faid", e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Download failed", e.getMessage()));
			return null;
		}
	}
	
	public void saveFile() {
		if (selectedDocumentModel != null) {
			try {
				service.saveFile(selectedDocumentModel);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Save success", ""));
			} catch (IOException e) {
				LOG.error("saveFile faid", e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Save failed", e.getMessage()));
			}
		}
	}
	
	public void deleteFile() {
		try {
			service.deleteDocumentModel(selectedDocumentModel);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Delete success", ""));
			selectedDocumentModel = null;
			selectedNode = null;
			init();
		} catch (SecurityException e) {
			LOG.error("deleteFile faid", e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete failed", e.getMessage()));
		}
	}

	public void handleFileUpload(FileUploadEvent event) {
		FacesMessage message;
		try {
			service.saveFile(event.getFile(), currendDir);
			message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
			init();
		} catch (IOException e) {
			LOG.error("handleFileUpload faid", e);
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload failed", e.getMessage());
		}
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void setService(FileService service) {
		this.service = service;
	}

	public void displaySelectedSingle() {
		if (selectedNode != null) {
			if (selectedNode.getType().equals(DefaultTreeNode.DEFAULT_TYPE)) {
				currendDir = currendDir.replaceAll(System.getProperty("file.separator") + "\\s*$", "")
						+ System.getProperty("file.separator") + selectedNode.toString();
				init();
			} 
		}
	}

	public String getCurrendDir() {
		return currendDir;
	}

	public void setCurrendDir(String currendDir) {
		this.currendDir = currendDir;
	}

	public boolean isWalkRecursive() {
		return walkRecursive;
	}

	public void setWalkRecursive(boolean walkRecursive) {
		this.walkRecursive = walkRecursive;
	}

	public DocumentModel getSelectedDocumentModel() {
		return selectedDocumentModel;
	}

	public void setSelectedDocumentModel(DocumentModel selectedDocumentModel) {
		this.selectedDocumentModel = selectedDocumentModel;
	}
}
