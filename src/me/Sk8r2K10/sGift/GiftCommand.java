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

public class GiftCommand implements CommandExecutor {

    private sGift plugin;
    private Player player = null;
    private String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "sGift" + ChatColor.WHITE + "] ";
    Logger log = Logger.getLogger("Minecraft");

    public GiftCommand(sGift instance) {
	plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

	int maxAmount = plugin.getConfig().getInt("Options.max-amount");

	if (sender instanceof Player) {

	    player = (Player) sender;
	} else {

	    player = null;
	}
	if (commandLabel.equalsIgnoreCase("gift") && plugin.getPerms(player, "sgift.gift.gift")) {

	    PluginDescriptionFile pdf = plugin.getDescription();
	    String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	    if (plugin.getConfig().getBoolean("Features.enable-gift")) {
		if (player == null) {

		    log.warning(logpre + "Don't send sGift commands through console!");

		} else if (args.length == 1) {
		    if (args[0].equalsIgnoreCase("auto") && plugin.getPerms(player, "sgift.gift.auto")) {
			if (plugin.getConfig().getBoolean("Features.allow-auto.gift")) {
			    if (!player.hasPermission("sgift.toggles.gift.accept")) {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept enabled for Gifting!");
				plugin.getPermissions().playerAdd(player, "sgift.toggles.gift.accept");
				plugin.getPermissions().playerRemove(player, "-sgift.toggles.gift.accept");
			    } else {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accept disabled for Gifting!");
				plugin.getPermissions().playerRemove(player, "sgift.toggles.gift.accept");
				plugin.getPermissions().playerAdd(player, "-sgift.toggles.gift.accept");
			    }

			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			}
		    } else if (args[0].equalsIgnoreCase("auto-deny") && plugin.getPerms(player, "sgift.gift.autodeny")) {
			if (plugin.getConfig().getBoolean("Features.allow-auto.gift")) {
			    if (!player.hasPermission("sgift.toggles.gift.deny")) {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny enabled for Gifting!");
				plugin.getPermissions().playerAdd(player, "sgift.toggles.gift.deny");
				plugin.getPermissions().playerRemove(player, "-sgift.toggles.gift.deny");
			    } else {

				player.sendMessage(prefix + ChatColor.YELLOW + "Auto-Deny disabled for Gifting!");
				plugin.getPermissions().playerRemove(player, "sgift.toggles.gift.deny");
				plugin.getPermissions().playerAdd(player, "-sgift.toggles.gift.deny");
			    }
			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Auto-Features are Disabled.");
			}
		    } else if (args[0].equalsIgnoreCase("help") && plugin.getPerms(player, "sgift.gift.help")) {

			player.sendMessage(ChatColor.DARK_GRAY + "----------------[" + ChatColor.GREEN + "sGift - Gift Help Menu" + ChatColor.DARK_GRAY + "]-----------------");
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Gift"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Example"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Accept"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Deny"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Cancel"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Auto"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.AutoDeny"));
			player.sendMessage(plugin.getConfig().getString("Help.Gift.Help"));

			if (player.hasPermission("sgift.admin")) {

			    player.sendMessage(plugin.getConfig().getString("Help.Gift.Stop"));
			}

		    } else if (args[0].equalsIgnoreCase("accept") && plugin.getPerms(player, "sgift.gift.accept")) {

			Gift gift = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Gift g : plugin.gifts) {

			    if (g.Victim == player) {

				gift = g;

				for (Sender s : plugin.senders) {

				    if (s.Sender == gift.playerSender) {

					Sender1 = s;
				    }
				}
				for (Timeout o : plugin.timeout) {

				    if (o.ID == gift.ID) {

					time = o;
				    }
				}
			    }
			}
			if (gift == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Gifts to accept!");
			} else {

			    Player playerSendingItems = gift.playerSender;
			    Player Victim = gift.Victim;
			    ItemStack items = gift.itemStack;

			    if (player.getInventory().firstEmpty() == -1) {

				Location playerloc = player.getLocation();
				player.getWorld().dropItemNaturally(playerloc, items);

				player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + "!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + "!");

				log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				plugin.timeout.remove(time);
				plugin.gifts.remove(gift);
				plugin.senders.remove(Sender1);

			    } else {

				Victim.getInventory().addItem(items);
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + "!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + "!");

				log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				plugin.timeout.remove(time);
				plugin.gifts.remove(gift);
				plugin.senders.remove(Sender1);
			    }
			}
		    } else if (args[0].equalsIgnoreCase("deny") && plugin.getPerms(player, "sgift.gift.deny")) {

			Gift gift = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Gift g : plugin.gifts) {

			    if (g.Victim == player) {

				gift = g;

				for (Sender s : plugin.senders) {

				    if (s.Sender == g.playerSender) {

					Sender1 = s;
				    }
				}
				for (Timeout o : plugin.timeout) {

				    if (o.ID == gift.ID) {

					time = o;
				    }
				}
			    }
			}

			if (gift == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Gifts to deny!");
			} else {

			    Player playerSendingItems = gift.playerSender;
			    Player Victim = gift.Victim;
			    ItemStack items = gift.itemStack;

			    if (playerSendingItems.getInventory().firstEmpty() == -1) {

				Location playerloc = playerSendingItems.getLocation();

				playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
				playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Gift request!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Gift!");

				log.info(logpre + Victim.getDisplayName() + " denied " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				plugin.timeout.remove(time);
				plugin.gifts.remove(gift);
				plugin.senders.remove(Sender1);

			    } else if (!playerSendingItems.isOnline()) {
				player.sendMessage(prefix + ChatColor.RED + "Player sending items is not Online!");
				player.sendMessage(prefix + ChatColor.RED + "Please wait for " + playerSendingItems.getName() + " to come back online!");

			    } else {
				playerSendingItems.getInventory().addItem(items);
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Gift request!");

				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Gift!");

				log.info(logpre + Victim.getDisplayName() + " denied " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				plugin.timeout.remove(time);
				plugin.gifts.remove(gift);
				plugin.senders.remove(Sender1);
			    }

			}

		    } else if (args[0].equalsIgnoreCase("stop") && plugin.getPerms(player, "sgift.sgift")) {
			while (plugin.gifts.size() > 0) {

			    Gift gift = null;
			    Timeout time = null;
			    Sender Sender1 = null;

			    for (Gift g : plugin.gifts) {

				if (g.itemStack != null) {
				    gift = g;

				    for (Sender s : plugin.senders) {

					if (s.Sender != null) {

					    Sender1 = s;
					}
				    }
				    for (Timeout o : plugin.timeout) {

					if (o.ID == gift.ID) {

					    time = o;
					}
				    }
				}
			    }

			    if (gift == null) {

				player.sendMessage(prefix + ChatColor.RED + "No Gifts to stop!");
			    } else {

				Player playerSendingItems = gift.playerSender;
				Player Victim = gift.Victim;
				ItemStack items = gift.itemStack;

				if (playerSendingItems.getInventory().firstEmpty() == -1) {

				    Location playerloc = playerSendingItems.getLocation();

				    playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
				    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Gift has been cancelled by an Admin!");
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Gift.");
				    log.info(logpre + "stopped a gift of " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				    plugin.timeout.remove(time);
				    plugin.gifts.remove(gift);
				    plugin.senders.remove(Sender1);

				} else {

				    playerSendingItems.getInventory().addItem(items);

				    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Gift has been cancelled by an Admin!");
				    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " has been returned to you.");
				    Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Gift.");

				    log.info(logpre + "stopped a gift of " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

				    plugin.timeout.remove(time);
				    plugin.gifts.remove(gift);
				    plugin.senders.remove(Sender1);
				}

			    }
			}
			player.sendMessage(prefix + ChatColor.GREEN + "Cancelled all Gifts safely.");

		    } else if (args[0].equalsIgnoreCase("cancel") && plugin.getPerms(player, "sgift.gift.cancel")) {

			Gift gift = null;
			Timeout time = null;
			Sender Sender1 = null;

			for (Gift g : plugin.gifts) {

			    if (g.playerSender == player) {

				gift = g;
			    }
			}

			for (Sender s : plugin.senders) {

			    if (s.Sender == player) {

				Sender1 = s;
			    }

			}

			for (Timeout o : plugin.timeout) {
			    try {
				if (o.ID == gift.ID) {

				time = o;
			    }
				
			    } catch (NullPointerException e) {
				log.info(logpre + "Nag Sk8r2K9 to fix this! (Some strange bug!(Nothing should be affected, Just silly code))");
				player.sendMessage(prefix + ChatColor.RED + "No Gifts to cancel!");
				log.severe(e.toString());
				return true;
			    }
			}

			if (gift == null || time == null) {

			    player.sendMessage(prefix + ChatColor.RED + "No Gifts to cancel!");
			} else {

			    Player playerSendingItems = gift.playerSender;
			    Player Victim = gift.Victim;
			    ItemStack items = gift.itemStack;

			    if (playerSendingItems.getInventory().firstEmpty() == -1) {

				Location playerloc = playerSendingItems.getLocation();

				playerSendingItems.getWorld().dropItemNaturally(playerloc, items);

				playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled gift!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Gift!");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.gifts.remove(gift);

			    } else {

				playerSendingItems.getInventory().addItem(items);

				playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled gift!");
				playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.RED + " Has been returned to you.");
				Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Gift!");

				plugin.timeout.remove(time);
				plugin.senders.remove(Sender1);
				plugin.gifts.remove(gift);
			    }

			}
		    } else if (Bukkit.getServer().getPlayer(args[0]) == null) {

			player.sendMessage(prefix + ChatColor.RED + "Player not Online.");

		    } else if (Bukkit.getServer().getPlayer(args[0]) == player) {

			player.sendMessage(prefix + ChatColor.RED + "Don't gift Items to yourself!");

		    } else if (plugin.getPerms(player, "sgift.gift.start")) {

			player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
			player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /gift <Player> <Item> <Amount>");

		    }
		} else if (args.length == 2) {

		    player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /gift <Player> <Item> <Amount>");

		} else if (args.length == 3 && plugin.getPerms(player, "sgift.gift.start")) {
		    if (Bukkit.getServer().getPlayer(args[0]) != player) {
			if (Bukkit.getServer().getPlayer(args[0]) != null) {
			    if (!plugin.inCreative(player, Bukkit.getServer().getPlayer(args[0]))) {
				
				Player Victim = null;
				int amount = plugin.getInt(args[2]);
				ItemStack Item = null;

				ItemInfo ii = Items.itemByString(args[1]);

				if (args[1].equalsIgnoreCase("hand")) {
				    if (plugin.getHand(player, player.getItemInHand().clone())) {

					Item = player.getItemInHand().clone();
					Victim = Bukkit.getServer().getPlayer(args[0]);
										
					Item.setAmount(amount);

					Location VictimLoc = Victim.getLocation();
					Location playerLoc = player.getLocation();
					
					new StartExchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, true).start();
					
				    } else {

					player.sendMessage(prefix + ChatColor.RED + "There's no Item in your Hand!");
				    }
				} else if (Items.itemByString(args[1]) != null) {

				    Item = new ItemStack(ii.getType(), amount, ii.getSubTypeId());

				    Victim = Bukkit.getServer().getPlayer(args[0]);

				    Location VictimLoc = Victim.getLocation();
				    Location playerLoc = player.getLocation();
				    
				    new StartExchange(plugin, player, playerLoc, Victim, VictimLoc, Item, amount, false).start();

				} else {

				    player.sendMessage(prefix + ChatColor.RED + "Material specified is Invalid!");
				}
			    }
			} else {

			    player.sendMessage(prefix + ChatColor.RED + "Player not Online!");
			}

		    } else {

			player.sendMessage(prefix + ChatColor.RED + "You can't Gift yourself!");
		    }

		} else if (args.length == 0) {

		    player.sendMessage(prefix + ChatColor.RED + "By Sk8r2K9. /gift help for more info.");

		} else if (args.length >= 4) {

		    player.sendMessage(prefix + ChatColor.RED + "Too many arguments!");
		    player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /gift <Player> <Item> <Amount>");
		}
	    } else {
		if (player != null) {

		    player.sendMessage(prefix + "Gifting is currently disabled!");
		} else {

		    log.warning(logpre + "Don't send sGift commands through console!");
		}
	    }
	}

	return false;
    }
}
