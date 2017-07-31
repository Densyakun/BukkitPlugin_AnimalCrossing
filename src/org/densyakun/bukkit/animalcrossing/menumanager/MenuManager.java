package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.densyakun.bukkit.animalcrossing.Main;
public class MenuManager implements Listener {
	public Main main;
	public List<MenuInventory> mis = new ArrayList<MenuInventory>();
	public MenuManager(Main main) {
		this.main = main;
		main.getServer().getPluginManager().registerEvents(this, main);
		main.getLogger().info("MenuManager: 有効");
	}
	public void OpenMenu(HumanEntity player) {
		player.openInventory(new InventoryMenu(this, player.getUniqueId()).getInventory());
		main.getLogger().info(player.getName() + "がメニューを開きました");
	}
	@EventHandler
	public void InventoryClick(InventoryClickEvent e) {
		for (int a = 0; a < mis.size(); a++) {
			try {
				for (int b = 0; b < mis.get(a).getInventory().getViewers().size(); b++) {
					if (mis.get(a).getInventory().getViewers().get(b).getUniqueId().equals(e.getWhoClicked().getUniqueId())) {
						e.setCancelled(true);
						switch (e.getAction()) {
						case PICKUP_ALL:
						case PICKUP_HALF:
						case PICKUP_ONE:
						case PICKUP_SOME:
							if ((e.getWhoClicked() instanceof Player) && (e.getRawSlot() < mis.get(a).getSize())) {
								((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.NOTE_STICKS, 1, 12);
								main.getLogger().info(e.getWhoClicked().getName() + "がメニューをクリック: " + e.getInventory().getName() + ", " + e.getRawSlot() + ", " + e.getCurrentItem());
								mis.get(a).Click(e);
							}
							break;
						default:
							break;
						}
						return;
					}
				}
			} catch (IndexOutOfBoundsException x) {
			}
		}
	}
	@EventHandler
	public void InventoryClose(InventoryCloseEvent e) {
		for (int a = 0; a < mis.size(); a++) {
			if (e.getPlayer().getUniqueId().equals(mis.get(a).getUuid())) {
				if (e.getPlayer() instanceof Player) {
					((Player) e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.NOTE_PIANO, 1, 0);
					main.getLogger().info(e.getPlayer().getName() + "がメニューを終了");
					mis.get(a).Close(e);
					mis.remove(a);
				}
				break;
			}
		}
	}
	@EventHandler
	public void InventoryDrag(InventoryDragEvent e) {
		for (int a = 0; a < mis.size(); a++) {
			for (int b = 0; b < mis.get(a).getInventory().getViewers().size(); b++) {
				if (mis.get(a).getInventory().getViewers().get(b).getUniqueId().equals(e.getWhoClicked().getUniqueId())) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	@EventHandler
	public void InventoryOpen(InventoryOpenEvent e) {
		for (int a = 0; a < mis.size(); a++) {
			for (int b = 0; b < mis.get(a).getInventory().getViewers().size(); b++) {
				if (mis.get(a).getInventory().getViewers().get(b).getUniqueId().equals(e.getPlayer().getUniqueId())) {
					if (e.getPlayer() instanceof Player) {
						((Player) e. getPlayer()).playSound(e.getPlayer().getLocation(), Sound.NOTE_PIANO, 1, 12);
						main.getLogger().info(e.getPlayer().getName() + "がメニューを開く: " + e.getInventory().getName());
						mis.get(a).Open(e);
					}
					return;
				}
			}
		}
	}
}
