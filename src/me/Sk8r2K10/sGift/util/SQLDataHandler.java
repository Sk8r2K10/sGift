package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SQLDataHandler {

	private String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
	private sGift plugin;

	public SQLDataHandler(sGift instance) {

		plugin = instance;
	}

	public boolean addGift(Player player, Player Victim, ItemStack Item, int amount) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		String query = "INSERT INTO `Gift` (`player`, `Victim`, `Item`, `amount`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "')";
		plugin.SQLt.query(query);

		return true;
	}

	public ResultSet scanGift(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Gift` WHERE `Victim` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public ResultSet scanGiftforCancel(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Gift` WHERE `player` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public boolean removeGift(Player player, Player Victim, ItemStack Item, int amount) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		String query = "DELETE FROM `Gift` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "'";
		plugin.SQLt.query(query);

		return true;
	}

	public boolean addTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		String query = "INSERT INTO `Trade` (`player`, `Victim`, `Item`, `amount`, `price`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + price + "')";
		plugin.SQLt.query(query);

		return true;
	}

	public ResultSet scanTrade(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Trade` WHERE `Victim` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public ResultSet scanTradeforCancel(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Trade` WHERE `player` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public boolean removeTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();

		String query = "DELETE FROM `Trade` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `price` = '" + price + "'";
		plugin.SQLt.query(query);

		return true;
	}

	public boolean addSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();
		String vItem = Items.itemByStack(ItemFromVictim).getName();

		String query = "INSERT INTO `Swap` (`player`, `Victim`, `Item`, `amount`, `ItemFromVictim`, `amountFromVictim`) VALUES('" + Player + "', '" + victim + "', '" + item + "', '" + amount + "', '" + vItem + "', '" + amountFromVictim + "')";
		plugin.SQLt.query(query);

		return true;
	}

	public ResultSet scanSwap(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Swap` WHERE `Victim` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public ResultSet scanSwapforCancel(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Swap` WHERE `player` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public boolean removeSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {

		String Player = player.getName();
		String victim = Victim.getName();
		String item = Items.itemByStack(Item).getName();
		String vItem = Items.itemByStack(ItemFromVictim).getName();

		String query = "DELETE FROM `Swap` WHERE `player` = '" + Player + "' AND `Victim` = '" + victim + "' AND `Item` = '" + item + "' AND `amount` = '" + amount + "' AND `ItemFromVictim` = '" + vItem + "' AND `amountFromVictim` = '" + amountFromVictim + "'";
		plugin.SQLt.query(query);

		return true;
	}

	public boolean addSender(Player player) throws SQLException {

		String Player = player.getName();

		String query = "INSERT INTO `Sender` (`player`) VALUES('" + Player + "')";
		plugin.SQLt.query(query);

		return true;
	}

	public ResultSet scanSender(Player player) throws SQLException {

		String Player = player.getName();

		String query = "SELECT * FROM `Sender` WHERE `player` = '" + Player + "'";
		ResultSet result = plugin.SQLt.query(query);

		return result;
	}

	public boolean removeSender(Player player) throws SQLException {

		String Player = player.getName();

		String query = "DELETE FROM `Sender` WHERE `player` = '" + Player + "'";
		plugin.SQLt.query(query);

		return true;
	}
}
