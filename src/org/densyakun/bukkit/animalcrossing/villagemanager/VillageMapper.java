package org.densyakun.bukkit.animalcrossing.villagemanager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
public class VillageMapper implements Runnable {
	// 村のマッピングクラス。村が生成されたときなどにブロックを更新する。
	private Thread thread;// スレッド。大量のブロックを変更する際にメインスレッドに負担がかからないようにするために必要。
	private VillageManager manager;
	private HashMap<Integer, List<String>> tasks = new HashMap<Integer, List<String>>();// タスク。メインスレッドに負担がかからないように順番にタスクをこなす。
	public VillageMapper(VillageManager manager) {
		this.manager = manager;
	}
	@Override
	public void run() {
		Iterator<Integer> a = tasks.keySet().iterator();
		while (a.hasNext()) {
			int id = a.next();
			Iterator<String> values = tasks.get(id).iterator();
			while (values.hasNext()) {
				String task = values.next();
				manager.main.getServer().getScheduler().scheduleSyncDelayedTask(manager.main, new Runnable() {
					public void run() {
						System.out.println("どう森タスクを実行中: " + task + " id: " + id);
						/*if (task.equals("c")) {
						for (int x = Village.getVillagePosX(id); x < Village.getVillagePosX(id) + VillageManager.village_width; x++) {
							for (int z = -16; z < VillageManager.village_height + VillageManager.sea_length; z++) {
								for (int y = 0; y < 256; y++) {
									manager.getVillagesWorld().getBlockAt(x, y, z).setType(Material.AIR);
								}
							}
						}
					} else */if (task.equals("g")) {
						int vx = Village.getVillagePosX(id);
						int x2 = vx + VillageManager.village_width + VillageManager.edge_width;
						for (int x = vx - VillageManager.edge_width; x < x2; x++) {
							for (int z = -16; z < VillageManager.village_length + VillageManager.sea_length; z++) {
								for (int y = 59; y < 128; y++) {
									Block block = manager.getVillagesWorld().getBlockAt(x, y, z);
									if (x < 0 || x >= vx + VillageManager.village_width || z < -3) {
										if (y == VillageManager.cliff_height) {
											block.setType(Material.GRASS);
										} else if (y < VillageManager.cliff_height) {
											block.setType(Material.STONE);
										} else {
											block.setType(Material.AIR);
										}
									} else if (z < VillageManager.village_length - 8) {
										if (y == VillageManager.ground_height) {
											block.setType(Material.GRASS);
										} else if (y < VillageManager.ground_height) {
											block.setType(Material.STONE);
										} else {
											block.setType(Material.AIR);
										}
									} else if (z < VillageManager.village_length - 4) {
										if (y == VillageManager.beach_height) {
											block.setType(Material.SAND);
										} else if (y < VillageManager.beach_height) {
											block.setType(Material.STONE);
										} else {
											block.setType(Material.AIR);
										}
									} else {
										if (y == VillageManager.beach_height) {
											block.setType(Material.WATER);
										} else if (y < VillageManager.beach_height) {
											block.setType(Material.SAND);
										} else {
											block.setType(Material.AIR);
										}
									}
								}
							}
						}
					} else {
						org.densyakun.bukkit.animalcrossing.Main.getMain().getLogger().info("[VillageMapper]不明なタスク: " + task);
					}
					}
				});
			}
		}
	}
	void addTask(int villageid, String value) {
		List<String> a = tasks.get(villageid);
		if (a == null) {
			a = new ArrayList<String>();
		}
		a.add(value);
		tasks.put(villageid, a);
	}
	void startThread() {
		if (thread == null || !thread.isAlive()) {
			(thread = new Thread(this)).start();
		}
	}
	/*public void clearVillage(int id) {
		addTask(id, "c");
		startThread();
	}*/
	public void generateVillage(int id) {
		addTask(id, "g");
		startThread();
	}
}
