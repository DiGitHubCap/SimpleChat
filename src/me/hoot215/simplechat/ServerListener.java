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

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

public class ServerListener implements Listener
  {
    private final SimpleChat plugin = SimpleChat.getInstance();
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onServerCommand (ServerCommandEvent event)
      {
        String command = event.getCommand().toLowerCase();
        if (command.equals("/p reload") || command.equals("/bp reload")
            || command.equals("/perm reload")
            || command.equals("/permissions reload"))
          {
            final CommandSender sender = event.getSender();
            plugin.getServer().getScheduler()
                .scheduleSyncDelayedTask(plugin, new Runnable()
                  {
                    public void run ()
                      {
                        plugin.refreshCache(sender);
                      }
                  }, 1L);
            return;
          }
      }
  }
