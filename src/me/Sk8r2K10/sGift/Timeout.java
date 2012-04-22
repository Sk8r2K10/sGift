package me.Sk8r2K10.sGift;

import org.bukkit.entity.Player;

public class Timeout {
    
    public Timeout(Gift g, Player p, int i, long t) {	
	gift = g;
	player = p;
	ID = i;
	time = t;
    }
    
    public Timeout(Swap s, Player p, int i, long t) {
	swap = s;
	player = p;
	ID = i;
	time = t;
    }
    
    public Timeout(Trade t, Player p, int i, long o) {
	trade = t;
	player = p;
	ID = i;
	time = o;
    }
    
    public Gift gift;
    public Swap swap;
    public Trade trade;
    public Player player;
    public int ID;
    public long time;
}
