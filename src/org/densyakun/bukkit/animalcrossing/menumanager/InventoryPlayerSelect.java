package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
public class InventoryPlayerSelect extends ListInventory<Player> {
	public InventoryPlayerSelect(MenuManager menumanager, UUID uuid) {
		super(menumanager, "管理者機能 > プレイヤーを選択", uuid, new ArrayList<Player>(menumanager.main.getServer().getOnlinePlayers()).toArray(new Player[0]), Material.ENDER_CHEST, true);
	}
	@Override
	public void Open(InventoryOpenEvent e) {
		if (!getMenuManager().main.playermanager.getPlayerData(e.getPlayer().getUniqueId()).getRank().isAdmin()) {
			e.setCancelled(true);
		}
	}
	@Override
	public void ListClick(int n, InventoryClickEvent e) {
		if (e.getInventory().getItem(e.getRawSlot()) != null) {
			e.getWhoClicked().openInventory(new InventoryPlayer(getMenuManager(), this.e[n].getUniqueId()).getInventory());
		}
	}
	@Override
	public String getName(Player e) {
		return e.getDisplayName();
	}
}
