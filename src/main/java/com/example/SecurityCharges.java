package com.example;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SecurityCharges {
	
	private String villaNo;
	private String name;
	private String mobile;
	private List<MonthlyChargesDetail> monthlyCharges;
	
	public MonthlyChargesDetail createMonthlyCharges() {
		return new MonthlyChargesDetail();
	}
	
	class MonthlyChargesDetail {
		
		private String month;
		private int amount;
		private String status;
		private LocalDate dueDate;
		private LocalDate paymentDate;
		private String paymentMode;
		private String receiptNo;
		private String transactionId;
		
		public String getMonth() {
			return month;
		}
		public void setMonth(String month) {
			this.month = month;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public LocalDate getDueDate() {
			return dueDate;
		}
		public void setDueDate(LocalDate dueDate) {
			this.dueDate = dueDate;
		}
		public LocalDate getPaymentDate() {
			return paymentDate;
		}
		public void setPaymentDate(LocalDate paymentDate) {
			this.paymentDate = paymentDate;
		}
		public String getPaymentMode() {
			return paymentMode;
		}
		public void setPaymentMode(String paymentMode) {
			this.paymentMode = paymentMode;
		}
		public String getReceiptNo() {
			return receiptNo;
		}
		public void setReceiptNo(String receiptNo) {
			this.receiptNo = receiptNo;
		}
		public String getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(String transactionId) {
			this.transactionId = transactionId;
		}
		
	}

	public String getVillaNo() {
		return villaNo;
	}

	public void setVillaNo(String villaNo) {
		this.villaNo = villaNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public List<MonthlyChargesDetail> getMonthlyCharges() {
		return monthlyCharges;
	}

	public void setMonthlyCharges(List<MonthlyChargesDetail> monthlyCharges) {
		this.monthlyCharges = monthlyCharges;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) 
            return true; 
        
        if(obj == null || obj.getClass()!= this.getClass()) 
            return false; 
        
		return this.getVillaNo().equals(((SecurityCharges)obj).getVillaNo());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(villaNo);
	}

}
