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

import java.util.HashMap;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutorServices {
	
	protected static Logger log = Logger.getLogger(ExecutorServices.class.getName());
	
	final public static String DEFAULT_NAME 	= "default";
	final public static String DB_NAME 			= "db";				// use to communicate with database
	final public static String CORE_NAME 		= "core";			// use to run program main flow
	final public static String IO_NAME			= "io";				// use to read/save file.
	final public static String NET_NAME			= "net";			// use to communicate with internet service.			
	
	
	final private static HashMap<String, ExecutorService> _ESs;// = new HashMap<>();
	
	static {
		_ESs = new HashMap<String, ExecutorService>();
		//log.log(Level.SEVERE, "What!!{0}", new Object[]{_ESs});
	}
	
	synchronized public static ExecutorService initES(String name, int numberOfThreads) {
		ExecutorService es = Executors.newFixedThreadPool(numberOfThreads);
		_ESs.put(name, es);
		log.log(Level.INFO, "ExecutorService({0}) created with {1} threads.", new Object[]{name, numberOfThreads});
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
				es = initES(DEFAULT_NAME, Runtime.getRuntime().availableProcessors());
			//} else if (DB_NAME.equals(name)) {
			//	es = initES(DB_NAME, 3);
			} else {
				es = es(DEFAULT_NAME);
			}
		}
		
		return es;
	}
	
	public static <V> CompletionService<V> cs(String name) {
		return new ExecutorCompletionService<V>(es(name));
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
	
	public static void await() {
		for (ExecutorService es: _ESs.values()) {
			try {
				while(!es.awaitTermination(1, TimeUnit.MINUTES)) {
					log.log(Level.INFO, "Await..");
				}
			} catch (InterruptedException e) {
				log.log(Level.WARNING, "Await interrupt", e);
			}
		}
	}
	
	public static void shutdownThenAwait() {
		for (ExecutorService es: _ESs.values()) {
			try {
				es.shutdown();
				while(!es.awaitTermination(1, TimeUnit.MINUTES)) {
					log.log(Level.INFO, "Await..");
				}
			} catch (InterruptedException e) {
				log.log(Level.WARNING, "Await interrupt", e);
			}
		}
	}
}





















