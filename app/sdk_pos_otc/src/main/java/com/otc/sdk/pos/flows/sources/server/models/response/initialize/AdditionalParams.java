package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

public class AdditionalParams {
	private String p1;
	private String p2;
	private String sponsoredMerchantName;
	private String sponsoredMerchantMCCI;
	private String sponsoredMerchantId;
	private String sponsoredMerchantPhoneNumber;
	private String sponsoredMerchantAddress;

	public void setP1(String p1){
		this.p1 = p1;
	}

	public String getP1(){
		return p1;
	}

	public void setP2(String p2){
		this.p2 = p2;
	}

	public String getP2(){
		return p2;
	}

	public void setSponsoredMerchantName(String sponsoredMerchantName){
		this.sponsoredMerchantName = sponsoredMerchantName;
	}

	public String getSponsoredMerchantName(){
		return sponsoredMerchantName;
	}

	public void setSponsoredMerchantMCCI(String sponsoredMerchantMCCI){
		this.sponsoredMerchantMCCI = sponsoredMerchantMCCI;
	}

	public String getSponsoredMerchantMCCI(){
		return sponsoredMerchantMCCI;
	}

	public void setSponsoredMerchantId(String sponsoredMerchantId){
		this.sponsoredMerchantId = sponsoredMerchantId;
	}

	public String getSponsoredMerchantId(){
		return sponsoredMerchantId;
	}

	public void setSponsoredMerchantPhoneNumber(String sponsoredMerchantPhoneNumber){
		this.sponsoredMerchantPhoneNumber = sponsoredMerchantPhoneNumber;
	}

	public String getSponsoredMerchantPhoneNumber(){
		return sponsoredMerchantPhoneNumber;
	}

	public void setSponsoredMerchantAddress(String sponsoredMerchantAddress){
		this.sponsoredMerchantAddress = sponsoredMerchantAddress;
	}

	public String getSponsoredMerchantAddress(){
		return sponsoredMerchantAddress;
	}

	@Override
 	public String toString(){
		return 
			"AdditionalParams{" + 
			"p1 = '" + p1 + '\'' + 
			",p2 = '" + p2 + '\'' + 
			",sponsoredMerchantName = '" + sponsoredMerchantName + '\'' + 
			",sponsoredMerchantMCCI = '" + sponsoredMerchantMCCI + '\'' + 
			",sponsoredMerchantId = '" + sponsoredMerchantId + '\'' + 
			",sponsoredMerchantPhoneNumber = '" + sponsoredMerchantPhoneNumber + '\'' + 
			",sponsoredMerchantAddress = '" + sponsoredMerchantAddress + '\'' + 
			"}";
		}
}
