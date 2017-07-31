package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryClickEvent;
public class InventoryMenu extends MenuInventory {
	public InventoryMenu(MenuManager menumanager, UUID uuid) {
		super(menumanager, 9, "メニュー", uuid);
		if (menumanager.main.playermanager.getPlayerData(uuid).getRank().isAdmin()) {
			setitem(0, Material.COMMAND, ChatColor.BOLD + "管理者機能");
		}
	}
	@Override
	public void Click(InventoryClickEvent e) {
		switch (e.getRawSlot()) {
		case 0:
			if (getMenuManager().main.playermanager.getPlayerData(e.getWhoClicked().getUniqueId()).getRank().isAdmin()) {
				e.getWhoClicked().openInventory(new InventoryAdmin(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			} else {
				e.getWhoClicked().closeInventory();
				if (e.getWhoClicked() instanceof CommandSender) {
					((CommandSender) e.getWhoClicked()).sendMessage(ChatColor.RED + "管理者ではありません");
				}
			}
			break;
		default:
			break;
		}
	}
}
