package me.Sk8r2K10.sGift;

import java.util.ArrayList;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class sGift extends JavaPlugin {
    
    private final TradeCommand trade = new TradeCommand(this);
    private final GiftCommand gift = new GiftCommand(this);
    private final sGiftCommand admin = new sGiftCommand(this);
    
    public static Economy econ = null;
    public static Permission perms = null;
    private static final Logger log = Logger.getLogger("Minecraft");
    
    public ArrayList<Trade> trades = new ArrayList<Trade>();
    public ArrayList<Sender> senders = new ArrayList<Sender>();
    public ArrayList<Gift> gifts = new ArrayList<Gift>();

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        getCommand("gift").setExecutor(gift);
        getCommand("trade").setExecutor(trade);
        getCommand("sgift").setExecutor(admin);
        
        PluginDescriptionFile pdf = getDescription();
        String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";

        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            
            log.info(logpre + "Vault found! Enabling plugin.");
            
            if (!setupEconomy() && getConfig().getBoolean("Features.enable-trade")) {
                
                log.severe(logpre + "Economy plugin not found. Disabling trading."); 
                
                getConfig().set("Features.enable-trade", false);
                saveConfig();
            } else if (getConfig().getBoolean("Features.enable-trade")) {
                
                log.info(logpre + "Economy plugin " + econ.getName() + " has been found. Trading will remain enabled.");
                
            }
            
        } else {
            
            log.severe(logpre + "Vault not found! Disabling plugin.");
            
            getServer().getPluginManager().disablePlugin(this);
            
        }
	if (setupPerms()) {
	    
	    log.info(logpre + "Permissions plugin " + getPermissions().getName() + " has been found. Plugin will remain enabled.");
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
	
	if (player.hasPermission(permNode)) {
	    
	    return true;
	    
	} else {
	    
	    noPerms(player, permNode);	    
	    
	    return false;
	    
	}
    }
}
