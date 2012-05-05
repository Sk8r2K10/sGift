package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public class Exchange {

    private String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
    private sGift plugin;
    private Player player;
    private Player Victim;
    private Location playerLoc;
    private Location VictimLoc;
    private ItemStack Item;
    private ItemStack ItemFromVictim;
    private int amount;
    private int amountFromVictim;
    private int price;
    private boolean hand;
    final static Logger log = Logger.getLogger("Minecraft");

    public Exchange(sGift instance) {

	plugin = instance;
    }

    public Exchange(sGift instance, Player Player) {

	plugin = instance;
	player = Player;
    }

    public Exchange(sGift instance, Player Player, Location PlayerLoc, Player victim, Location victimLoc, ItemStack item, int Amount, boolean Hand) {

	player = Player;
	playerLoc = PlayerLoc;
	Victim = victim;
	VictimLoc = victimLoc;
	Item = item;
	amount = Amount;
	price = -1;
	ItemFromVictim = null;
	amountFromVictim = -1;
	plugin = instance;
	hand = Hand;


    }

    public Exchange(sGift instance, Player Player, Location PlayerLoc, Player victim, Location victimLoc, ItemStack item, int Amount, int Price, boolean Hand) {

	player = Player;
	playerLoc = PlayerLoc;
	Victim = victim;
	VictimLoc = victimLoc;
	Item = item;
	amount = Amount;
	price = Price;
	ItemFromVictim = null;
	amountFromVictim = -1;
	plugin = instance;
	hand = Hand;

    }

    public Exchange(sGift instance, Player Player, Location PlayerLoc, Player victim, Location victimLoc, ItemStack item, ItemStack vItem, int Amount, int vAmount, boolean Hand) {

	player = Player;
	playerLoc = PlayerLoc;
	Victim = victim;
	VictimLoc = victimLoc;
	Item = item;
	amount = Amount;
	price = -1;
	ItemFromVictim = vItem;
	amountFromVictim = vAmount;
	plugin = instance;
	hand = Hand;

    }

    public void start() {

	PluginDescriptionFile pdf = plugin.getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";


	int maxAmount = plugin.getConfig().getInt("Options.max-amount");

	if (plugin.alreadyRequested(player, Victim)) {

	    return;

	}
	if (plugin.differentWorlds(player, Victim)) {

	    player.sendMessage(errpre + "You are not in the same world as that player!");
	    player.sendMessage(errpre + ChatColor.GRAY + "You must be in '" + Victim.getWorld().getName() + "' to Gift " + Victim.getName() + ".");
	    return;
	}
	if (!plugin.rangeIsDisabled() && !plugin.isWithinRange(VictimLoc, playerLoc)) {

	    player.sendMessage(errpre + "You are out of range with that player!");
	    player.sendMessage(errpre + ChatColor.GRAY + "You must be within " + plugin.getConfig().getInt("Options.max-distance") + " blocks of each other.");
	    return;

	}
	if (amount == 0 || (((maxAmount < amount) && (maxAmount != 0)) && !player.hasPermission("sgift.overrides.max"))) {

	    player.sendMessage(errpre + "Invalid amount!");

	    if (maxAmount != 0) {

		player.sendMessage(errpre + ChatColor.GRAY + "Amount is too large! Max is: " + maxAmount);

	    }
	    return;
	}
	if (plugin.itemsAreNull(Item)) {

	    player.sendMessage(errpre + "Items attempted to gift are currently unsupported.");
	    return;

	}
	if (!new InventoryManager(player).contains(Item, true, true)) {

	    player.sendMessage(errpre + "You do not have enough of that Item!");
	    return;

	}
	if (player.getItemInHand().getAmount() < Item.getAmount() && hand) {

	    player.sendMessage(errpre + "You do not have enough of that Item in your hand!");
	    return;
	}
	if (price == -1 && ItemFromVictim == null) {
	    if (!Victim.hasPermission("sgift.toggles.gift.deny")) {
		String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "sGift" + ChatColor.WHITE + "] ";

		Item.setAmount(amount);

		plugin.ID += 1;

		Gift tgift = new Gift(Victim, player, Item, plugin.ID);

		long time = player.getWorld().getTime();

		plugin.gifts.add(tgift);
		plugin.timeout.add(new Timeout(tgift, player, plugin.ID, time));
		plugin.senders.add(new Sender(player));

		new InventoryManager(player).remove(Item);

		player.sendMessage(prefix + ChatColor.WHITE + "Now Gifting " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName() + ChatColor.WHITE + " with " + ChatColor.YELLOW + Victim.getName());
		player.sendMessage(prefix + ChatColor.YELLOW + "Waiting for " + Victim.getName() + " to accept...");
		Victim.sendMessage(prefix + ChatColor.WHITE + "New Gift from " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " of " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName());
		Victim.sendMessage(prefix + ChatColor.WHITE + "Do " + ChatColor.YELLOW + "/Gift accept" + ChatColor.WHITE + " to accept this Gift or " + ChatColor.YELLOW + "/Gift deny" + ChatColor.WHITE + " to deny this Gift!");

		plugin.newTimeout(player, Victim, Item);

		if (Item.getDurability() < Item.getType().getMaxDurability()) {

		    Victim.sendMessage(prefix + ChatColor.RED + "Warning! This item has " + (Item.getType().getMaxDurability() - Item.getDurability()) + " uses left out of a maximum of " + Item.getType().getMaxDurability() + " uses.");

		}
		if (Item.getEnchantments().size() > 0) {

		    Victim.sendMessage(prefix + ChatColor.YELLOW + "This Item is enchanted!");

		}
		if (plugin.auto(Victim, "gift", "sgift.toggles.gift.accept")) {

		    Timeout out = null;
		    Gift gift = null;
		    Sender Sender1 = null;

		    for (Gift g : plugin.gifts) {

			if (g.Victim == Victim) {

			    gift = g;

			    for (Sender s : plugin.senders) {

				if (s.Sender == g.playerSender) {

				    Sender1 = s;
				}
			    }
			    for (Timeout o : plugin.timeout) {

				if (o.ID == gift.ID) {

				    out = o;
				}
			    }
			}
		    }

		    if (gift == null) {

			Victim.sendMessage(prefix + ChatColor.RED + "No Gifts to accept!");

		    } else {

			Player playerSendingItems = gift.playerSender;
			ItemStack items = gift.itemStack;

			if (Victim.getInventory().firstEmpty() == -1) {

			    Location playerloc = Victim.getLocation();
			    Victim.getWorld().dropItemNaturally(playerloc, items);
			    Victim.sendMessage(prefix + ChatColor.YELLOW + "Auto Accepting, Use /gift auto to toggle this on or off!");

			    Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
			    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + "!");
			    Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + "!");

			    log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

			    plugin.timeout.remove(out);
			    plugin.gifts.remove(gift);
			    plugin.senders.remove(Sender1);

			} else {

			    Victim.getInventory().addItem(items);
			    Victim.sendMessage(prefix + ChatColor.YELLOW + "Auto Accepting, Use /gift auto to toggle this on or off!");

			    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + "!");
			    Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + "!");

			    log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName());

			    plugin.timeout.remove(out);
			    plugin.gifts.remove(gift);
			    plugin.senders.remove(Sender1);

			    return;
			}
		    }
		}
	    } else {
		
		player.sendMessage(errpre + "That player doesn't want to be Gifted!");
		
	    }
	}

	if (price > 0) {
	    if (!Victim.hasPermission("sgift.toggles.trade.deny")) {
		String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";

		Item.setAmount(amount);

		plugin.ID += 1;

		Trade ttrade = new Trade(Victim, player, Item, price, plugin.ID);

		long time = player.getWorld().getTime();

		plugin.trades.add(ttrade);
		plugin.timeout.add(new Timeout(ttrade, player, plugin.ID, time));
		plugin.senders.add(new Sender(player));

		new InventoryManager(player).remove(Item);

		player.sendMessage(prefix + ChatColor.WHITE + "Now Trading " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName() + ChatColor.WHITE + " with " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");
		player.sendMessage(prefix + ChatColor.YELLOW + "Waiting for " + Victim.getName() + " to accept...");
		Victim.sendMessage(prefix + ChatColor.WHITE + "New Trade from " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " of " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");
		Victim.sendMessage(prefix + ChatColor.WHITE + "Do " + ChatColor.YELLOW + "/trade accept" + ChatColor.WHITE + " to accept this Trade or " + ChatColor.YELLOW + "/trade deny" + ChatColor.WHITE + " to deny this trade!");

		plugin.newTimeout(player, Victim, Item, price);

		if (Item.getEnchantments().size() > 0) {

		    Victim.sendMessage(prefix + ChatColor.YELLOW + "This Item is enchanted!");

		}
		if (Item.getDurability() < Item.getType().getMaxDurability()) {

		    Victim.sendMessage(prefix + ChatColor.RED + "Warning! This item has " + (Item.getType().getMaxDurability() - Item.getDurability()) + " uses left out of a maximum of " + Item.getType().getMaxDurability() + " uses.");

		}
		if (plugin.auto(Victim, "trade", "sgift.toggles.trade.accept")) {

		    Trade trade = null;
		    Timeout out = null;
		    Sender Sender1 = null;

		    for (Trade t : plugin.trades) {

			if (t.Victim == Victim) {

			    trade = t;

			    for (Sender s : plugin.senders) {

				if (s.Sender == t.playerSender) {

				    Sender1 = s;
				}
			    }

			    for (Timeout o : plugin.timeout) {

				if (o.ID == trade.ID) {

				    out = o;
				}
			    }
			}
		    }

		    if (trade == null) {

			player.sendMessage(prefix + ChatColor.RED + "No Trades to accept!");
		    } else {

			Player playerSendingItems = trade.playerSender;
			ItemStack items = trade.itemStack;

			if (Victim.getInventory().firstEmpty() == -1) {
			    Location playerloc = player.getLocation();
			    Victim.getWorld().dropItemNaturally(playerloc, items);

			    Victim.sendMessage(prefix + ChatColor.YELLOW + "Auto Accepting, Use /trade auto to toggle this on or off!");
			    Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

			    plugin.getEcon().withdrawPlayer(Victim.getName(), price);
			    plugin.getEcon().depositPlayer(playerSendingItems.getName(), price);


			    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
			    Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
			    log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + plugin.getEcon().currencyNameSingular() + "(s)");

			    plugin.timeout.remove(out);
			    plugin.trades.remove(trade);
			    plugin.senders.remove(Sender1);

			} else {
			    Victim.getInventory().addItem(items);

			    plugin.getEcon().withdrawPlayer(Victim.getName(), price);
			    plugin.getEcon().depositPlayer(playerSendingItems.getName(), price);

			    Victim.sendMessage(prefix + ChatColor.YELLOW + "Auto Accepting, Use /trade auto to toggle this on or off!");

			    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
			    Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.itemByStack(items).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
			    log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.itemByStack(items).getName() + " from " + playerSendingItems.getDisplayName() + " for " + price + plugin.getEcon().currencyNameSingular() + "(s)");

			    plugin.timeout.remove(out);
			    plugin.trades.remove(trade);
			    plugin.senders.remove(Sender1);

			    return;
			}
		    }
		}
	    } else {

		player.sendMessage(errpre + "That player doesn't want to be Traded with!");
	    }
	}

	if (ItemFromVictim != null && price == -1) {
	    if (!Victim.hasPermission("sgift.toggles.swap.deny")) {
		if (new InventoryManager(Victim).contains(ItemFromVictim, true, true)) {
		    String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "sGift" + ChatColor.WHITE + "] ";

		    plugin.ID += 1;

		    Swap tswap = new Swap(Victim, player, Item, ItemFromVictim, plugin.ID);

		    long time = player.getWorld().getTime();

		    plugin.swaps.add(tswap);
		    plugin.timeout.add(new Timeout(tswap, player, plugin.ID, time));
		    plugin.senders.add(new Sender(player));

		    new InventoryManager(player).remove(Item);
		    new InventoryManager(Victim).remove(ItemFromVictim);

		    player.sendMessage(prefix + ChatColor.WHITE + "Now Swapping " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + ItemFromVictim.getAmount() + " " + Items.itemByStack(ItemFromVictim).getName() + " with " + ChatColor.YELLOW + Victim.getName() + "!");
		    player.sendMessage(prefix + ChatColor.YELLOW + "Waiting for " + Victim.getName() + " to accept...");
		    Victim.sendMessage(prefix + ChatColor.WHITE + "New Swap Request from " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " of " + ChatColor.YELLOW + Item.getAmount() + " " + Items.itemByStack(Item).getName() + " for " + ItemFromVictim.getAmount() + " " + Items.itemByStack(ItemFromVictim).getName());
		    Victim.sendMessage(prefix + ChatColor.WHITE + "Do " + ChatColor.YELLOW + "/swap accept" + ChatColor.WHITE + " to accept this Swap or " + ChatColor.YELLOW + "/swap deny" + ChatColor.WHITE + " to deny this Swap!");

		    plugin.newTimeout(player, Victim, Item, ItemFromVictim);

		    if (Item.getDurability() < Item.getType().getMaxDurability()) {

			Victim.sendMessage(prefix + ChatColor.RED + "Warning! This item has " + (Item.getType().getMaxDurability() - Item.getDurability()) + " uses left out of a maximum of " + Item.getType().getMaxDurability() + " uses.");

		    }
		    if (Item.getEnchantments().size() > 0) {

			Victim.sendMessage(prefix + ChatColor.YELLOW + "This Item is enchanted!");
			player.sendMessage(Item.getEnchantments().toString());

		    }
		    if (plugin.auto(Victim, "swap", "sgift.toggles.swap.accept")) {

			Swap swap = null;
			Timeout out = null;
			Sender Sender1 = null;

			for (Swap sw : plugin.swaps) {

			    if (sw.Victim == Victim) {

				swap = sw;

				for (Sender s : plugin.senders) {

				    if (s.Sender == sw.playerSender) {

					Sender1 = s;
				    }
				}
				for (Timeout o : plugin.timeout) {

				    if (o.ID == swap.ID) {

					out = o;
				    }
				}
			    }
			}

			if (swap == null) {

			    Victim.sendMessage(prefix + ChatColor.RED + "No Swaps to accept!");
			} else {

			    Player playerInitial = swap.playerSender;
			    Victim = swap.Victim;
			    ItemStack itemsFromSender = swap.itemSender;
			    ItemStack itemsFromVictim = swap.itemVictim;

			    player.sendMessage(prefix + ChatColor.YELLOW + "Other player Auto-Accepted!");
			    Victim.sendMessage(prefix + ChatColor.YELLOW + "Auto-Accepted Swap request! use /swap auto to toggle this on or off.");

			    if (Victim.getInventory().firstEmpty() == -1) {

				Location playerloc = Victim.getLocation();
				Victim.getWorld().dropItemNaturally(playerloc, itemsFromSender);

				Victim.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");

			    } else {

				Victim.getInventory().addItem(itemsFromSender);

				Victim.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");
			    }
			    if (playerInitial.getInventory().firstEmpty() == -1) {

				Location playerloc = playerInitial.getLocation();
				playerInitial.getWorld().dropItemNaturally(playerloc, itemsFromVictim);

				player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");
				player.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerInitial.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");

			    } else {

				playerInitial.getInventory().addItem(itemsFromVictim);

				playerInitial.sendMessage(prefix + ChatColor.YELLOW + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.YELLOW + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + ChatColor.WHITE + "!");
			    }
			    log.info(logpre + Victim.getDisplayName() + " recieved " + itemsFromSender.getAmount() + " " + Items.itemByStack(itemsFromSender).getName() + " from " + playerInitial.getName() + " for " + itemsFromVictim.getAmount() + " " + Items.itemByStack(itemsFromVictim).getName() + "!");

			    plugin.timeout.remove(out);
			    plugin.swaps.remove(swap);
			    plugin.senders.remove(Sender1);

			    return;
			}
		    }
		}
	    } else {

		player.sendMessage(errpre + "That player doesn't want to be Swapped with!");
	    }
	}
    }

    public void cancel(boolean Gift, boolean Trade, boolean Swap) {

	PluginDescriptionFile pdf = plugin.getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	if (Gift) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "sGift" + ChatColor.WHITE + "] ";

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
		    return;
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
	}
	if (Trade) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";

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
		    return;
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
	}
	if (Swap) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "sGift" + ChatColor.WHITE + "] ";

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
		    break;
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
	}
    }

    public void accept(boolean Gift, boolean Trade, boolean Swap) {

	PluginDescriptionFile pdf = plugin.getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	if (Gift) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "sGift" + ChatColor.WHITE + "] ";

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
	}
	if (Trade) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";

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
	}
	if (Swap) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "sGift" + ChatColor.WHITE + "] ";

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
	}
    }

    public void stop(boolean Gift, boolean Trade, boolean Swap) {

	String prefix = ChatColor.WHITE + "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] ";

	if (Gift) {

	    player.sendMessage(prefix + ChatColor.GREEN + "Stopped " + plugin.gifts.size() + " Gifts.");

	    while (plugin.gifts.size() > 0) {

		this.cancel(true, false, false);
	    }
	}
	if (Trade) {

	    player.sendMessage(prefix + ChatColor.GREEN + "Stopped " + plugin.trades.size() + " Trades.");

	    while (plugin.trades.size() > 0) {

		this.cancel(false, true, false);
	    }
	}
	if (Swap) {

	    player.sendMessage(prefix + ChatColor.GREEN + "Stopped " + plugin.swaps.size() + " Swaps.");

	    while (plugin.swaps.size() > 0) {

		this.cancel(false, false, true);
	    }
	}
    }

    public void deny(boolean Gift, boolean Trade, boolean Swap) {

	PluginDescriptionFile pdf = plugin.getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	if (Gift) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + "sGift" + ChatColor.WHITE + "] ";

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
	}
	if (Trade) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";


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
	}
	if (Swap) {

	    String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "sGift" + ChatColor.WHITE + "] ";

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

	}
    }
}