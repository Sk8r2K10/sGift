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
        
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Vault not Found! Disabling Plugin.", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
        }
        final FileConfiguration config = this.getConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            econ = rsp.getProvider();
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
