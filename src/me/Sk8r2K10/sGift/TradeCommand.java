package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import net.milkbowl.vault.item.ItemInfo;
import net.milkbowl.vault.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public class TradeCommand implements CommandExecutor {

    private sGift plugin;
    Player player = null;
    String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";
    Logger log = Logger.getLogger("Minecraft");

    public TradeCommand(sGift instance) {
	plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

	if (sender instanceof Player) {

	    player = (Player) sender;

	} else {

	    player = null;
	}

	if (commandLabel.equalsIgnoreCase("trade") && plugin.getPerms(player, "sgift.trade.trade")) {

	    int maxAmount = plugin.getConfig().getInt("Options.max-amount");

	    PluginDescriptionFile pdf = plugin.getDescription();
	    String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	    if (plugin.getConfig().getBoolean("Features.enable-trade")) {
		if (player != null) {
		    if (args.length == 1) {
			if (args[0].equalsIgnoreCase("auto") && plugin.getPerms(player, "sgift.trade.auto")) {
			    if (plugin.getConfig().getBoolean("Features.allow-auto.trade")) {
				if (!player.hasPermission("sgift.toggles.trade.accept")) {

				    player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept enabled for Trading!");
				    plugin.getPermissions().playerAdd(player, "sgift.toggles.trade.accept");
				    plugin.getPermissions().playerRemove(player, "-sgift.toggles.trade.accept");
				} else {

				    player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept disabled for Trading!");
				    plugin.getPermissions().playerRemove(player, "sgift.toggles.trade.accept");
				    plugin.getPermissions().playerAdd(player, "-sgift.toggles.trade.accept");
				}
			    } else {

				player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			    }
			} else if (args[0].equalsIgnoreCase("auto-deny") && plugin.getPerms(player, "sgift.trade.autodeny")) {
			    if (plugin.getConfig().getBoolean("Features.allow-auto.trade")) {
				if (!player.hasPermission("sgift.toggles.trade.deny")) {

				    player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny enabled for Trading!");
				    plugin.getPermissions().playerAdd(player, "sgift.toggles.trade.deny");
				    plugin.getPermissions().playerRemove(player, "-sgift.toggles.trade.deny");
				} else {

				    player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny disabled for Trading!");
				    plugin.getPermissions().playerRemove(player, "sgift.toggles.trade.deny");
				    plugin.getPermissions().playerAdd(player, "-sgift.toggles.trade.deny");
				}
			    } else {

				player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			    }
			} else if (args[0].equalsIgnoreCase("help") && plugin.getPerms(player, "sgift.trade.help")) {

			    player.sendMessage(ChatColor.DARK_GRAY + "---------------[" + ChatColor.GOLD + "sGift - Trade Help Menu" + ChatColor.DARK_GRAY + "]----------------");
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Trade"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Example"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Accept"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Deny"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Cancel"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Auto"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.AutoDeny"));
			    player.sendMessage(plugin.getConfig().getString("Help.Trade.Help"));

			    if (player.hasPermission("sgift.admin")) {

				player.sendMessage(plugin.getConfig().getString("Help.Trade.Stop"));
			    }

			} else if (args[0].equalsIgnoreCase("accept") && plugin.getPerms(player, "sgift.trade.accept")) {

			    Trade trade = null;
			    Timeout time = null;
			    Sender Sender1 = null;

			    for (Trade t : plugin.trades) {

				if (t.Victim == player) {

				    trade = t;

				    for (Sender s : plugin.senders) {

					if (s.Sender == t.playerSender) {

					    Sender1 = s;
					}
				    }
				    for (Timeout o : plugin.timeout) {

					if (o.ID == trade.ID) {

					    time = o;
					}
				    }
				}
			    }

			    if (trade == null) {

				player.sendMessage(prefix + ChatColor.RED + "No Trades to accept!");
			    } else {

				Player playerSendingItems = trade.playerSender;
				Player Victim = trade.Victim;
				ItemStack items = trade.itemStack;
				int price = trade.price;

				if (plugin.getEcon().getBalance(player.getName()) >= price) {

				    if (player.getInventory().firstEmpty() == -1) {
					Location playerloc = player.getLocation();
					player.getWorld().dropItemNaturally(playerloc, items);
					player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

					plugin.getEcon().withdrawPlayer(Victim.getName(), price);
					plugin.getEcon().depositPlayer(playerSendingItems.getName(), price);

					playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
					Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
					log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + plugin.getEcon().currencyNameSingular() + "(s)");

					plugin.timeout.remove(time);
					plugin.trades.remove(trade);
					plugin.senders.remove(Sender1);

				    } else {
					player.getInventory().addItem(items);

					plugin.getEcon().withdrawPlayer(Victim.getName(), price);
					plugin.getEcon().depositPlayer(playerSendingItems.getName(), price);

					playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
					Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
					log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + plugin.getEcon().currencyNameSingular() + "(s)");

					plugin.timeout.remove(time);
					plugin.trades.remove(trade);
					plugin.senders.remove(Sender1);
				    }
				} else {

				    player.sendMessage(prefix + ChatColor.RED + "You don't have enough money to accept that!");
				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "The player you were trading with doesn't have enough money anymore!");

				    if (playerSendingItems.getInventory().firstEmpty() == -1) {

					Location playerloc = playerSendingItems.getLocation();
					playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
					playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

				    } else {

					playerSendingItems.getInventory().addItem(items);
				    }

				    plugin.timeout.remove(time);
				    plugin.trades.remove(trade);
				    plugin.senders.remove(Sender1);
				}
			    }
			} else if (args[0].equalsIgnoreCase("deny") && plugin.getPerms(player, "sgift.trade.deny")) {

			    Trade trade = null;
			    Timeout time = null;
			    Sender Sender1 = null;

			    for (Trade t : plugin.trades) {

				if (t.Victim == player) {

				    trade = t;

				    for (Sender s : plugin.senders) {

					if (s.Sender == t.playerSender) {

					    Sender1 = s;
					}
				    }
				    for (Timeout o : plugin.timeout) {

					if (o.ID == trade.ID) {

					    time = o;
					}
				    }
				}
			    }

			    if (trade == null) {

				player.sendMessage(prefix + ChatColor.RED + "No Trades to deny!");
			    } else {

				Player playerSendingItems = trade.playerSender;
				Player Victim = trade.Victim;
				ItemStack items = trade.itemStack;
				int price = trade.price;

				if (playerSendingItems.getInventory().firstEmpty() == -1) {
				    Location playerloc = playerSendingItems.getLocation();
				    playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
				    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Trade request!");

				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Trade!");
				    log.info(logpre + Victim.getDisplayName() + " denied " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

				    plugin.timeout.remove(time);
				    plugin.trades.remove(trade);
				    plugin.senders.remove(Sender1);

				} else if (!playerSendingItems.isOnline()) {

				    player.sendMessage(prefix + ChatColor.RED + "Player sending items is not Online!");
				    player.sendMessage(prefix + ChatColor.RED + "Please wait for " + playerSendingItems.getName() + " to come back online!");

				} else {

				    playerSendingItems.getInventory().addItem(items);
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Trade request!");

				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Trade!");
				    log.info(logpre + Victim.getDisplayName() + " denied " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

				    plugin.timeout.remove(time);
				    plugin.trades.remove(trade);
				    plugin.senders.remove(Sender1);
				}
			    }

			} else if (args[0].equalsIgnoreCase("stop") && plugin.getPerms(player, "sgift.sgift")) {
			    while (plugin.trades.size() > 0) {

				Trade trade = null;
				Timeout time = null;
				Sender Sender1 = null;

				for (Trade t : plugin.trades) {

				    if (t.itemStack != null) {

					trade = t;

					for (Sender s : plugin.senders) {

					    if (s.Sender != null) {

						Sender1 = s;
					    }
					}
					for (Timeout o : plugin.timeout) {

					    if (o.ID == trade.ID) {

						time = o;
					    }

					}
				    }
				}
				if (trade == null) {

				    player.sendMessage(prefix + ChatColor.RED + "No Trades to stop!");
				} else {

				    Player playerSendingItems = trade.playerSender;
				    Player Victim = trade.Victim;
				    ItemStack items = trade.itemStack;
				    int price = trade.price;

				    if (playerSendingItems.getInventory().firstEmpty() == -1) {

					Location playerloc = playerSendingItems.getLocation();

					playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
					playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

					playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Trade has been cancelled by an Admin!");

					playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " has been returned to you.");
					Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Trade.");
					log.info(logpre + "stopped a trade of " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

					plugin.timeout.remove(time);
					plugin.trades.remove(trade);
					plugin.senders.remove(Sender1);

				    } else {
					playerSendingItems.getInventory().addItem(items);

					playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Trade has been cancelled by an Admin!");

					playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " has been returned to you.");
					Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Trade.");
					log.info(logpre + "stopped a trade of " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

					plugin.timeout.remove(time);
					plugin.trades.remove(trade);
					plugin.senders.remove(Sender1);
				    }


				}
			    }
			    player.sendMessage(prefix + ChatColor.GREEN + "Cancelled all Trades safely.");

			} else if (args[0].equalsIgnoreCase("cancel") && plugin.getPerms(player, "sgift.trade.cancel")) {

			    Trade trade = null;
			    Timeout time = null;
			    Sender Sender1 = null;

			    for (Trade t : plugin.trades) {

				if (t.playerSender == player) {

				    trade = t;
				}
			    }

			    for (Sender s : plugin.senders) {

				if (s.Sender == player) {

				    Sender1 = s;
				}
			    }

			    for (Timeout o : plugin.timeout) {
				try {
				    if (o.ID == trade.ID) {

					time = o;
				    }
				} catch (NullPointerException e) {
				    log.info(logpre + "Nag Sk8r2K9 to fix this! (Some strange bug!(Nothing should be affected, Just silly code))");
				    player.sendMessage(prefix + ChatColor.RED + "No Trades to cancel!");
				    log.severe(e.toString());
				    return true;
				}
			    }

			    if (trade == null || time == null) {

				player.sendMessage(prefix + ChatColor.RED + "No Trades to cancel!");
			    } else {

				Player playerSendingItems = trade.playerSender;
				Player Victim = trade.Victim;
				ItemStack items = trade.itemStack;
				int price = trade.price;

				if (playerSendingItems.getInventory().firstEmpty() == -1) {

				    Location playerloc = playerSendingItems.getLocation();

				    playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
				    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled trade!");

				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Trade!");

				    plugin.timeout.remove(time);
				    plugin.trades.remove(trade);
				    plugin.senders.remove(Sender1);

				} else {
				    playerSendingItems.getInventory().addItem(items);

				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled trade!");

				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Trade!");

				    plugin.timeout.remove(time);
				    plugin.trades.remove(trade);
				    plugin.senders.remove(Sender1);
				}


			    }



			} else if (Bukkit.getServer().getPlayer(args[0]) == player && sender.hasPermission("sgift.trade.start")) {

			    player.sendMessage(prefix + ChatColor.RED + "Don't trade Items with yourself!");

			} else if (Bukkit.getServer().getPlayer(args[0]) == null && sender.hasPermission("sgift.trade.start")) {

			    player.sendMessage(prefix + ChatColor.RED + "Player not Online.");

			} else if (plugin.getPerms(player, "sgift.trade.start")) {

			    player.sendMessage(prefix + ChatColor.RED + "Too few arguments!");
			    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");
			}
		    } else if (args.length == 2) {

			player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

		    } else if (args.length == 3) {

			player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

		    } else if (args.length == 4 && plugin.getPerms(player, "sgift.trade.start")) {
			if (Bukkit.getServer().getPlayer(args[0]) != player) {
			    if (Bukkit.getServer().getPlayer(args[0]) != null) {
				if (!plugin.inCreative(player, Bukkit.getServer().getPlayer(args[0]))) {
				    int price = plugin.getInt(args[3]);
				    Player Victim = Bukkit.getServer().getPlayer(args[0]);
				    int amount = plugin.getInt(args[2]);
				    ItemStack Item = null;

				    ItemInfo ii = Items.itemByString(args[1]);

				    if (args[1].equalsIgnoreCase("hand")) {
					if (plugin.getHand(player, player.getItemInHand().clone())) {

					    Item = player.getItemInHand().clone();

					    Item.setAmount(amount);

					    Location VictimLoc = Victim.getLocation();
					    Location playerLoc = player.getLocation();

					    new StartExchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, price, true).start();
					} else {

					    player.sendMessage(prefix + ChatColor.RED + "There's no Item in your Hand!");
					}
				    } else if (ii != null) {

					Item = new ItemStack(ii.getType(), amount, ii.getSubTypeId());

					Location VictimLoc = Victim.getLocation();
					Location playerLoc = player.getLocation();

					new StartExchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, price, false).start();
				    } else {

					player.sendMessage(prefix + ChatColor.RED + "Material specified is Invalid!");
				    }

				}
			    } else {

				player.sendMessage(prefix + ChatColor.RED + "Player not Online!");
			    }

			} else {

			    player.sendMessage(prefix + ChatColor.RED + "You can't Trade with yourself!");
			}

		    } else if (args.length == 4 && sender.hasPermission("sgift.trade.start")) {

			player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

		    } else if (args.length == 0) {

			player.sendMessage(prefix + ChatColor.RED + "By Sk8r2K9. /trade help for more info");

		    } else if (args.length >= 5) {

			player.sendMessage(prefix + ChatColor.RED + "Too many arguments!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");
		    } else {

			player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");
		    }
		} else {

		    log.warning(logpre + "Don't send sGift commands through console!");
		}

	    } else {
		if (player != null) {

		    player.sendMessage(prefix + ChatColor.RED + "Trading currently disabled.");
		} else {

		    log.warning(logpre + "Don't send sGift commands through console!");
		}
	    }
	}
	return false;
    }
}