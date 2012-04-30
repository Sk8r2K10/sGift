package me.Sk8r2K10.sGift;

import java.util.ArrayList;
import java.util.logging.Logger;
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

    private final TradeCommand trade = new TradeCommand(this);
    private final GiftCommand gift = new GiftCommand(this);
    private final sGiftCommand admin = new sGiftCommand(this);
    private final SwapCommand swap = new SwapCommand(this);
    
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

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
	getCommand("gift").setExecutor(gift);
	getCommand("trade").setExecutor(trade);
	getCommand("sgift").setExecutor(admin);
	getCommand("swap").setExecutor(swap);

	PluginDescriptionFile pdf = getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

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

	getConfig().options().copyDefaults(true);
	saveConfig();
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

    public void newTimeout(Player player, Player Victim, ItemStack Item) {

	long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;

	task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(this, player, Victim, Item), outtatime);
    }

    public void newTimeout(Player player, Player Victim, ItemStack Item, int price) {

	long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;

	task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(this, player, Victim, Item, price), outtatime);
    }

    public void newTimeout(Player player, Player Victim, ItemStack Item, ItemStack ItemFromVictim) {

	long outtatime = this.getConfig().getInt("Options.request-timeout") * 20;

	task = this.getServer().getScheduler().scheduleSyncDelayedTask(this, new RunTimeout(this, player, Victim, Item, ItemFromVictim), outtatime);
    }

    public void stop(Player player, Player victim, String type) {

	PluginDescriptionFile pdf = getDescription();
	String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

	getServer().getScheduler().cancelTask(task);
	log.info(logpre + player.getName() + "'s " + type + " request to " + victim.getName() + " timed out.");
    }

    public boolean alreadyRequested(Player player, Player Victim) {
	
	String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
	
	for (Gift g : gifts) {

	    if (g.playerSender == player || g.Victim == player) {
		player.sendMessage(errpre + "You are already Involved in a Gift!");
		
		return true;
	    }
	    if (g.Victim == Victim || g.playerSender == Victim) {
		player.sendMessage(errpre + "Player is already Involved in a Gift!");
		
		return true;

	    }
	}

	for (Trade t : trades) {

	    if (t.playerSender == player || t.Victim == player) {
		player.sendMessage(errpre + "You are already Involved in a Trade!");
				
		return true;
	    }
	    if (t.Victim == Victim || t.playerSender == Victim) {
		player.sendMessage(errpre + "Player is already Involved in a Trade!");
		
		return true;
	    }
	}

	for (Swap s : swaps) {

	    if (s.playerSender == player || s.Victim == player) {
		player.sendMessage(errpre + "You are already Involved in a Swap!");
		
		return true;
	    }
	    if (s.Victim == Victim || s.playerSender == Victim) {
		player.sendMessage(errpre + "Player is already Involved in a Swap!");
		
		return true;
	    }
	}
	return false;
    }
    
    public boolean inCreative(Player player, Player Victim) {
	if (this.getConfig().getBoolean("Options.allow-creative")) {
	    
	    if (player.getGameMode() == GameMode.CREATIVE) {
		
		return true;
	    }
	    if (Victim.getGameMode() == GameMode.CREATIVE) {
		
		return true;
	    }
	}
	return false;
    }
}