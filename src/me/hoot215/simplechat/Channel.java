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
import java.util.logging.Logger;

public interface Channel
  {
    public String getName ();
    
    public int getRadius ();
    
    public String getFormat ();
    
    public String[] getAliases ();
    
    public String getPermission ();
    
    public Logger getLogger ();
    
    public Set<Chatter> getMembers ();
    
    public boolean isHidden ();
    
    public boolean isMember (Chatter chatter);
    
    public void addMember (Chatter chatter);
    
    public void removeMember (Chatter chatter);
    
    public String formatMessage (Chatter chatter, String message);
    
    public void sendMessage (Chatter chatter, String message);
  }
