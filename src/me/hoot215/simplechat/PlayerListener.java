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

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove (PlayerMoveEvent event)
      {
        plugin.getChatterManager().getChatter(event.getPlayer())
            .updateLocation(event.getPlayer().getLocation().clone());
      }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangedWorld (PlayerChangedWorldEvent event)
      {
        plugin.getChatterManager().getChatter(event.getPlayer())
            .updateLocation(event.getPlayer().getLocation());
      }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin (PlayerJoinEvent event)
      {
        Player player = event.getPlayer();
        Chatter chatter = plugin.getChatterManager().initializeChatter(player);
        String message = (String) chatter.getGroupConfigValue("join");
        message =
            message.replace("$name", player.getName())
                .replace("$displayname", player.getDisplayName())
                .replace("$world", player.getWorld().getName());
        if (message.contains("$prefix") && plugin.getChat() != null)
          {
            message = message.replace("$prefix", chatter.getPrefix());
          }
        if (message.contains("$suffix") && plugin.getChat() != null)
          {
            message = message.replace("$suffix", chatter.getSuffix());
          }
        if (message.contains("$wname"))
          {
            String worldName =
                plugin.getConfig().isString(
                    "worlds." + player.getWorld().getName() + ".alias")
                    ? plugin.getConfig().getString(
                        "worlds." + player.getWorld().getName() + ".alias")
                    : player.getWorld().getName();
            message = message.replace("$wname", worldName);
          }
        message = ChatColor.translateAlternateColorCodes('&', message);
        event.setJoinMessage(message);
      }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit (PlayerQuitEvent event)
      {
        Player player = event.getPlayer();
        Chatter chatter = plugin.getChatterManager().getChatter(player);
        String message = (String) chatter.getGroupConfigValue("quit");
        message =
            message.replace("$name", player.getName())
                .replace("$displayname", player.getDisplayName())
                .replace("$world", player.getWorld().getName());
        if (message.contains("$prefix") && plugin.getChat() != null)
          {
            message =
                message.replace("$prefix",
                    plugin.getChat().getPlayerPrefix(player));
          }
        if (message.contains("$suffix") && plugin.getChat() != null)
          {
            message =
                message.replace("$suffix",
                    plugin.getChat().getPlayerSuffix(player));
          }
        if (message.contains("$wname"))
          {
            String worldName =
                plugin.getConfig().isString(
                    "worlds." + player.getWorld().getName() + ".alias")
                    ? plugin.getConfig().getString(
                        "worlds." + player.getWorld().getName() + ".alias")
                    : player.getWorld().getName();
            message = message.replace("$wname", worldName);
          }
        message = ChatColor.translateAlternateColorCodes('&', message);
        event.setQuitMessage(message);
        plugin.getChatterManager().destructChatter(player);
      }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess (PlayerCommandPreprocessEvent event)
      {
        String message = event.getMessage();
        String messageLower = message.toLowerCase();
        if (messageLower.equals("/p reload")
            || messageLower.equals("/bp reload")
            || messageLower.equals("/perm reload")
            || messageLower.equals("/permissions reload"))
          {
            final Player player = event.getPlayer();
            if ( !player.hasPermission("simplechat.recache"))
              return;
            plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable()
                  {
                    public void run ()
                      {
                        plugin.refreshCache(player);
                      }
                  }, 1L);
            return;
          }
        Map<String, Channel> aliases = plugin.getChannelManager().getAliases();
        int spaceIndex = message.indexOf(' ');
        String command;
        if (spaceIndex == -1)
          {
            command = messageLower.substring(1);
            if (aliases.containsKey(command))
              {
                Channel ch = aliases.get(command);
                event.setMessage("/channel " + ch.getName());
              }
          }
        else
          {
            command = messageLower.substring(1, spaceIndex);
            if (aliases.containsKey(command))
              {
                Channel ch = aliases.get(command);
                event.setMessage("/channel " + ch.getName()
                    + message.substring(spaceIndex, message.length()));
              }
          }
      }
    
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat (AsyncPlayerChatEvent event)
      {
        event.setCancelled(true);
        Chatter chatter =
            plugin.getChatterManager().getChatter(event.getPlayer());
        if ( !chatter.hasPermission("simplechat.talk"))
          return;
        plugin.handleChat(chatter, chatter.getActiveChannel(),
            event.getMessage());
      }
  }
