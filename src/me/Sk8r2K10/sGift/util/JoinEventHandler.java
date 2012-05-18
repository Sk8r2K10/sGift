package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinEventHandler implements Listener {

    private sGift plugin;
    private Player player;
    private ItemStack Item;
    private int amount;
    private ResultSet result;
    private String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;
    private final static Logger log = Logger.getLogger("Minecraft");

    public JoinEventHandler(sGift instance) {

        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent e) {

        if (!plugin.getConfig().getBoolean("Features.cancel-exchanges-on-leave")) {

            return;
        }
        if (!plugin.getConfig().getBoolean("Options.use-sql.sqlite") && !plugin.getConfig().getBoolean("Options.use-sql.mysql.use")) {

            return;
        }

        try {
            result = plugin.SQL.scanLost(e.getPlayer());

            if (!result.next()) {
                result.close();
                return;
            }
            if (result.getString("player").equals(e.getPlayer().getName())) {
                player = Bukkit.getPlayer(result.getString("player"));
                Item = Items.itemByName(result.getString("Item")).toStack();
                amount = result.getInt("amount");

                result.close();

                Item.setAmount(amount);

                this.refundItems(player, Item);

                plugin.SQL.removeLost(player, Item, amount);
            } else {
                result.close();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void refundItems(Player player, ItemStack Item) {

        player.sendMessage(errpre + "You were involved in an exchange, Then left.");
        player.sendMessage(errpre + "Returning your items.");

        if (player.getInventory().firstEmpty() == -1) {

            Location playerLoc = player.getLocation();

            playerLoc.getWorld().dropItemNaturally(playerLoc, Item);
            player.sendMessage(errpre + "No room in your inventory!");
            player.sendMessage(errpre + "Dropped items at your feet.");
        } else {

            player.getInventory().addItem(Item);
        }
    }
}
