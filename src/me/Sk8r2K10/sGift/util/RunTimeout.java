package me.Sk8r2K10.sGift.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import me.Sk8r2K10.sGift.sGift;
import net.milkbowl.vault.item.Items;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RunTimeout implements Runnable {

	private sGift plugin;
	private Exchange exchange;
	private Player Player;
	private Player victim;
	private ItemStack item;
	private int Price;
	private ItemStack vItem;
	private int Amount;
	private int vAmount;
	private int Task;
	private ResultSet result;
	Logger log = Logger.getLogger("Minecraft");
	String errpre = "[" + ChatColor.RED + "sGift" + ChatColor.WHITE + "] " + ChatColor.RED;

	public RunTimeout(Exchange instance) {
		exchange = instance;
	}

	public RunTimeout(sGift instance, Player player) {
		plugin = instance;
		Player = player;
	}

	public RunTimeout(Exchange inst, sGift instance, Player player, Player Victim, ItemStack Item, int amount, int task) {
		exchange = inst;
		plugin = instance;
		item = Item;
		victim = Victim;
		Player = player;
		Amount = amount;
		Task = task;
	}

	public RunTimeout(Exchange inst, sGift instance, Player player, Player Victim, ItemStack Item, int price, int amount, int task) {

		exchange = inst;
		plugin = instance;
		item = Item;
		victim = Victim;
		Player = player;
		Price = price;
		Amount = amount;
		Task = task;
	}

	public RunTimeout(Exchange inst, sGift instance, Player player, Player Victim, ItemStack Item, ItemStack ItemFromVictim, int amount, int vamount, int task) {
		exchange = inst;
		plugin = instance;
		item = Item;
		victim = Victim;
		Player = player;
		vItem = ItemFromVictim;
		Amount = amount;
		vAmount = vamount;
		Task = task;

	}
	Timeout timeout = null;
	Gift gift = null;
	Trade trade = null;
	Swap swap = null;

	@Override
	public void run() {
		long timeleft = plugin.getConfig().getLong("Options.request-timeout");

		if (plugin.getConfig().getBoolean("Options.use-sql.sqlite") && timeleft != 0) {
			try {

				result = plugin.SQL.scanGiftforAll();

				while (result.next()) {
					if (Player.getName().equals(result.getString("player"))) {
						Player gPlayer = plugin.getServer().getPlayer(result.getString("player"));
						Player gvictim = plugin.getServer().getPlayer(result.getString("Victim"));
						ItemStack gitem = Items.itemByName(result.getString("Item")).toStack();
						int gAmount = result.getInt("amount");
						// Might need these later
						result.close();

						gitem.setAmount(gAmount);

						if (Player == gPlayer && victim == gvictim && gitem == item) {

							exchange.returnGift(Player, victim, item);

							plugin.SQL.removeGift(Player, victim, item, Amount);
							plugin.SQL.removeSender(Player);
						}
					}
				}
				result.close();

				result = plugin.SQL.scanTradeforCancel(Player);

				while (result.next()) {

					Player tPlayer = plugin.getServer().getPlayer(result.getString("player"));
					Player tvictim = plugin.getServer().getPlayer(result.getString("Victim"));
					ItemStack titem = Items.itemByName(result.getString("Item")).toStack();
					int tAmount = result.getInt("amount");
					int tPrice = result.getInt("price");
					// Might need these later
					result.close();

					titem.setAmount(tAmount);

					if (Player == tPlayer && victim == tvictim && titem == item && tPrice == Price) {

						exchange.returnTrade(Player, victim, item, Price);

						plugin.SQL.removeTrade(Player, victim, item, Amount, Price);
						plugin.SQL.removeSender(Player);
					}
				}
				result.close();

				result = plugin.SQL.scanSwapforCancel(Player);

				while (result.next()) {

					Player sPlayer = plugin.getServer().getPlayer(result.getString("player"));
					Player svictim = plugin.getServer().getPlayer(result.getString("Victim"));
					ItemStack sitem = Items.itemByName(result.getString("Item")).toStack();
					ItemStack svItem = Items.itemByName(result.getString("ItemFromVictim")).toStack();
					int sAmount = result.getInt("amount");
					int svAmount = result.getInt("amountFromVictim");
					// Might need these later
					result.close();

					sitem.setAmount(sAmount);
					svItem.setAmount(svAmount);

					if (Player == sPlayer && victim == svictim && sitem == item && svItem == vItem) {

						exchange.returnSwap(Player, victim, item, vItem);

						plugin.SQL.removeSwap(Player, victim, item, Amount, vItem, Amount);
						plugin.SQL.removeSender(Player);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			if (timeleft != 0) {
				for (Gift g : plugin.gifts) {
					for (Timeout o : plugin.timeout) {

						if (g.playerSender == Player) {
							if (g.Victim == victim) {
								if (g.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
									if (g.itemStack == item) {
										gift = g;
										timeout = o;
									}

								}
							}
						}
					}
				}

				if (gift != null) {
					Player player = gift.playerSender;
					Player Victim = gift.Victim;

					if (player.getInventory().firstEmpty() == -1) {

						Location playerloc = player.getLocation();
						player.getWorld().dropItemNaturally(playerloc, gift.itemStack);

						player.sendMessage(errpre + "Gift timed out! Items returned.");
						Victim.sendMessage(errpre + "Gift timed out!");

						plugin.timeout.remove(timeout);
						plugin.gifts.remove(gift);
					} else {

						player.getInventory().addItem(gift.itemStack);
						player.sendMessage(errpre + "Gift timed out! Items returned.");
						victim.sendMessage(errpre + "Gift timed out!");

						plugin.timeout.remove(timeout);
						plugin.gifts.remove(gift);
					}

				} else {

					for (Trade t : plugin.trades) {
						for (Timeout o : plugin.timeout) {

							if (t.playerSender == Player) {
								if (t.Victim == victim) {
									if (t.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
										if (t.itemStack == item) {
											if (t.price == Price) {

												trade = t;
												timeout = o;
											}
										}

									}
								}
							}
						}
					}

					if (trade != null) {
						Player player = trade.playerSender;
						Player Victim = trade.Victim;

						if (player.getInventory().firstEmpty() == -1) {

							Location playerloc = player.getLocation();
							player.getWorld().dropItemNaturally(playerloc, trade.itemStack);

							player.sendMessage(errpre + "Trade timed out! Items returned.");
							Victim.sendMessage(errpre + "Trade timed out!");

							plugin.timeout.remove(timeout);
							plugin.trades.remove(trade);
						} else {

							player.getInventory().addItem(trade.itemStack);
							player.sendMessage(errpre + "Trade timed out! Items returned.");
							victim.sendMessage(errpre + "Trade timed out!");

							plugin.timeout.remove(timeout);
							plugin.trades.remove(trade);
						}

					} else {

						for (Swap s : plugin.swaps) {
							for (Timeout o : plugin.timeout) {

								if (s.playerSender == Player) {
									if (s.Victim == victim) {
										if (s.playerSender.getWorld().getTime() >= (o.time + (timeleft * 20))) {
											if (s.itemSender == item) {
												if (s.itemVictim == vItem) {

													swap = s;
													timeout = o;
												}
											}

										}
									}
								}
							}
						}

						if (swap != null) {
							Player player = swap.playerSender;
							Player Victim = swap.Victim;

							if (player.getInventory().firstEmpty() == -1) {

								Location playerloc = player.getLocation();
								player.getWorld().dropItemNaturally(playerloc, swap.itemSender);

								player.sendMessage(errpre + "Swap timed out! Items returned.");

							} else {

								player.getInventory().addItem(swap.itemSender);
								player.sendMessage(errpre + "Swap timed out! Items returned.");

							}
							if (Victim.getInventory().firstEmpty() == -1) {

								Location vicloc = Victim.getLocation();
								victim.getWorld().dropItemNaturally(vicloc, swap.itemVictim);

								Victim.sendMessage(errpre + "Swap timed out! Items returned.");

							} else {

								Victim.getInventory().addItem(swap.itemVictim);
								Victim.sendMessage(errpre + "Swap timed out! Items returned.");
							}
							plugin.timeout.remove(timeout);
							plugin.swaps.remove(swap);
						}
					}
				}
			}
		}
	}
}
