package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class sGift extends JavaPlugin {

    private final sGiftCommandExecutor executor = new sGiftCommandExecutor(this);
    public static Economy econ = null;
    private static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        getCommand("gift").setExecutor(executor);
        getCommand("trade").setExecutor(executor);
        getCommand("sgift").setExecutor(executor);

        if (!setupEconomy()) {
            if (!this.getConfig().getBoolean("use-vault")) {
                
                log.info("[" + getDescription().getName() + "]" + " Vault Disabled! Trading is Disabled");
                
                this.getConfig().set("enable-trade", false);
                saveConfig();
                
            } else if (getServer().getPluginManager().getPlugin("Vault") == null) {
                
                log.info("[" + getDescription().getName() + "]" + " Vault not Found! Disabling Trading by Default");
                
                this.getConfig().set("use-vault", false);
                this.getConfig().set("enable-trade", false);
                saveConfig();
            
            } else if (this.getConfig().getBoolean("use-vault")) {
                
                log.info("[" + getDescription().getName() + "]" + " Vault Enabled! Trading is Enabled");
            }
        }

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public boolean setupEconomy() {
        if (this.getConfig().getBoolean("use-vault")) {
            if (getServer().getPluginManager().getPlugin("Vault") == null) {
                this.getConfig().set("use-vault", false);
                this.getConfig().set("enable-trade", false);
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
}
