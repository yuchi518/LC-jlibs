package me.tuple.util;

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
}





















