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

		player.sendMessage(errpre + "Gift timed out! Item's returned.");
		Victim.sendMessage(errpre + "Gift timed out!");

		plugin.timeout.remove(timeout);
		plugin.gifts.remove(gift);
	    } else {
		
		player.getInventory().addItem(gift.itemStack);
		player.sendMessage(errpre + "Gift timed out! Item's returned.");
		victim.sendMessage(errpre + "Gift timed out!");

		plugin.timeout.remove(timeout);
		plugin.gifts.remove(gift);
	    }

	} else {

	    for (Trade t : plugin.trades) {
		for (Timeout o : plugin.timeout) {

		    if (t.playerSender == o.player) {
			if (t.ID == o.ID) {

			    trade = t;
			    timeout = o;
			}
		    }
		}
	    }

	    if (trade != null) {
		Player player = trade.playerSender;

	    } else {

		for (Swap s : plugin.swaps) {
		    for (Timeout o : plugin.timeout) {

			if (s.playerSender == o.player) {
			    if (s.ID == o.ID) {

				swap = s;
				timeout = o;
			    }

			}
		    }
		}

		if (swap != null) {
		    Player player = swap.playerSender;

		}
	    }
	}
    }
}
