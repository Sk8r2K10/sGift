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
			    
			    new Exchange(plugin, player).accept(false, true, false);
			    return true;
			    
			} else if (args[0].equalsIgnoreCase("deny") && plugin.getPerms(player, "sgift.trade.deny")) {
			    
			    new Exchange(plugin, player).deny(false, true, false);
			    return true;

			} else if (args[0].equalsIgnoreCase("stop") && plugin.getPerms(player, "sgift.sgift")) {
			   
			    new Exchange(plugin, player).stop(false, true, false);
			    return true;

			} else if (args[0].equalsIgnoreCase("cancel") && plugin.getPerms(player, "sgift.trade.cancel")) {
			    
			    new Exchange(plugin, player).cancel(false, true, false);
			    return true;			    

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

					    new Exchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, price, true).start();
					} else {

					    player.sendMessage(prefix + ChatColor.RED + "There's no Item in your Hand!");
					}
				    } else if (ii != null) {

					Item = new ItemStack(ii.getType(), amount, ii.getSubTypeId());

					Location VictimLoc = Victim.getLocation();
					Location playerLoc = player.getLocation();

					new Exchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, price, false).start();
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