package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RunTimeout implements Runnable {

    private sGift plugin;
    private Player Player;
    private Player victim;
    private ItemStack item;
    private int Price;
    private ItemStack vItem;
    Logger log = Logger.getLogger("Minecraft");
    String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;

    public RunTimeout(sGift instance, Player player, Player Victim, ItemStack Item) {
	plugin = instance;
	item = Item;
	victim = Victim;
	Player = player;
    }

    public RunTimeout(sGift instance, Player player, Player Victim, ItemStack Item, int price) {
	plugin = instance;
	item = Item;
	victim = Victim;
	Player = player;
	Price = price;
    }

    public RunTimeout(sGift instance, Player player, Player Victim, ItemStack Item, ItemStack ItemFromVictim) {
	plugin = instance;
	item = Item;
	victim = Victim;
	Player = player;
	vItem = ItemFromVictim;
    }
    Timeout timeout = null;
    Gift gift = null;
    Trade trade = null;
    Swap swap = null;

    @Override
    public void run() {
	long timeleft = plugin.getConfig().getLong("Options.request-timeout");

	if (timeleft != 0) {
	    for (Gift g : plugin.gifts) {
		for (Timeout o : plugin.timeout) {

		    if (g.playerSender == Player) {
			if (g.Victim == victim) {
			    if (g.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
				if (g.itemStack == item) {
				    gift = g;
				    timeout = o;
				}

			    }
			}
		    }
		}
	    }

	    if (gift != null) {
		Player player = gift.playerSender;
		Player Victim = gift.Victim;

		if (player.getInventory().firstEmpty() == -1) {

		    Location playerloc = player.getLocation();
		    player.getWorld().dropItemNaturally(playerloc, gift.itemStack);

		    player.sendMessage(errpre + "Gift timed out! Items returned.");
		    Victim.sendMessage(errpre + "Gift timed out!");

		    plugin.timeout.remove(timeout);
		    plugin.gifts.remove(gift);
		} else {

		    player.getInventory().addItem(gift.itemStack);
		    player.sendMessage(errpre + "Gift timed out! Items returned.");
		    victim.sendMessage(errpre + "Gift timed out!");

		    plugin.timeout.remove(timeout);
		    plugin.gifts.remove(gift);
		}

	    } else {

		for (Trade t : plugin.trades) {
		    for (Timeout o : plugin.timeout) {

			if (t.playerSender == Player) {
			    if (t.Victim == victim) {
				if (t.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
				    if (t.itemStack == item) {
					if (t.price == Price) {

					    trade = t;
					    timeout = o;
					}
				    }

				}
			    }
			}
		    }
		}

		if (trade != null) {
		    Player player = trade.playerSender;
		    Player Victim = trade.Victim;

		    if (player.getInventory().firstEmpty() == -1) {

			Location playerloc = player.getLocation();
			player.getWorld().dropItemNaturally(playerloc, trade.itemStack);

			player.sendMessage(errpre + "Trade timed out! Items returned.");
			Victim.sendMessage(errpre + "Trade timed out!");

			plugin.timeout.remove(timeout);
			plugin.trades.remove(trade);
		    } else {

			player.getInventory().addItem(trade.itemStack);
			player.sendMessage(errpre + "Trade timed out! Items returned.");
			victim.sendMessage(errpre + "Trade timed out!");

			plugin.timeout.remove(timeout);
			plugin.trades.remove(trade);
		    }

		} else {

		    for (Swap s : plugin.swaps) {
			for (Timeout o : plugin.timeout) {

			    if (s.playerSender == Player) {
				if (s.Victim == victim) {
				    if (s.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
					if (s.itemSender == item) {
					    if (s.itemVictim == vItem) {

						swap = s;
						timeout = o;
					    }
					}

				    }
				}
			    }
			}
		    }

		    if (swap != null) {
			Player player = swap.playerSender;
			Player Victim = swap.Victim;

			if (player.getInventory().firstEmpty() == -1) {

			    Location playerloc = player.getLocation();
			    player.getWorld().dropItemNaturally(playerloc, swap.itemSender);

			    player.sendMessage(errpre + "Swap timed out! Items returned.");
			    
			} else {

			    player.getInventory().addItem(swap.itemSender);
			    player.sendMessage(errpre + "Swap timed out! Items returned.");

			}
			if (Victim.getInventory().firstEmpty() == -1) {
			    
			    Location vicloc = Victim.getLocation();
			    victim.getWorld().dropItemNaturally(vicloc, swap.itemVictim);
			    
			    Victim.sendMessage(errpre + "Swap timed out! Items returned.");
			    
			} else {
			    
			    Victim.getInventory().addItem(swap.itemVictim);
			    Victim.sendMessage(errpre + "Swap timed out! Items returned.");
			}
			plugin.timeout.remove(timeout);
			plugin.swaps.remove(swap);
		    }
		}
	    }
	}
    }
}
