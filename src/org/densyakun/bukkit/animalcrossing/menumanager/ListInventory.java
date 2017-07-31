package org.densyakun.bukkit.animalcrossing.menumanager;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
public abstract class ListInventory<E> extends MenuInventory {
	int page = 0;
	int maxpage = 0;
	E[] e;
	Material type;
	public ListInventory(MenuManager menumanager, String name, UUID uuid, E[] e, Material type, boolean reloadbutton) {
		super(menumanager, 54, name, uuid);
		this.e = e;
		this.type = type;
		setitem(0, Material.ARROW, ChatColor.RED + "前");
		if (reloadbutton) {
			setitem(4, Material.BEACON, ChatColor.RED + "再読み込み");
		}
		setitem(8, Material.ARROW, ChatColor.RED + "次");
		reload();
	}
	@Override
	public void Click(InventoryClickEvent e) {
		if (e.getRawSlot() == 0) {
			if (0 < page) {
				page--;
			}
			reload();
		} else if (e.getRawSlot() == 4) {
			reload();
		} else if (e.getRawSlot() == 8) {
			if (page < maxpage) {
				page++;
			}
			reload();
		} else if (9 <= e.getRawSlot()) {
			ListClick((((getSize() - 9) * (page + 1)) - (getSize() - 9)) + (e.getRawSlot() - 9), e);
		} else {
			ClickButton(e);
		}
	}
	public void reload() {
		int n = 0;
		for (int a = (((getSize() - 9) * (page + 1)) - (getSize() - 9)); a < e.length; a++) {
			if (e[a] != null) {
				setitem(9 + n, type, ChatColor.RED + getName(e[a]));
			}
			n++;
			if (getInventory().getSize() <= n) {
				break;
			}
		}
	}
	public void ClickButton(InventoryClickEvent e) {
	}
	public void ListClick(int n, InventoryClickEvent e) {
	}
	public String getName(E e) {
		return e.toString();
	}
}
