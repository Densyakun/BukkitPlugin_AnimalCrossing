package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.densyakun.bukkit.animalcrossing.playermanager.PlayerData;
import org.densyakun.bukkit.animalcrossing.playermanager.PlayerRank;
public class InventoryChangeRank extends MenuInventory {
	PlayerData playerdata;
	public InventoryChangeRank(MenuManager menumanager, UUID uuid, PlayerData playerdata) {
		super(menumanager, 9, "内部ランクを変更", uuid);
		this.playerdata = playerdata;
		setitem(0, Material.WOOD, PlayerRank.Default.getChatColor() + PlayerRank.Default.name());
		setitem(2, Material.IRON_BLOCK, PlayerRank.Admin.getChatColor() + PlayerRank.Admin.name());
		setitem(4, Material.OBSIDIAN, PlayerRank.Owner.getChatColor() + PlayerRank.Owner.name());
		
	}
	@Override
	public void Click(InventoryClickEvent e) {
		switch (e.getRawSlot()) {
		case 0:
			playerdata.setRank(PlayerRank.Default);
			break;
		case 2:
			playerdata.setRank(PlayerRank.Admin);
			break;
		case 4:
			playerdata.setRank(PlayerRank.Owner);
			break;
		default:
			break;
		}
		e.getWhoClicked().closeInventory();
		e.getWhoClicked().sendMessage(ChatColor.AQUA + "内部ランクを変更: " + playerdata.getRank().getChatColor() + playerdata.getInternalRank());
	}
}
