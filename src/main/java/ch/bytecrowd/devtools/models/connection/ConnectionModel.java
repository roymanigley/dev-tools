package ch.bytecrowd.devtools.models.connection;

import java.sql.Connection;
import java.sql.SQLException;

import ch.bytecrowd.devtools.utils.SQLUtil;

public class ConnectionModel {

	private String jdbcUrl = (System.getProperty("sqleditor-jdbc-url")) ==null ? "jdbc:mysql://localhost:3006/information_schema" : System.getProperty("sqleditor-jdbc-url").replaceAll("EQUAL_SIGN", "=");
	private String username = (System.getProperty("sqleditor-username")) ==null ? "test" : System.getProperty("sqleditor-username");
	private String password = (System.getProperty("sqleditor-password")) ==null ? "test" : System.getProperty("sqleditor-password");;
	

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public Connection createConnection() throws SQLException {
		return SQLUtil.openConnection(jdbcUrl, username, password);
	}

	@Override
	public String toString() {
		return "ConnectionModel [jdbcUrl=" + jdbcUrl + ", username=" + username + "]";
	}
}
