package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
public class InventoryAdmin extends MenuInventory {
	public InventoryAdmin(MenuManager menumanager, UUID uuid) {
		super(menumanager, 9, "メニュー > 管理者機能", uuid);
		setitem(0, Material.SKULL_ITEM, ChatColor.AQUA + "プレイヤーの情報を見る");
		setitem(2, Material.THIN_GLASS, ChatColor.GREEN + "一般プレイヤーから姿を隠す");
		setitem(4, Material.STAINED_GLASS_PANE, ChatColor.GREEN + "姿を表示");
		ItemStack item = setitem(6, Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "管理モード: " + (menumanager.main.playermanager.isAdminmode() ? ChatColor.AQUA : ChatColor.RED) + menumanager.main.playermanager.isAdminmode());
		MaterialData data = item.getData();
		if (data instanceof Wool) {
			((Wool) data).setColor(menumanager.main.playermanager.isAdminmode() ? DyeColor.CYAN : DyeColor.RED);
		}
		item.setData(data);
		item = setitem(8, Material.WOOL, ChatColor.RED.toString() + ChatColor.BOLD + "ホワイトモード: " + (menumanager.main.playermanager.isWhitemode() ? ChatColor.AQUA : ChatColor.RED) + menumanager.main.playermanager.isWhitemode());
		data = item.getData();
		if (data instanceof Wool) {
			((Wool) data).setColor(menumanager.main.playermanager.isWhitemode() ? DyeColor.CYAN : DyeColor.RED);
		}
		item.setData(data);
	}
	@Override
	public void Open(InventoryOpenEvent e) {
		if (!getMenuManager().main.playermanager.getPlayerData(e.getPlayer().getUniqueId()).getRank().isAdmin()) {
			e.setCancelled(true);
		}
	}
	@Override
	public void Click(InventoryClickEvent e) {
		switch (e.getRawSlot()) {
		case 0:
			e.getWhoClicked().openInventory(new InventoryPlayerSelect(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			break;
		case 2:
			e.getWhoClicked().closeInventory();
			if (e.getWhoClicked() instanceof Player) {
				getMenuManager().main.playermanager.sethide((Player) e.getWhoClicked(), true);
				((Player) e.getWhoClicked()).sendMessage(ChatColor.AQUA + "一般プレイヤーから姿を隠しました");
			}
			e.getWhoClicked().openInventory(new InventoryAdmin(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			break;
		case 4:
			e.getWhoClicked().closeInventory();
			if (e.getWhoClicked() instanceof Player) {
				getMenuManager().main.playermanager.sethide((Player) e.getWhoClicked(), false);
				((Player) e.getWhoClicked()).sendMessage(ChatColor.AQUA + "姿を表示しました");
			}
			e.getWhoClicked().openInventory(new InventoryAdmin(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			break;
		case 6:
			e.getWhoClicked().closeInventory();
			getMenuManager().main.playermanager.adminmodetoggle();
			e.getWhoClicked().openInventory(new InventoryAdmin(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			break;
		case 8:
			e.getWhoClicked().closeInventory();
			getMenuManager().main.playermanager.whitemodetoggle();
			e.getWhoClicked().openInventory(new InventoryAdmin(getMenuManager(), e.getWhoClicked().getUniqueId()).getInventory());
			break;
		default:
			break;
		}
	}
}
