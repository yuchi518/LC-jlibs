/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package lets.cool.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger numOfPools = new AtomicInteger(1);

    final private ThreadGroup group;
    final private AtomicInteger numOfThreads = new AtomicInteger(1);
    final private String prefixOfName;

    private boolean isDaemon = false;
    private int priority = Thread.NORM_PRIORITY;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;

    DefaultThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        prefixOfName = "lc-TF[" +  numOfPools.getAndIncrement() + "] #thread: ";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r, prefixOfName + numOfThreads.getAndIncrement(), 0);

        if (t.isDaemon() != this.isDaemon)
            t.setDaemon(this.isDaemon);

        if (t.getPriority() != this.priority)
            t.setPriority(this.priority);

        if (this.uncaughtExceptionHandler != null)
            t.setUncaughtExceptionHandler(this.uncaughtExceptionHandler);

        return t;
    }

    public void setDaemon(boolean daemon) {
        this.isDaemon = daemon;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
        this.uncaughtExceptionHandler = handler;
    }
}
