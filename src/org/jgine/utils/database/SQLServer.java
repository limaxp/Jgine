package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;

public class SQLServer extends Database {

	public SQLServer(String url, int port, String database, String username, String password) {
		super(url, port, database, username, password, "jdbc:sqlserver://" + url + ":" + port + "/" + database);
	}

	@Override
	public CallableStatement prepareCall(String procedure) throws SQLException {
		return super.prepareCall("EXEC " + procedure);
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
	public List<Object[]> call(String procedure, Object[] args) {
		return super.call("EXEC " + procedure, args);
	}
}
