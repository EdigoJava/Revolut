package com.moneytransfer.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="TRANSFER")
public class Transfer {
	
	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long id;
	
	@Column(name="AMOUNT")
	private BigDecimal transferAmount;
	
	@Column(name="FROMACCID")
	private long fromAccID;
	
	@Column(name="TOACCID")
	private long toAccID;
	
	public Transfer() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BigDecimal getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(BigDecimal transferAmount) {
		this.transferAmount = transferAmount;
	}

	public long getFromAccID() {
		return fromAccID;
	}

	public void setFromAccID(long fromAccID) {
		this.fromAccID = fromAccID;
	}

	public long getToAccID() {
		return toAccID;
	}

	public void setToAccID(int toAccID) {
		this.toAccID = toAccID;
	}
}
