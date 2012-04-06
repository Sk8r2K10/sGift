package me.Sk8r2K10.sGift;

import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class sGiftCommand implements CommandExecutor {

    private sGift plugin;
    
    Player player = null;
    String prefix = ChatColor.WHITE + "[" + ChatColor.DARK_RED + "sGift" + ChatColor.WHITE + "] ";
    Logger log = Logger.getLogger("Minecraft");
    
    

    public sGiftCommand(sGift instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        
        PluginDescriptionFile pdf = plugin.getDescription();
        String logpre = "[" + pdf.getName() + " " + pdf.getVersion() + "] ";          
        
        if (sender instanceof Player) {

            player = (Player) sender;
        } else {
            
            player = null;
        }
                
        if (player != null) {
            if (commandLabel.equalsIgnoreCase("sgift") && sender.hasPermission("sgift.sgift")) {
                
                              
                
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("info")) {
                        
                        String info1 = Boolean.toString(plugin.getConfig().getBoolean("Features.enable-gift"));
                        String info2 = Boolean.toString(plugin.getConfig().getBoolean("Features.enable-trade"));

                        player.sendMessage(ChatColor.DARK_RED + "-----------------[" + ChatColor.RED + "sGift - Information" + ChatColor.DARK_RED + "]------------------");
                        player.sendMessage(ChatColor.RED + "Gift: " + ChatColor.AQUA + info1);
                        player.sendMessage(ChatColor.RED + "Trade: " + ChatColor.AQUA + info2);

                        StringBuilder senderList = new StringBuilder();

                        for (Sender s : plugin.senders) {
                            if (senderList.length() > 0) {

                                senderList.append(ChatColor.RED + ", " + ChatColor.AQUA);

                            }
                            senderList.append(s.Sender.getName());
                        }
                        player.sendMessage(ChatColor.RED + "Senders: " + ChatColor.AQUA + senderList);


                    } else if (args[0].equalsIgnoreCase("halt") && sender.hasPermission("sgift.halt")) {

                        player.sendMessage(prefix + ChatColor.RED + "Abruptly halted all Gifts and Trades!");
                        player.sendMessage(prefix + ChatColor.RED + "No items have been refunded to players!");

                        plugin.trades.clear();
                        plugin.gifts.clear();
                        plugin.senders.clear();

                    } else if (args[0].equalsIgnoreCase("help")) {

                        player.sendMessage(ChatColor.DARK_RED + "---------------[" + ChatColor.RED + "sGift - sGift Help Menu" + ChatColor.DARK_RED + "]----------------");
                        player.sendMessage(plugin.getConfig().getString("Help.sGift.Info"));
                        player.sendMessage(plugin.getConfig().getString("Help.sGift.Halt"));
                        player.sendMessage(plugin.getConfig().getString("Help.sGift.Set"));
                        player.sendMessage(plugin.getConfig().getString("Help.sGift.Example"));

                    } else {

                        player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                        player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
                    }
                } else if (args.length == 2) {

                    player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                    player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");

                } else if (args.length == 3 && args[0].equalsIgnoreCase("set") && sender.hasPermission("sgift.set")) {
                     if (args[1].equalsIgnoreCase("gift")) {
                        if (args[2].equalsIgnoreCase("true")) {
                            if (!plugin.getConfig().getBoolean("Features.enable-gift")) {

                                plugin.getConfig().set("Features.enable-gift", true);
                                plugin.saveConfig();

                                player.sendMessage(prefix + ChatColor.AQUA + "Gifting has been set to true in Config");

                            } else {

                                player.sendMessage(prefix + ChatColor.RED + "Gifting is already enabled!");
                            }
                        } else if (args[2].equalsIgnoreCase("false")) {
                            if (plugin.getConfig().getBoolean("Features.enable-gift")) {

                                plugin.getConfig().set("Features.enable-gift", false);
                                plugin.saveConfig();

                                player.sendMessage(prefix + ChatColor.AQUA + "Gifting has been set to false in Config");

                            } else {

                                player.sendMessage(prefix + ChatColor.RED + "Gifting is already disabled!");
                            }
                        } else {

                            player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                            player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
                        }

                    } else if (args[1].equalsIgnoreCase("trade")) {
                        if (args[2].equalsIgnoreCase("true")) {
                            if (!plugin.getConfig().getBoolean("Features.enable-trade")) {

                                plugin.getConfig().set("Features.enable-trade", true);
                                plugin.saveConfig();

                                player.sendMessage(prefix + ChatColor.AQUA + "Trading has been set to true in Config");

                            } else {

                                player.sendMessage(prefix + ChatColor.RED + "Trading is already enabled!");
                            }
                        } else if (args[2].equalsIgnoreCase("false")) {
                            if (plugin.getConfig().getBoolean("Features.enable-trade")) {

                                plugin.getConfig().set("Features.enable-trade", false);
                                plugin.saveConfig();

                                player.sendMessage(prefix + ChatColor.AQUA + "Trading has been set to false in Config");

                            } else {

                                player.sendMessage(prefix + ChatColor.RED + "Trading is already disabled!");
                            }
                        } else {

                            player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                            player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
                        }
                    } else {

                        player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                        player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
                    }
                } else if (args.length == 0) {

                    player.sendMessage(prefix + ChatColor.RED + "By Sk8r2K9. /sgift info|halt|set <Option> [true|false]");
                } else if (args.length > 3) {

                    player.sendMessage(prefix + ChatColor.RED + "Invalid command usage!");
                    player.sendMessage(prefix + ChatColor.GRAY + "/sgift info|halt|set <Option> [true|false]");
                }

            } else {

                plugin.hasPerms(player, commandLabel);
            }

        } else {
            
            log.warning(logpre + "Don't send sGift commands through console!");
        }
        return false;
    }
}
