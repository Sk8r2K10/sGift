package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveEventHandler implements Listener{
	
	private sGift plugin = new sGift();
	private Player player;
	private ItemStack item;
	private ItemStack vItem;
	private int amount;
	private ResultSet result;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave (PlayerQuitEvent e) {
		try {
			result = plugin.SQL.scanSender(e.getPlayer());
			
			player = plugin.getServer().getPlayer(result.getString("player"));
			if (result.getString("player").equals(e.getPlayer().getName())) {
				result.close();
				
				result = plugin.SQL.scanGift(player);
				
				if (result.getString("player").equals(player.getName())) {
					
					item = Items.itemByName(result.getString("Item")).toStack();
					amount = result.getInt("amount");
					
					result.close();
					
					item.setAmount(amount);
					
					plugin.SQL.addLost(player, item, amount);
				} else {
					result.close();
				}
				
				
				result = plugin.SQL.scanSwap(player);
				
				if (result.getString("player").equals(player.getName())) {
					
					item = Items.itemByName(result.getString("Item")).toStack();
					amount = result.getInt("amount");
					
					result.close();
					
					item.setAmount(amount);
					
					plugin.SQL.addLost(player, item, amount);
					
				} else if (result.getString("Victim").equals(player.getName())) {
					
					item = Items.itemByName(result.getString("ItemFromVictim")).toStack();
					amount = result.getInt("amountFromVictim");
					
					result.close();
					
					item.setAmount(amount);
					
					plugin.SQL.addLost(player, item, amount);					
				} else {
					result.close();
				}
				
				result = plugin.SQL.scanTrade(player);
				
				if (result.getString("player").equals(player.getName())) {
					
					item = Items.itemByName(result.getString("Item")).toStack();
					amount = result.getInt("amount");
					
					result.close();
					
					item.setAmount(amount);
					
					plugin.SQL.addLost(player, item, amount);
					
				} else {
					result.close();
				}
				
				// Add to "Left during transaction" table
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
