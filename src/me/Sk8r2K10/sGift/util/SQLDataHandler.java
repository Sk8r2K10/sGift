package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import me.Sk8r2K10.sGift.sGift;
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
	
	String query = "INSERT INTO `Gift` (`player`, `Victim`, `Item`, `amount`) VALUES('" + player.getName() + "', '" + Victim.getName() + "', '" + Item + "', '" + amount + "')";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public ResultSet scanGift(Player player) throws SQLException {
	
	String query = "SELECT * FROM `Gift` WHERE `Victim` = '" + player.getName() + "'";
	ResultSet result = plugin.SQLt.query(query);
		
	return result;
    }
    
    public boolean removeGift(Player player, Player Victim, ItemStack Item, int amount) throws SQLException {
	
	String query = "DELETE FROM `Gift` WHERE `player` = '" + player.getName() + "' AND `Victim` = '" + Victim.getName() + "' AND `Item` = '" + Item + "' AND `amount` = '" + amount + "'";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public boolean addTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {
	
	String query = "INSERT INTO `Trade` (`player`, `Victim`, `Item`, `amount`, `price`) VALUES('" + player.getName() + "', '" + Victim.getName() + "', '" + Item + "', '" + amount + "', '" + price + "')";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public ResultSet scanTrade(Player player) throws SQLException {
	
	String query = "SELECT * FROM `Trade` WHERE `Victim` = '" + player.getName() + "'";
	ResultSet result = plugin.SQLt.query(query);
	
	return result;
    }
    
    public boolean removeTrade(Player player, Player Victim, ItemStack Item, int amount, int price) throws SQLException {
	
	String query = "DELETE FROM `Trade` WHERE `player` = '" + player.getName() + "' AND `Victim` = '" + Victim.getName() + "' AND `Item` = '" + Item + "' AND `amount` = '" + amount + "' AND `price` = '" + price + "'";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public boolean addSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {
	
	String query = "INSERT INTO `Swap` (`player`, `Victim`, `Item`, `amount`, `ItemFromVictim`, `amountFromVictim`) VALUES('" + player.getName() + "', '" + Victim.getName() + "', '" + Item + "', '" + amount + "', '" + ItemFromVictim + "', '" + amountFromVictim + "')";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public ResultSet scanSwap(Player player) throws SQLException {
	
	String query = "SELECT * FROM `Swap` WHERE `Victim` = '" + player.getName() + "'";
	ResultSet result = plugin.SQLt.query(query);
	
	return result;
    }
    
    public boolean removeSwap(Player player, Player Victim, ItemStack Item, int amount, ItemStack ItemFromVictim, int amountFromVictim) throws SQLException {
	
	String query = "DELETE FROM `Swap` WHERE `player` = '" + player.getName() + "' AND `Victim` = '" + Victim.getName() + "' AND `Item` = '" + Item + "' AND `amount` = '" + amount + "' AND `ItemFromVictim` = '" + ItemFromVictim + "' AND `amountFromVictim` = '" + amountFromVictim +"'";
	plugin.SQLt.query(query);
	
	return true;
    }
    
     public boolean addSender(Player player) throws SQLException {
	
	String query = "INSERT INTO `Sender` (`player`) VALUES('" + player.getName() + "')";
	plugin.SQLt.query(query);
	
	return true;
    }
    
    public ResultSet scanSender(Player player) throws SQLException {
	
	String query = "SELECT * FROM `Sender` WHERE `player` = '" + player.getName() + "'";
	ResultSet result = plugin.SQLt.query(query);
	
	return result;
    }
    
    public boolean removeSender(Player player) throws SQLException {
	
	String query = "DELETE FROM `Sender` WHERE `player` = '" + player.getName() + "'";
	plugin.SQLt.query(query);
	
	return true;
    }
}
