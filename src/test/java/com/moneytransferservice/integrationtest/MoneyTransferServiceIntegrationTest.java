package com.moneytransferservice.integrationtest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.net.URI;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import com.moneytransfer.controller.TransferController;
import com.moneytransfer.dbrepository.PersistenceManager;
import com.moneytransfer.dbrepository.TransferRepository;
import com.moneytransfer.entity.Account;
import com.moneytransfer.entity.Transfer;
import com.moneytransfer.validator.TransferValidator;


public class MoneyTransferServiceIntegrationTest extends JerseyTest{
		  
	  private TransferRepository repo;
	  	  
	  EntityManagerFactory emf = PersistenceManager.getInstance().getEntityManagerFactory();
	  
	  private static final String BASE_URI = "http://localhost:8080/api/";
	  
	  private static HttpServer server;
	  
	  ResourceConfig rc;
	  
	  protected ResourceConfig configure() {
	        return new ResourceConfig(TransferController.class);
	    }
	  	
	
	@Before
	public  void setUp() {
		
		repo = new TransferRepository();
				
	}
	
	//Repositoty Integration Test
	
	@Test
	public void makeTransferSuccessFullyAndGetTransferByIDTest() {
		
		addEntities();//From Balance:200, To Balance:100
										
		Transfer trf1 =  createTransferObject(1,2,BigDecimal.valueOf(50));// Transfer Amount is 50
				
		EntityManager em = emf.createEntityManager();
		                                      							
		assertEquals("Transfer is successfully made!", repo.saveTransfer(trf1));
		
		//First Transfer Entity is commited to DB
						
		BigDecimal fromBalance = em.find(Account.class, Long.valueOf(1)).getBalance();
		 
		BigDecimal toBalance = em.find(Account.class, Long.valueOf(2)).getBalance();
		
		assertEquals(fromBalance, toBalance);//150==150
				
		assertEquals(BigDecimal.valueOf(50.00).intValue(), repo.getTransferByID(1).getTransferAmount().intValue());//150==150

		deleteAccountTable();
		
		em.close();
								
	}
	
	
	@Test
	public void fromAccountNotFoundTest() {
		
		addEntities();
				
		Transfer trf1 =  createTransferObject(3,2,BigDecimal.valueOf(50));
		
		EntityManager em = emf.createEntityManager();
		
		assertEquals(TransferValidator.ACCOUNT_NUMBER_WRONG, repo.saveTransfer(trf1));
						
		em.close();
		
		deleteAccountTable();
		
	}
	
	@Test
	public void toAccountNotFoundTest() {
		
		addEntities();
				
		Transfer trf1 =  createTransferObject(1,3,BigDecimal.valueOf(50));
		
		assertEquals(TransferValidator.TRANSFER_ACCOUNT_NUMBER_WRONG, repo.saveTransfer(trf1));
		
		deleteAccountTable();
		
	}
	
	public void insufficientBalanceTest() {
		
		addEntities();
		
		Transfer trf1 =  createTransferObject(1,2,BigDecimal.valueOf(250));//250 > 200
		
		assertEquals(TransferValidator.INSUFFICIENT_ACCOUNT_BALANCE, repo.saveTransfer(trf1));
		
		deleteAccountTable();
		
	}
	
	// Controller Integration Tests:
	
	   @Test
	   public void testMakeTransferControllerSuccessfullyAndGetByID() {
		   
		   addEntities();
		   
		   if(server==null || !server.isStarted()) {
			   
			   rc = configure();
			   
			   server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc); 
			   
		   }
		    		   		   
		   Transfer transfer = createTransferObject(1, 2, BigDecimal.valueOf(50));
		   		   		   
		   Client client = ClientBuilder.newClient();
		   
		   Response response = client.target(BASE_URI).path("transfers/maketransfer").request(MediaType.APPLICATION_JSON_TYPE)
			        .post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));
		   
		   
		 //Second Transfer Entity is commited to DB
		   
		   		   		   
		   assertEquals(Status.OK.getStatusCode(), response.getStatus());
		   
		   //Make Successfull finished.
		   
		   //getTransferById Starts
		   		   
		   Client client2 = ClientBuilder.newClient();
		   
		   // There 2 Transfer entities in the DB
		   Transfer resultTransfer = client.target(BASE_URI).path("transfers/1").request(MediaType.APPLICATION_JSON).get(Transfer.class);
		   
		   long control = 1;

		   assertEquals(resultTransfer.getId(), control);
		   
		   client2 = null;
		   
		   client2 = ClientBuilder.newClient();
		   
		   Response resp = client.target(BASE_URI).path("transfers/3").request(MediaType.APPLICATION_JSON).get();
		   
		   assertEquals(true,resp.readEntity(String.class).startsWith("Transfer not found for ID:"));
		  		   
		   deleteAccountTable();			        	     	   	   		   
	   }
	   
	   @Test
	   public void testWrongAccountNumber() {
		   
		   addEntities();
		   		   
		   Transfer transfer = createTransferObject(0, 2,BigDecimal.valueOf(50));
		   
		   Client client = ClientBuilder.newClient();
		   
		   Response response = client.target(BASE_URI).path("transfers/maketransfer").request(MediaType.APPLICATION_JSON_TYPE)
			        .post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));
		   
		   assertEquals(400, response.getStatus());
		   
		   deleteAccountTable();
		   
	   }
	   
	   @Test
	   public void testWrongTransferingAccountNumber() {
		   
		   rc = configure();
		   
		   server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);  
		   
		   addEntities();
		   		   
		   Transfer transfer = createTransferObject(1, 0,BigDecimal.valueOf(50));
		   
		   Client client = ClientBuilder.newClient();
		   
		   Response response = client.target(BASE_URI).path("transfers/maketransfer").request(MediaType.APPLICATION_JSON_TYPE)
			        .post(Entity.entity(transfer, MediaType.APPLICATION_JSON_TYPE));
		   
		   assertEquals(400, response.getStatus());
		   
		   deleteAccountTable();
		   
	   }
	
		
	private void deleteAccountTable() {
		
		EntityManager em = emf.createEntityManager();
		
		em.getTransaction().begin();
		
		em.createQuery("DELETE FROM Account a").executeUpdate();
				
		em.getTransaction().commit();
		
	}
	
	private Transfer createTransferObject(int fromId,int toId, BigDecimal amount) {
		
		Transfer trf1 = new Transfer();
		
		trf1.setFromAccID(fromId);
		trf1.setToAccID(toId);
		trf1.setTransferAmount(amount);
		
		return trf1;
						
	}
	
	private void addEntities() {
		
		EntityManager em = emf.createEntityManager();
		
		Account acc1 = new Account();  
		acc1.setId(1);
        acc1.setFirstName("Ozan"); 
        acc1.setBalance(new BigDecimal(200));
        
        Account acc2 = new Account();
        acc2.setId(2);
        acc2.setFirstName("Derman"); 
        acc2.setBalance(new BigDecimal(100));
        
        em.getTransaction().begin();
        
        em.merge(acc1);
        em.merge(acc2);
        
        em.getTransaction().commit();

        // Can not restart SEQUENCE ID which is a Primary Key
        // So Update them 1 and 2 to find by ID easy
        em.getTransaction().begin();
        
        Query qr = em.createNativeQuery("UPDATE ACCOUNT SET ID = 1 WHERE FIRSTNAME = ?");
        qr.setParameter(1, "Ozan");
        
        qr.executeUpdate();
                
        Query qr2 = em.createNativeQuery("UPDATE ACCOUNT SET ID = 2 WHERE FIRSTNAME = ?");
        qr2.setParameter(1, "Derman");
        
        qr2.executeUpdate();
        
        em.getTransaction().commit();
        
        em.close();
	}
}
