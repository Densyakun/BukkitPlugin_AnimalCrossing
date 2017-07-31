package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.densyakun.bukkit.animalcrossing.playermanager.PlayerData;
public class InventoryPlayer extends MenuInventory {
	UUID uuid;
	public InventoryPlayer(MenuManager menumanager, UUID uuid) {
		super(menumanager, 9, "管理者機能 > プレイヤーの情報", uuid);
		this.uuid = uuid;
		setitem(0, Material.BEACON, ChatColor.RED + "再読み込み");
		Player player = getMenuManager().main.getServer().getPlayer(uuid);
		if (player != null) {
			setitem(1, Material.ENDER_CHEST, ChatColor.AQUA + "プレイヤーのエンダーチェストを開く");
			setitem(2, Material.FURNACE, ChatColor.AQUA + "プレイヤーの開いているインベントリを開く");
			setitem(3, Material.CHEST, ChatColor.AQUA + "プレイヤーのインベントリを開く");
			setitem(4, Material.NETHER_BRICK, ChatColor.AQUA + "ロビーに転送");
			List<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GOLD.toString() + ChatColor.BOLD + "クリックでUUID表示");
			lore.add("Name:" + player.getName());
			lore.add("DispName:" + player.getDisplayName());
			lore.add("Address:" + player.getAddress());
			lore.add("GameMode:" + player.getGameMode());
			lore.add("ItemInCursor:" + player.getItemOnCursor());
			String[] a = player.getLocation().toString().split(",");
			for (int b = 0; b < a.length; b++) {
				if (b == 0) {
					lore.add("Loc:" + a[b]);
				} else {
					lore.add("," + a[b]);
				}
			}
			setitem(7, Material.PAPER, ChatColor.GREEN + "プレイヤーの情報(1/2)", lore);
		}
		PlayerData data = menumanager.main.playermanager.getPlayerData(uuid);
		if (!data.getUuid().equals(menumanager.main.playermanager.getOwnerUniqueID())) { 
			setitem(6, Material.GOLDEN_APPLE, ChatColor.AQUA + "内部ランクを変更");
		}
		List<String> lore = new ArrayList<String>();
		lore.add("Rank: " + data.getRank());
		lore.add("InternalRank: " + data.getInternalRank());
		lore.add("isHide: " + data.isHide());
		String[] keys = data.getMetadataKeys();
		for (int a = 0; a < keys.length; a++) {
			lore.add(keys[a] + ": " + data.getMetadata(keys[a]));
		}
		setitem(8, Material.PAPER, ChatColor.GREEN + "プレイヤーの情報(2/2)", lore);
	}
	@Override
	public void Click(InventoryClickEvent e) {
		Player player = getMenuManager().main.getServer().getPlayer(uuid);
		switch (e.getRawSlot()) {
		case 0:
			e.getWhoClicked().openInventory(new InventoryPlayer(getMenuManager(), uuid).getInventory());
			break;
		case 1:
			if (player != null) {
				e.getWhoClicked().openInventory(player.getEnderChest());
			}
			break;
		case 2:
			if (player != null && player.getOpenInventory().getType() != InventoryType.CREATIVE && player.getOpenInventory().getType() != InventoryType.PLAYER) {
				e.getWhoClicked().openInventory(player.getOpenInventory().getTopInventory());
			}
			break;
		case 3:
			if (player != null) {
				e.getWhoClicked().openInventory(player.getInventory());
			}
			break;
		case 4:
			if (player != null) {
				if (getMenuManager().main.getServer().getPluginManager().getPlugin("HubSpawn") != null) {
					org.densyakun.bukkit.hubspawn.Main.hubspawn.spawn(player, 0);
					e.getWhoClicked().openInventory(new InventoryPlayer(getMenuManager(), uuid).getInventory());
				} else {
					if (!player.teleport(player.getWorld().getSpawnLocation())) {
						if (player.leaveVehicle()) {
							if (!player.teleport(player.getWorld().getSpawnLocation())) {
								e.getWhoClicked().sendMessage(ChatColor.RED + "テレポートに失敗しました");
							}
						} else {
							e.getWhoClicked().sendMessage(ChatColor.RED + "テレポートに失敗しました(乗り物から降りることができません)");
						}
					}
				}
			}
			break;
		case 6:
			e.getWhoClicked().openInventory(new InventoryChangeRank(getMenuManager(), uuid, getMenuManager().main.playermanager.getPlayerData(uuid)).getInventory());
			break;
		case 7:
		case 8:
			if (e.getWhoClicked() instanceof Player) {
				((Player) e.getWhoClicked()).sendMessage("UUID: " + player.getUniqueId());
			}
			break;
		default:
			break;
		}
	}
}
