package me.Sk8r2K10.sGift.Commands;

import java.util.logging.Logger;
import me.Sk8r2K10.sGift.sGift;
import me.Sk8r2K10.sGift.util.Sender;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class sGiftCommand implements CommandExecutor {

	private sGift plugin;
	Player player = null;
	String prefix = ChatColor.WHITE + "[" + ChatColor.DARK_RED + "sGift" + ChatColor.WHITE + "] ";
	Logger log = Logger.getLogger("Minecraft");
	private boolean halt = false;
	private boolean reload = false;

	public sGiftCommand(sGift instance) {
		plugin = instance;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		PluginDescriptionFile pdf = plugin.getDescription();
		String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

		if (sender instanceof Player) {

			player = (Player) sender;
		} else {

			player = null;
		}

		if (player != null) {
			if (commandLabel.equalsIgnoreCase("sgift") && plugin.getPerms(player, "sgift.sgift")) {

				if (args.length == 1) {
					if (args[0].equalsIgnoreCase("info")) {

						String info1 = Boolean.toString(plugin.getConfig().getBoolean("Features.enable-gift"));
						String info2 = Boolean.toString(plugin.getConfig().getBoolean("Features.enable-trade"));
						String info3 = Boolean.toString(plugin.getConfig().getBoolean("Features.enable-swap"));
						String info4 = Boolean.toString(plugin.getConfig().getBoolean("Options.allow-creative"));
						String info5 = Boolean.toString(plugin.getConfig().getBoolean("Options.allow-between-worlds"));
						String info6 = Integer.toString(plugin.getConfig().getInt("Options.max-distance"));
						String info7 = Integer.toString(plugin.getConfig().getInt("Options.max-amount"));
						String info8 = Integer.toString(plugin.getConfig().getInt("Options.request-timeout"));

						player.sendMessage(ChatColor.DARK_RED + "-----------------[" + ChatColor.RED + "sGift - Information" + ChatColor.DARK_RED + "]------------------");
						player.sendMessage(ChatColor.RED + "Gift: " + ChatColor.AQUA + info1);
						player.sendMessage(ChatColor.RED + "Trade: " + ChatColor.AQUA + info2);
						player.sendMessage(ChatColor.RED + "Swap: " + ChatColor.AQUA + info3);
						player.sendMessage(ChatColor.RED + "Allow-Creative: " + ChatColor.AQUA + info4);
						player.sendMessage(ChatColor.RED + "Allow-Between-Worlds: " + ChatColor.AQUA + info5);
						player.sendMessage(ChatColor.RED + "Max-Distance: " + ChatColor.AQUA + info6);
						player.sendMessage(ChatColor.RED + "Max-Amount: " + ChatColor.AQUA + info7);
						player.sendMessage(ChatColor.RED + "Request-Timeout: " + ChatColor.AQUA + info8);

						StringBuilder senderList = new StringBuilder();

						for (Sender s : plugin.senders) {
							if (senderList.length() > 0) {

								senderList.append(ChatColor.RED + ", " + ChatColor.AQUA);

							}
							senderList.append(s.Sender.getName());
						}
						player.sendMessage(ChatColor.RED + "Senders: " + ChatColor.AQUA + senderList);


					} else if (args[0].equalsIgnoreCase("halt") && plugin.getPerms(player, "sgift.halt")) {

						player.sendMessage(prefix + ChatColor.RED + "This will Abruptly halt all Gifts, Trades and Swaps!");
						player.sendMessage(prefix + ChatColor.YELLOW + ChatColor.ITALIC + "No items will be refunded!");
						player.sendMessage(prefix + ChatColor.YELLOW + "Are you sure you wish to continue?");
						player.sendMessage("/sgift yes" + ChatColor.YELLOW + " or " + ChatColor.WHITE + "/sgift no.");

						halt = true;

					} else if (args[0].equalsIgnoreCase("help")) {

						player.sendMessage(ChatColor.DARK_RED + "---------------[" + ChatColor.RED + "sGift - sGift Help Menu" + ChatColor.DARK_RED + "]----------------");
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Info"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Halt"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Set"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Example"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Reload"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Confirm"));
						player.sendMessage(plugin.getConfig().getString("Help.sGift.Deny"));

					} else if (args[0].equalsIgnoreCase("reload") && plugin.getPerms(player, "sgift.reload")) {

						player.sendMessage(prefix + ChatColor.RED + "Reloading plugin. Any active Trades, Gifts or Swaps will be Forcefully cancelled. No Items will be returned.");
						player.sendMessage(prefix + ChatColor.YELLOW + "It is recommended that you do /gift stop then /trade stop followed by /swap stop before executing this command.");
						player.sendMessage(prefix + ChatColor.YELLOW + "Are you sure you wish to continue?");
						player.sendMessage("/sgift yes" + ChatColor.YELLOW + " or " + ChatColor.WHITE + "/sgift no.");

						reload = true;

					} else if (args[0].equalsIgnoreCase("reload-config")) {

						plugin.reloadConfig();
						player.sendMessage(prefix + ChatColor.YELLOW + "Config reloaded.");

					} else if (args[0].equalsIgnoreCase("yes") && (plugin.getPerms(player, "sgift.reload") || plugin.getPerms(player, "sgift.halt"))) {
						if (reload) {

							player.sendMessage(prefix + ChatColor.RED + "Reloading plugin...");
							plugin.getPluginLoader().disablePlugin(plugin);
							plugin.getPluginLoader().enablePlugin(plugin);
							player.sendMessage(prefix + ChatColor.RED + "Plugin reloaded.");

							reload = false;
						} else if (halt) {

							player.sendMessage(prefix + ChatColor.RED + "Abruptly halted all Gifts and Trades!");
							player.sendMessage(prefix + ChatColor.RED + "No items have been refunded to players!");

							plugin.trades.clear();
							plugin.gifts.clear();
							plugin.senders.clear();

							halt = false;
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Nothing to confirm!");
						}

					} else if (args[0].equalsIgnoreCase("no") && (plugin.getPerms(player, "sgift.reload") || plugin.getPerms(player, "sgift.halt"))) {

						player.sendMessage(prefix + ChatColor.RED + "Cancelled!");

						halt = false;
						reload = false;
					} else {

						player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
						player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
					}
				} else if (args.length == 2) {

					player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
					player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");

				} else if (args.length >= 3 && args[0].equalsIgnoreCase("set") && plugin.getPerms(player, "sgift.set")) {
					if (args[1].equalsIgnoreCase("gift")) {
						if (args[2].equalsIgnoreCase("true")) {
							if (!plugin.getConfig().getBoolean("Features.enable-gift")) {

								plugin.getConfig().set("Features.enable-gift", true);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Gifting has been set to true in Config");

							} else {

								player.sendMessage(prefix + ChatColor.RED + "Gifting is already enabled!");
							}
						} else if (args[2].equalsIgnoreCase("false")) {
							if (plugin.getConfig().getBoolean("Features.enable-gift")) {

								plugin.getConfig().set("Features.enable-gift", false);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Gifting has been set to false in Config");

							} else {

								player.sendMessage(prefix + ChatColor.RED + "Gifting is already disabled!");
							}
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
							player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
						}

					} else if (args[1].equalsIgnoreCase("trade")) {
						if (args[2].equalsIgnoreCase("true")) {
							if (!plugin.getConfig().getBoolean("Features.enable-trade")) {

								plugin.getConfig().set("Features.enable-trade", true);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Trading has been set to true in Config");

							} else {

								player.sendMessage(prefix + ChatColor.RED + "Trading is already enabled!");
							}
						} else if (args[2].equalsIgnoreCase("false")) {
							if (plugin.getConfig().getBoolean("Features.enable-trade")) {

								plugin.getConfig().set("Features.enable-trade", false);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Trading has been set to false in Config");

							} else {

								player.sendMessage(prefix + ChatColor.RED + "Trading is already disabled!");
							}
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
							player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
						}
					} else if (args[1].equalsIgnoreCase("swap")) {
						if (args[2].equalsIgnoreCase("true")) {
							if (!plugin.getConfig().getBoolean("Features.enable-swap")) {

								plugin.getConfig().set("Features.enable-swap", true);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Swapping has been set to true in Config");

							} else {

								player.sendMessage(prefix + ChatColor.RED + "Swapping is already enabled!");
							}
						} else if (args[2].equalsIgnoreCase("false")) {
							if (plugin.getConfig().getBoolean("Features.enable-swap")) {

								plugin.getConfig().set("Features.enable-swap", false);
								plugin.saveConfig();

								player.sendMessage(prefix + ChatColor.AQUA + "Swapping has been set to false in Config");
							} else {

								player.sendMessage(prefix + ChatColor.RED + "Swapping is already disabled!");
							}
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
							player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
						}
					} else if (args[1].equalsIgnoreCase("distance")) {

						int dist = plugin.getInt(args[2]);

						if (dist != 0) {

							plugin.getConfig().set("Options.max-distance", dist);
							plugin.saveConfig();
							player.sendMessage(prefix + ChatColor.YELLOW + "Adjusted distance to " + dist);
						} else if (args[2].equalsIgnoreCase("reset")) {

							plugin.getConfig().set("Options.max-distance", 0);
							plugin.saveConfig();
							player.sendMessage(prefix + ChatColor.YELLOW + "Reset distance to 0");
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Please enter an valid number.");
						}
					} else if (args[1].equalsIgnoreCase("allow-auto")) {
						if (args[2].equalsIgnoreCase("gift")) {
							if (args[3].equalsIgnoreCase("true")) {
								
								plugin.getConfig().set("Features.allow-auto.gift", true);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Enabling Auto-Features for Gifting.");
							} else if (args[3].equalsIgnoreCase("false")) {
								
								plugin.getConfig().set("Features.allow-auto.gift", false);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Disabling Auto-Features for Gifting.");
							} else {

								player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
								player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
							}
						} else if (args[2].equalsIgnoreCase("trade")) {
							if (args[3].equalsIgnoreCase("true")) {
								
								plugin.getConfig().set("Features.allow-auto.trade", true);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Enabling Auto-Features for Trading.");
							} else if (args[3].equalsIgnoreCase("false")) {
								
								plugin.getConfig().set("Features.allow-auto.trade", false);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Disabling Auto-Features for Trading.");
							} else {

								player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
								player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
							}
						} else if (args[2].equalsIgnoreCase("swap")) {
							if (args[3].equalsIgnoreCase("true")) {
								
								plugin.getConfig().set("Features.allow-auto.swap", true);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Enabling Auto-Features for Swapping.");
							} else if (args[3].equalsIgnoreCase("false")) {
								
								plugin.getConfig().set("Features.allow-auto.swap", false);
								plugin.saveConfig();
								player.sendMessage(prefix + ChatColor.YELLOW + "Disabling Auto-Features for Swapping.");
							} else {

								player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
								player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
							}
						} else {

							player.sendMessage(prefix + ChatColor.RED + "Please enter an valid number.");
						}
					} else {

						player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
						player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
					}
				} else if (args.length == 0) {

					player.sendMessage(prefix + ChatColor.RED + "By Sk8r2K9. /sgift info|halt|set <Option> [true|false]");
				} else if (args.length > 3) {

					player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
					player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
				} else {

					player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
					player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
					player.sendMessage(ChatColor.DARK_GRAY + "How did you even achieve this? I don't even...");
				}
			}
		} else {

			log.warning(logpre + "Don't send sGift commands through console!");
		}
		return false;
	}
}
