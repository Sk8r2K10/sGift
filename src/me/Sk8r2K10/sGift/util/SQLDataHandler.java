package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SQLDataHandler {

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

	public SQLDataHandler(sGift instance) {

		plugin = instance;
	}

	public boolean addGift(Player player, Player Victim, ItemStack Item, int amount) throws SQLException {
		
		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		String query = "INSERT INTO `Gift` (`player`, `Victim`, `Item`, `amount`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "')";
		
		if (plugin.SQLt.checkConnection()) {
			
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public ResultSet scanGift(Player player) throws SQLException {

		String Player = player.getName();
		
		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Gift` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Gift` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public ResultSet scanGiftforCancel(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Gift` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Gift` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public boolean removeGift(Player player, Player Victim, ItemStack Item, int amount) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "DELETE FROM `Gift` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "'";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "DELETE FROM `" + plugin.db + "`.`Gift` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "'";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public boolean addTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "INSERT INTO `Trade` (`player`, `Victim`, `Item`, `amount`, `price`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + price + "')";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "INSERT INTO `" + plugin.db + "`.`Trade` (`player`, `Victim`, `Item`, `amount`, `price`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + price + "')";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public ResultSet scanTrade(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Trade` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Trade` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public ResultSet scanTradeforCancel(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Trade` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Trade` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public boolean removeTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

				
		if (plugin.SQLt.checkConnection()) {
			
			String query = "DELETE FROM `Trade` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `price` = '" + price + "'";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "DELETE FROM `" + plugin.db + "`.`Trade` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `price` = '" + price + "'";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public boolean addSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();
		String vItem = Items.itemByStack(ItemFromVictim).getName();

				
		if (plugin.SQLt.checkConnection()) {

			String query = "INSERT INTO `Swap` (`player`, `Victim`, `Item`, `amount`, `ItemFromVictim`, `amountFromVictim`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + vItem + "', '" + amountFromVictim + "')";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "INSERT INTO `" + plugin.db + "`.`Swap` (`player`, `Victim`, `Item`, `amount`, `ItemFromVictim`, `amountFromVictim`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + vItem + "', '" + amountFromVictim + "')";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public ResultSet scanSwap(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Swap` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Swap` WHERE `Victim` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public ResultSet scanSwapforCancel(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Swap` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Swap` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public boolean removeSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();
		String vItem = Items.itemByStack(ItemFromVictim).getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "DELETE FROM `Swap` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `ItemFromVictim` = '" + vItem + "' AND `amountFromVictim` = '" + amountFromVictim + "'";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "DELETE FROM `" + plugin.db + "`.`Swap` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `ItemFromVictim` = '" + vItem + "' AND `amountFromVictim` = '" + amountFromVictim + "'";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public boolean addSender(Player player) throws SQLException {

		String Player = player.getName();
		
		if (plugin.SQLt.checkConnection()) {
			
			String query = "INSERT INTO `Sender` (`player`) VALUES('" + Player + "')";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "INSERT INTO `" + plugin.db + "`.`Sender` (`player`) VALUES('" + Player + "')";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}

	public ResultSet scanSender(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "SELECT * FROM `Sender` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.SQLt.query(query);
			return result;
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "SELECT * FROM `" + plugin.db + "`.`Sender` WHERE `player` = '" + Player + "'";
			ResultSet result = plugin.MSQL.query(query);
			return result;
		} else {
			
			return null;
		}
	}

	public boolean removeSender(Player player) throws SQLException {

		String Player = player.getName();

		if (plugin.SQLt.checkConnection()) {
			
			String query = "DELETE FROM `Sender` WHERE `player` = '" + Player + "'";
			plugin.SQLt.query(query);
		} else if (plugin.MSQL.checkConnection()) {
			
			String query = "DELETE FROM `" + plugin.db + "`.`Sender` WHERE `player` = '" + Player + "'";
			plugin.MSQL.query(query);
		} else {
			
			return false;
		}

		return true;
	}
	
	public boolean giftEmpty() {

		if (plugin.SQLt.checkConnection()) {
			try {
				String query = "SELECT * FROM `Gift`";
				ResultSet result = plugin.SQLt.query(query);
				
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (plugin.MSQL.checkConnection()) {
			try {
				String query = "SELECT * FROM `" + plugin.db + "`.`Gift`";
				ResultSet result = plugin.MSQL.query(query);
				
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			
			return true;
		}
		return true;
	}
	
	public boolean tradeEmpty() {
	
		if (plugin.SQLt.checkConnection()) {
			
			try {
				String query = "SELECT * FROM `Trade` WHERE `";
				ResultSet result = plugin.SQLt.query(query);
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (plugin.MSQL.checkConnection()) {
			
			try {
				String query = "SELECT * FROM `" + plugin.db + "`.`Trade`";
				ResultSet result = plugin.MSQL.query(query);
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			
			return true;
		}
		return true;
	}
	
	public boolean swapEmpty() {

		if (plugin.SQLt.checkConnection()) {
			
			try {
				String query = "SELECT * FROM `Swap`";
				ResultSet result = plugin.SQLt.query(query);
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (plugin.MSQL.checkConnection()) {
						
			try {
				String query = "SELECT * FROM `" + plugin.db + "`.`Swap`";
				ResultSet result = plugin.MSQL.query(query);
				if (result.next()) {
					
					result.close();
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			
			return true;
		}
		return true;
	}
	
	public ResultSet scanGiftforAll() {
		
		String query = "SELECT * FROM `Gift`";
		ResultSet result = plugin.SQLt.query(query);
		
		return result;
	}
}
