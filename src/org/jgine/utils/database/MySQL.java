package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class MySQL extends Database {

	public MySQL(String url, int port, String database, String username, String password) {
		super(url, port, database, username, password);
	}

	@Override
	protected Connection openConnection() throws SQLException, ClassNotFoundException {
		String connectionURL = "jdbc:mysql://" + url + ":" + port;
		if (database != null)
			connectionURL = connectionURL + "/" + database;

		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(connectionURL, user, password);
	}

	@Override
	public CallableStatement prepareCall(String procedure) throws SQLException {
		return super.prepareCall("CALL " + procedure);
	}

	@Override
	public boolean callExec(String procedure) {
		return super.callExec("CALL " + procedure);
	}

	@Override
	public List<Object[]> call(String procedure) {
		return super.call("CALL " + procedure);
	}

	@Override
	public List<Object[]> call(String procedure, List<Object> args) {
		return super.call("CALL " + procedure, args);
	}
}
