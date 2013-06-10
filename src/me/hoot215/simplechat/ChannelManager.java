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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChannelManager
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    private final Map<String, Channel> channels = Collections
        .synchronizedMap(new HashMap<String, Channel>());
    private final Map<String, Channel> aliases = Collections
        .synchronizedMap(new HashMap<String, Channel>());
      
      {
        for (String s : plugin.getConfig().getStringList("channels"))
          {
            if ( !plugin.getConfig().isConfigurationSection("channel." + s))
              {
                plugin.getLogger().severe("Channel '" + s + "' is not defined");
                continue;
              }
            List<String> aliases =
                plugin.getConfig().getStringList("channel." + s + ".aliases");
            Channel channel =
                new SimpleChannel(s, plugin.getConfig().isInt(
                    "channel." + s + ".radius") ? plugin.getConfig().getInt(
                    "channel." + s + ".radius") : -1, SimpleChat.getInstance()
                    .getConfig().isString("channel." + s + ".format")
                    ? SimpleChat.getInstance().getConfig()
                        .getString("channel." + s + ".format")
                    : "<$name>: $message", aliases.toArray(new String[aliases
                    .size()]));
            channels.put(s, channel);
            for (String a : aliases)
              {
                if (a == null)
                  break;
                this.aliases.put(a, channel);
              }
          }
      }
    
    public Collection<Channel> getChannels ()
      {
        return new HashMap<String, Channel>(channels).values();
      }
    
    public Map<String, Channel> getAliases ()
      {
        return new HashMap<String, Channel>(aliases);
      }
    
    public Channel getChannel (String channelName)
      {
        return channels.get(channelName);
      }
    
    public void addChannel (Channel channel)
      {
        channels.put(channel.getName(), channel);
      }
    
    public void removeChannel (Channel channel)
      {
        channels.remove(channel.getName());
      }
  }
