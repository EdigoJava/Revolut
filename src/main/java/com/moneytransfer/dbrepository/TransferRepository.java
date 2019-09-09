package com.moneytransfer.dbrepository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.moneytransfer.entity.Account;
import com.moneytransfer.entity.Transfer;
import com.moneytransfer.validator.TransferValidator;

public class TransferRepository {
	     
    EntityManagerFactory emf = PersistenceManager.getInstance().getEntityManagerFactory();
    
	
	public TransferRepository() {
	  }
	 
	 	
	public Transfer getTransferByID(long id) {
		
		try {
			EntityManager em = emf.createEntityManager();
			
			Transfer resultTransfer = em.find(Transfer.class, id);
			
			return resultTransfer;
		} catch (Exception e) {
			
			e.printStackTrace();
			return null;
		}								
	}
	
	public List<Transfer> getAllTransfers(){
		
		EntityManager em = emf.createEntityManager();
		
		Query qr = em.createQuery("SELECT t FROM Transfer t");
		List<Transfer> transferList = qr.getResultList();
		
		em.close();
				
		return transferList;
		
	}
	
	
	public String saveTransfer(Transfer transfer) {
		
		String transactionResult = "";
				
        EntityManager em = emf.createEntityManager();
                
        em = emf.createEntityManager();
                                                      
        Account from;
		Account to;
				
		try {
			
			//Transaction begin
			
			em.getTransaction().begin();
						
			from = em.find(Account.class, transfer.getFromAccID(),LockModeType.PESSIMISTIC_WRITE);
						
			to = em.find(Account.class, transfer.getToAccID(),LockModeType.PESSIMISTIC_WRITE);
			
			if(from==null) {
				
				transactionResult = TransferValidator.ACCOUNT_NUMBER_WRONG;
				em.getTransaction().rollback();
				em.close();				
				return transactionResult;
				
			}
			if(to ==null) {
				
				transactionResult = TransferValidator.TRANSFER_ACCOUNT_NUMBER_WRONG;
				em.getTransaction().rollback();
				em.close();
				return transactionResult;				
			}
			
			if((from.getBalance().compareTo(transfer.getTransferAmount()) == 1) ||
					(from.getBalance().compareTo(transfer.getTransferAmount()) == 0)) {
				
			    from.setBalance(from.getBalance().subtract(transfer.getTransferAmount()));
		        
		        to.setBalance(to.getBalance().add(transfer.getTransferAmount()));
		        
		        em.persist(transfer);
		        
		        em.getTransaction().commit();
		        
		        em.close();
		    	
				transactionResult = TransferValidator.TRANSFER_SUCCESSFULL_MESSAGE;
				
				return transactionResult;
				
			}
			
			else {
							
				transactionResult = TransferValidator.INSUFFICIENT_ACCOUNT_BALANCE;
				em.getTransaction().rollback();
				em.close();
				return transactionResult;
				
			}
				        
	        //Transaction End!;
	        
	        				
		} catch (Exception e) {
			
			transactionResult = TransferValidator.TECHNICAL_PROBLEM;
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
			return transactionResult;
			
		}
	
	}
}
