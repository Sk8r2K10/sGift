package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Swap {
    
    public Swap(Player v, Player pS, ItemStack iS, ItemStack iS2) {
        Victim = v;
        playerSender = pS;
        itemSender = iS;
	itemVictim = iS2;
	

    }
    public Player Victim;
    public Player playerSender;
    public ItemStack itemSender;
    public ItemStack itemVictim;
    
}
