/*
 * A simple chat plugin with channels.
 * Copyright (C) 2013 Andrew Stevanus (Hoot215) <hoot893@gmail.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.hoot215.simplechat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    
    @Override
    public boolean onCommand (CommandSender sender, Command cmd, String label,
      String[] args)
      {
        if (cmd.getName().equals("simplechat"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            
            if (args.length == 1)
              {
                if (args[0].equalsIgnoreCase("help"))
                  return false;
                
                if (args[0].equalsIgnoreCase("reload"))
                  {
                    if ( !sender.hasPermission("simplechat.reload"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                    return true;
                  }
                
                if (args[0].equalsIgnoreCase("recache"))
                  {
                    if ( !sender.hasPermission("simplechat.recache"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    plugin.refreshCache(sender);
                    return true;
                  }
                return false;
              }
            return false;
          }
        if (cmd.getName().equals("channel"))
          {
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            
            ChatterManager playerManager = plugin.getChatterManager();
            ChannelManager channelManager = plugin.getChannelManager();
            
            if (args.length == 1)
              {
                if (args[0].equalsIgnoreCase("help"))
                  return false;
                
                if (args[0].equalsIgnoreCase("list"))
                  {
                    if ( !sender.hasPermission("simplechat.channel.list"))
                      {
                        sender.sendMessage(cmd.getPermissionMessage());
                        return true;
                      }
                    
                    boolean isChatter = sender instanceof Player;
                    Chatter chatter = null;
                    if (isChatter)
                      {
                        chatter = playerManager.getChatter((Player) sender);
                      }
                    boolean showHidden =
                        sender.hasPermission("simplechat.channel.list.hidden");
                    sender.sendMessage(ChatColor.GRAY + "("
                        + ChatColor.DARK_PURPLE + "Normal" + ChatColor.GRAY
                        + ", " + ChatColor.LIGHT_PURPLE + "Joined"
                        + ChatColor.GRAY + ", " + ChatColor.YELLOW + "Active"
                        + ChatColor.GRAY + ")");
                    sender.sendMessage(ChatColor.BLUE
                        + "All available channels:");
                    for (Channel ch : channelManager.getChannels())
                      {
                        if ( !isChatter)
                          {
                            sender.sendMessage(ChatColor.DARK_PURPLE
                                + ch.getName());
                            continue;
                          }
                        if (chatter.isInChannel(ch))
                          {
                            if (chatter.getActiveChannel() == ch)
                              {
                                sender.sendMessage(ChatColor.YELLOW
                                    + ch.getName());
                              }
                            else
                              {
                                sender.sendMessage(ChatColor.LIGHT_PURPLE
                                    + ch.getName());
                              }
                          }
                        else
                          {
                            if ( !ch.isHidden() || showHidden)
                              {
                                sender.sendMessage(ChatColor.DARK_PURPLE
                                    + ch.getName());
                              }
                          }
                      }
                    return true;
                  }
                
                if (args[0].equalsIgnoreCase("join"))
                  {
                    sender.sendMessage(ChatColor.RED
                        + "You must supply a channel to join");
                    return true;
                  }
                
                if (args[0].equalsIgnoreCase("leave"))
                  {
                    sender.sendMessage(ChatColor.RED
                        + "You must supply a channel to leave");
                    return true;
                  }
                
                if (channelManager.getChannel(args[0]) != null)
                  {
                    if ( ! (sender instanceof Player))
                      {
                        sender.sendMessage("This command can only be run "
                            + "by a player");
                        return true;
                      }
                    Channel channel = channelManager.getChannel(args[0]);
                    Chatter chatter = playerManager.getChatter((Player) sender);
                    if (chatter.setActiveChannel(channel))
                      {
                        sender.sendMessage(ChatColor.GREEN
                            + "Active channel set to '" + channel.getName()
                            + '\'');
                      }
                    else
                      {
                        sender.sendMessage(ChatColor.RED
                            + "You don't have permission to join this channel");
                      }
                    return true;
                  }
                return false;
              }
            
            if (args.length == 2)
              {
                if (args[0].equalsIgnoreCase("join"))
                  {
                    if ( ! (sender instanceof Player))
                      {
                        sender.sendMessage("This command can only be run "
                            + "by a player");
                        return true;
                      }
                    Chatter chatter = playerManager.getChatter((Player) sender);
                    Channel channel = channelManager.getChannel(args[1]);
                    
                    if (channel == null)
                      {
                        chatter.sendMessage(ChatColor.RED
                            + "That channel does not exist");
                        return true;
                      }
                    
                    if (chatter.isInChannel(channel))
                      {
                        chatter.sendMessage(ChatColor.RED
                            + "You are already in that channel");
                        return true;
                      }
                    
                    if (chatter.joinChannel(channel))
                      {
                        chatter.sendMessage(ChatColor.GREEN
                            + "Joined channel '" + channel.getName() + '\'');
                      }
                    else
                      {
                        chatter.sendMessage(ChatColor.RED
                            + "You don't have permission to join this channel");
                      }
                    
                    return true;
                  }
                if (args[0].equalsIgnoreCase("leave"))
                  {
                    if ( ! (sender instanceof Player))
                      {
                        sender.sendMessage("This command can only be run "
                            + "by a player");
                        return true;
                      }
                    Chatter chatter = playerManager.getChatter((Player) sender);
                    Channel channel = channelManager.getChannel(args[1]);
                    
                    if (channel == null)
                      {
                        chatter.sendMessage(ChatColor.RED
                            + "That channel does not exist");
                        return true;
                      }
                    
                    if ( !chatter.isInChannel(channel))
                      {
                        chatter.sendMessage(ChatColor.RED
                            + "You are not in that channel");
                      }
                    else
                      {
                        chatter.leaveChannel(channel);
                        chatter.sendMessage(ChatColor.GREEN + "Left channel '"
                            + channel.getName() + '\'');
                      }
                    
                    return true;
                  }
              }
            
            if (channelManager.getChannel(args[0]) != null)
              {
                if ( ! (sender instanceof Player))
                  {
                    sender.sendMessage("This command can only be run "
                        + "by a player");
                    return true;
                  }
                Chatter chatter = playerManager.getChatter((Player) sender);
                if ( !chatter.hasPermission("simplechat.talk"))
                  {
                    chatter.sendMessage(cmd.getPermissionMessage());
                    return true;
                  }
                
                StringBuilder sbr = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                  {
                    sbr.append(args[i]).append(' ');
                  }
                String message = sbr.toString();
                plugin.handleChat(chatter, channelManager.getChannel(args[0]),
                    message);
                return true;
              }
            return false;
          }
        if (cmd.getName().equals("ignore"))
          {
            if ( ! (sender instanceof Player))
              {
                sender.sendMessage("This command can only be run by a player");
                return true;
              }
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            Chatter chatter =
                plugin.getChatterManager().getChatter((Player) sender);
            for (String s : args)
              {
                Player player = plugin.getServer().getPlayer(s);
                if (player == null || !player.isOnline())
                  {
                    sender.sendMessage(ChatColor.RED + "Player '" + s
                        + "' is not online");
                    continue;
                  }
                String playerName = player.getName();
                if (player.hasPermission("simplechat.unignorable"))
                  {
                    sender.sendMessage(ChatColor.RED + "Player "
                        + ChatColor.DARK_RED + playerName + ChatColor.RED
                        + " cannot be ignored");
                    continue;
                  }
                if (chatter.isIgnoring(playerName))
                  {
                    chatter.unignore(playerName);
                    sender.sendMessage(ChatColor.GREEN
                        + "You are no longer ignoring " + ChatColor.DARK_GREEN
                        + playerName);
                    continue;
                  }
                else
                  {
                    chatter.ignore(playerName);
                    sender.sendMessage(ChatColor.GREEN
                        + "You are now ignoring " + ChatColor.DARK_GREEN
                        + playerName);
                    continue;
                  }
              }
            return true;
          }
        if (cmd.getName().equals("unignore"))
          {
            if ( ! (sender instanceof Player))
              {
                sender.sendMessage("This command can only be run by a player");
                return true;
              }
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            if (args.length == 0)
              return false;
            Chatter chatter =
                plugin.getChatterManager().getChatter((Player) sender);
            for (String s : args)
              {
                Player player = plugin.getServer().getPlayer(s);
                if (player == null || !player.isOnline())
                  {
                    sender.sendMessage(ChatColor.RED + "Player '" + s
                        + "' is not online");
                    continue;
                  }
                String playerName = player.getName();
                if (chatter.isIgnoring(playerName))
                  {
                    chatter.unignore(playerName);
                    sender.sendMessage(ChatColor.GREEN
                        + "You are no longer ignoring " + ChatColor.DARK_GREEN
                        + playerName);
                    continue;
                  }
                else
                  {
                    sender.sendMessage(ChatColor.RED + "You aren't ignoring "
                        + ChatColor.DARK_RED + playerName);
                    continue;
                  }
              }
            return true;
          }
        if (cmd.getName().equals("mute"))
          {
            if (args.length == 0)
              return false;
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            for (String s : args)
              {
                Player player = plugin.getServer().getPlayer(s);
                if (player == null || !player.isOnline())
                  {
                    sender.sendMessage(ChatColor.RED + "Player '" + s
                        + "' is not online");
                    continue;
                  }
                plugin.getChatterManager().getChatter(player).setMuted(true);
                sender.sendMessage(ChatColor.BLUE + player.getName()
                    + ChatColor.AQUA + " has been muted");
              }
            return true;
          }
        if (cmd.getName().equals("unmute"))
          {
            if (args.length == 0)
              return false;
            if ( !sender.hasPermission(cmd.getPermission()))
              {
                sender.sendMessage(cmd.getPermissionMessage());
                return true;
              }
            for (String s : args)
              {
                Player player = plugin.getServer().getPlayer(s);
                if (player == null || !player.isOnline())
                  {
                    sender.sendMessage(ChatColor.RED + "Player '" + s
                        + "' is not online");
                    continue;
                  }
                plugin.getChatterManager().getChatter(player).setMuted(false);
                sender.sendMessage(ChatColor.BLUE + player.getName()
                    + ChatColor.AQUA + " has been unmuted");
              }
            return true;
          }
        return false;
      }
  }
