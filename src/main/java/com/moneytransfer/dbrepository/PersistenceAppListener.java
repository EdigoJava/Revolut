package com.moneytransfer.dbrepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PersistenceAppListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		PersistenceManager.getInstance().closeEntityManagerFactory();
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		PersistenceManager.getInstance().getEntityManagerFactory();
		
	}

}
