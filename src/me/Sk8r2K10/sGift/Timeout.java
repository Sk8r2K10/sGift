package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;

public class Timeout {
    
    public Timeout(Gift g, Player p, int i) {	
	gift = g;
	player = p;
	ID = i;
    }
    
    public Timeout(Swap s, Player p, int i) {
	swap = s;
	player = p;
	ID = i;
    }
    
    public Timeout(Trade t, Player p, int i) {
	trade = t;
	player = p;
	ID = i;
    }
    
    public Gift gift;
    public Swap swap;
    public Trade trade;
    public Player player;
    public int ID;
}
