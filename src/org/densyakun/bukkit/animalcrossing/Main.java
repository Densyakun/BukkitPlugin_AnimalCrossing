package org.densyakun.bukkit.animalcrossing;
import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.densyakun.bukkit.animalcrossing.menumanager.MenuManager;
import org.densyakun.bukkit.animalcrossing.playermanager.PlayerData;
import org.densyakun.bukkit.animalcrossing.playermanager.PlayerManager;
import org.densyakun.bukkit.animalcrossing.villagemanager.Village;
import org.densyakun.bukkit.animalcrossing.villagemanager.VillageManager;
public class Main extends JavaPlugin implements Listener {
	public static final String param_is_not_enough = "パラメータが足りません";
	public static final String param_wrong_cmd = "パラメータが間違っています";
	public static final String cmd_player_only = "このコマンドはプレイヤーのみ実行できます";
	
	private static Main main;
	private TrayIcon tray;
	
	public PlayerManager playermanager;
	public MenuManager menumanager;
	public VillageManager villagemanager;
	@Override
	public void onEnable() {
		main = this;
		try {
			BufferedImage image = null;
			try {
				image = ImageIO.read(new File("./server-icon.png"));
			} catch (IOException e) {
				image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
			}
			SystemTray.getSystemTray().add(tray = new TrayIcon(image, getServer().getServerName()));
		} catch (AWTException e) {
			e.printStackTrace();
		}
		load();
		playermanager = new PlayerManager(this);
		menumanager = new MenuManager(this);
		villagemanager = new VillageManager(this);
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getName() + ": 有効");
	}
	public void load() {
		saveDefaultConfig();
	}
	public void save() {
		playermanager.save();
		villagemanager.save();
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("domori")) {
			if (args.length == 0) {
				sender.sendMessage(ChatColor.GOLD + "パラメータを入力して下さい");
				sender.sendMessage(ChatColor.GREEN +  "/domori (village|v)");
			} else if (args[0].equalsIgnoreCase("admin")) {
				if (sender.isOp()) {
					if (args.length == 1) {
						sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
						sender.sendMessage(ChatColor.GREEN +  "/domori admin (load|save|reload|hide|show|mode|whitemode|white|player|village|v)");
					} else if (args[1].equalsIgnoreCase("load")) {
						load();
						sender.sendMessage(ChatColor.AQUA + "ロードしました");
					} else if (args[1].equalsIgnoreCase("save")) {
						save();
						sender.sendMessage(ChatColor.AQUA + "セーブしました");
					} else if (args[1].equalsIgnoreCase("reload")) {
						save();
						load();
						sender.sendMessage(ChatColor.AQUA + "リロードしました");
					} else if (args[1].equalsIgnoreCase("hide")) {
						if (sender instanceof Player) {
							playermanager.sethide((Player) sender, true);
							sender.sendMessage(ChatColor.AQUA + "一般プレイヤーから姿を隠しました");
						} else {
							sender.sendMessage(ChatColor.RED + cmd_player_only);
						}
					} else if (args[1].equalsIgnoreCase("show")) {
						if (sender instanceof Player) {
							playermanager.sethide((Player) sender, false);
							sender.sendMessage(ChatColor.AQUA + "姿を表示しました");
						} else {
							sender.sendMessage(ChatColor.RED + cmd_player_only);
						}
					} else if (args[1].equalsIgnoreCase("mode")) {
						playermanager.adminmodetoggle();
					} else if (args[1].equalsIgnoreCase("whitemode") || args[1].equalsIgnoreCase("white")) {
						playermanager.whitemodetoggle();
					} else if (args[1].equalsIgnoreCase("player")) {
						if (args.length == 2) {
							sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
							sender.sendMessage(ChatColor.GREEN +  "/domori admin player (perfcmd)");
						} else if (args[2].equalsIgnoreCase("perfcmd")) {
							if (args.length < 5) {
								sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
								sender.sendMessage(ChatColor.GREEN +  "/domori admin player perfcmd (cmd) [args...]");
							} else {
								Player player = getServer().getPlayer(args[3]);
								if (player != null) {
									String cmd = args[4];
									for (int a = 5; a < args.length; a++) {
										cmd += " " + args[a];
									}
									player.performCommand(cmd);
									if (sender instanceof ConsoleCommandSender) {
										getServer().getConsoleSender().sendMessage(ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + "がコマンド" + cmd + "を実行しました");
									} else {
										sender.sendMessage(ChatColor.AQUA + player.getDisplayName() + ChatColor.AQUA + "がコマンド" + cmd + "を実行しました");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
								}
							}
						} else {
							sender.sendMessage(ChatColor.GOLD + param_wrong_cmd);
							sender.sendMessage(ChatColor.GREEN +  "/domori admin player (perfcmd)");
						}
					} else if (args[1].equalsIgnoreCase("village") || args[1].equalsIgnoreCase("v")) {
						if (args.length == 2) {
							sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
							sender.sendMessage(ChatColor.GREEN +  "/domori admin village|v (g)");
						} else if (args[2].equalsIgnoreCase("g")) {
							if (args.length == 3) {
								sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
								sender.sendMessage(ChatColor.GREEN +  "/domori admin village|v g (vnum)");
							} else {
								try {
									Village village = villagemanager.getVillage(Integer.parseInt(args[3]));
									if (village == null) {
										sender.sendMessage(ChatColor.RED + "村が見つかりません");
									} else {
										villagemanager.getMapper().generateVillage(village.getId());
										sender.sendMessage(ChatColor.AQUA + "SUCCESS");
									}
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + "村番地を入力して下さい");
								}
							}
						} else {
							sender.sendMessage(ChatColor.GOLD + param_wrong_cmd);
							sender.sendMessage(ChatColor.GREEN +  "/domori admin village|v (g)");
						}
					} else {
						sender.sendMessage(ChatColor.GOLD + param_wrong_cmd);
						sender.sendMessage(ChatColor.GREEN +  "/domori admin (load|save|reload|hide|show|mode|whitemode|white|player|village|v)");
					}
				} else {
					if (sender instanceof ConsoleCommandSender) {
						getServer().getConsoleSender().sendMessage(ChatColor.RED + "管理者専用です");
					} else {
						sender.sendMessage(ChatColor.RED + "管理者専用です");
					}
				}
			} else if (args[0].equalsIgnoreCase("village") || args[0].equalsIgnoreCase("v")) {
				if (args.length == 1) {
					sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
					sender.sendMessage(ChatColor.GREEN +  "/domori village|v (new|kick|ban|pardon|join)");
				} else if (args[1].equalsIgnoreCase("new")) {
					if (sender instanceof Player) {
						if (args.length == 2) {
							sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
							sender.sendMessage(ChatColor.GREEN +  "/domori village|v new (name)");
						} else {
							villagemanager.joinVillage(villagemanager.addVillage(args[2], (Player) sender), (Player) sender);
						}
					} else {
						sender.sendMessage(ChatColor.RED + cmd_player_only);
					}
				} else if (args[1].equalsIgnoreCase("kick")) {
					if (sender instanceof Player) {
						if (args.length == 2) {
							sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
							sender.sendMessage(ChatColor.GREEN +  "/domori village|v kick (player)");
						} else {
							Player player = getServer().getPlayer(args[2]);
							if (player == null) {
								sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
							} else {
								int villageid = villagemanager.getPlayingVillage(player.getUniqueId());
								if (0 <= villageid) {
									Village village = villagemanager.getVillage(villageid);
									if (playermanager.getPlayerData(((Player) sender).getUniqueId()).getRank().isAdmin() || village.getVillagers().containsKey(((Player) sender).getUniqueId())) {
										villagemanager.kickVillage(village, player);
									} else {
										sender.sendMessage(ChatColor.RED + "権限が満たしていないためキックできません");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "プレイヤーが村にいないためキックできません");
								}
							}
						}
					} else {
						sender.sendMessage(ChatColor.RED + cmd_player_only);
					}
				} else if (args[1].equalsIgnoreCase("ban")) {
					if (args.length == 2) {
						sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
						sender.sendMessage(ChatColor.GREEN +  "/domori village|v ban (player)");
					} else {
						@SuppressWarnings("deprecation")
						OfflinePlayer offlineplayer = getServer().getOfflinePlayer(args[2]);
						if (offlineplayer == null) {
							sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
						} else {
							Village village = null;
							if (args.length == 3) {
								if (sender instanceof Player) {
									int villageid = villagemanager.getPlayingVillage(((Player) sender).getUniqueId());
									if (0 <= villageid) {
										village = villagemanager.getVillage(villageid);
									} else {
										sender.sendMessage(ChatColor.RED + "プレイヤーをBANしたい村に参加している状態でもう一度実行するか、村番地をパラメーターに追加して下さい");
									}
								} else {
									sender.sendMessage(ChatColor.RED + cmd_player_only);
								}
							} else {
								try {
									if ((village = villagemanager.getVillage(Integer.parseInt(args[3]))) == null) {
										sender.sendMessage(ChatColor.RED + "村が見つかりません");
									}
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + "村番地を入力して下さい");
								}
							}
							if (village != null) {
								if (playermanager.getPlayerData(((Player) sender).getUniqueId()).getRank().isAdmin() || village.getVillagers().containsKey(((Player) sender).getUniqueId())) {
									villagemanager.banVillage(village, offlineplayer.getUniqueId());
								} else {
									sender.sendMessage(ChatColor.RED + "権限が満たしていないためBANできません");
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("pardon")) {
					if (args.length == 2) {
						sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
						sender.sendMessage(ChatColor.GREEN +  "/domori village|v pardon (player)");
					} else {
						@SuppressWarnings("deprecation")
						OfflinePlayer offlineplayer = getServer().getOfflinePlayer(args[2]);
						if (offlineplayer == null) {
							sender.sendMessage(ChatColor.RED + "プレイヤーが見つかりません");
						} else {
							Village village = null;
							if (args.length == 3) {
								if (sender instanceof Player) {
									int villageid = villagemanager.getPlayingVillage(((Player) sender).getUniqueId());
									if (0 <= villageid) {
										village = villagemanager.getVillage(villageid);
									} else {
										sender.sendMessage(ChatColor.RED + "プレイヤーのBANを解除したい村に参加している状態でもう一度実行するか、村番地をパラメーターに追加して下さい");
									}
								} else {
									sender.sendMessage(ChatColor.RED + cmd_player_only);
								}
							} else {
								try {
									if ((village = villagemanager.getVillage(Integer.parseInt(args[3]))) == null) {
										sender.sendMessage(ChatColor.RED + "村が見つかりません");
									}
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.RED + "村番地を入力して下さい");
								}
							}
							if (village != null) {
								if (playermanager.getPlayerData(((Player) sender).getUniqueId()).getRank().isAdmin() || village.getVillagers().containsKey(((Player) sender).getUniqueId())) {
									villagemanager.pardonVillage(village, offlineplayer.getUniqueId());
								} else {
									sender.sendMessage(ChatColor.RED + "権限が満たしていないためBANを解除できません");
								}
							}
						}
					}
				} else if (args[1].equalsIgnoreCase("join")) {
					if (sender instanceof Player) {
						if (args.length == 2) {
							sender.sendMessage(ChatColor.GOLD + param_is_not_enough);
							sender.sendMessage(ChatColor.GREEN +  "/domori village|v join (vnum)");
						} else {
							try {
								Village village = villagemanager.getVillage(Integer.parseInt(args[2]));
								if (village == null) {
									sender.sendMessage(ChatColor.RED + "村が見つかりません");
								} else {
									villagemanager.joinVillage(village, (Player) sender);
								}
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.RED + "村番地を入力して下さい");
							}
						}
					} else {
						sender.sendMessage(ChatColor.RED + cmd_player_only);
					}
				} else {
					sender.sendMessage(ChatColor.GOLD + param_wrong_cmd);
					sender.sendMessage(ChatColor.GREEN +  "/domori village|v (new|kick|ban|pardon|join)");
				}
			} else {
				sender.sendMessage(ChatColor.GOLD + param_wrong_cmd);
				sender.sendMessage(ChatColor.GREEN +  "/domori (village|v)");
			}
		} else if (label.equalsIgnoreCase("menu")) {
			if (sender instanceof HumanEntity) {
				menumanager.OpenMenu((HumanEntity) sender);
			} else {
				sender.sendMessage(cmd_player_only);
			}
		} else if (label.equalsIgnoreCase("nick")) {
			PlayerData playerdata = playermanager.getPlayerData(((HumanEntity) sender).getUniqueId());
			if (args.length == 0) {
				playerdata.removeMetadata("nick");
				playermanager.namereload((Player) sender);
				sender.sendMessage(ChatColor.AQUA + "ニックネームを初期化しました");
				sender.sendMessage(ChatColor.GOLD + "ニックネームを設定するには、/nick (name) を実行して下さい");
			} else {
				if (sender instanceof Player) {
					playerdata.setMetadata("nick", args[0]);
					playermanager.namereload((Player) sender);
					sender.sendMessage(ChatColor.AQUA + "ニックネームを変更しました");
				} else {
					sender.sendMessage(cmd_player_only);
				}
			}
		}
		return true;
	}
	@Override
	public void onDisable() {
		Player[] players = getServer().getOnlinePlayers().toArray(new Player[0]);
		for (int a = 0; a < players.length; a++) {
			//players[a].kickPlayer("再起動またはサーバー停止のため自動キックされました");
			/*players[a].kickPlayer("くらぁーーっ！！\n"
					+ "みのがしてもらったとおもったら、\n"
					+ "オオマチガイなんじゃぁあ！\n"
					+ "リセットするなーゆーてんのが\n"
					+ "わからんのかー！\n"
					+ "※再起動またはサーバー停止中です");*/
			players[a].kickPlayer("リセットや リセット！\n"
					+ "また リセットしたな！\n"
					+ "※再起動またはサーバー停止中です");
		}
		save();
		SystemTray.getSystemTray().remove(tray);
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + getName() + ": 無効");
	}
	public static Main getMain() {
		return main;
	}
	public void traysend(String title, String msg, MessageType type) {
		tray.displayMessage(title, msg, type);
	}
}
