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

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface Chatter
  {
    public Player getPlayer ();
    
    public String getName ();
    
    public String getDisplayName ();
    
    public String getGroup ();
    
    public Object getGroupConfigValue (String key);
    
    public String getPrefix ();
    
    public String getSuffix ();
    
    public Location getLocation ();
    
    public World getWorld ();
    
    public Channel getActiveChannel ();
    
    public Set<Channel> getChannels ();
    
    public boolean isMuted ();
    
    public boolean isInChannel (Channel channel);
    
    public boolean hasPermission (String perm);
    
    public void setMuted (boolean value);
    
    public boolean setActiveChannel (String channelName);
    
    public boolean setActiveChannel (Channel channel);
    
    public boolean joinChannel (String channelName);
    
    public boolean joinChannel (Channel channel);
    
    public boolean leaveChannel (String channelName);
    
    public boolean leaveChannel (Channel channel);
    
    public void sendMessage (String message);
    
    public void updateLocation (Location loc);
    
    public void updateCache ();
  }
