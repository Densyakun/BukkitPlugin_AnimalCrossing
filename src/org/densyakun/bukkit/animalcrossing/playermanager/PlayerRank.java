package org.densyakun.bukkit.animalcrossing.playermanager;
import org.bukkit.ChatColor;
import org.bukkit.Color;
public enum PlayerRank {
	Owner, Admin, Default;
	public Color getColor() {
		switch (this) {
		case Owner:
			return Color.RED;
		case Admin:
			return Color.ORANGE;
		default:
			return Color.WHITE;
		}
	}
	public ChatColor getChatColor() {
		switch (this) {
		case Owner:
			return ChatColor.RED;
		case Admin:
			return ChatColor.GOLD;
		default:
			return ChatColor.WHITE;
		}
	}
	public boolean isAdmin() {
		return this != Default;
	}
	public static PlayerRank getDefault() {
		return Default;
	}
}
