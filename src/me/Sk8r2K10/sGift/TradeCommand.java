package me.Sk8r2K10.sGift;

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;

public class TradeCommand implements CommandExecutor {

    private sGift plugin;
    
    Player player = null;
    String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "sGift" + ChatColor.WHITE + "] ";
    Logger log = Logger.getLogger("Minecraft");

    public TradeCommand(sGift instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (sender instanceof Player) {

            player = (Player) sender;
            
        } else {

            player = null;
        }

        if (commandLabel.equalsIgnoreCase("trade") && sender.hasPermission("sgift.trade")) {
            
            PluginDescriptionFile pdf = plugin.getDescription();
            String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";            
            
            if (plugin.getConfig().getBoolean("Features.enable-trade")) {
                if (player != null) {
                    if (args.length == 1) {
                        if (args[0].equalsIgnoreCase("help") && player.hasPermission("sgift.trade.help")) {

                            player.sendMessage(ChatColor.DARK_GRAY + "---------------[" + ChatColor.GOLD + "sGift - Trade Help Menu" + ChatColor.DARK_GRAY + "]----------------");
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Trade"));
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Example"));
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Accept"));
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Deny"));
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Cancel"));
                            player.sendMessage(plugin.getConfig().getString("Help.Trade.Help"));

                            if (player.hasPermission("sgift.admin")) {

                                player.sendMessage(plugin.getConfig().getString("Help.Trade.Stop"));
                            }

                        } else if (args[0].equalsIgnoreCase("accept")) {

                            Trade trade = null;
                            Sender Sender1 = null;

                            for (Trade t : plugin.trades) {

                                if (t.Victim == player) {

                                    trade = t;

                                    for (Sender s : plugin.senders) {

                                        if (s.Sender == t.playerSender) {

                                            Sender1 = s;
                                        }
                                    }
                                }
                            }

                            if (trade == null) {

                                player.sendMessage(prefix + ChatColor.RED + "No Trades to accept!");
                            } else {

                                Player playerSendingItems = trade.playerSender;
                                Player Victim = trade.Victim;
                                ItemStack items = trade.itemStack;
                                int price = trade.price;

                                if (player.getInventory().firstEmpty() == -1) {
                                    Location playerloc = player.getLocation();
                                    player.getWorld().dropItemNaturally(playerloc, items);
                                    player.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

                                } else {
                                    player.getInventory().addItem(items);
                                }


                                plugin.getEcon().withdrawPlayer(Victim.getName(), price);
                                plugin.getEcon().depositPlayer(playerSendingItems.getName(), price);

                                playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.name(items) + ChatColor.WHITE + " Delivered to " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
                                Victim.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.name(items) + ChatColor.WHITE + " Recieved from " + ChatColor.YELLOW + playerSendingItems.getDisplayName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + plugin.getEcon().currencyNameSingular() + "(s)");
                                log.info(logpre + Victim.getDisplayName() + " recieved " + items.getAmount() + " " + Items.name(items) + " from " + playerSendingItems.getDisplayName() + " for " + price + plugin.getEcon().currencyNameSingular() + "(s)");

                                plugin.trades.remove(trade);
                                plugin.senders.remove(Sender1);
                            }

                        } else if (args[0].equalsIgnoreCase("deny")) {

                            Trade trade = null;
                            Sender Sender1 = null;

                            for (Trade t : plugin.trades) {

                                if (t.Victim == player) {

                                    trade = t;

                                    for (Sender s : plugin.senders) {

                                        if (s.Sender == t.playerSender) {

                                            Sender1 = s;
                                        }
                                    }
                                }
                            }

                            if (trade == null) {

                                player.sendMessage(prefix + ChatColor.RED + "No Trades to deny!");
                            } else {

                                Player playerSendingItems = trade.playerSender;
                                Player Victim = trade.Victim;
                                ItemStack items = trade.itemStack;
                                int price = trade.price;

                                if (playerSendingItems.getInventory().firstEmpty() == -1) {
                                    Location playerloc = playerSendingItems.getLocation();
                                    playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
                                    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

                                }
                                if (!playerSendingItems.isOnline()) {
                                    player.sendMessage(prefix + ChatColor.RED + "Player sending items is not Online!");
                                    player.sendMessage(prefix + ChatColor.RED + "Please wait for " + playerSendingItems.getName() + " to come back online!");
                                } else {
                                    playerSendingItems.getInventory().addItem(items);
                                    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + Victim.getDisplayName() + ChatColor.RED + " has Denied your Trade request!");
                                    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.name(items) + ChatColor.RED + " Has been returned to you.");
                                    Victim.sendMessage(prefix + ChatColor.RED + "You denied " + playerSendingItems.getName() + "'s Trade!");
                                    log.info(logpre + Victim.getDisplayName() + " denied " + items.getAmount() + " " + Items.name(items) + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

                                    plugin.trades.remove(trade);
                                    plugin.senders.remove(Sender1);
                                }
                            }

                        } else if (args[0].equalsIgnoreCase("stop") && player.hasPermission("sgift.admin")) {
                            while (plugin.trades.size() > 0) {

                                Trade trade = null;
                                Sender Sender1 = null;

                                for (Trade t : plugin.trades) {

                                    if (t.itemStack != null) {

                                        trade = t;

                                        for (Sender s : plugin.senders) {

                                            if (s.Sender != null) {

                                                Sender1 = s;
                                            }
                                        }
                                    }
                                }
                                if (trade == null) {

                                    player.sendMessage(prefix + ChatColor.RED + "No Trades to stop!");
                                } else {

                                    Player playerSendingItems = trade.playerSender;
                                    Player Victim = trade.Victim;
                                    ItemStack items = trade.itemStack;
                                    int price = trade.price;

                                    if (playerSendingItems.getInventory().firstEmpty() == -1) {
                                        Location playerloc = playerSendingItems.getLocation();
                                        playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
                                        playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

                                    } else {
                                        playerSendingItems.getInventory().addItem(items);
                                    }

                                    playerSendingItems.sendMessage(prefix + ChatColor.RED + "Your Trade has been cancelled by an Admin!");
                                    playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.name(items) + ChatColor.RED + " has been returned to you.");
                                    Victim.sendMessage(prefix + ChatColor.RED + "Admin cancelled your Trade.");
                                    log.info(logpre + "stopped a trade of " + items.getAmount() + " " + Items.name(items) + " from " + playerSendingItems.getDisplayName() + " for " + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");

                                    plugin.trades.remove(trade);
                                    plugin.senders.remove(Sender1);
                                }
                            }
                            player.sendMessage(prefix + ChatColor.GREEN + "Cancelled all Trades safely.");

                        } else if (args[0].equalsIgnoreCase("cancel")) {

                            Trade trade = null;
                            Sender Sender1 = null;

                            for (Trade t : plugin.trades) {

                                if (t.playerSender == player) {

                                    trade = t;
                                }
                            }

                            for (Sender s : plugin.senders) {

                                if (s.Sender == player) {

                                    Sender1 = s;
                                }

                            }

                            if (trade == null) {

                                player.sendMessage(prefix + ChatColor.RED + "No Trades to cancel!");
                            } else {

                                Player playerSendingItems = trade.playerSender;
                                Player Victim = trade.Victim;
                                ItemStack items = trade.itemStack;
                                int price = trade.price;

                                if (playerSendingItems.getInventory().firstEmpty() == -1) {
                                    Location playerloc = playerSendingItems.getLocation();
                                    playerSendingItems.getWorld().dropItemNaturally(playerloc, items);
                                    playerSendingItems.sendMessage(prefix + "Inventory full! Dropped Items at your feet!");

                                } else {
                                    playerSendingItems.getInventory().addItem(items);
                                }

                                playerSendingItems.sendMessage(prefix + ChatColor.RED + "Cancelled trade!");
                                playerSendingItems.sendMessage(prefix + ChatColor.YELLOW + items.getAmount() + " " + Items.name(items) + ChatColor.RED + " Has been returned to you.");
                                Victim.sendMessage(prefix + ChatColor.YELLOW + playerSendingItems.getName() + ChatColor.RED + " Cancelled the Trade!");

                                plugin.trades.remove(trade);
                                plugin.senders.remove(Sender1);
                            }


                        } else if (Bukkit.getServer().getPlayer(args[0]) == null) {

                            player.sendMessage(prefix + ChatColor.RED + "Player not Online.");

                        } else if (Bukkit.getServer().getPlayer(args[0]) == player) {

                            player.sendMessage(prefix + ChatColor.RED + "Don't trade Items with yourself!");

                        } else {

                            player.sendMessage(prefix + ChatColor.RED + "Too few arguments!");
                            player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

                        }
                    } else if (args.length == 2) {

                        player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
                        player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

                    } else if (args.length == 3) {

                        player.sendMessage(prefix + ChatColor.RED + "Too Few arguments!");
                        player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");

                    } else if (args.length == 4) {
                        if (Bukkit.getServer().getPlayer(args[0]) != player) {
                            if (Bukkit.getServer().getPlayer(args[0]) != null) {

                                int price = plugin.getInt(args[3]);
                                Player Victim = Bukkit.getServer().getPlayer(args[0]);
                                int amount = plugin.getInt(args[2]);

                                if (Items.parse(args[1], amount) != null) {

                                    ItemStack Item = new ItemStack(Items.parse(args[1], amount));

                                    if (amount != 0) {
                                        if (price != 0) {
                                            if (plugin.inventoryContains(player.getInventory(), Item)) {
                                                if (Item.getEnchantments().isEmpty()) {
                                                    if (plugin.getEcon().getBalance(Victim.getName()) >= price) {

                                                        plugin.trades.add(new Trade(Victim, player, Item, price));
                                                        plugin.senders.add(new Sender(player));

                                                        player.getInventory().removeItem(Item);

                                                        player.sendMessage(prefix + ChatColor.WHITE + "Now Trading " + ChatColor.YELLOW + Item.getAmount() + " " + Items.name(Item) + ChatColor.WHITE + " with " + ChatColor.YELLOW + Victim.getName() + ChatColor.WHITE + " for " + ChatColor.GOLD + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");
                                                        player.sendMessage(prefix + ChatColor.YELLOW + "Waiting for " + Victim.getName() + " to accept...");
                                                        Victim.sendMessage(prefix + ChatColor.WHITE + "New Trade from " + ChatColor.YELLOW + player.getDisplayName() + ChatColor.WHITE + " of " + ChatColor.YELLOW + Item.getAmount() + " " + Items.name(Item) + ChatColor.WHITE + " for " + ChatColor.GOLD + price + " " + plugin.getEcon().currencyNameSingular() + "(s)");
                                                        Victim.sendMessage(prefix + ChatColor.WHITE + "Do " + ChatColor.YELLOW + "/trade accept" + ChatColor.WHITE + " to accept this Trade or " + ChatColor.YELLOW + "/trade deny" + ChatColor.WHITE + " to deny this trade!");
                                                    } else {

                                                        player.sendMessage(prefix + ChatColor.RED + "That player doesn't have enough money!");
                                                    }

                                                } else {

                                                    player.sendMessage(prefix + ChatColor.RED + "You can't trade enchanted Items! (Yet)");
                                                }

                                            } else {

                                                player.sendMessage(prefix + ChatColor.RED + "You don't have enough " + Items.name(Item) + ", or Item is partially Used!");
                                                player.sendMessage(prefix + ChatColor.RED + "Check your Item ID's and don't forget data Values.");
                                            }


                                        } else {
                                            player.sendMessage(prefix + ChatColor.RED + "Price specified is Invalid!");
                                        }

                                    } else {

                                        player.sendMessage(prefix + ChatColor.RED + "Amount specified is Invalid!");
                                    }
                                } else {
                                    player.sendMessage(prefix + ChatColor.RED + "Material specified is Invalid!");
                                }

                            } else {

                                player.sendMessage(prefix + ChatColor.RED + "Player not Online!");
                            }

                        } else {

                            player.sendMessage(prefix + ChatColor.RED + "You can't Trade with yourself!");
                        }

                    } else if (args.length == 0) {

                        player.sendMessage(prefix + ChatColor.RED + "By Sk8r2K9. /trade help for more info");

                    } else if (args.length >= 5) {

                        player.sendMessage(prefix + ChatColor.RED + "Too many arguments!");
                        player.sendMessage(prefix + ChatColor.GRAY + "Correct usage: /trade <Player> <Item> <Amount> <Price>");
                    }
                } else {
                    
                    log.warning(logpre + ChatColor.RED + "Don't send sGift commands through console!");
                }
                
            } else {
                if (player != null) {
                    
                    player.sendMessage(prefix + ChatColor.RED + "Trading currently disabled.");
                } else {
                    
                    log.warning(logpre + "Don't send sGift commands through console!");
                    
                }
                
            }

        } else {
            plugin.hasPerms(player, commandLabel);
        }
        return false;
    }
}