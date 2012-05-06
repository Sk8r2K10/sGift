package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveEventHandler implements Listener{
	
	private sGift plugin;
	
	public LeaveEventHandler(sGift instance) {
		
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerLeave (PlayerQuitEvent e) {
		try {
			ResultSet result = plugin.SQLite.scanSender(e.getPlayer());
			
			if (result.getString("player").equals(e.getPlayer().getName())) {
				result.close();
				// Add to "Left during transaction" table
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}
