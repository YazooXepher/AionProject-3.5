package com.aionemu.loginserver;

import com.aionemu.commons.services.CronService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.utils.ExitCode;
import com.aionemu.loginserver.network.NetConnector;
import com.aionemu.loginserver.utils.ThreadPoolManager;

/**
 * @author -Nemesiss-, nrg
 */
public class Shutdown extends Thread {

	/**
	 * Logger for this class
	 */
	private static final Logger log = LoggerFactory.getLogger(Shutdown.class);
	/**
	 * Instance of Shutdown.
	 */
	private static Shutdown instance = new Shutdown();
        /**
         * Indicates wether the loginserver should shut dpwn or only restart
         */
        private static boolean restartOnly = false;
        
        /**
         * Set's restartOnly attribute
         * @param restartOnly Indicates wether the loginserver should shut dpwn or only restart
         */
        public void setRestartOnly(boolean restartOnly) {
            Shutdown.restartOnly = restartOnly;
        }

	/**
	 * get the shutdown-hook instance the shutdown-hook instance is created by the first call of this function, but it has
	 * to be registrered externaly.
	 * 
	 * @return instance of Shutdown, to be used as shutdown hook
	 */
	public static Shutdown getInstance() {
		return instance;
	}

	/**
	 * this function is called, when a new thread starts if this thread is the thread of getInstance, then this is the
	 * shutdown hook and we save all data and disconnect all clients. after this thread ends, the server will completely
	 * exit if this is not the thread of getInstance, then this is a countdown thread. we start the countdown, and when we
	 * finished it, and it was not aborted, we tell the shutdown-hook why we call exit, and then call exit when the exit
	 * status of the server is 1, startServer.sh / startServer.bat will restart the server.
	 */
	@Override
	public void run() {
		try
		{
			NetConnector.getInstance().shutdown();
		}
		catch (Throwable t)
		{
			log.error("Can't shutdown NetConnector", t);
		}
		/* Shuting down DB connections */
		try {
			DatabaseFactory.shutdown();
		}
		catch (Throwable t) {
			log.error("Can't shutdown DatabaseFactory", t);
		}

		// shutdown cron service prior to threadpool shutdown
		CronService.getInstance().shutdown();

		/* Shuting down threadpools */
		try {
			ThreadPoolManager.getInstance().shutdown();
		}
		catch (Throwable t) {
			log.error("Can't shutdown ThreadPoolManager", t);
		}
                
		// Do system exit
		if (restartOnly)
			Runtime.getRuntime().halt(ExitCode.CODE_RESTART);
		else
			Runtime.getRuntime().halt(ExitCode.CODE_NORMAL);
	}
}