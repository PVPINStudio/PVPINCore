/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pvpin.pvpincore.impl.listener.listenerimpl;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
*
* @author William_Shi
* 通过神奇方法创建的监听器
* 理论上包含1.15的所有事件监听
*/

public class EntityEventListener implements Listener{

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEventCalled_LOWEST(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.LOWEST);
    }
    
    @EventHandler(priority = EventPriority.LOW)
    public void onEventCalled_LOW(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.LOW);
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onEventCalled_NORMAL(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.NORMAL);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onEventCalled_HIGH(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.HIGH);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEventCalled_HIGHEST(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.HIGHEST);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEventCalled_MONITOR(org.bukkit.event.entity.EntityEvent event){
        com.pvpin.pvpincore.impl.listener.ListenerManager.call(event, EventPriority.MONITOR);
    }

}
