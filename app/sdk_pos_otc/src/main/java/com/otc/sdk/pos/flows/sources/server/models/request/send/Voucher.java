package com.otc.sdk.pos.flows.sources.server.models.request.send;

public class Voucher{
	private String phone;
	private String signature;
	private String documentId;
	private String email;

	public void setPhone(String phone){
		this.phone = phone;
	}

	public String getPhone(){
		return phone;
	}

	public void setSignature(String signature){
		this.signature = signature;
	}

	public String getSignature(){
		return signature;
	}

	public void setDocumentId(String documentId){
		this.documentId = documentId;
	}

	public String getDocumentId(){
		return documentId;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}

	@Override
 	public String toString(){
		return 
			"Voucher{" + 
			"phone = '" + phone + '\'' + 
			",signature = '" + signature + '\'' + 
			",documentId = '" + documentId + '\'' + 
			",email = '" + email + '\'' + 
			"}";
		}
}
