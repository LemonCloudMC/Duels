package me.realized.duels.data.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	public static final int DEFAULT_PORT = 3306;

	private String host;
	private String database;
	private String username;
	private String password;

	private int port;

	private Connection connection;

	public DatabaseConnection(String host, String database, String username, String password) {
		this(host, database, username, password, DEFAULT_PORT);
	}

	public DatabaseConnection(String host, String database, String username, String password, int port) {
		this.host = host;
		this.database = database;
		this.username = username;
		this.password = password;
		this.port = port;
	}

	public void connect() {
		try {
			synchronized (this) {
				if (connection != null && !connection.isClosed()) {
					return;
				}

				connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s",
						host,
						port,
						database
				), username, password);
			}
		}
		catch (SQLException ignored) {
			connect();
		}
	}

	public void disconnect() {
		try {
			connection.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return connection;
	}
}

