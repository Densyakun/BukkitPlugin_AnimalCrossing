package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
public abstract class MenuInventory {
	private MenuManager menumanager;
	private int size;
	private String name;
	private Inventory inv;
	private UUID uuid;
	public MenuInventory(MenuManager menumanager, int size, String name, UUID uuid) {
		this.menumanager = menumanager;
		this.size = size;
		this.name = name;
		inv = menumanager.main.getServer().createInventory(null, size, name);
		this.uuid = uuid;
		menumanager.mis.add(this);
	}
	public MenuManager getMenuManager() {
		return menumanager;
	}
	public int getSize() {
		return size;
	}
	public String getName() {
		return name;
	}
	public Inventory getInventory() {
		return inv;
	}
	public UUID getUuid() {
		return uuid;
	}
	public ItemStack setitem(int loc, Material mat, String name) {
		return setitem(loc, mat, name, null);
	}
	public ItemStack setitem(int loc, Material mat, String name, List<String> lore) {
		if (loc < 0) {
			loc = 0;
		} else if (size <= loc) {
			return null;
		}
		ItemStack idi = new ItemStack(mat);
		ItemMeta idimeta = idi.getItemMeta();
		if (name != null) {
			idimeta.setDisplayName(name);
		}
		if (lore != null) {
			idimeta.setLore(lore);
		}
		idi.setItemMeta(idimeta);
		idimeta = null;
		inv.setItem(loc, idi);
		return idi;
	}
	public void Click(InventoryClickEvent e) {
	}
	public void Close(InventoryCloseEvent e) {
	}
	public void Open(InventoryOpenEvent e) {
	}
}
