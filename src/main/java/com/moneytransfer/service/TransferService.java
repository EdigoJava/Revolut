package com.moneytransfer.service;

import java.util.List;

import com.moneytransfer.dbrepository.TransferRepository;
import com.moneytransfer.entity.Transfer;
import com.moneytransfer.validator.TransferValidator;

public class TransferService {
	
	private TransferRepository transferRepo = new TransferRepository();
	
	private static TransferService instance;
	
	public synchronized static TransferService getInstance() {
		
	    if (instance == null) {
	          instance = new TransferService();
	    }
	       return instance;
	    }
	   
    private TransferService() {
    	
	  }
	
	public synchronized String makeTransfer(Transfer transfer) {
				
		String serviceResult = "";
				
		try {
			transferRepo.saveTransfer(transfer);
		} catch (Exception e) {
			serviceResult = TransferValidator.TECHNICAL_PROBLEM;
			e.printStackTrace();
		}
		
		return serviceResult;		
	}
	
	public Transfer getTransferByID(long id) {
		
		return transferRepo.getTransferByID(id);
	}
	
	public List<Transfer> getAllTransfers(){
				
		return transferRepo.getAllTransfers();
	}
}
