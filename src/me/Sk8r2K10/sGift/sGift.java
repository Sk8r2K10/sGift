package me.Sk8r2K10.sGift;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;
import lib.PatPeter.SQLibrary.MySQL;
import lib.PatPeter.SQLibrary.SQLite;
import me.Sk8r2K10.sGift.Commands.GiftCommand;
import me.Sk8r2K10.sGift.Commands.SwapCommand;
import me.Sk8r2K10.sGift.Commands.TradeCommand;
import me.Sk8r2K10.sGift.Commands.sGiftCommand;
import me.Sk8r2K10.sGift.util.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.item.Items;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class sGift extends JavaPlugin {

	public SQLite SQLt;
	public MySQL MSQL;
	public SQLDataHandler SQL = new SQLDataHandler(this);
	private final TradeCommand trade = new TradeCommand(this);
	private final GiftCommand gift = new GiftCommand(this);
	private final sGiftCommand admin = new sGiftCommand(this);
	private final SwapCommand swap = new SwapCommand(this);
	public final Exchange exc = new Exchange(this);
	public static Economy econ = null;
	public static Permission perms = null;
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public ArrayList<Trade> trades = new ArrayList<Trade>();
	public ArrayList<Sender> senders = new ArrayList<Sender>();
	public ArrayList<Gift> gifts = new ArrayList<Gift>();
	public ArrayList<Swap> swaps = new ArrayList<Swap>();
	public ArrayList<Timeout> timeout = new ArrayList<Timeout>();
	
	public int ID;
	public int task = -1;
	private GameMode GameMode;
	private ResultSet result;
	private String logpre;
	private String host;
	private String port;
	private String user;
	private String pass;
	public String db;

	@Override
	public void onDisable() {

		if (SQLt != null) {

			SQLt.close();
		}
	}

	@Override
	public void onEnable() {
		getCommand("gift").setExecutor(gift);
		getCommand("trade").setExecutor(trade);
		getCommand("sgift").setExecutor(admin);
		getCommand("swap").setExecutor(swap);
		
		this.getServer().getPluginManager().registerEvents(new LeaveEventHandler(this), this);
		this.getServer().getPluginManager().registerEvents(new JoinEventHandler(this), this);
		
		PluginDescriptionFile pdf = getDescription();
		logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

		getConfig().options().copyDefaults(true);
		saveConfig();

		if (getServer().getPluginManager().getPlugin("Vault") != null) {

			log.info(logpre + "Vault found! Enabling plugin.");

			if (!setupEconomy() && getConfig().getBoolean("Features.enable-trade")) {

				log.severe(logpre + "Economy plugin not found. Disabling trading.");

				getConfig().set("Features.enable-trade", false);
				saveConfig();
			} else if (getConfig().getBoolean("Features.enable-trade")) {

				log.info(logpre + "Economy plugin " + econ.getName() + " has been found.");
			}
		} else {

			log.severe(logpre + "Vault not found! Disabling plugin.");

			getServer().getPluginManager().disablePlugin(this);

		}
		if (setupPerms()) {

			log.info(logpre + "Permissions plugin " + getPermissions().getName() + " has been found.");
		} else {

			log.severe(logpre + "Permissions plugin was not Found! Disabling plugin!");

			getServer().getPluginManager().disablePlugin(this);
		}
		if (this.getConfig().getBoolean("Options.use-sql.sqlite")) {

			loadSQLt();

			if (!SQLt.checkConnection()) {

				log.severe(logpre + "Could not connect to SQLite, Please check you have SQLite available!");
				this.getConfig().set("Options.use-sql.sqlite", false);
			}
		} else if (this.getConfig().getBoolean("Options.use-sql.mysql.use")) {
			
			loadMSQL();
			
			if (!MSQL.checkConnection()) {
				
				log.severe(logpre + "Could not connect to SQLite, Please check you have SQLite available!");
				this.getConfig().set("Options.use-sql.mysql.use", false);
			}	
		}
	}

	public void loadSQLt() {

		SQLt = new SQLite(log, "[sGift]", "Exchanges", this.getDataFolder().getPath());

		SQLt.open();
		log.info(logpre + "SQLite Connection established.");

		String Gift = "CREATE TABLE IF NOT EXISTS `Gift` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, PRIMARY KEY(`ID`))";
		String Trade = "CREATE TABLE IF NOT EXISTS `Trade` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, `price` int NOT NULL, PRIMARY KEY(`ID`))";
		String Swap = "CREATE TABLE IF NOT EXISTS `Swap` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, `ItemFromVictim` VARCHAR(255) NOT NULL, `amountfromVictim` int NOT NULL, PRIMARY KEY(`ID`))";
		String Sender = "CREATE TABLE IF NOT EXISTS `Sender` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, PRIMARY KEY(`ID`))";
		String Lost = "CREATE TABLE IF NOT EXISTS `Lost` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, PRIMARY KEY(`ID`))";

		SQLt.createTable(Gift);
		SQLt.createTable(Trade);
		SQLt.createTable(Swap);
		SQLt.createTable(Sender);
		SQLt.createTable(Lost);

		log.info(logpre + "SQLite is initialised.");
	}

	public void loadMSQL() {
		
		host = this.getConfig().getString("Options.use-sql.mysql.host");
		port = this.getConfig().getString("Options.use-sql.mysql.port");
		user = this.getConfig().getString("Options.use-sql.mysql.user");
		pass = this.getConfig().getString("Options.use-sql.mysql.pass");
		db = this.getConfig().getString("Options.use-sql.mysql.dbname");
		
		MSQL = new MySQL(log, "[sGift]", host, port, db, user, pass);
		
		MSQL.open();
		log.info(logpre + "MySQL Connection established.");
		
		String Gift = "CREATE TABLE IF NOT EXISTS `" + db + "`.`Gift` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, PRIMARY KEY(`ID`))";
		String Trade = "CREATE TABLE IF NOT EXISTS `" + db + "`.`Trade` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, `price` int NOT NULL, PRIMARY KEY(`ID`))";
		String Swap = "CREATE TABLE IF NOT EXISTS `" + db + "`.`Swap` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Victim` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, `ItemFromVictim` VARCHAR(255) NOT NULL, `amountfromVictim` int NOT NULL, PRIMARY KEY(`ID`))";
		String Sender = "CREATE TABLE IF NOT EXISTS `" + db + "`.`Sender` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, PRIMARY KEY(`ID`))";
		String Lost = "CREATE TABLE IF NOT EXISTS `" + db + "`.`Lost` (`ID` int AUTO_INCREMENT, `player` VARCHAR(30) NOT NULL, `Item` VARCHAR(255) NOT NULL, `amount` int NOT NULL, PRIMARY KEY(`ID`))";

		MSQL.createTable(Gift);
		MSQL.createTable(Trade);
		MSQL.createTable(Swap);
		MSQL.createTable(Sender);
		MSQL.checkTable(Lost);
		
		log.info(logpre + "MySQL is initialised.");
	}

	public boolean setupEconomy() {
		if (this.getConfig().getBoolean("Features.enable-trade")) {
			if (getServer().getPluginManager().getPlugin("Vault") == null) {
				this.getConfig().set("Features.enable-trade", false);
				saveConfig();
				return false;
			}
			RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
			if (rsp != null) {
				econ = rsp.getProvider();
			} else {
				return false;
			}

		} else {
			return false;
		}
		return econ != null;

	}

	public boolean setupPerms() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);

		if (rsp != null) {

			perms = rsp.getProvider();
			return perms != null;
		} else {

			return false;
		}
	}

	public Economy getEcon() {

		return this.econ;
	}

	public Permission getPermissions() {

		return this.perms;
	}

	public static int getInt(String string) {
		if (isInt(string)) {
			return Integer.parseInt(string);
		} else {
			return 0;
		}
	}

	public static boolean isInt(String string) {
		try {
			Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void noPerms(Player player, String permNode) {

		String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;

		player.sendMessage(errpre + ChatColor.RED + "You don't have permission to use that command!");
		player.sendMessage(errpre + "Permission node: " + ChatColor.YELLOW + permNode + ChatColor.RED + " is required.");
	}

	public boolean getPerms(Player player, String permNode) {

		PluginDescriptionFile pdf = getDescription();
		String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

		if (player == null) {

			log.warning(logpre + "Don't send sGift Commands through Console!");
			return false;
		} else if (player.hasPermission(permNode)) {

			return true;
		} else {

			noPerms(player, permNode);

			return false;

		}
	}

	public boolean rangeIsDisabled() {

		if (getConfig().getInt("Options.max-distance") == 0) {

			return true;
		} else {

			return false;
		}

	}

	public boolean isWithinRange(Location VictimLoc, Location playerLoc) {

		int maxDist = this.getConfig().getInt("Options.max-distance");

		double x = playerLoc.getX() - VictimLoc.getX();
		double y = playerLoc.getY() - VictimLoc.getY();
		double z = playerLoc.getZ() - VictimLoc.getZ();

		double dist = x * x + y * y + z * z;
		if (dist < maxDist * maxDist) {

			return true;
		}
		return false;
	}

	public boolean itemsAreNull(ItemStack Item) {

		PluginDescriptionFile pdf = getDescription();
		String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

		try {
			Items.itemByStack(Item).getName();

		} catch (NullPointerException e) {
			log.warning(logpre + "Vault is either out of date, Or not up to speed!");
			return true;
		}

		return false;
	}

	public boolean itemsAreNull(ItemStack Item, ItemStack ItemFromVictim) {

		PluginDescriptionFile pdf = getDescription();
		String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

		try {
			Items.itemByStack(Item).getName();
			Items.itemByStack(ItemFromVictim).getName();

		} catch (NullPointerException e) {
			log.warning(logpre + "Vault is either out of date, Or not up to speed!");
			log.severe(e.toString());
			return true;
		}

		return false;
	}

	public boolean auto(Player Victim, String cmd, String perm) {
		boolean autogift = this.getConfig().getBoolean("Features.allow-auto.gift");
		boolean autotrade = this.getConfig().getBoolean("Features.allow-auto.trade");
		boolean autoswap = this.getConfig().getBoolean("Features.allow-auto.swap");

		if (cmd.equalsIgnoreCase("gift")) {
			if (autogift) {
				if (Victim.hasPermission(perm)) {
					return true;
				} else {
					return false;
				}
			} else {

				return false;
			}
		} else if (cmd.equalsIgnoreCase("trade")) {
			if (autotrade) {
				if (Victim.hasPermission(perm)) {
					return true;
				} else {
					return false;
				}
			} else {

				return false;
			}
		} else if (cmd.equalsIgnoreCase("swap")) {
			if (autoswap) {
				if (Victim.hasPermission(perm)) {
					return true;
				} else {
					return false;
				}
			} else {

				return false;
			}
		}
		return false;
	}

	public boolean differentWorlds(Player player, Player Victim) {
		if (!this.getConfig().getBoolean("Options.allow-between-worlds")) {
			if (!player.getWorld().getName().equalsIgnoreCase(Victim.getWorld().getName())) {
				if (!player.hasPermission("sgift.override.world")) {
					return true;
				}
			}
		}
		return false;
	}

	public void newTimeout(Player player, Player Victim, ItemStack Item, int amount) {

		long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;

		task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(exc, this, player, Victim, Item, amount, task), outtatime);
	}

	public void newTimeout(Player player, Player Victim, ItemStack Item, int price, int amount) {

		long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;

		task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(exc, this, player, Victim, Item, price, amount, task), outtatime);
	}

	public void newTimeout(Player player, Player Victim, ItemStack Item, ItemStack ItemFromVictim, int amount, int amountFromVictim) {

		long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;
		
		task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(exc, this, player, Victim, Item, ItemFromVictim, amount, amountFromVictim, task), outtatime);
	}

	public boolean alreadyRequested(Player player, Player Victim) {

		String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
	
		if (this.getConfig().getBoolean("Options.use-sql.sqlite") || this.getConfig().getBoolean("Options.use-sql.mysql.use")) {
			try {

				result = SQL.scanGiftforAll();

				while (result.next()) {
					if (player.getName().equals(result.getString("player"))) {
						player.sendMessage(errpre + "You are already Involved in a Gift!");
						player.sendMessage(errpre + ChatColor.GRAY + "Accept, deny or cancel this Gift to make a new one!");
						result.close();
						return true;
					} else if (Victim.getName().equals(result.getString("Victim"))) {
						Victim.sendMessage(errpre + "You are already Involved in a Gift!");
						Victim.sendMessage(errpre + ChatColor.GRAY + "Accept, deny or cancel this Gift to make a new one!");
						result.close();
						return true;						
					}
					if (result.isAfterLast()) {
						result.close();
						return false;
					}
				}
				result.close();
				
				result = SQL.scanTradeforAll();
				
				while (result.next()) {
					if (player.getName().equals(result.getString("player"))) {
						player.sendMessage(errpre + "You are already Involved in a Trade!");
						player.sendMessage(errpre + ChatColor.GRAY + "Accept, deny or cancel this Trade to make a new one!");
						result.close();
						return true;
					} else if (Victim.getName().equals(result.getString("Victim"))) {
						Victim.sendMessage(errpre + "You are already Involved in a Trade!");
						Victim.sendMessage(errpre + ChatColor.GRAY + "Accept, deny or cancel this Trade to make a new one!");
						result.close();
						return true;						
					} 
					if (result.isAfterLast()) {
						result.close();
						return false;
					}
				}
				result.close();
				
				result = SQL.scanSwapforAll();
				
				while (result.next()) {
					if (player.getName().equals(result.getString("player"))) {
						player.sendMessage(errpre + "You are already Involved in a Swap!");
						player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Swap to make a new one!");
						result.close();
						return true;
					} else if (Victim.getName().equals(result.getString("Victim"))) {
						Victim.sendMessage(errpre + "You are already Involved in a Swap!");
						Victim.sendMessage(errpre + ChatColor.GRAY + "Accept, deny or cancel this Swap to make a new one!");
						result.close();
						return true;						
					} 
					if (result.isAfterLast()) {
						result.close();
						return false;
					}
				}
				result.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} else {
			for (Gift g : gifts) {

				if (g.playerSender == player || g.Victim == player) {
					player.sendMessage(errpre + "You are already Involved in a Gift!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Gift to make a new one!");
					
					return true;
				}
				if (g.Victim == Victim || g.playerSender == Victim) {
					player.sendMessage(errpre + "Player is already Involved in a Gift!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Gift to make a new one!");
					
					return true;

				}
			}

			for (Trade t : this.trades) {

				if (t.playerSender == player || t.Victim == player) {
					player.sendMessage(errpre + "You are already Involved in a Trade!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Trade to make a new one!");
					
					return true;
				}
				if (t.Victim == Victim || t.playerSender == Victim) {
					player.sendMessage(errpre + "Player is already Involved in a Trade!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Trade to make a new one!");
					
					return true;
				}
			}

			for (Swap s : swaps) {

				if (s.playerSender == player || s.Victim == player) {
					player.sendMessage(errpre + "You are already Involved in a Swap!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Swap to make a new one!");
					
					return true;
				}
				if (s.Victim == Victim || s.playerSender == Victim) {
					player.sendMessage(errpre + "Player is already Involved in a Swap!");
					player.sendMessage(errpre + ChatColor.GRAY + "Accept or deny this Swap to make a new one!");
					
					return true;
				}
			}
		}
		return false;
	}

	public boolean inCreative(Player player, Player Victim) {
		if (!this.getConfig().getBoolean("Options.allow-creative")) {

			String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;

			if (player.hasPermission("sgift.overrides.creative")) {

				return false;
			}
			if (player.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(errpre + "You cannot exchange items in Creative mode!");
				return true;
			}
			if (Victim.getGameMode() == GameMode.CREATIVE) {
				player.sendMessage(errpre + "The target player is in creative mode, They don't need your items!");
				return true;
			}
		}
		return false;
	}

	public boolean getHand(Player player, ItemStack Item) {

		try {
			Item.setAmount(1);

			return true;
		} catch (NullPointerException e) {
			return false;
		}
	}
}