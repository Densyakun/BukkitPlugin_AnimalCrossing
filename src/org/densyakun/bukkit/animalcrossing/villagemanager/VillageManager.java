package org.densyakun.bukkit.animalcrossing.villagemanager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.densyakun.bukkit.animalcrossing.Main;
import org.densyakun.bukkit.hubspawn.Home;
import org.densyakun.bukkit.hubspawn.HubSpawnListener;
public class VillageManager implements Listener {
	//村の情報を管理するクラス
	public static final int cliff_height = 63+8;
	public static final int ground_height = 63+4;
	public static final int beach_height = 63;
	public static final int village_width = 16*4;
	public static final int village_length = 16*5;
	public static final int edge_width = 16;
	public static final int sea_length = 16*16;
	
	public static final int playing_lobby = -1;
	
	public static final int join_joined = 1;
	public static final int join_obtaining = 0;
	public static final int join_failed = -1;
	
	private String addvillagemsg;
	private String removevillagemsg;
	private String kickvillagemsg;
	private String banvillagemsg;
	private String bannedvillagemsg;
	private String pardonvillagemsg;
	private String joinvillageobtainingmsg;
	private String joinvillagenotobtainedmsg;
	private String joinvillagenotobtainedforobtainermsg;
	private String joinvillageobtainmsg;
	private String joinvillagevillagernotfoundmsg;
	private String joinvillageobtaincrowdedmsg;
	private String joinvillageobtaincancelmsg;
	private String joinvillagemsg;
	private String quitvillagemsg;
	
	Main main;
	private File dir;//村の情報を格納するフォルダ
	private File datafile;//村の情報ファイル
	private World villagesworld;//村のワールド
	ArrayList<Village> villages;//村のリスト
	private VillageMapper mapper;//村のマッピングクラス。村が生成されたときなどにブロックを更新する。
	private HashMap<UUID, Integer> playing_villagesmap = new HashMap<UUID, Integer>();
	private HashMap<UUID, Integer> joinobtainings = new HashMap<UUID, Integer>();
	@SuppressWarnings("unchecked")
	public VillageManager(Main main) {
		this.main = main;
		dir = new File(main.getDataFolder(), "VillageManager/");
		datafile = new File(dir, "Villages.txt");
		if ((villagesworld = main.getServer().getWorld("Villages")) == null) {
			villagesworld = main.getServer().createWorld(new WorldCreator("Villages"));
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datafile));
			villages = (ArrayList<Village>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			villages = new ArrayList<Village>();
		}
		main.getLogger().info("村作成メッセージ: \"" + (addvillagemsg = main.getConfig().getString("addvillage-msg", "Villageが誕生しました！遊びに来てね！")) + "\"");
		main.getLogger().info("村削除メッセージ: \"" + (removevillagemsg = main.getConfig().getString("removevillage-msg", "Villageが削除されました。")) + "\"");
		main.getLogger().info("村追放メッセージ: \"" + (kickvillagemsg = main.getConfig().getString("kickvillage-msg", "PlayerがVillageから追放されました。")) + "\"");
		main.getLogger().info("村立入禁止メッセージ: \"" + (banvillagemsg = main.getConfig().getString("banvillage-msg", "PlayerがVillageへの立ち入りが禁止されています。")) + "\"");
		main.getLogger().info("村追放+立入禁止メッセージ: \"" + (bannedvillagemsg = main.getConfig().getString("bannedvillage-msg", "PlayerがVillageへの立ち入りが禁止されました。")) + "\"");
		main.getLogger().info("村立入禁止解除メッセージ: \"" + (pardonvillagemsg = main.getConfig().getString("pardonvillage-msg", "PlayerがVillageへの立ち入りの禁止が解除されました。")) + "\"");
		main.getLogger().info("村参加許可申出メッセージ: \"" + (joinvillageobtainingmsg = main.getConfig().getString("joinvillage-obtaining-msg", "Villageに参加の許可を取っています。")) + "\"");
		main.getLogger().info("村参加拒否メッセージ: \"" + (joinvillagenotobtainedmsg = main.getConfig().getString("joinvillage-notobtained-msg", "Villageに参加を拒否しました。")) + "\"");
		main.getLogger().info("村参加拒否メッセージ(村長向け): \"" + (joinvillagenotobtainedforobtainermsg = main.getConfig().getString("joinvillage-notobtainedforobtainer-msg", "Villageに参加を拒否されました。")) + "\"");
		main.getLogger().info("村参加可否選択メッセージ: \"" + (joinvillageobtainmsg = main.getConfig().getString("joinvillage-obtain-msg", "PlayerがVillageに遊びに来ようとしています。(Y/n)")) + "\"");
		main.getLogger().info("村参加村人不在メッセージ: \"" + (joinvillagevillagernotfoundmsg = main.getConfig().getString("joinvillage-villagernotfound-msg", "現在村人が遊んでいないため参加できません。")) + "\"");
		main.getLogger().info("村参加混雑メッセージ: \"" + (joinvillageobtaincrowdedmsg = main.getConfig().getString("joinvillage-obtaincrowded-msg", "現在混み合っています。しばらくしてもう一度参加して下さい。")) + "\"");
		main.getLogger().info("村参加キャンセルメッセージ: \"" + (joinvillageobtaincancelmsg = main.getConfig().getString("joinvillage-obtaincancel-msg", "PlayerがVillageへの参加をキャンセルしました。")) + "\"");
		main.getLogger().info("村参加メッセージ: \"" + (joinvillagemsg = main.getConfig().getString("joinvillage-msg", "PlayerがVillageに遊びに来たよ！")) + "\"");
		main.getLogger().info("村退出メッセージ: \"" + (quitvillagemsg = main.getConfig().getString("quitvillage-msg", "PlayerがVillageから出て行きました。")) + "\"");
		mapper = new VillageMapper(this);
		main.getServer().getPluginManager().registerEvents(this, main);
		if (main.getServer().getPluginManager().getPlugin("HubSpawn") != null) {
			org.densyakun.bukkit.hubspawn.Main.hubspawn.addHubSpawnListener(new HubSpawnListener() {
				@Override
				public void spawn(org.densyakun.bukkit.hubspawn.Main main, Player player) {
					int id = getPlayingVillage(player.getUniqueId());
					if (0 <= id) {
						quitVillage(getVillage(id), player);
					}
				}
				@Override
				public void home(org.densyakun.bukkit.hubspawn.Main main, Player player, Home home) {
				}
				@Override
				public void bed(org.densyakun.bukkit.hubspawn.Main main, Player player) {
				}
			});
		}
		main.getLogger().info("VillageManager: 有効");
	}
	public void save() {
		dir.mkdirs();
		try {
			datafile.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datafile));
			oos.writeObject(villages);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public World getVillagesWorld() {
		return villagesworld;
	}
	public Village addVillage(String name, Player mayor) {
		Village village = new Village(villages.size(), name, mayor.getUniqueId());
		villages.add(village);
		mapper.generateVillage(village.getId());
		String message = addvillagemsg.replaceAll("Village", village.toString());
		main.getLogger().info(message);
		Iterator<? extends Player> players = main.getServer().getOnlinePlayers().iterator();
		while (players.hasNext()) {
			players.next().sendMessage(ChatColor.stripColor(message));
		}
		return village;
	}
	public void ghosttown(Village village) {
		Iterator<Player> vplayers = getVillagePlayers(village.getId()).iterator();
		while (vplayers.hasNext()) {
			quitVillage(village, vplayers.next());
		}
		village.ghosttown();
		String message = removevillagemsg.replaceAll("Village", village.toString());
		main.getLogger().info(message);
		Iterator<? extends Player> players = main.getServer().getOnlinePlayers().iterator();
		while (players.hasNext()) {
			players.next().sendMessage(ChatColor.stripColor(message));
		}
	}
	public Village getVillage(int id) {
		for (int a = 0; a < villages.size(); a++) {
			Village village = villages.get(a);
			if (village.getId() == id) {
				return village.isGhosttown() ? null : village;
			}
		}
		return null;
	}
	public VillageMapper getMapper() {
		return mapper;
	}
	public int getPlayingVillage(UUID uuid) {
		Integer playing = playing_villagesmap.get(uuid);
		return playing == null ? playing_lobby : playing;
	}
	public List<Player> getVillagePlayers(int id) {
		List<Player> players = new ArrayList<Player>();
		Iterator<UUID> keys = playing_villagesmap.keySet().iterator();
		while (keys.hasNext()) {
			UUID uuid = keys.next();
			if (playing_villagesmap.get(uuid) == id) {
				Player player = main.getServer().getPlayer(uuid);
				if (player != null) {
					players.add(player);
				}
			}
		}
		return players;
	}
	public Integer getJoinObtaining(UUID uuid) {
		return joinobtainings.get(uuid);
	}
	public void tolobby(Player player) {
		playing_villagesmap.put(player.getUniqueId(), playing_lobby);
		if (main.getServer().getPluginManager().getPlugin("HubSpawn") != null) {
			org.densyakun.bukkit.hubspawn.Main.hubspawn.spawn(player, 0);
		} else {
			if (!player.teleport(player.getWorld().getSpawnLocation())) {
				if (player.leaveVehicle()) {
					player.teleport(player.getWorld().getSpawnLocation());
				}
			}
		}
	}
	public boolean joinable(Village village, Player player) {
		List<UUID> banplayers = village.getBanplayers();
		for (int a = 0; a < banplayers.size(); a++) {
			if (banplayers.get(a).equals(player.getUniqueId())) {
				return false;
			}
		}
		return true;
	}
	public void kickVillage(Village village, Player player) {
		String message = kickvillagemsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
		main.getLogger().info(message);
		Iterator<UUID> players = village.getVillagers().keySet().iterator();
		while (players.hasNext()) {
			Player p = main.getServer().getPlayer(players.next());
			if (p != null) {
				p.sendMessage(ChatColor.stripColor(message));
			}
		}
		quitVillage(village, player);
	}
	public void banVillage(Village village, UUID uuid) {
		joinobtainings.remove(uuid);
		village.getBanplayers().add(uuid);
		String message = bannedvillagemsg.replaceAll("Village", village.toString());
		Player player = main.getServer().getPlayer(uuid);
		if (player != null) {
			message.replaceAll("Player", player.getDisplayName());
		} else {
			message.replaceAll("Player", main.getServer().getOfflinePlayer(uuid).getName());
		}
		main.getLogger().info(message);
		Iterator<UUID> players = village.getVillagers().keySet().iterator();
		while (players.hasNext()) {
			Player p = main.getServer().getPlayer(players.next());
			if (p != null) {
				p.sendMessage(ChatColor.stripColor(message));
			}
		}
		if (getPlayingVillage(player.getUniqueId()) == village.getId()) {
			quitVillage(village, player);
		}
	}
	public void pardonVillage(Village village, UUID uuid) {
		village.getBanplayers().remove(uuid);
		String message = pardonvillagemsg.replaceAll("Village", village.toString());
		Player player = main.getServer().getPlayer(uuid);
		if (player != null) {
			message.replaceAll("Player", player.getDisplayName());
		} else {
			message.replaceAll("Player", main.getServer().getOfflinePlayer(uuid).getName());
		}
		main.getLogger().info(message);
		player.sendMessage(ChatColor.stripColor(message));
		Iterator<UUID> players = village.getVillagers().keySet().iterator();
		while (players.hasNext()) {
			Player p = main.getServer().getPlayer(players.next());
			if (p != null) {
				p.sendMessage(ChatColor.stripColor(message));
			}
		}
	}
	public void joinVillage(Village village, Player player) {
		if (joinable(village, player)) {
			if (joinobtainings.get(player.getUniqueId()) != null) {
				joinCancel(player);
			}
			if (village.getMayorUUID().equals(player.getUniqueId()) || village.getVillagers().containsKey(player.getUniqueId())) {
				int id = getPlayingVillage(player.getUniqueId());
				if (0 <= id) {
					quitVillage(getVillage(id), player);
				}
				playing_villagesmap.put(player.getUniqueId(), village.getId());
				player.teleport(village.getVillageStationSquare().toLocation(villagesworld));
				String message = joinvillagemsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
				main.getLogger().info(message);
				List<Player> players = getVillagePlayers(village.getId());
				for (int a = 0; a < players.size(); a++) {
					players.get(a).sendMessage(ChatColor.stripColor(message));
				}
			} else {
				if (joinobtainings.containsValue(village.getId())) {
					player.sendMessage(ChatColor.stripColor(joinvillageobtaincrowdedmsg));
				} else {
					Iterator<UUID> villagers = village.getVillagers().keySet().iterator();
					int a = 0;
					String message = joinvillageobtainmsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
					Player p = main.getServer().getPlayer(village.getMayorUUID());
					if (p != null) {
						p.sendMessage(ChatColor.stripColor(message));
						a++;
					}
					while (villagers.hasNext()) {
						p = main.getServer().getPlayer(villagers.next());
						if (p != null) {
							p.sendMessage(ChatColor.stripColor(message));
							a++;
						}
					}
					if (a == 0) {
						player.sendMessage(joinvillagevillagernotfoundmsg);
					} else {
						joinobtainings.put(player.getUniqueId(), village.getId());
						message = joinvillageobtainingmsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
						main.getLogger().info(message);
						player.sendMessage(ChatColor.stripColor(message));
					}
				}
			}
		} else {
			player.sendMessage(ChatColor.stripColor(banvillagemsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString())));
		}
	}
	public void joinCancel(Player player) {
		Integer villageid = joinobtainings.remove(player.getUniqueId());
		if (villageid != null) {
			List<Player> players = getVillagePlayers(villageid);
			String message = joinvillageobtaincancelmsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", getVillage(villageid).toString());
			player.sendMessage(ChatColor.stripColor(message));
			for (int a = 0; a < players.size(); a++) {
				players.get(a).sendMessage(ChatColor.stripColor(message));
			}
		}
	}
	public void joinNotObtained(Player player) {
		Integer villageid = joinobtainings.remove(player.getUniqueId());
		if (villageid != null) {
			Village village = getVillage(villageid);
			String message = joinvillagenotobtainedmsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
			main.getLogger().info(message);
			player.sendMessage(ChatColor.stripColor(joinvillagenotobtainedforobtainermsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString())));
			List<Player> players = getVillagePlayers(village.getId());
			for (int a = 0; a < players.size(); a++) {
				players.get(a).sendMessage(ChatColor.stripColor(message));
			}
		}
	}
	public void joinObtained(Village village, Player player) {
		int id = getPlayingVillage(player.getUniqueId());
		if (0 <= id) {
			quitVillage(getVillage(id), player);
		}
		joinobtainings.remove(player.getUniqueId());
		playing_villagesmap.put(player.getUniqueId(), village.getId());
		player.teleport(village.getVillageStationSquare().toLocation(villagesworld));
		String message = joinvillagemsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
		main.getLogger().info(message);
		List<Player> players = getVillagePlayers(village.getId());
		for (int a = 0; a < players.size(); a++) {
			players.get(a).sendMessage(ChatColor.stripColor(message));
		}
	}
	public void quitVillage(Village village, Player player) {
		String message = quitvillagemsg.replaceAll("Player", player.getDisplayName()).replaceAll("Village", village.toString());
		main.getLogger().info(message);
		List<Player> players = getVillagePlayers(village.getId());
		for (int a = 0; a < players.size(); a++) {
			players.get(a).sendMessage(ChatColor.stripColor(message));
		}
		tolobby(player);
	}
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent e) {
		int id = getPlayingVillage(e.getPlayer().getUniqueId());
		if (0 <= id) {
			quitVillage(getVillage(id), e.getPlayer());
		}
		if (joinobtainings.get(e.getPlayer().getUniqueId()) != null) {
			joinCancel(e.getPlayer());
		}
	}
	@EventHandler
	public void PlayerRespawn(PlayerRespawnEvent e) {
		int id = getPlayingVillage(e.getPlayer().getUniqueId());
		if (0 <= id) {
			Village village = getVillage(id);
			if (village != null) {
				e.getPlayer().teleport(village.getVillageStationSquare().toLocation(villagesworld));
			}
		}
	}
	@EventHandler
	public void AsyncPlayerChat(AsyncPlayerChatEvent e) {
		if (e.getMessage().equals("Y") || e.getMessage().equals("n")) {
			int villageid = getPlayingVillage(e.getPlayer().getUniqueId());
			if (0 <= villageid) {
				Village village = getVillage(villageid);
				if (village != null) {
					Iterator<UUID> keys = village.getVillagers().keySet().iterator();
					if (village.getVillagers().containsKey(e.getPlayer().getUniqueId())) {
						keys = joinobtainings.keySet().iterator();
						while (keys.hasNext()) {
							UUID uuid = keys.next();
							if (joinobtainings.get(uuid) == villageid) {
								Player player = main.getServer().getPlayer(uuid);
								if (e.getMessage().equals("Y")) {
									joinObtained(village, player);
								} else {
									joinNotObtained(player);
								}
								break;
							}
						}
					}
				}
			}
			e.setCancelled(true);
		}
	}
}
