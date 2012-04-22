package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Trade {

    public Trade(Player v, Player pS, ItemStack iS, int p, int i) {
        Victim = v;
        playerSender = pS;
        itemStack = iS;
        price = p;
	ID = i;

    }
    public Player Victim;
    public Player playerSender;
    public ItemStack itemStack;
    public int price;
    public int ID;
    
}
