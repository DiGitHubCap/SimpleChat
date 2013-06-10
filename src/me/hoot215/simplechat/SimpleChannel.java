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

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import me.hoot215.simplechat.log.LogFormatter;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

public class SimpleChannel implements Channel
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    private final String name;
    private final int radius;
    private final String format;
    private final String[] aliases;
    private final boolean logToFile;
    private final boolean logToConsole;
    private final String permission;
    private final Logger logger;
    private final Set<Chatter> members = Collections
        .synchronizedSet(new HashSet<Chatter>());
    
    public SimpleChannel(String name, int radius, String format,
      String[] aliases)
      {
        this.name = name;
        this.radius = radius;
        this.aliases = (aliases == null ? new String[0] : aliases);
        this.format = format;
        String configPrefix = "channel." + name + '.';
        logToFile = plugin.getConfig().getBoolean(configPrefix + "log-to-file");
        logToConsole =
            plugin.getConfig().getBoolean(configPrefix + "log-to-console");
        permission =
            plugin.getConfig().isString(configPrefix + "permission") ? plugin
                .getConfig().getString(configPrefix + "permission")
                : "simplechat.channel";
        if (logToFile)
          {
            logger = Logger.getLogger("SimpleChat:" + name);
            logger.setUseParentHandlers(false);
            LogFormatter formatter = new LogFormatter();
            for (Handler handler : logger.getHandlers())
              {
                handler.setFormatter(formatter);
              }
            try
              {
                Handler chatHandler = new FileHandler("chat.log", true);
                chatHandler.setFormatter(formatter);
                logger.addHandler(chatHandler);
              }
            catch (IOException e)
              {
                e.printStackTrace();
              }
          }
        else
          {
            logger = null;
          }
      }
    
    public String getName ()
      {
        return name;
      }
    
    public int getRadius ()
      {
        return radius;
      }
    
    public String getFormat ()
      {
        return format;
      }
    
    public String[] getAliases ()
      {
        return aliases;
      }
    
    public String getPermission ()
      {
        return permission;
      }
    
    public Logger getLogger ()
      {
        return logger;
      }
    
    public Set<Chatter> getMembers ()
      {
        return new HashSet<Chatter>(members);
      }
    
    public boolean isMember (Chatter chatter)
      {
        return members.contains(chatter);
      }
    
    public void addMember (Chatter chatter)
      {
        members.add(chatter);
      }
    
    public void removeMember (Chatter chatter)
      {
        members.remove(chatter);
      }
    
    public String formatMessage (Chatter chatter, String message)
      {
        String formattedMessage = format;
        formattedMessage =
            formattedMessage.replace("$name", chatter.getName())
                .replace("$displayname", chatter.getDisplayName())
                .replace("$world", chatter.getWorld().getName());
        if (formattedMessage.contains("$prefix"))
          {
            formattedMessage =
                formattedMessage.replace("$prefix", chatter.getPrefix());
          }
        if (formattedMessage.contains("$suffix"))
          {
            formattedMessage =
                formattedMessage.replace("$suffix", chatter.getSuffix());
          }
        if (formattedMessage.contains("$wname"))
          {
            String worldName =
                plugin.getWorldConfig().containsKey(
                    chatter.getWorld().getName() + ".alias") ? (String) plugin
                    .getWorldConfig().get(
                        chatter.getWorld().getName() + ".alias") : chatter
                    .getWorld().getName();
            formattedMessage = formattedMessage.replace("$wname", worldName);
          }
        formattedMessage =
            ChatColor.translateAlternateColorCodes('&', formattedMessage);
        formattedMessage =
            formattedMessage.replace(
                "$message",
                chatter.hasPermission("simplechat.colours") ? ChatColor
                    .translateAlternateColorCodes('&', message) : message);
        return formattedMessage;
      }
    
    public void sendMessage (Chatter chatter, String message)
      {
        switch ( radius )
          {
            case -1 :
              for (Chatter c : members)
                {
                  c.sendMessage(message);
                }
              break;
            case 0 :
              World w1 = chatter.getWorld();
              for (Chatter c : members)
                {
                  if (c.getWorld() == w1)
                    {
                      c.sendMessage(message);
                    }
                }
              break;
            default :
              World w2 = chatter.getWorld();
              Location origin = chatter.getLocation();
              for (Chatter c : members)
                {
                  if (c.getWorld() == w2
                      && (c.getLocation() == origin || c.getLocation()
                          .distance(origin) < radius))
                    {
                      c.sendMessage(message);
                    }
                }
              break;
          }
        if (logToFile)
          {
            logger.info(message);
          }
        if (logToConsole)
          {
            System.out.println(LogFormatter.stripColour(message));
          }
      }
  }
