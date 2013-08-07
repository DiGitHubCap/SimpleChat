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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

public class SimpleChatter implements Chatter
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    private final Player player;
    private volatile boolean muted = false;
    private final Set<String> ignoring = new HashSet<String>();
    private final Set<Channel> channels = Collections
        .synchronizedSet(new HashSet<Channel>());
    private Channel activeChannel = null;
    private final Object activeChannelMutex = new Object();
    private String group;
    private final Object groupMutex = new Object();
    private String prefix;
    private final Object prefixMutex = new Object();
    private String suffix;
    private final Object suffixMutex = new Object();
    private Map<String, Object> config = Collections
        .synchronizedMap(new HashMap<String, Object>());
    private Location location;
    private final Object locationMutex = new Object();
    private Map<String, Boolean> permissions = Collections
        .synchronizedMap(new HashMap<String, Boolean>());
    
    public SimpleChatter(Player player)
      {
        this.player = player;
        this.location = player.getLocation().clone();
        this.updateCache();
      }
    
    public Player getPlayer ()
      {
        return player;
      }
    
    public String getName ()
      {
        return player.getName();
      }
    
    public String getDisplayName ()
      {
        return player.getDisplayName();
      }
    
    public String getGroup ()
      {
        synchronized (groupMutex)
          {
            return group;
          }
      }
    
    public Object getGroupConfigValue (String key)
      {
        return config.get(key);
      }
    
    public String getPrefix ()
      {
        synchronized (prefixMutex)
          {
            return prefix;
          }
      }
    
    public String getSuffix ()
      {
        synchronized (suffixMutex)
          {
            return suffix;
          }
      }
    
    public Location getLocation ()
      {
        synchronized (locationMutex)
          {
            return location;
          }
      }
    
    public World getWorld ()
      {
        synchronized (locationMutex)
          {
            return location.getWorld();
          }
      }
    
    public Set<Channel> getChannels ()
      {
        synchronized (channels)
          {
            return new HashSet<Channel>(channels);
          }
      }
    
    public boolean isMuted ()
      {
        return muted;
      }
    
    public Channel getActiveChannel ()
      {
        synchronized (activeChannelMutex)
          {
            return activeChannel;
          }
      }
    
    public boolean isInChannel (Channel channel)
      {
        return channels.contains(channel);
      }
    
    public boolean isIgnoring (Chatter chatter)
      {
        return this.isIgnoring(chatter.getName());
      }
    
    public boolean isIgnoring (String chatterName)
      {
        return ignoring.contains(chatterName);
      }
    
    public boolean hasPermission (String perm)
      {
        Boolean result = permissions.get(perm);
        return result == null ? false : result;
      }
    
    public void setMuted (boolean value)
      {
        muted = value;
      }
    
    public boolean setActiveChannel (String channelName)
      {
        return this.setActiveChannel(plugin.getChannelManager().getChannel(
            channelName));
      }
    
    public boolean setActiveChannel (Channel channel)
      {
        if ( !this.hasPermission(channel.getPermission()))
          return false;
        synchronized (activeChannelMutex)
          {
            if ( !channels.contains(channel))
              {
                channel.addMember(this);
              }
            activeChannel = channel;
          }
        return true;
      }
    
    public boolean joinChannel (String channelName)
      {
        return this.joinChannel(plugin.getChannelManager().getChannel(
            channelName));
      }
    
    public boolean joinChannel (Channel channel)
      {
        if (channel == null)
          return false;
        if ( !this.hasPermission(channel.getPermission()))
          return false;
        
        channels.add(channel);
        if ( !channel.isMember(this))
          {
            channel.addMember(this);
          }
        return true;
      }
    
    public boolean leaveChannel (String channelName)
      {
        return this.leaveChannel(plugin.getChannelManager().getChannel(
            channelName));
      }
    
    public boolean leaveChannel (Channel channel)
      {
        if (channel == null)
          return false;
        
        channels.remove(channel);
        if (channel.isMember(this))
          {
            channel.removeMember(this);
          }
        synchronized (activeChannelMutex)
          {
            if (channel == activeChannel)
              {
                activeChannel = null;
              }
          }
        return true;
      }
    
    public void ignore (Chatter chatter)
      {
        this.ignore(chatter.getName());
      }
    
    public void ignore (String chatterName)
      {
        if ( !ignoring.contains(chatterName))
          {
            ignoring.add(chatterName);
          }
      }
    
    public void unignore (Chatter chatter)
      {
        this.unignore(chatter.getName());
      }
    
    public void unignore (String chatterName)
      {
        if (ignoring.contains(chatterName))
          {
            ignoring.remove(chatterName);
          }
      }
    
    public void sendMessage (String message)
      {
        player.sendMessage(message);
      }
    
    public void updateLocation (Location loc)
      {
        synchronized (locationMutex)
          {
            location = loc;
          }
      }
    
    public void updateCache ()
      {
        // Update group cache
        synchronized (groupMutex)
          {
            group = "default";
            for (String s : plugin.getConfig().getStringList("groups"))
              {
                if (s == null)
                  break;
                if (player.hasPermission("simplechat.group." + s))
                  {
                    group = s;
                  }
              }
          }
        
        // Update config cache
        config.clear();
        config.putAll(plugin.getConfig()
            .getConfigurationSection("group.default").getValues(true));
        config.putAll(plugin.getConfig()
            .getConfigurationSection("group." + this.getGroup())
            .getValues(true));
        
        // Update Vault data
        if (plugin.getChat() != null)
          {
            synchronized (prefixMutex)
              {
                prefix = plugin.getChat().getPlayerPrefix(player);
              }
            synchronized (suffixMutex)
              {
                suffix = plugin.getChat().getPlayerSuffix(player);
              }
          }
        else
          {
            synchronized (prefixMutex)
              {
                prefix = "";
              }
            synchronized (suffixMutex)
              {
                suffix = "";
              }
          }
        
        // Update permissions cache
        permissions.clear();
        for (Permission perm : plugin.getDescription().getPermissions())
          {
            permissions.put(perm.getName(), player.hasPermission(perm));
          }
        for (Channel channel : plugin.getChannelManager().getChannels())
          {
            permissions.put(channel.getPermission(),
                player.hasPermission(channel.getPermission()));
          }
      }
  }
