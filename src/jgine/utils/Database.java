package jgine.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	public static Database mySQL(String url, int port, String database, String username, String password) {
		return new Database(url, port, database, username, password,
				"jdbc:mysql://" + url + ":" + port + "/" + database, "CALL ");
	}

	public static Database sqlServer(String url, int port, String database, String username, String password) {
		return new Database(url, port, database, username, password,
				"jdbc:sqlserver://" + url + ":" + port + "/" + database, "EXEC ");
	}

	public final String url;
	public final int port;
	public final String database;
	public final String user;
	private final String password;
	private final String connectionUrl;
	private final String callCommand;

	public Database(String url, int port, String database, String username, String password, String connectionUrl,
			String callCommand) {
		this.url = url;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
		this.connectionUrl = connectionUrl;
		this.callCommand = callCommand;
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

	public void query(String sql, DatabaseConsumer<ResultSet> func) {
		try (Connection connection = openConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(sql)) {
			while (resultSet.next()) {
				func.accept(resultSet);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in query [" + sql + "]", e);
		}
	}

	public void call(String procedure, DatabaseConsumer<ResultSet> func) {
		try (Connection connection = openConnection();
				CallableStatement statement = connection.prepareCall(callCommand + procedure);
				ResultSet resultSet = statement.executeQuery()) {
			while (resultSet.next()) {
				func.accept(resultSet);
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
	}

	public void call(String procedure, Object[] args, DatabaseConsumer<ResultSet> func) {
		try (Connection connection = openConnection();
				CallableStatement statement = connection.prepareCall(callCommand + procedure)) {
			for (int i = 0; i < args.length; i++)
				statement.setObject(i + 1, args[i]);
			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					func.accept(resultSet);
				}
			}
		} catch (SQLException e) {
			Logger.err("Database: Error in procedure [" + procedure + "]", e);
		}
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

	@FunctionalInterface
	public static interface DatabaseConsumer<T> {

		void accept(T t) throws SQLException;
	}
}