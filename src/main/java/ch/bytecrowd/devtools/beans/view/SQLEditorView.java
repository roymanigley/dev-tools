package ch.bytecrowd.devtools.beans.view;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import ch.bytecrowd.devtools.beans.session.SQLEditorSession;
import ch.bytecrowd.devtools.models.view.TableModel;
import ch.bytecrowd.devtools.utils.DBUnitUtils;
import ch.bytecrowd.devtools.utils.QueryBuilderUtil;
import ch.bytecrowd.devtools.utils.SQLUtil;

@Named("sqlEditorView")
@ViewScoped
public class SQLEditorView implements Serializable {

	private static final long serialVersionUID = 5969895348296613587L;
	
	private static final Logger LOG = Logger.getLogger(SQLEditorView.class);

	@Inject
	private SQLEditorSession editorSession;
	
	private TableModel tableModel = null;
	private String editorContent = "";
	private DefaultStreamedContent streamedContent = null; 
	
//	@PostConstruct
//	public void init() {
//		editorSession.init();
//	}

	
	public void executeQuery() {
		executeQuerySelection(editorContent);
	}

	public void executeQuerySelection(String sql) {
		LOG.info(sql);
		boolean error = false;
		long start = System.currentTimeMillis();
		tableModel = new TableModel(sql);
		try {
			if (sql.trim().toLowerCase().startsWith("select "))
				tableModel = SQLUtil.executeSelectQuery(editorSession.getCurrentConnection(), sql);
			else
				SQLUtil.executeDMLQuery(editorSession.getCurrentConnection(), sql);
		} catch (SQLException e) {
			error = true;
			LOG.error("executeQuerySelection failed: " + e);
			LOG.debug("executeQuerySelection failed", e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Query failed", e.getMessage()));
			tableModel = null;
		}
		if (!error) {
			editorSession.getSqlQueryHistory().remove(sql.trim());
			editorSession.getSqlQueryHistory().add(sql.trim());
			showQueryExecutionTimeMessage(start);
		}
	}

	public void prepareDBUnit() {
		try {
			StringBuilder dbUnit = DBUnitUtils.toDbUnit(editorSession.getCurrentConnection(), tableModel.getSqlQuery());
			try (InputStream inputStream = IOUtils.toInputStream(dbUnit,"UTF-8");) {
				streamedContent = new DefaultStreamedContent(inputStream, "text/xml", String.format("DBUnitDump-%s.xml", tableModel.getGuessedTableName()));
			} catch (Exception e) {
				throw e;
			}
		} catch (Exception e) {
			LOG.error("prepareDBUnit failed", e);
			streamedContent = null;
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Download failed", e.getMessage()));
		}
	}
	
	public StreamedContent downloadDBUnit() {
		return streamedContent;
	}

	public void showQueryExecutionTimeMessage(long start) {
		long duration = System.currentTimeMillis() - start;
		String formatedDuration = new DecimalFormat("###,###.###").format(duration);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Success:", String.format("Query fetched %s records in %s ms", tableModel.getData().size(), formatedDuration)));
	}
	
	public void toDeleteQuery() {
		setEditorContentAppend(QueryBuilderUtil.toDeleteQuery(tableModel));
	}
	
	public void toInsertQuery() {
		setEditorContentAppend(QueryBuilderUtil.toInsertQuery(tableModel));
	}
	
	public void toUpdateQuery() {
		setEditorContentAppend(QueryBuilderUtil.toUpdateQuery(tableModel));
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	public void setTableModel(TableModel tableModel) {
		this.tableModel = tableModel;
	}

	public SQLEditorSession getEditorSession() {
		return editorSession;
	}

	public String getEditorContent() {
		return editorContent;
	}

	public void setEditorContent(String editorContent) {
		this.editorContent = editorContent;
	}

	public void setEditorContentAppend(String editorContent) {
		this.editorContent = String.format("%s\n\n%s", this.editorContent, editorContent);
	}
	
	
	
	public String getEditorContentAppend() {
		return null;
	}
}
