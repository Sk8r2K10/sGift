package me.Sk8r2K10.sGift.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gift {

    public Gift(Player v, Player pS, ItemStack iS, int i) {
        Victim = v;
        playerSender = pS;
        itemStack = iS;
	ID = i;

    }
    public Player Victim;
    public Player playerSender;
    public ItemStack itemStack;
    public int ID;
    
}
