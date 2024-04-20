package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jgine.utils.logger.Logger;

public abstract class Database {

	public final String url;
	public final int port;
	public final String database;
	public final String user;
	protected final String password;

	public Database(String url, int port, String database, String username, String password) {
		this.url = url;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	protected abstract Connection openConnection() throws SQLException, ClassNotFoundException;

	public Connection getConnection() {
		try {
			return openConnection();
		} catch (Exception e) {
			Logger.err("Database: Error! Could not open connection!", e);
		}
		return null;
	}

	public Statement createStatement() throws SQLException {
		return getConnection().createStatement();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		return getConnection().createStatement(resultSetType, resultSetConcurrency);
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return getConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return getConnection().prepareStatement(sql);
	}

	public CallableStatement prepareCall(String procedure) throws SQLException {
		return getConnection().prepareCall(procedure);
	}

	public List<Object[]> query(String sql) {
		List<Object[]> list = new ArrayList<Object[]>();
		try (Connection connection = getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			int columnsNumber = resultSet.getMetaData().getColumnCount();
			int i;
			Object[] arr;
			while (resultSet.next()) {
				arr = new Object[columnsNumber];
				for (i = 0; i < columnsNumber; i++)
					arr[i] = resultSet.getObject(i + 1);
				list.add(arr);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in query [" + sql + "]", e);
		}
		return list;
	}

	public boolean exec(String sql) {
		boolean result = false;
		try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
			result = statement.execute(sql);
		} catch (SQLException e) {
			Logger.err("Database: Error in execute [" + sql + "]", e);
		}
		return result;
	}

	public int update(String sql) {
		int result = 0;
		try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
			result = statement.executeUpdate(sql);
		} catch (SQLException e) {
			Logger.err("Database: Error in update [" + sql + "]", e);
		}
		return result;
	}

	public int[] batch(Iterable<String> queries) {
		int[] result = null;
		try (Connection connection = getConnection();
				Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_UPDATABLE)) {
			for (String s : queries)
				statement.addBatch(s);
			result = statement.executeBatch();
		} catch (SQLException e) {
			Logger.err("Database: Error in batch [" + queries.toString() + "]", e);
		}
		return result;
	}

	public boolean callExec(String procedure) {
		try (Connection connection = getConnection();
				CallableStatement statement = connection.prepareCall(procedure);
				ResultSet resultSet = statement.executeQuery()) {
			return true;
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
		return false;
	}

	public List<Object[]> call(String procedure) {
		List<Object[]> list = new ArrayList<Object[]>();
		try (Connection connection = getConnection();
				CallableStatement statement = connection.prepareCall(procedure);
				ResultSet resultSet = statement.executeQuery()) {
			int columnsNumber = resultSet.getMetaData().getColumnCount();
			int i;
			Object[] arr;
			while (resultSet.next()) {
				arr = new Object[columnsNumber];
				for (i = 0; i < columnsNumber; i++)
					arr[i] = resultSet.getObject(i + 1);
				list.add(arr);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
		return list;
	}

	public List<Object[]> call(String procedure, List<Object> args) {
		List<Object[]> list = new ArrayList<Object[]>();
		try (Connection connection = getConnection(); CallableStatement statement = connection.prepareCall(procedure)) {
			int i;
			for (i = 0; i < args.size(); i++)
				statement.setObject(i + 1, args.get(i));
			try (ResultSet resultSet = statement.executeQuery()) {
				int columnsNumber = resultSet.getMetaData().getColumnCount();
				Object[] arr;
				while (resultSet.next()) {
					arr = new Object[columnsNumber];
					for (i = 0; i < columnsNumber; i++)
						arr[i] = resultSet.getObject(i + 1);
					list.add(arr);
				}
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
		return list;
	}
}
