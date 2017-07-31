package org.densyakun.bukkit.animalcrossing.playermanager;
import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.densyakun.bukkit.animalcrossing.Main;
public class PlayerData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private UUID uuid;
	private PlayerRank rank;
	private boolean hide = false;
	private HashMap<String, String> metadata = new HashMap<String, String>();
	public PlayerData(UUID uuid) {
		this(uuid, PlayerRank.getDefault());
	}
	public PlayerData(UUID uuid, PlayerRank rank) {
		this.uuid = uuid;
		this.rank = rank;
	}
	public UUID getUuid() {
		return uuid;
	}
	public PlayerRank getRank() {
		if (uuid.equals(Main.getMain().playermanager.getOwnerUniqueID())) {
			return PlayerRank.Owner;
		}
		return rank;
	}
	public PlayerRank getInternalRank() {
		return rank;
	}
	public void setRank(PlayerRank rank) {
		Main main = Main.getMain();
		if (!uuid.equals(main.playermanager.getOwnerUniqueID())) {
			if (rank == PlayerRank.Owner) {
				setMetadata("ownerpromotemsg", "");
				removeMetadata("adminpromotemsg");
				removeMetadata("admindemotemsg");
			}
			if (rank == PlayerRank.Admin) {
				if (this.rank.isAdmin()) {
					setMetadata("admindemotemsg", "");
					removeMetadata("ownerpromotemsg");
					removeMetadata("adminpromotemsg");
				} else {
					setMetadata("adminpromotemsg", "");
					removeMetadata("ownerpromotemsg");
					removeMetadata("admindemotemsg");
				}
			}
			if (rank == PlayerRank.Default) {
				setMetadata("promotemsg", "");
				removeMetadata("ownerpromotemsg");
				removeMetadata("adminpromotemsg");
				removeMetadata("admindemotemsg");
			}
			this.rank = rank;
			Player player = main.getServer().getPlayer(uuid);
			if (player != null) {
				main.playermanager.namereload(player);
				main.playermanager.rankmessage(player, this);
			}
		}
	}
	public boolean isHide() {
		return hide;
	}
	public void setHide(boolean hide) {
		this.hide = hide;
	}
	public String[] getMetadataKeys() {
		return metadata.keySet().toArray(new String[0]);
	}
	public String getMetadata(String key) {
		return metadata.get(key);
	}
	public void setMetadata(String key, String value) {
		metadata.put(key, value);
	}
	public String removeMetadata(String key) {
		return metadata.remove(key);
	}
}
