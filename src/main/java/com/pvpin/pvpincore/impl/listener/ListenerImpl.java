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
package com.pvpin.pvpincore.impl.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

/**
 * A ListenerImpl is used to listen a certain event and when the event happens, it calls ListenerManager#call.<p>
 * The call method walks all the JSListeners of a certain event and calls them.
 *
 * @author William_Shi
 */
public class ListenerImpl implements Listener {
    public class LOWEST implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.LOWEST);
        }
    }

    public class LOW implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.LOW);
        }
    }

    public class NORMAL implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.NORMAL);
        }
    }

    public class HIGH implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.HIGH);
        }
    }

    public class HIGHEST implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.HIGHEST);
        }
    }

    public class MONITOR implements EventExecutor {
        @Override
        public void execute(Listener listener, Event event) throws EventException {
            ListenerManager.call(event, EventPriority.MONITOR);
        }
    }
}
