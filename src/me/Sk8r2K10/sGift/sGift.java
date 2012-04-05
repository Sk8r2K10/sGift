package me.Sk8r2K10.sGift;

import java.util.ArrayList;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class sGift extends JavaPlugin {
    
    private final TradeCommand trade = new TradeCommand(this);
    private final GiftCommand gift = new GiftCommand(this);
    private final sGiftCommand admin = new sGiftCommand(this);
    
    public static Economy econ = null;
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

        if (!setupEconomy()) {
            if (!this.getConfig().getBoolean("use-vault")) {
                
                log.info(logpre + " Vault Disabled! Trading is Disabled");
                
                this.getConfig().set("Features.enable-trade", false);
                saveConfig();
                
            } else if (getServer().getPluginManager().getPlugin("Vault") == null) {
                
                log.info(logpre + " Vault not Found! Disabling Trading by Default");
                
                this.getConfig().set("use-vault", false);
                this.getConfig().set("Features.enable-trade", false);
                saveConfig();
            
            } else if (this.getConfig().getBoolean("use-vault")) {
                
                log.info(logpre + " Vault Enabled! Trading is Enabled");
            }
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public boolean setupEconomy() {
        if (this.getConfig().getBoolean("use-vault")) {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                this.getConfig().set("use-vault", false);
                this.getConfig().set("Features.enable-trade", false);
                saveConfig();
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                econ = rsp.getProvider();
            }

        } else {
            return false;
        }
        return econ != null;

    }

    public Economy getEcon() {

        return this.econ;
    }

    public boolean inventoryContains(Inventory inventory, ItemStack item) {
        int count = 0;
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() == item.getType() && items[i].getDurability() == item.getDurability()) {
                count += items[i].getAmount();
            }
            if (count >= item.getAmount()) {
                return true;
            }
        }
        return false;
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
    
    public void hasPerms(Player player, String commandLabel) {
        
        String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
        
        if (commandLabel.equalsIgnoreCase("trade")) {
            if (!player.hasPermission("sgift.trade")) {
                
                player.sendMessage(errpre + "You don't have permission for /trade!");
                player.sendMessage(errpre + "Node required: sgift.trade");
                
            } else if (!player.hasPermission("sgift.trade.help")) {
                
                player.sendMessage(errpre + "You don't have permission for /trade help!");
                player.sendMessage(errpre + "Node required: sgift.trade.help");
                
            }
            
        }
        if (commandLabel.equalsIgnoreCase("gift")) {
            if (!player.hasPermission("sgift.gift")) {
                
                player.sendMessage(errpre + "You don't have permission for /gift!");
                player.sendMessage(errpre + "Node required: sgift.gift");
            } else if (!player.hasPermission("sgift.gift.help")) {
                
                player.sendMessage(errpre + "You don't have permission for /gift help!");
                player.sendMessage(errpre + "Node required: sgift.gift.help");
            }
            
        } 
        if (commandLabel.equalsIgnoreCase("sgift")) {
            if (!player.hasPermission("sgift.admin")) {
                
                player.sendMessage(errpre + "You don't have permission for sGift admin commands!");
                player.sendMessage(errpre + "Node required: sgift.admin");
            } else  if (!player.hasPermission("sgift.set")) {
                
                player.sendMessage(errpre + "You don't have permission for /sgift set!");
                player.sendMessage(errpre + "Node required: sgift.set");
            } else if (!player.hasPermission("sgift.halt")) {
                
                player.sendMessage(errpre + "You don't have permission for /sgift hakt!");
                player.sendMessage(errpre + "Node required: sgift.halt");
            }
        }
        
        
    }
}
