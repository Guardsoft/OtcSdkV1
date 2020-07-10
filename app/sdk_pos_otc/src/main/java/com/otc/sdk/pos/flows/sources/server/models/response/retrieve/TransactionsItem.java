package com.otc.sdk.pos.flows.sources.server.models.response.retrieve;

import android.os.Parcel;
import android.os.Parcelable;

public class TransactionsItem implements Parcelable {
	private int unattended;
	private String saleId;
	private int fastPayment;
	private String traceNumber;
	private String authorizationDate;
	private int installmentAmount;
	private String bin;
	private String authorizationId;
	private String terminalId;
	private String acquirer;
	private int noInterestDiscount;
	private String merchantName;
	private double orderAmount;
	private String merchantId;
	private int authorizedAmount;
	private String actionCode;
	private String currency;
	private String brand;
	private String purchaseNumber;
	private String branchId;
	private int qr;
	private int noInterestAmount;
	private String externalId;
	private String transactionDate;
	private String last4digits;
	private int installment;
	private int settlementAmount;
	private String workflowId;
	private int countable;
	private String status;

	protected TransactionsItem(Parcel in) {
		unattended = in.readInt();
		saleId = in.readString();
		fastPayment = in.readInt();
		traceNumber = in.readString();
		authorizationDate = in.readString();
		installmentAmount = in.readInt();
		bin = in.readString();
		authorizationId = in.readString();
		terminalId = in.readString();
		acquirer = in.readString();
		noInterestDiscount = in.readInt();
		merchantName = in.readString();
		orderAmount = in.readDouble();
		merchantId = in.readString();
		authorizedAmount = in.readInt();
		actionCode = in.readString();
		currency = in.readString();
		brand = in.readString();
		purchaseNumber = in.readString();
		branchId = in.readString();
		qr = in.readInt();
		noInterestAmount = in.readInt();
		externalId = in.readString();
		transactionDate = in.readString();
		last4digits = in.readString();
		installment = in.readInt();
		settlementAmount = in.readInt();
		workflowId = in.readString();
		countable = in.readInt();
		status = in.readString();
	}

	public static final Creator<TransactionsItem> CREATOR = new Creator<TransactionsItem>() {
		@Override
		public TransactionsItem createFromParcel(Parcel in) {
			return new TransactionsItem(in);
		}

		@Override
		public TransactionsItem[] newArray(int size) {
			return new TransactionsItem[size];
		}
	};

	public void setUnattended(int unattended){
		this.unattended = unattended;
	}

	public int getUnattended(){
		return unattended;
	}

	public void setSaleId(String saleId){
		this.saleId = saleId;
	}

	public String getSaleId(){
		return saleId;
	}

	public void setFastPayment(int fastPayment){
		this.fastPayment = fastPayment;
	}

	public int getFastPayment(){
		return fastPayment;
	}

	public void setTraceNumber(String traceNumber){
		this.traceNumber = traceNumber;
	}

	public String getTraceNumber(){
		return traceNumber;
	}

	public void setAuthorizationDate(String authorizationDate){
		this.authorizationDate = authorizationDate;
	}

	public String getAuthorizationDate(){
		return authorizationDate;
	}

	public void setInstallmentAmount(int installmentAmount){
		this.installmentAmount = installmentAmount;
	}

	public int getInstallmentAmount(){
		return installmentAmount;
	}

	public void setBin(String bin){
		this.bin = bin;
	}

	public String getBin(){
		return bin;
	}

	public void setAuthorizationId(String authorizationId){
		this.authorizationId = authorizationId;
	}

	public String getAuthorizationId(){
		return authorizationId;
	}

	public void setTerminalId(String terminalId){
		this.terminalId = terminalId;
	}

	public String getTerminalId(){
		return terminalId;
	}

	public void setAcquirer(String acquirer){
		this.acquirer = acquirer;
	}

	public String getAcquirer(){
		return acquirer;
	}

	public void setNoInterestDiscount(int noInterestDiscount){
		this.noInterestDiscount = noInterestDiscount;
	}

	public int getNoInterestDiscount(){
		return noInterestDiscount;
	}

	public void setMerchantName(String merchantName){
		this.merchantName = merchantName;
	}

	public String getMerchantName(){
		return merchantName;
	}

	public void setOrderAmount(double orderAmount){
		this.orderAmount = orderAmount;
	}

	public double getOrderAmount(){
		return orderAmount;
	}

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	public void setAuthorizedAmount(int authorizedAmount){
		this.authorizedAmount = authorizedAmount;
	}

	public int getAuthorizedAmount(){
		return authorizedAmount;
	}

	public void setActionCode(String actionCode){
		this.actionCode = actionCode;
	}

	public String getActionCode(){
		return actionCode;
	}

	public void setCurrency(String currency){
		this.currency = currency;
	}

	public String getCurrency(){
		return currency;
	}

	public void setBrand(String brand){
		this.brand = brand;
	}

	public String getBrand(){
		return brand;
	}

	public void setPurchaseNumber(String purchaseNumber){
		this.purchaseNumber = purchaseNumber;
	}

	public String getPurchaseNumber(){
		return purchaseNumber;
	}

	public void setBranchId(String branchId){
		this.branchId = branchId;
	}

	public String getBranchId(){
		return branchId;
	}

	public void setQr(int qr){
		this.qr = qr;
	}

	public int getQr(){
		return qr;
	}

	public void setNoInterestAmount(int noInterestAmount){
		this.noInterestAmount = noInterestAmount;
	}

	public int getNoInterestAmount(){
		return noInterestAmount;
	}

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}

	public String getExternalId(){
		return externalId;
	}

	public void setTransactionDate(String transactionDate){
		this.transactionDate = transactionDate;
	}

	public String getTransactionDate(){
		return transactionDate;
	}

	public void setLast4digits(String last4digits){
		this.last4digits = last4digits;
	}

	public String getLast4digits(){
		return last4digits;
	}

	public void setInstallment(int installment){
		this.installment = installment;
	}

	public int getInstallment(){
		return installment;
	}

	public void setSettlementAmount(int settlementAmount){
		this.settlementAmount = settlementAmount;
	}

	public int getSettlementAmount(){
		return settlementAmount;
	}

	public void setWorkflowId(String workflowId){
		this.workflowId = workflowId;
	}

	public String getWorkflowId(){
		return workflowId;
	}

	public void setCountable(int countable){
		this.countable = countable;
	}

	public int getCountable(){
		return countable;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	@Override
 	public String toString(){
		return 
			"TransactionsItem{" + 
			"unattended = '" + unattended + '\'' + 
			",saleId = '" + saleId + '\'' + 
			",fastPayment = '" + fastPayment + '\'' + 
			",traceNumber = '" + traceNumber + '\'' + 
			",authorizationDate = '" + authorizationDate + '\'' + 
			",installmentAmount = '" + installmentAmount + '\'' + 
			",bin = '" + bin + '\'' + 
			",authorizationId = '" + authorizationId + '\'' + 
			",terminalId = '" + terminalId + '\'' + 
			",acquirer = '" + acquirer + '\'' + 
			",noInterestDiscount = '" + noInterestDiscount + '\'' + 
			",merchantName = '" + merchantName + '\'' + 
			",orderAmount = '" + orderAmount + '\'' + 
			",merchantId = '" + merchantId + '\'' + 
			",authorizedAmount = '" + authorizedAmount + '\'' + 
			",actionCode = '" + actionCode + '\'' + 
			",currency = '" + currency + '\'' + 
			",brand = '" + brand + '\'' + 
			",purchaseNumber = '" + purchaseNumber + '\'' + 
			",branchId = '" + branchId + '\'' + 
			",qr = '" + qr + '\'' + 
			",noInterestAmount = '" + noInterestAmount + '\'' + 
			",externalId = '" + externalId + '\'' + 
			",transactionDate = '" + transactionDate + '\'' + 
			",last4digits = '" + last4digits + '\'' + 
			",installment = '" + installment + '\'' + 
			",settlementAmount = '" + settlementAmount + '\'' + 
			",workflowId = '" + workflowId + '\'' + 
			",countable = '" + countable + '\'' + 
			",status = '" + status + '\'' + 
			"}";
		}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(unattended);
		parcel.writeString(saleId);
		parcel.writeInt(fastPayment);
		parcel.writeString(traceNumber);
		parcel.writeString(authorizationDate);
		parcel.writeInt(installmentAmount);
		parcel.writeString(bin);
		parcel.writeString(authorizationId);
		parcel.writeString(terminalId);
		parcel.writeString(acquirer);
		parcel.writeInt(noInterestDiscount);
		parcel.writeString(merchantName);
		parcel.writeDouble(orderAmount);
		parcel.writeString(merchantId);
		parcel.writeInt(authorizedAmount);
		parcel.writeString(actionCode);
		parcel.writeString(currency);
		parcel.writeString(brand);
		parcel.writeString(purchaseNumber);
		parcel.writeString(branchId);
		parcel.writeInt(qr);
		parcel.writeInt(noInterestAmount);
		parcel.writeString(externalId);
		parcel.writeString(transactionDate);
		parcel.writeString(last4digits);
		parcel.writeInt(installment);
		parcel.writeInt(settlementAmount);
		parcel.writeString(workflowId);
		parcel.writeInt(countable);
		parcel.writeString(status);
	}
}
