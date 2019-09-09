package com.moneytransfer.dbrepository;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class PersistenceManager {
	

	  private static final PersistenceManager singleton = new PersistenceManager();
	  
	  protected EntityManagerFactory emf;
	  
	  private static final String PERSISTENCE_UNIT_NAME = "User";
	  
	  
	  public static PersistenceManager getInstance() {
		    
		    return singleton;
		  }
	  
	  private PersistenceManager() {
	  }
	  
	  public EntityManagerFactory getEntityManagerFactory() {
		  
		    if (emf == null)
		      createEntityManagerFactory();
		    return emf;
		    
		  }
	  
	  protected void createEntityManagerFactory() {
		    
		    this.emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);		
		  }
	  
	  public void closeEntityManagerFactory() {
		    
		    if (emf != null) {
		      emf.close();
		      emf = null;
		      
		    }
		  }

}
