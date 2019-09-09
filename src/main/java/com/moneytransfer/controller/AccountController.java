package com.moneytransfer.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.moneytransfer.dbrepository.PersistenceManager;
import com.moneytransfer.entity.Account;


/*
 * Warning !! This controller is created for purpose of testing !!
 * 
 * It is not a task requriement.
 * 
 * I cover creating accounts for testing in my Testing class but I add this controller in case of:
 * you want to test it via api with the tools such as POSTMAN. It is easier.
 * 
 * For this purpose I do not create AccountRepository or any Service or validator etc.
 */


@Path("/accounts")
public class AccountController {
	
	EntityManagerFactory emf = PersistenceManager.getInstance().getEntityManagerFactory();
	
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Account> getAllAccounts () {
		
	   EntityManager em = emf.createEntityManager();
	   
	   Query qr = em.createQuery("SELECT a FROM Account a");
	   List<Account> accountList = qr.getResultList();
			   
	   em.close();
		  		  
	   return accountList;
	  }
		
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Account getTransferById (@PathParam("id") long id) {
		
	   EntityManager em = emf.createEntityManager();
	   
	   Account acc = em.find(Account.class, id);
	   
	   em.close();
		  		  
	   return acc;
	  }
	
	
	@POST
	@Path("/saveaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response saveAccount(Account account) {
		
	    EntityManager em = emf.createEntityManager();
	    
	    em.getTransaction().begin();
	    
	    em.persist(account);
	    
	    em.getTransaction().commit();
	    
	    em.close();
		
		return Response.status(200).entity("Account is saved!").build();
				
	}
}
