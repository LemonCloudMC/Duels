package me.realized.duels.data.dao;

import me.realized.duels.data.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DuelsDAO {

	public static boolean createDuelsTable(Connection connection) {
		String query = "CREATE TABLE IF NOT EXISTS duels("
				+ "uuid VARCHAR(32) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,"
				+ "wins INT NOT NULL,"
				+ "losses INT NOT NULL,"
				+ "PRIMARY KEY (uuid)"
				+ ");";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.execute();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static boolean saveUser(Connection connection, UUID uuid, int wins, int losses) {
		String query = "INSERT INTO duels (uuid, wins, losses) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE wins=VALUES(wins), losses=VALUES(losses);";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, uuid.toString().replaceAll("-", ""));
			preparedStatement.setInt(2, wins);
			preparedStatement.setInt(3, losses);
			preparedStatement.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static Optional<UserData> fetchUser(Connection connection, UUID uuid) {
		String query = "SELECT * FROM duels WHERE uuid =?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, uuid.toString().replaceAll("-", ""));
			preparedStatement.executeQuery();
			try (ResultSet result = preparedStatement.executeQuery()) {
				if (result.next()) {

					int wins = result.getInt("wins");
					int losses = result.getInt("losses");

					UserData userData = new UserData(uuid);
					userData.setWins(wins);
					userData.setLosses(losses);

					return Optional.of(userData);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public static boolean deleteUser(Connection connection, UUID uuid) {
		try (PreparedStatement statement = connection.prepareStatement("DELETE FROM duels WHERE uuid =? LIMIT 1;")) {
			statement.setString(1, uuid.toString().replaceAll("-", ""));
			statement.executeUpdate();
		}
		catch (SQLException exc) {
			exc.printStackTrace();
			return false;
		}

		return true;
	}

}
