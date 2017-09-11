package me.realized.duels.data;

import java.util.*;

public class UserData {

	private final UUID uuid;

	private int wins = 0;
	private int losses = 0;

	private boolean requests = true;

	public UserData(UUID uuid) {
		this.uuid = uuid;
	}

	public UUID getUUID() {
		return uuid;
	}

	public boolean canRequest() {
		return requests;
	}

	public void setRequestEnabled(boolean requests) {
		this.requests = requests;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getLosses() {
		return losses;
	}

	public void setLosses(int losses) {
		this.losses = losses;
	}
}
