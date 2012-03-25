package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Gift {

    public Gift(Player v, Player pS, ItemStack iS) {
        Victim = v;
        playerSender = pS;
        itemStack = iS;

    }
    public Player Victim;
    public Player playerSender;
    public ItemStack itemStack;
}
