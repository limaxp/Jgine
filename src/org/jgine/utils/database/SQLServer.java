package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class SQLServer extends Database {

	public SQLServer(String url, int port, String database, String username, String password) {
		super(url, port, database, username, password);
	}

	@Override
	protected Connection openConnection() throws SQLException, ClassNotFoundException {
		String connectionURL = "jdbc:sqlserver://" + url + ":" + port;
		if (database != null)
			connectionURL = connectionURL + "/" + database;

		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		return DriverManager.getConnection(connectionURL, user, password);
	}

	@Override
	public CallableStatement prepareCall(Connection connection, String procedure) throws SQLException {
		return super.prepareCall(connection, "EXEC " + procedure);
	}

	@Override
	public boolean callExec(String procedure) {
		return super.callExec("EXEC " + procedure);
	}

	@Override
	public List<Object[]> call(String procedure) {
		return super.call("EXEC " + procedure);
	}

	@Override
	public List<Object[]> call(String procedure, List<Object> args) {
		return super.call("EXEC " + procedure, args);
	}
}
