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
import java.util.Map;

import net.milkbowl.vault.chat.Chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class SimpleChat extends JavaPlugin
  {
    private static SimpleChat instance;
    private ChannelManager channelManager;
    private ChatterManager chatterManager;
    private CommandHandler commandExecutor;
    private PlayerListener playerListener;
    private Map<String, Object> worldConfig = Collections
        .synchronizedMap(new HashMap<String, Object>());
    private Chat chat;
    
    public static SimpleChat getInstance ()
      {
        return instance;
      }
    
    public ChannelManager getChannelManager ()
      {
        return channelManager;
      }
    
    public ChatterManager getChatterManager ()
      {
        return chatterManager;
      }
    
    public Map<String, Object> getWorldConfig ()
      {
        return new HashMap<String, Object>(worldConfig);
      }
    
    public Chat getChat ()
      {
        return chat;
      }
    
    public void handleChat (Chatter chatter, Channel ch, String message)
      {
        if (ch == null)
          {
            chatter.sendMessage(ChatColor.RED + "You are not in any channels");
            return;
          }
        if (chatter.isMuted())
          {
            chatter.sendMessage(ChatColor.RED + "You are muted");
            return;
          }
        if ( !chatter.hasPermission(ch.getPermission()))
          {
            chatter.sendMessage(ChatColor.RED
                + "You do not have permission to speak in this channel");
            return;
          }
        ch.sendMessage(chatter, ch.formatMessage(chatter, message));
      }
    
    public void refreshCache (CommandSender sender)
      {
        this.refreshCache();
        sender.sendMessage(ChatColor.GREEN + "SimpleChat cache refreshed!");
      }
    
    public void refreshCache ()
      {
        for (Chatter c : chatterManager.getChatters())
          {
            c.updateCache();
          }
      }
    
    @Override
    public void onDisable ()
      {
        // Null static variables
        instance = null;
        
        this.getLogger().info("Is now disabled");
      }
    
    @Override
    public void onEnable ()
      {
        // Set static variables
        instance = this;
        
        // Create managers
        channelManager = new ChannelManager();
        chatterManager = new ChatterManager();
        commandExecutor = new CommandHandler();
        playerListener = new PlayerListener();
        
        // Vault Integration
        if (this.getServer().getPluginManager().getPlugin("Vault") != null)
          {
            if (this.setupChat())
              {
                this.getLogger().info("Vault has been hooked");
              }
            else
              {
                this.getLogger().warning("Vault could not be hooked");
              }
          }
        
        // World config
        worldConfig.putAll(this.getConfig().getConfigurationSection("worlds")
            .getValues(true));
        
        // Commands
        this.getCommand("simplechat").setExecutor(commandExecutor);
        this.getCommand("channel").setExecutor(commandExecutor);
        this.getCommand("ignore").setExecutor(commandExecutor);
        this.getCommand("unignore").setExecutor(commandExecutor);
        this.getCommand("mute").setExecutor(commandExecutor);
        this.getCommand("unmute").setExecutor(commandExecutor);
        
        // Events
        this.getServer().getPluginManager()
            .registerEvents(playerListener, this);
        
        // Config
        this.saveDefaultConfig();
        this.saveConfig();
        
        Player[] onlinePlayers = this.getServer().getOnlinePlayers();
        for (int i = 0; i < onlinePlayers.length; i++)
          {
            chatterManager.initializeChatter(onlinePlayers[i]);
          }
        
        this.getLogger().info("Is now enabled");
      }
    
    private boolean setupChat ()
      {
        RegisteredServiceProvider<Chat> chatProvider =
            this.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null)
          {
            chat = chatProvider.getProvider();
          }
        
        return (chat != null);
      }
  }
