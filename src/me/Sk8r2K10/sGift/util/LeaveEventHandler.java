package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class LeaveEventHandler implements Listener {

	private sGift plugin;
	private Player player;
	private Player Victim;
	private ItemStack item;
	private ItemStack vItem;
	private int amount;
	private int vAmount;
	private int price;
	private int i;
	private ResultSet result;
	private String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
	private static final Logger log = Logger.getLogger("Minecraft");

	public LeaveEventHandler(sGift instance) {

		plugin = instance;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (!plugin.getConfig().getBoolean("Features.cancel-exchanges-on-leave")) {

			return;
		}
		if (!plugin.getConfig().getBoolean("Options.use-sql.sqlite") && !plugin.getConfig().getBoolean("Options.use-sql.mysql.use")) {

			return;
		}

		try {
			result = plugin.SQL.scanSender(e.getPlayer());
			if (!result.next()) {
				result.close();
				return;
			} else {
				player = plugin.getServer().getPlayer(result.getString("player"));
				if (result.getString("player").equals(e.getPlayer().getName())) {
					result.close();

					result = plugin.SQL.scanGiftforAll();

					while (result.next()) {
						if (result.getString("player").equals(player.getName())) {

							Victim = Bukkit.getPlayer(result.getString("Victim"));
							item = Items.itemByName(result.getString("Item")).toStack();
							amount = result.getInt("amount");

							if (Victim == null) {
								String vic = result.getString("Victim");
								
								result.close();
								
								item.setAmount(amount);

								plugin.SQL.addLost(player, item, amount);
								plugin.SQL.removeGift(player, Bukkit.getOfflinePlayer(vic), item, amount);
								plugin.SQL.removeSender(player);
								return;
							} else {
								result.close();

								item.setAmount(amount);

								Victim.sendMessage(errpre + "Gift ended, Player sending items left the game.");

								plugin.SQL.addLost(player, item, amount);
								plugin.SQL.removeGift(player, Victim, item, amount);
								plugin.SQL.removeSender(player);

								return;
							}

						} else {
							
							result.close();
						}
					}

					result = plugin.SQL.scanSwapforAll();
					
					while (result.next()) {
						if (result.getString("player").equals(player.getName())) {
							
							player = Bukkit.getPlayer(result.getString("player"));
							Victim = Bukkit.getPlayer(result.getString("Victim"));
							item = Items.itemByName(result.getString("Item")).toStack();
							vItem = Items.itemByName(result.getString("ItemFromVictim")).toStack();
							amount = result.getInt("amount");
							vAmount = result.getInt("amountFromVictim");

							result.close();

							item.setAmount(amount);
							vItem.setAmount(vAmount);

							this.stopSwap(Victim, vItem);

							plugin.SQL.addLost(player, item, amount);
							plugin.SQL.removeSwap(player, Victim, item, amount, vItem, vAmount);
							
							return;
						} else if (result.getString("Victim").equals(player.getName())) {
							
							player = Bukkit.getPlayer(result.getString("player"));
							Victim = Bukkit.getPlayer(result.getString("Victim"));
							item = Items.itemByName(result.getString("Item")).toStack();
							vItem = Items.itemByName(result.getString("ItemFromVictim")).toStack();
							amount = result.getInt("amount");
							vAmount = result.getInt("amountFromVictim");
							
							result.close();

							item.setAmount(amount);
							vItem.setAmount(vAmount);

							this.stopSwap(player, item);

							plugin.SQL.addLost(Victim, vItem, vAmount);
							plugin.SQL.removeSwap(player, Victim, item, amount, vItem, vAmount);
							
							return;
						} else {
							
							result.close();
						}
					}
					
					result = plugin.SQL.scanTradeforAll();
					
					while (result.next()) {
						if (result.getString("player").equals(player.getName())) {

							Victim = Bukkit.getPlayer(result.getString("Victim"));
							item = Items.itemByName(result.getString("Item")).toStack();
							amount = result.getInt("amount");
							price = result.getInt("price");

							if (Victim == null) {
								String vic = result.getString("Victim");

								result.close();

								item.setAmount(amount);

								plugin.SQL.addLost(player, item, amount);
								plugin.SQL.removeTrade(player, Bukkit.getOfflinePlayer(vic), item, amount, price);
								plugin.SQL.removeSender(player);
								return; 
							} else {
								result.close();

								item.setAmount(amount);

								Victim.sendMessage(errpre + "Trade ended, Player offering items left the game.");

								plugin.SQL.addLost(player, item, amount);
								plugin.SQL.removeTrade(player, Victim, item, amount, price);
								plugin.SQL.removeSender(player);
								return;
							}
						} else {
							result.close();
							return;
						}
					}
				}
			}
			result.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void stopSwap(Player Victim, ItemStack vItem) {

		Victim.sendMessage(errpre + "Other player left the swap, Returning your items.");

		if (Victim.getInventory().firstEmpty() == -1) {

			Location VicLoc = Victim.getLocation();

			VicLoc.getWorld().dropItemNaturally(VicLoc, vItem);
			Victim.sendMessage(errpre + "No room in your inventory!");
			Victim.sendMessage(errpre + "Dropped items at your feet.");
		} else {

			Victim.getInventory().addItem(vItem);
		}
	}
}
