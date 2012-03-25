package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Trade {

    public Trade(Player v, Player pS, ItemStack iS, int p) {
        Victim = v;
        playerSender = pS;
        itemStack = iS;
        price = p;

    }
    public Player Victim;
    public Player playerSender;
    public ItemStack itemStack;
    public int price;
}
