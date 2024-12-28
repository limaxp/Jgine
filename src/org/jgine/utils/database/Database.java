package org.jgine.utils.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
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
	private final String password;
	private final String connectionUrl;

	public Database(String url, int port, String database, String username, String password, String connectionUrl) {
		this.url = url;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.connectionUrl = connectionUrl;
	}

	public Connection openConnection() throws SQLException {
		return DriverManager.getConnection(connectionUrl, user, password);
	}

	public CallableStatement prepareCall(String procedure) throws SQLException {
		return openConnection().prepareCall(procedure);
	}

	public boolean exec(String sql) {
		boolean result = false;
		try (Connection connection = openConnection(); Statement statement = connection.createStatement()) {
			result = statement.execute(sql);
		} catch (SQLException e) {
			Logger.err("Database: Error in execute [" + sql + "]", e);
		}
		return result;
	}

	public int update(String sql) {
		int result = 0;
		try (Connection connection = openConnection(); Statement statement = connection.createStatement()) {
			result = statement.executeUpdate(sql);
		} catch (SQLException e) {
			Logger.err("Database: Error in update [" + sql + "]", e);
		}
		return result;
	}

	public List<Object[]> query(String sql) {
		List<Object[]> list = new ArrayList<Object[]>();
		try (Connection connection = openConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			int columnsNumber = resultSet.getMetaData().getColumnCount();
			while (resultSet.next()) {
				Object[] arr = new Object[columnsNumber];
				for (int i = 0; i < columnsNumber; i++)
					arr[i] = resultSet.getObject(i + 1);
				list.add(arr);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in query [" + sql + "]", e);
		}
		return list;
	}

	public boolean callExec(String procedure) {
		try (Connection connection = openConnection();
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
		try (Connection connection = openConnection();
				CallableStatement statement = connection.prepareCall(procedure);
				ResultSet resultSet = statement.executeQuery()) {
			int columnsNumber = resultSet.getMetaData().getColumnCount();
			while (resultSet.next()) {
				Object[] arr = new Object[columnsNumber];
				for (int i = 0; i < columnsNumber; i++)
					arr[i] = resultSet.getObject(i + 1);
				list.add(arr);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
		return list;
	}

	public List<Object[]> call(String procedure, Object[] args) {
		List<Object[]> list = new ArrayList<Object[]>();
		try (Connection connection = openConnection();
				CallableStatement statement = connection.prepareCall(procedure)) {
			int i;
			for (i = 0; i < args.length; i++)
				statement.setObject(i + 1, args[i]);
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

	public int[] batch(Iterable<String> queries) {
		int[] result = null;
		try (Connection connection = openConnection();
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
}
