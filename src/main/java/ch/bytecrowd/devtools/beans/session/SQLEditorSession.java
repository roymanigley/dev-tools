package ch.bytecrowd.devtools.beans.session;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.BeforeDestroyed;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

import ch.bytecrowd.devtools.models.connection.ConnectionModel;
import ch.bytecrowd.devtools.utils.SQLAutoCompleteUtil;

@SessionScoped
public class SQLEditorSession implements Serializable {

	private static final Logger LOG = Logger.getLogger(SQLEditorSession.class);
	private static final long serialVersionUID = 5969895348296613587L;
	private ConnectionModel currentConnectionModel = new ConnectionModel();
	private Connection currentConnection;
	
	private List<ConnectionModel> connectionModels = new ArrayList<>();
	
	private String sqlAutocomplete; 
	
	private List<String> sqlQueryHistory = new ArrayList<>();
		
	private Boolean autoRefresh = Boolean.FALSE;
	
	public SQLEditorSession() {
		super();
	}

	public void init() {
		try {
			distroy();
			getCurrentConnection();
			if (autoRefresh)
				refreshSqlAutocompleteQuery();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "Connection etablished", String.format("<br>Username: %s<br>JDBC-Url: %s", currentConnectionModel.getUsername(), currentConnectionModel.getJdbcUrl())));
			LOG.error("connected: " + currentConnectionModel.toString());
		} catch (Exception e) {

			LOG.error("init failed: " + currentConnectionModel.toString() + " " + e);
			LOG.debug("init failed" , e);
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Connection failed", e.getMessage()));
		}
	}

	@BeforeDestroyed(SessionScoped.class)
	public void distroy() {
		try {
			if (currentConnection != null && !currentConnection.isClosed())
			currentConnection.close();
		} catch (SQLException e) {
			LOG.error("distroy failed: " + e);
			LOG.debug("distroy failed", e);
		}
	}
	
	private void refreshSqlAutocompleteQuery() throws SQLException {
		sqlAutocomplete = SQLAutoCompleteUtil.fetchJsonAutocompleteSQL(getCurrentConnection());
		autoRefresh = Boolean.FALSE;
	}

	public String getSqlAutocomplete() {
		return sqlAutocomplete;
	}

	public void setSqlAutocomplete(String sqlAutocomplete) {
		this.sqlAutocomplete = sqlAutocomplete;
	}

	public List<String> getSqlQueryHistory() {
		return sqlQueryHistory;
	}
	
	public List<ConnectionModel> getConnectionModels() {
		return connectionModels;
	}

	public void setConnectionModels(List<ConnectionModel> connectionModels) {
		this.connectionModels = connectionModels;
	}
	
	public Connection getCurrentConnection() throws SQLException {
		try {
			if (currentConnection == null || currentConnection.isClosed())
				currentConnection = currentConnectionModel.createConnection();
		} catch (SQLException e) {
			throw e;
		}
		return currentConnection;
	}

	public ConnectionModel getCurrentConnectionModel() {
		return currentConnectionModel;
	}

	public void setCurrentConnectionModel(ConnectionModel currentConnectionModel) {
		this.currentConnectionModel = currentConnectionModel;
	}

	public Boolean getAutoRefresh() {
		return autoRefresh;
	}

	public void setAutoRefresh(Boolean autoRefresh) {
		this.autoRefresh = autoRefresh;
	}
	
}