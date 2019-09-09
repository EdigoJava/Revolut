package com.moneytransfer.controller;

import java.io.IOException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import com.moneytransfer.entity.Transfer;
import com.moneytransfer.service.TransferService;
import com.moneytransfer.validator.TransferValidator;


@Path("/transfers")
public class TransferController {
		
	private TransferService transferService = TransferService.getInstance();
		
	private TransferValidator validator = new TransferValidator();
	
	private String clientResult = "";
			
	public TransferController() {

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllAccounts () {
		
	   List<Transfer> transferResults = transferService.getAllTransfers();
	   
	   if(transferResults.size() == 0) {
		   return Response.status(Response.Status.OK).entity("There is no transfer in the system!").build();
	   }
	   
	   else {
		   
		   return Response.ok(transferResults).build();	   
	   }
	  }

	@GET
	@Path("/{id}")
	public Response getTransferById(@PathParam("id") long id) {
		
	   if(id<0) {			   
			return Response.serverError().entity("ID can not be negative!").build();
		   }
		   	   		
	   Transfer resultTransfer = transferService.getTransferByID(id);
	   
	   if(resultTransfer!=null) {
		   convertObjectToJSON(resultTransfer);
		   return  Response.ok(convertObjectToJSON(resultTransfer), MediaType.APPLICATION_JSON).build();
	   }	   
	   else {
		   return Response.status(Response.Status.OK).entity("Transfer not found for ID: " + id).build();
	   }
	  }
	  	  
	
	  //http://localhost:8080/moneytransferservice/api/transfers/maketransfer
	  @POST
	  @Path("/maketransfer")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public Response makeTransfer(Transfer transfer) {
		  		  
		  validator = validator.validateTransfer(transfer);
		  
		  if(validator.getHttpstatus() == 200 ) {
			  clientResult = transferService.makeTransfer(transfer);
			  return Response.status(200).entity(clientResult).build();			  
		  }
		  		  		  		  		  
		  return Response.status(validator.getHttpstatus()).entity(validator.getValidationResult()).build();
	  }
	
	  
	//To do : Move this into helper class if you need to use it more !  
	private String convertObjectToJSON(Object obj) {
		
		ObjectMapper mapper = new ObjectMapper();
		String jsonStr = "";
		
	       try { 
	    	   
	            jsonStr = mapper.writeValueAsString((Transfer)obj); 
	  
	            return jsonStr; 
	        } 
	  
	        catch (IOException e) { 
	        	jsonStr ="There is technical problem please try again later";
	            e.printStackTrace();
	            return jsonStr;
	        } 		
	}

	public String getClientResult() {
		return clientResult;
	}

	public void setClientResult(String clientResult) {
		this.clientResult = clientResult;
	}
}
