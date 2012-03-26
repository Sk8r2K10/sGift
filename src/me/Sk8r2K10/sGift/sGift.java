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
        config.options().header("sGift Configuration File");
        config.addDefault("Help.Gift.Gift", ChatColor.GREEN + "/gift <PlayerName> <ItemID:#> <Amount>" + ChatColor.GRAY + " - Gifts a player a block");
        config.addDefault("Help.Gift.Example", ChatColor.GRAY + "example: /gift Bob log:redwood 1337");
        config.addDefault("Help.Gift.Accept" , ChatColor.GREEN + "/gift accept" + ChatColor.GRAY + " - Accepts a Pending Gift.");
        config.addDefault("Help.Gift.Deny", ChatColor.GREEN + "/gift deny" + ChatColor.GRAY + " - Denies a pending Gift.");
        config.addDefault("Help.Gift.Cancel", ChatColor.GREEN + "/gift cancel" + ChatColor.GRAY + " - Cancels a gift in progress");
        config.addDefault("Help.Gift.Stop", ChatColor.GREEN + "/gift stop" + ChatColor.GRAY + " - Stops all gifts in progress");
        config.addDefault("Help.Gift.Help", ChatColor.GREEN + "/gift help" + ChatColor.GRAY + " - Brings up this Menu.");
        config.addDefault("Help.Trade.Trade", ChatColor.GOLD + "/trade <PlayerName> <ItemID:#> <Amount> <Price>" + ChatColor.GRAY + " - Trades with a player");
        config.addDefault("Help.Trade.Example", ChatColor.GRAY + "example: /trade Bob log:redwood 1337 9001");
        config.addDefault("Help.Trade.Accept", ChatColor.GOLD + "/trade accept" + ChatColor.GRAY + " - Accepts a Pending Trade.");
        config.addDefault("Help.Trade.Deny", ChatColor.GOLD + "/trade deny" + ChatColor.GRAY + " - Denies a pending Trade.");
        config.addDefault("Help.Trade.Cancel", ChatColor.GOLD + "/trade cancel" + ChatColor.GRAY + " - Cancels a trade in progress");
        config.addDefault("Help.Trade.Stop", ChatColor.GOLD + "/trade stop" + ChatColor.GRAY + " - Stops all trades in progress");
        config.addDefault("Help.Trade.Help", ChatColor.GOLD + "/trade help" + ChatColor.GRAY + " - Brings up this Menu.");
        config.options().copyDefaults(true);
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
