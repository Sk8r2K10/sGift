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

public class SwapCommand implements CommandExecutor {
    // Command: /swap <player> <item> <amount> <item2> <amount2>

    private sGift plugin;
    private int ID;
    Player player = null;
    String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "sGift" + ChatColor.WHITE + "] ";
    Logger log = Logger.getLogger("Minecraft");

    public SwapCommand(sGift instance) {
	plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

	int maxAmount = plugin.getConfig().getInt("Options.max-amount");

	PluginDescriptionFile pdf = plugin.getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	if (sender instanceof Player) {

	    player = (Player) sender;
	} else {

	    player = null;
	}
	if (commandLabel.equalsIgnoreCase("swap") && plugin.getPerms(player, "sgift.swap.swap")) {
	    if (plugin.getConfig().getBoolean("Features.enable-swap")) {
		if (player == null) {

		    log.warning(logpre + "Don't send sGift commands through console!");

		} else if (args.length == 1) {
		    if (args[0].equalsIgnoreCase("auto") && plugin.getPerms(player, "sgift.swap.auto")) {
			if (plugin.getConfig().getBoolean("Features.allow-auto.swap")) {
			    if (!player.hasPermission("sgift.toggles.swap.accept")) {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept enabled for Swapping!");
				plugin.getPermissions().playerAdd(player, "sgift.toggles.swap.accept");
				plugin.getPermissions().playerRemove(player, "-sgift.toggles.swap.accept");
			    } else {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept disabled for Swapping!");
				plugin.getPermissions().playerRemove(player, "sgift.toggles.swap.accept");
				plugin.getPermissions().playerAdd(player, "-sgift.toggles.swap.accept");
			    }
			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			}
		    } else if (args[0].equalsIgnoreCase("auto-deny") && plugin.getPerms(player, "sgift.swap.autodeny")) {
			if (plugin.getConfig().getBoolean("Features.allow-auto.swap")) {
			    if (!player.hasPermission("sgift.toggles.swap.deny")) {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny enabled for Swapping!");
				plugin.getPermissions().playerAdd(player, "sgift.toggles.swap.deny");
				plugin.getPermissions().playerRemove(player, "-sgift.toggles.swap.deny");
			    } else {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny disabled for Swapping!");
				plugin.getPermissions().playerRemove(player, "sgift.toggles.swap.deny");
				plugin.getPermissions().playerAdd(player, "-sgift.toggles.swap.deny");
			    }
			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			}
		    } else if (args[0].equalsIgnoreCase("help") && plugin.getPerms(player, "sgift.swap.help")) {

			player.sendMessage(ChatColor.DARK_GRAY + "----------------[" + ChatColor.GREEN + "sGift - Swap Help Menu" + ChatColor.DARK_GRAY + "]----------------");
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Swap"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Example"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Accept"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Deny"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Cancel"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Auto"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.AutoDeny"));
			player.sendMessage(plugin.getConfig().getString("Help.Swap.Help"));

			if (player.hasPermission("sgift.admin")) {

			    player.sendMessage(plugin.getConfig().getString("Help.Swap.Stop"));
			}
		    } else if (args[0].equalsIgnoreCase("accept") && plugin.getPerms(player, "sgift.swap.accept")) {

			Swap swap = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Swap sw : plugin.swaps) {

			    if (sw.Victim == player) {

				swap = sw;

				for (Sender s : plugin.senders) {

				    if (s.Sender == sw.playerSender) {

					Sender1 = s;
				    }
				}
				for (Timeout o : plugin.timeout) {

				    if (o.ID == swap.ID) {

					time = o;
				    }
				}
			    }
			}
			if (swap == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Swaps to accept!");
			} else {

			    Player playerInitial = swap.playerSender;
			    Player Victim = swap.Victim;
			    ItemStack itemsFromSender = swap.itemSender;
			    ItemStack itemsFromVictim = swap.itemVictim;

			    if (player.getInventory().firstEmpty() == -1) {

				Location playerloc = player.getLocation();
				player.getWorld().dropItemNaturally(playerloc, itemsFromSender);

				player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");

			    } else {

				Victim.getInventory().addItem(itemsFromSender);

				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");
			    }
			    if (playerInitial.getInventory().firstEmpty() == -1) {

				Location playerloc = playerInitial.getLocation();
				playerInitial.getWorld().dropItemNaturally(playerloc, itemsFromVictim);

				player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");

			    } else {

				playerInitial.getInventory().addItem(itemsFromVictim);

				playerInitial.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");
			    }
			    log.info(logpre + Victim.getDisplayName() + " recieved " + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + " from " + playerInitial.getName() + " for " + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + "!");

			    plugin.timeout.remove(time);
			    plugin.swaps.remove(swap);
			    plugin.senders.remove(Sender1);
			}
		    } else if (args[0].equalsIgnoreCase("deny") && plugin.getPerms(player, "sgift.swap.deny")) {

			Swap swap = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Swap sw : plugin.swaps) {

			    if (sw.Victim == player) {

				swap = sw;

				for (Sender s : plugin.senders) {

				    if (s.Sender == sw.playerSender) {

					Sender1 = s;
				    }
				}
				for (Timeout o : plugin.timeout) {

				    if (o.ID == swap.ID) {

					time = o;
				    }
				}
			    }
			}

			if (swap == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Swaps to deny!");
			} else {

			    Player playerSendingItems = swap.playerSender;
			    Player Victim = swap.Victim;
			    ItemStack itemsFromSender = swap.itemSender;
			    ItemStack itemsFromVictim = swap.itemVictim;

			    if (Victim.getInventory().firstEmpty() == -1) {

				Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				Location playerloc = Victim.getLocation();
				Victim.getWorld().dropItemNaturally(playerloc, itemsFromSender);
			    } else {
				Victim.sendMessage(prefix + ChatColor.YELLOW + "Your Items have been returned to you.");
				Victim.getInventory().addItem(itemsFromVictim);
			    }

			    if (playerSendingItems.getInventory().firstEmpty() == -1) {

				Location playerloc = playerSendingItems.getLocation();
				playerSendingItems.getWorld().dropItemNaturally(playerloc, itemsFromSender);

				playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Swap request!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Swap!");



			    } else if (!playerSendingItems.isOnline() || !Victim.isOnline()) {
				player.sendMessage(prefix + ChatColor.RED + "Player sending items is not Online!");
				player.sendMessage(prefix + ChatColor.RED + "Please wait for the Player to come back online!");

			    } else {
				playerSendingItems.getInventory().addItem(itemsFromSender);
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Swap request!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Swap!");
			    }

			    log.info(logpre + Victim.getDisplayName() + " denied " + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + " from " + playerSendingItems.getDisplayName());

			    plugin.timeout.remove(time);
			    plugin.swaps.remove(swap);
			    plugin.senders.remove(Sender1);
			}



		    } else if (args[0].equalsIgnoreCase("stop") && plugin.getPerms(player, "sgift.sgift")) {
			while (plugin.swaps.size() > 0) {

			    Swap swap = null;
			    Timeout time = null;
			    Sender Sender1 = null;

			    for (Swap sw : plugin.swaps) {

				if (sw.itemSender != null) {
				    swap = sw;

				    for (Sender s : plugin.senders) {

					if (s.Sender != null) {

					    Sender1 = s;
					}
				    }
				    for (Timeout o : plugin.timeout) {

					if (o.ID == swap.ID) {

					    time = o;
					}

				    }
				}
			    }

			    if (swap == null || time == null) {

				player.sendMessage(prefix + ChatColor.RED + "No Swaps to stop!");
			    } else {

				Player playerSendingItems = swap.playerSender;
				Player Victim = swap.Victim;
				ItemStack itemsFromSender = swap.itemSender;
				ItemStack itemsFromVictim = swap.itemVictim;

				if (playerSendingItems.getInventory().firstEmpty() == -1) {

				    Location playerloc = playerSendingItems.getLocation();

				    playerSendingItems.getWorld().dropItemNaturally(playerloc, itemsFromSender);
				    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Swap has been cancelled by an Admin!");
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Swap.");
				    log.info(logpre + "stopped a swap of " + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + " from " + playerSendingItems.getDisplayName() + " for " + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ".");

				    plugin.timeout.remove(time);
				    plugin.swaps.remove(swap);
				    plugin.senders.remove(Sender1);

				} else {

				    playerSendingItems.getInventory().addItem(itemsFromSender);
				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Swap has been cancelled by an Admin!");
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Swap.");

				    log.info(logpre + "stopped a swap of " + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + " from " + playerSendingItems.getDisplayName() + " for " + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ".");

				    plugin.timeout.remove(time);
				    plugin.swaps.remove(swap);
				    plugin.senders.remove(Sender1);
				}
				if (Victim.getInventory().firstEmpty() == -1) {

				    Location playerloc = Victim.getLocation();

				    Victim.getWorld().dropItemNaturally(playerloc, itemsFromVictim);
				    Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				    Victim.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				    Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");

				    plugin.timeout.remove(time);
				    plugin.senders.remove(Sender1);
				    plugin.swaps.remove(swap);

				} else {

				    Victim.getInventory().addItem(itemsFromVictim);
				    Victim.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				    Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");

				    plugin.timeout.remove(time);
				    plugin.senders.remove(Sender1);
				    plugin.swaps.remove(swap);
				}
			    }
			}
			player.sendMessage(prefix + ChatColor.GREEN + "Cancelled all Swaps safely.");

		    } else if (args[0].equalsIgnoreCase("cancel") && plugin.getPerms(player, "sgift.swap.cancel")) {

			Swap swap = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Swap sw : plugin.swaps) {

			    if (sw.playerSender == player) {

				swap = sw;
			    }
			}

			for (Sender s : plugin.senders) {

			    if (s.Sender == player) {

				Sender1 = s;
			    }

			}
			for (Timeout o : plugin.timeout) {
			    try {
				if (o.ID == swap.ID) {

				    time = o;
				}
			    } catch (NullPointerException e) {
				log.info(logpre + "Nag Sk8r2K9 to fix this! (Nothing should be affected, Just silly code)");
				player.sendMessage(prefix + ChatColor.RED + "No Swaps to cancel!");
				log.severe(e.toString());
				return true;
			    }
			}

			if (swap == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Swaps to cancel!");
			} else {

			    Player playerSendingItems = swap.playerSender;
			    Player Victim = swap.Victim;
			    ItemStack itemsFromSender = swap.itemSender;
			    ItemStack itemsFromVictim = swap.itemVictim;

			    if (playerSendingItems.getInventory().firstEmpty() == -1) {

				Location playerloc = playerSendingItems.getLocation();

				playerSendingItems.getWorld().dropItemNaturally(playerloc, itemsFromSender);
				playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Swap!");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.swaps.remove(swap);

			    } else {

				playerSendingItems.getInventory().addItem(itemsFromSender);
				playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Swap!");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.swaps.remove(swap);
			    }
			    if (Victim.getInventory().firstEmpty() == -1) {

				Location playerloc = Victim.getLocation();

				Victim.getWorld().dropItemNaturally(playerloc, itemsFromVictim);
				Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				Victim.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.RED + " Has been returned to you.");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.swaps.remove(swap);

			    } else {

				Victim.getInventory().addItem(itemsFromVictim);
				Victim.sendMessage(prefix + ChatColor.RED + "Cancelled Swap!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.RED + " Has been returned to you.");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.swaps.remove(swap);
			    }
			}
		    } else if (Bukkit.getServer().getPlayer(args[0]) == null) {

			player.sendMessage(prefix + ChatColor.RED + "Player not Online.");

		    } else if (Bukkit.getServer().getPlayer(args[0]) == player) {

			player.sendMessage(prefix + ChatColor.RED + "Don't swap Items with yourself!");

		    } else if (plugin.getPerms(player, "sgift.swap.start")) {

			player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount>");

		    }
		} else if (args.length == 2) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount>");

		} else if (args.length == 3) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount>");

		} else if (args.length == 4) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount>");

		} else if (args.length == 5) {
		    if (Bukkit.getServer().getPlayer(args[0]) != player) {
			if (Bukkit.getServer().getPlayer(args[0]) != null) {
			    if (!plugin.inCreative(player, Bukkit.getServer().getPlayer(args[0]))) {

				Player Victim = null;
				int amount = plugin.getInt(args[2]);
				ItemStack Item = null;
				ItemStack ItemFromVictim = null;
				int amountFromVictim = plugin.getInt(args[4]);

				ItemInfo ii = Items.itemByString(args[1]);
				ItemInfo ii2 = Items.itemByString(args[3]);

				if (args[1].equalsIgnoreCase("hand")) {
				    if (plugin.getHand(player, player.getItemInHand().clone())) {

					Item = player.getItemInHand().clone();

					Victim = Bukkit.getServer().getPlayer(args[0]);

					Item.setAmount(amount);

					Location VictimLoc = Victim.getLocation();
					Location playerLoc = player.getLocation();

					ItemFromVictim = new ItemStack(ii2.getType(), amountFromVictim, ii2.getSubTypeId());

					if (new InventoryManager(Victim).contains(ItemFromVictim, true, true)) {

					    new Exchange(plugin, player, playerLoc, Victim, VictimLoc, Item, ItemFromVictim, amount, amountFromVictim, true).start();

					} else {

					    player.sendMessage(prefix + ChatColor.RED + "That player doesn't have that Item!");
					}
				    } else {

					player.sendMessage(prefix + ChatColor.RED + "There's no Item in your Hand!");
				    }
				} else if (Items.itemByString(args[1]) != null) {

				    Item = new ItemStack(ii.getType(), amount, ii.getSubTypeId());

				    if (new InventoryManager(player).contains(Item, true, true)) {

					Victim = Bukkit.getServer().getPlayer(args[0]);

					Location VictimLoc = Victim.getLocation();
					Location playerLoc = player.getLocation();

					if (new InventoryManager(Victim).contains(ItemFromVictim, true, true)) {

					    new Exchange(plugin, player, playerLoc, Victim, VictimLoc, Item, ItemFromVictim, amount, amountFromVictim, false).start();

					} else {

					    player.sendMessage(prefix + ChatColor.RED + "That player doesn't have that Item!");
					}
				    } else {

					player.sendMessage(prefix + ChatColor.RED + "You don't have enough of that Item, Or Item is partially damaged!");
				    }
				} else {

				    player.sendMessage(prefix + ChatColor.RED + "Material specified is Invalid!");
				}
			    }
			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Player not Online!");
			}

		    } else {

			player.sendMessage(prefix + ChatColor.RED + "You can't Swap with yourself!");
		    }

		} else if (args.length > 5) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Many arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount> <Item_From_Player> <Amount>");
		} else if (args.length == 0) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount> <Item_From_Player> <Amount>");
		} else {

		    player.sendMessage(prefix + ChatColor.RED + "Invalid command usage.");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /swap <Player> <Item> <Amount> <Item_From_Player> <Amount>");
		}
	    } else {
		if (player != null) {

		    player.sendMessage(prefix + ChatColor.RED + "Swapping currently disabled.");
		} else {

		    log.warning(logpre + "Don't send sGift commands through console!");
		}
	    }
	}

	return false;
    }
}
