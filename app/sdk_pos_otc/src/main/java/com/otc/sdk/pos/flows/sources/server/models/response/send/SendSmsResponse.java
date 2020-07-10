package com.otc.sdk.pos.flows.sources.server.models.response.send;

public class SendSmsResponse{
	private Header header;
	private Transaction transaction;

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setTransaction(Transaction transaction){
		this.transaction = transaction;
	}

	public Transaction getTransaction(){
		return transaction;
	}

	@Override
 	public String toString(){
		return 
			"SendSmsResponse{" + 
			"header = '" + header + '\'' + 
			",transaction = '" + transaction + '\'' + 
			"}";
		}
}
