package com.moneytransfer.validator;

import java.math.BigDecimal;

import com.moneytransfer.entity.Transfer;

public class TransferValidator {
	
	public static String TRANSFER_SUCCESSFULL_MESSAGE = "Transfer is successfully made!";	
	public static String TECHNICAL_PROBLEM = "There is a technical problem currently ! Please try again later...";
	
	public static String ACCOUNT_NUMBER_WRONG = "We couldn't find your account , please check account number...";
	public static String TRANSFER_ACCOUNT_NUMBER_WRONG = "We couldn't find the sending account, please check the transfering account... ";
	public static String TRANSFER_AMOUNT_WRONG = "Transfer amount must greater than zero!";
	public static String INSUFFICIENT_ACCOUNT_BALANCE = "Your account balance is insufficient with this transaciton !";
	
	
	
	private boolean isValid =false;
	private String validationResult;
	private int httpstatus = 400;//Bad Request
	
	
	public TransferValidator validateTransfer(Transfer transfer) {
		
		if(transfer.getFromAccID()==0) {			
			validationResult = ACCOUNT_NUMBER_WRONG;
			return this;						
		}
		
	    if(transfer.getToAccID()==0) {	    	
	    	validationResult = TRANSFER_ACCOUNT_NUMBER_WRONG;
			return this;						
		}
		
		if((transfer.getTransferAmount() == null)||(transfer.getTransferAmount().compareTo(BigDecimal.ZERO) <= 0)) {			
			validationResult = TRANSFER_AMOUNT_WRONG;
			return this;						
		}
		
		validationResult = "Ok";
		isValid  = true;
		httpstatus = 200;
		
		return this;		
	}


	public boolean isValid() {
		return isValid;
	}



	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}



	public String getValidationResult() {
		return validationResult;
	}



	public void setValidationResult(String validationResult) {
		this.validationResult = validationResult;
	}


	public int getHttpstatus() {
		return httpstatus;
	}


	public void setHttpstatus(int httpstatus) {
		this.httpstatus = httpstatus;
	}
	
}
