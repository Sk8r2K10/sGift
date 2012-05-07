package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
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
	private int amount;
	private ResultSet result;
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLeave (PlayerQuitEvent e) {
		try {
			result = plugin.SQL.scanSender(e.getPlayer());
			
			player = plugin.getServer().getPlayer(result.getString("player"));
			if (result.getString("player").equals(e.getPlayer().getName())) {
				result.close();
				
				result = plugin.SQL.scanGift(player);
				
				
				// Add to "Left during transaction" table
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
