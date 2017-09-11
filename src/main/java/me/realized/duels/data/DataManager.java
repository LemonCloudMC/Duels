package me.realized.duels.data;

import me.realized.duels.Core;
import me.realized.duels.data.connection.DatabaseConnection;
import me.realized.duels.data.dao.DuelsDAO;
import me.realized.duels.event.UserCreateEvent;
import me.realized.duels.utilities.Reloadable;
import me.realized.duels.utilities.Storage;
import me.realized.duels.utilities.location.SimpleLocation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.*;
import java.util.*;

public class DataManager implements Listener, Reloadable {

	private final DatabaseConnection connection;

	private final Core instance;

	private final Map<UUID, UserData> users = new HashMap<>();

	private Location lobby;

	public DataManager(Core instance) {
		this.instance = instance;
		Bukkit.getPluginManager().registerEvents(this, instance);
		this.connection = new DatabaseConnection("databasesql", "lemon_KitPvP", "lemon", "Km6Y2rptKBx8KQcT", 3306);
		connection.connect();
	}

	public void load() {
		users.clear();

		DuelsDAO.createDuelsTable(connection.getConnection());

		File lobby = new File(instance.getDataFolder(), "lobby.json");

		if (lobby.exists()) {
			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(lobby))) {
				this.lobby = instance.getGson().fromJson(reader, SimpleLocation.class).toLocation();
			}
			catch (IOException ex) {
				instance.warn("Failed to load lobby location! (" + ex.getMessage() + ")");
			}
		}
	}

	public void save() {
		for (UUID uuid : users.keySet()) {
			if (users.containsKey(uuid)) {
				UserData userData = users.get(uuid);
				saveUser(userData);
			}
		}
	}

	private Optional<UserData> loadUser(UUID uuid) {

		Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
			@Override
			public void run() {
				if (Bukkit.getOfflinePlayer(uuid).hasPlayedBefore()) {
					Optional<UserData> userData = DuelsDAO.fetchUser(connection.getConnection(), uuid);

					if (userData.isPresent()) {
						users.put(uuid, userData.get());
					}
					else {
						UserData user = new UserData(uuid);
						users.put(uuid, user);

						UserCreateEvent event = new UserCreateEvent(user);
						Bukkit.getPluginManager().callEvent(event);
					}
				}
			}
		});

		if (users.containsKey(uuid)) {
			return Optional.of(users.get(uuid));
		}

		return Optional.empty();
	}

	private void saveUser(UserData data) {
		if (data != null) {
			Bukkit.getScheduler().runTaskAsynchronously(instance, new Runnable() {
				@Override
				public void run() {
					DuelsDAO.saveUser(connection.getConnection(), data.getUUID(), data.getWins(), data.getLosses());
				}
			});
		}
	}

	public Optional<UserData> getUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return Optional.of(users.get(uuid));
		}
		else {
			return loadUser(uuid);
		}
	}

	public Map<UUID, UserData> getUsers() {
		return Collections.unmodifiableMap(users);
	}

	public void setLobby(Player player) {
		Location lobby = player.getLocation().clone();

		try {
			File dataFile = new File(instance.getDataFolder(), "lobby.json");
			boolean generated = dataFile.createNewFile();

			if (generated) {
				instance.info("Generated 'lobby.json'.");
			}

			Writer writer = new OutputStreamWriter(new FileOutputStream(dataFile));
			instance.getGson().toJson(new SimpleLocation(lobby), writer);
			writer.flush();
			writer.close();

			this.lobby = lobby;
		}
		catch (IOException ex) {
			instance.warn("Failed to save lobby location! (" + ex.getMessage() + ")");
		}
	}

	public Location getLobby() {
		return lobby != null ? lobby : Bukkit.getWorlds().get(0).getSpawnLocation();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		loadUser(player.getUniqueId());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onQuit(PlayerQuitEvent event) {
		saveUser(users.get(event.getPlayer().getUniqueId()));
		users.remove(event.getPlayer().getUniqueId());
		Storage.remove(event.getPlayer());
	}

	@Override
	public void handleReload(ReloadType type) {
		if (type == ReloadType.STRONG) {
			save();
			load();
		}
	}
}
