package org.densyakun.bukkit.animalcrossing.playermanager;
import java.awt.TrayIcon.MessageType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.densyakun.bukkit.animalcrossing.Main;
import org.densyakun.csvm.CSVFile;
public class PlayerManager implements Listener/*, Runnable*/ {
	private Main main;
	private UUID ownerUUID;
	private File dir;
	private File datafile;
	private ArrayList<PlayerData> pdata;
	private String joinmsg;
	private String quitmsg;
	private String welcomemsg;
	private boolean adminmode = false;
	private boolean whitemode = false;
	
	private String noticemsg;
	private String demotemsg;
	private String adminpromotemsg;
	private String adminmsg;
	private String admindemotemsg;
	private String ownerpromotemsg;
	
	private String whitemodemsg;
	@SuppressWarnings("unchecked")
	public PlayerManager(Main main) {
		this.main = main;
		dir = new File(main.getDataFolder(), "PlayerManager/");
		datafile = new File(dir, "Rank.txt");
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datafile));
			pdata = (ArrayList<PlayerData>) ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			pdata = new ArrayList<PlayerData>();
		}
		String uuidstr = main.getConfig().getString("ownerUUID", null);
		if (uuidstr != null) {
			main.getLogger().info("ownerUUID: " + (ownerUUID = UUID.fromString(uuidstr)));
		} else {
			main.getLogger().info("ownerUUID: (:null:)");
		}
		main.getLogger().info("ログインメッセージ: \"" + (joinmsg = main.getConfig().getString("joinmsg", "Playerがログインしました")) + "\"");
		main.getLogger().info("ログアウトメッセージ: \"" + (quitmsg = main.getConfig().getString("quitmsg", "Playerがログアウトしました")) + "\"");
		main.getLogger().info("ウェルカムメッセージ: \"" + (welcomemsg = main.getConfig().getString("welcomemsg", "ようこそ、どうぶつの森サーバーへ! /menuで便利なメニューを開けます")) + "\"");
		
		main.getLogger().info("お知らせメッセージ: \"" + (noticemsg = main.getConfig().getString("notice-msg", "重要なお知らせがあります： URL")) + "\"");
		main.getLogger().info("住民降格メッセージ: \"" + (demotemsg = main.getConfig().getString("demote-msg", "一般プレイヤーへ降格されました。")) + "\"");
		main.getLogger().info("管理者昇格メッセージ: \"" + (adminpromotemsg = main.getConfig().getString("adminpromote-msg", "おめでとうございます。管理者へ昇格しました。管理者に向けて説明があります。こちらをご覧下さい: URL")) + "\"");
		main.getLogger().info("管理者用メッセージ: \"" + (adminmsg = main.getConfig().getString("admin-msg", "管理者の方々へお知らせがあります: URL")) + "\"");
		main.getLogger().info("管理者降格メッセージ: \"" + (admindemotemsg = main.getConfig().getString("admindemote-msg", "管理者へ降格されました。")) + "\"");
		main.getLogger().info("副鯖主昇格メッセージ: \"" + (ownerpromotemsg = main.getConfig().getString("ownerpromote-msg", "おめでとうございます。副鯖主へ昇格しました。")) + "\"");
		main.getLogger().info("管理者専用メッセージ: \"" + (whitemodemsg = main.getConfig().getString("whitemode-msg", "ホワイトモード(管理者専用モード)になったためキックされました。復旧するまでしばらくお待ち下さい。")) + "\"");
		main.getServer().getPluginManager().registerEvents(this, main);
		main.getLogger().info("PlayerManager: 有効");
	}
	public void save() {
		dir.mkdirs();
		try {
			datafile.createNewFile();
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datafile));
			oos.writeObject(pdata);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public UUID getOwnerUniqueID() {
		return ownerUUID;
	}
	public PlayerData getPlayerData(UUID uuid) {
		for (int a = 0; a < pdata.size(); a++) {
			if (pdata.get(a).getUuid().equals(uuid)) {
				return pdata.get(a);
			}
		}
		PlayerData data = new PlayerData(uuid);
		pdata.add(data);
		return data;
	}
	/*public void UpdatePlayerData(PlayerData data) {
		boolean a = true;
		for (int b = 0; b < pdata.size(); b++) {
			if (pdata.get(b).getUuid().equals(data.getRank())) {
				a = false;
				pdata.set(b, data);
				break;
			}
		}
		if (a) {
			pdata.add(data);
		}
		Player player = main.getServer().getPlayer(data.getUuid());
		if (player != null) {
			namereload(player);
		}
	}*/
	public void sethide(Player player, boolean hide) {
		for (int a = 0; a < pdata.size(); a++) {
			if (player.getUniqueId().equals(pdata.get(a).getUuid())) {
				pdata.get(a).setHide(hide);
				Player[] players = main.getServer().getOnlinePlayers().toArray(new Player[0]);
				for (int b = 0; b < players.length; b++) {
					if (pdata.get(a).isHide()) {
						if (getPlayerData(players[b].getUniqueId()).getRank().isAdmin()) {
							players[b].showPlayer(player);
						} else {
							players[b].hidePlayer(player);
						}
					} else {
						players[b].showPlayer(player);
					}
				}
				break;
			}
		}
	}
	public void adminmodetoggle() {
		if (adminmode = !adminmode) {
			main.getLogger().info("管理モードが有効");
		} else {
			main.getLogger().info("管理モードが無効");
		}
		Iterator<? extends Player> a = main.getServer().getOnlinePlayers().iterator();
		while (a.hasNext()) {
			Player player = a.next();
			PlayerData data = getPlayerData(player.getUniqueId());
			if (data.getRank().isAdmin()) {
				if (adminmode) {
					player.sendMessage(ChatColor.AQUA + "管理モードが有効");
				} else {
					player.sendMessage(ChatColor.RED + "管理モードが無効");
					if (player.getGameMode() == GameMode.CREATIVE) {
						player.setGameMode(GameMode.SURVIVAL);
					}
				}
			}
		}
	}
	public boolean isAdminmode() {
		return adminmode;
	}
	public void namereload(Player player) {
		PlayerData data = getPlayerData(player.getUniqueId());
		String name = new String();
		switch (data.getRank()) {
		case Owner:
			name += data.getRank().getChatColor() + "[O]" + ChatColor.WHITE;
			if (!isAdminmode() && player.getGameMode() == GameMode.CREATIVE) {
				player.setGameMode(GameMode.SURVIVAL);
			}
			break;
		case Admin:
			name += data.getRank().getChatColor() + "[A]" + ChatColor.WHITE;
			if (!isAdminmode() && player.getGameMode() == GameMode.CREATIVE) {
				player.setGameMode(GameMode.SURVIVAL);
			}
			break;
		default:
			player.setGameMode(GameMode.SURVIVAL);
			break;
		}
		player.setOp(data.getRank().isAdmin());
		if (data.getRank().isAdmin()) {
			name += ChatColor.BOLD;
		}
		String nick = data.getMetadata("nick");
		if (nick == null) {
			name += player.getName() + ChatColor.RESET;
		} else {
			name += nick + ChatColor.RESET;
		}
		String tabname = name.substring(0, name.length() <= 16 ? name.length() : 16);
		player.setDisplayName(name);
		player.setPlayerListName(tabname.charAt(tabname.length() - 1) == '§' ? tabname.substring(0, tabname.length() - 1) : tabname);
		for (int b = 0; b < pdata.size(); b++) {
			Player c = main.getServer().getPlayer(pdata.get(b).getUuid());
			if (c != null) {
				if (!getPlayerData(player.getUniqueId()).getRank().isAdmin() && pdata.get(b).isHide()) {
					player.hidePlayer(c);
				} else {
					player.showPlayer(c);
				}
			}
		}
	}
	public void whitemodetoggle() {
		if (whitemode = !whitemode) {
			main.getLogger().info("ホワイトモードが有効");
		} else {
			main.getLogger().info("ホワイトモードが無効");
		}
		Player[] players = main.getServer().getOnlinePlayers().toArray(new Player[0]);
		for (int a = 0; a < players.length; a++) {
			if (getPlayerData(players[a].getUniqueId()).getRank().isAdmin()) {
				if (whitemode) {
					players[a].sendMessage(ChatColor.AQUA + "ホワイトモードが有効");
				} else {
					players[a].sendMessage(ChatColor.RED + "ホワイトモードが無効");
				}
			} else if (whitemode) {
				players[a].kickPlayer(whitemodemsg);
			}
		}
	}
	public boolean isWhitemode() {
		return whitemode;
	}
	//@SuppressWarnings("deprecation")
	@EventHandler
	public void PlayerJoin(PlayerJoinEvent e) {
		namereload(e.getPlayer());
		PlayerData data = getPlayerData(e.getPlayer().getUniqueId());
		data.setMetadata("JoinHist_" + new Date().getTime(), CSVFile.ArrayToString(new String[]{e.getPlayer().getAddress().getHostName(), e.getPlayer().getName()}));
		if (!getPlayerData(e.getPlayer().getUniqueId()).getRank().isAdmin() && whitemode) {
			e.getPlayer().kickPlayer(whitemodemsg);
			main.traysend(main.getServer().getServerName(), e.getPlayer().getDisplayName() + "がホワイトモードによりキック", MessageType.INFO);
			e.setJoinMessage(null);
		} else {
			main.traysend(main.getServer().getServerName(), e.getPlayer().getDisplayName() + "がログイン", MessageType.INFO);
			e.setJoinMessage(joinmsg.replaceAll("Player", e.getPlayer().getDisplayName()));
			e.getPlayer().sendMessage(welcomemsg.replaceAll("Player", e.getPlayer().getDisplayName()));
			
			/*if (main.getServer().getPluginManager().getPlugin("iConomy") != null) {
				Date last = new Date(e.getPlayer().getLastPlayed());
				Date now = new Date();
				if ((last.getYear() != now.getYear()) || (last.getMonth() != now.getMonth()) || (last.getDay() != now.getDay())) {
					e.getPlayer().sendMessage(ChatColor.AQUA + "ログインボーナスです");
					double prize = main.getConfig().getDouble("joinbonus", 500.0);
					new Account(e.getPlayer().getName()).getHoldings().add(prize);
					iConomy.Template.set(Template.Node.PLAYER_CREDIT);
					iConomy.Template.add("name", e.getPlayer().getName());
					iConomy.Template.add("amount", iConomy.format(prize));
					Messaging.send(e.getPlayer(), iConomy.Template.color(Template.Node.TAG_MONEY) + iConomy.Template.parse());
				}
			}*/
			rankmessage(e.getPlayer(), data);
		}
	}
	public void rankmessage(Player player, PlayerData data) {
		if (data.getMetadata("ownerpromotemsg") != null) {
			player.sendMessage(ChatColor.GOLD + ownerpromotemsg);
			data.removeMetadata("ownerpromotemsg");
		}
		if (data.getMetadata("adminpromotemsg") != null) {
			player.sendMessage(ChatColor.GOLD + adminpromotemsg);
			data.removeMetadata("adminpromotemsg");
		}
		if (data.getMetadata("admindemotemsg") != null) {
			player.sendMessage(ChatColor.GOLD + admindemotemsg);
			data.removeMetadata("admindemotemsg");
		}
		if (data.getMetadata("demotemsg") != null) {
			player.sendMessage(ChatColor.GOLD + demotemsg);
			data.removeMetadata("demotemsg");
		}
		if (data.getRank().isAdmin() && !adminmsg.isEmpty()) {
			player.sendMessage(ChatColor.GOLD + adminmsg);
		}
		player.sendMessage(ChatColor.GOLD + noticemsg);
	}
	@EventHandler
	public void PlayerQuit(PlayerQuitEvent e) {
		e.getPlayer().leaveVehicle();
		main.traysend(main.getServer().getServerName(), e.getPlayer().getDisplayName() + "がログアウト:\n" + e.getQuitMessage(), MessageType.INFO);
		e.setQuitMessage(quitmsg.replaceAll("Player", e.getPlayer().getDisplayName()));
		PlayerData data = getPlayerData(e.getPlayer().getUniqueId());
		data.setMetadata("QuitPos_" + new Date().getTime(), e.getPlayer().getLocation().toString());
		System.out.println("QuitPos: " + e.getPlayer() + "_" + e.getPlayer().getLocation());
	}
	@EventHandler
	public void PlayerGameModeChange(PlayerGameModeChangeEvent e) {
		if (e.getNewGameMode() == GameMode.CREATIVE) {
			if (getPlayerData(e.getPlayer().getUniqueId()).getRank().isAdmin()) {
				if (!adminmode) {
					e.getPlayer().sendMessage(ChatColor.RED + "管理モードを有効にして下さい");
					e.setCancelled(true);
					e.getPlayer().setGameMode(GameMode.ADVENTURE);
				}
			} else {
				e.getPlayer().sendMessage(ChatColor.RED + "一般プレイヤーはゲームモードを変更できません");
				e.setCancelled(true);
				e.getPlayer().setGameMode(GameMode.ADVENTURE);
			}
		}
	}
	@EventHandler
	public void BlockBurn(BlockBurnEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void EntityBreakDoor(EntityBreakDoorEvent e) {
		e.setCancelled(true);
	}
	@EventHandler
	public void EntityChangeBlock(EntityChangeBlockEvent e) {
		e.setCancelled(true);
	}
	/*@EventHandler
	public void EntityDamageByBlock(EntityDamageByBlockEvent e) {
		if (e.getEntity() instanceof Player && getPlayerData(e.getEntity().getUniqueId()).getRank() == PlayerRank.Traveler) {
			e.setCancelled(true);
		}
	}
	@EventHandler
	public void EntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player && getPlayerData(e.getEntity().getUniqueId()).getRank() == PlayerRank.Traveler || e.getDamager() instanceof Player && getPlayerData(e.getDamager().getUniqueId()).getRank() == PlayerRank.Traveler) {
			e.setCancelled(true);
		}
	}*/
	@EventHandler
	public void EntityExplode(EntityExplodeEvent e) {
		e.blockList().clear();
	}
	/*@EventHandler
	public void PlayerDropItem(PlayerDropItemEvent e) {
		main.getServer().getConsoleSender().sendMessage("アイテムがポイ捨てされました Player: " + e.getPlayer().getDisplayName() + ChatColor.RESET + " Location: " +  e.getItemDrop().getLocation());
	}*/
	/*@EventHandler
	public void BlockBreak(BlockBreakEvent e) {
		if (getPlayerData(e.getPlayer().getUniqueId()).getRank() == PlayerRank.Traveler) {
			e.setCancelled(true);
		}
	}*/
	/*@EventHandler
	public void BlockIgnite(BlockIgniteEvent e) {
		if (e.getPlayer() != null) {
			if (getPlayerData(e.getPlayer().getUniqueId()).getRank() == PlayerRank.Traveler) {
				e.setCancelled(true);
			}
		} else {
			e.setCancelled(true);
		}
	}*/
	/*@EventHandler
	public void BlockPlace(BlockPlaceEvent e) {
		if (getPlayerData(e.getPlayer().getUniqueId()).getRank() == PlayerRank.Traveler) {
			e.setCancelled(true);
		}
	}*/
	/*@EventHandler
	public void EntityTargetLivingEntity(EntityTargetLivingEntityEvent e) {
		if (e.getTarget() instanceof Player) {
			if (new PlayerData(e.getTarget().getUniqueId()).getRank() == PlayerRank.Traveler) {
				e.setCancelled(true);
			}
		}
	}*/
	@EventHandler
	public void AsyncPlayerChat(AsyncPlayerChatEvent e) {
		main.traysend(main.getServer().getServerName(), e.getPlayer().getDisplayName() + " Chat:" + e.getMessage(), MessageType.INFO);
	}
	@EventHandler
	public void PlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
		main.traysend(main.getServer().getServerName(), e.getPlayer().getDisplayName() + " Cmd:" + e.getMessage(), MessageType.INFO);
	}
	/*@EventHandler
	public void InventoryOpen(InventoryOpenEvent e) {
		PlayerData data =getPlayerData(e.getPlayer().getUniqueId());
		if (data.getRank() == PlayerRank.Traveler) {
			e.setCancelled(true);
		}
	}*/
	/*@Override
	public void spawn(org.densyakun.bukkit.hubspawn.Main main, Player player) {
		/*Game game = main.gamemanager.getPlayingGame(player.getUniqueId());
		if (game != null) {
			if (game instanceof MultiGame) {
				((MultiGame) game).removePlayer(player.getUniqueId());
			} else {
				game.stop();
			}
		}
		firework = 5;
	}*/
}
