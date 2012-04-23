package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class RunTimeout implements Runnable {

    private sGift plugin;
    Logger log = Logger.getLogger("Minecraft");
    String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;

    public RunTimeout(sGift instance) {
	plugin = instance;
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

		if (g.playerSender == o.player) {
		    if (g.ID == o.ID) {
			if (g.playerSender.getWorld().getTime() == (o.time + (timeleft * 20))) {
		    gift = g;
			 timeout = o;
			}
		    }
		}
	    }
	}

	if (gift != null) {
	    Player player = gift.playerSender;
	    Player victim = gift.Victim;

	    if (player.getInventory().firstEmpty() == -1) {

		Location playerloc = player.getLocation();
		player.getWorld().dropItemNaturally(playerloc, gift.itemStack);

		player.sendMessage(errpre + "Gift timed out! Item's returned.");
		victim.sendMessage(errpre + "Gift timed out!");
		
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
