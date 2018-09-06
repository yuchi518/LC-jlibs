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

import lets.cool.util.logging.Logr;

import java.util.HashMap;
import java.util.concurrent.*;

public class ExecutorServices {
	
	protected static Logr log = Logr.logger();
	
	final public static String DEFAULT_NAME 	= "default";
	final public static String DB_NAME 			= "db";				// use to communicate with database
	final public static String CORE_NAME 		= "core";			// use to run program main flow
	final public static String IO_NAME			= "io";				// use to read/save file.
	final public static String NET_NAME			= "net";			// use to communicate with internet service.
	final public static String PIPELINE         = "pipeline";       // use for Pipeline flow

    final private static DefaultThreadFactory defaultFactory = new DefaultThreadFactory();
    final private static HashMap<String, ExecutorService> _ESs;// = new HashMap<>();
	
	static {
		_ESs = new HashMap<>();
		//log.error"What!!{0}", new Object[]{_ESs});
	}

	public static ExecutorService newFixedThreadPool(int nThreads, ThreadFactory threadFactory) {
		return new ThreadPoolExecutor(nThreads, nThreads,
				0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>(),
				threadFactory);
	}

	synchronized public static ExecutorService initiateExecutorService(String name, int numberOfThreads) {
		//ExecutorService es = Executors.newFixedThreadPool(numberOfThreads, defaultFactory);
        ExecutorService es = newFixedThreadPool(numberOfThreads, defaultFactory);
		_ESs.put(name, es);
		log.info("ExecutorService({0}) created with {1} threads.", new Object[]{name, numberOfThreads});
		return es;
	}
	
	/**
	 * If ExecutorService doesn't exist, and the name is not 'default', 
	 * the default's ExecutorService will be return.
	 * If the name is 'default', create a default ExecutorService with
	 * fix number threads (The number is Runtime.getRuntime().availableProcessors()).
	 * @param name
	 * @return
	 */
	synchronized public static ExecutorService es(String name) {
		ExecutorService es = _ESs.get(name);
		
		if (es==null) {
			if (DEFAULT_NAME.equals(name)) {
				es = initiateExecutorService(DEFAULT_NAME, Runtime.getRuntime().availableProcessors());
			//} else if (DB_NAME.equals(name)) {
			//	es = initiateExecutorService(DB_NAME, 3);
			} else {
				es = es(DEFAULT_NAME);
			}
		}
		
		return es;
	}
	
	public static <V> CompletionService<V> cs(String name) {
		return new ExecutorCompletionService<>(es(name));
	}

    public static boolean isIdle() {
        for (ExecutorService es: _ESs.values()) {
            if (((ThreadPoolExecutor)es).getActiveCount() != 0 ||
                    ((ThreadPoolExecutor)es).getTaskCount() != ((ThreadPoolExecutor)es).getCompletedTaskCount()) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param timeout_millis = 0, check and return immediately. > 0, check N times (N milliseconds). < 0, await infinitely if not idle
     * @return return false after
     */
    public static boolean awaitIdle(long timeout_millis) {
	    long time = 0;

	    while (!isIdle()) {
            time ++;
            if ((time % 60000) == 0)    // about 1 minute
                log.info("Await Idle..");

            if (timeout_millis >= 0) {
                if (time > timeout_millis) return false;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.warn("Await interrupt", e);
            }
        }
        return true;
    }
	
	public static void shutdown() {
		for (ExecutorService es: _ESs.values()) {
			es.shutdown();
		}
	}

	public static void shutdownNow() {
		for (ExecutorService es: _ESs.values()) {
			es.shutdownNow();
		}
	}
	
	public static void awaitTermination() {
		for (ExecutorService es: _ESs.values()) {
			try {
				while(!es.awaitTermination(1, TimeUnit.MINUTES)) {
					log.info("Await Termination..");
				}
			} catch (InterruptedException e) {
				log.warn("Await interrupt", e);
			}
		}
	}
	
	public static void shutdownThenAwaitTermination() {
		for (ExecutorService es: _ESs.values()) {
			try {
				es.shutdown();
				while(!es.awaitTermination(1, TimeUnit.MINUTES)) {
					log.info("Await Termination..");
				}
			} catch (InterruptedException e) {
				log.warn("Await interrupt", e);
			}
		}
	}

	public static void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler) {
	    defaultFactory.setUncaughtExceptionHandler(handler);
    }

}





















