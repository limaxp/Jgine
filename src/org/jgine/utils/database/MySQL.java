package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

public class MySQL extends Database {

	public MySQL(String url, int port, String database, String username, String password) {
		super(url, port, database, username, password, "jdbc:mysql://" + url + ":" + port + "/" + database);
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
	public List<Object[]> call(String procedure, Object[] args) {
		return super.call("CALL " + procedure, args);
	}
}
