package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import com.google.gson.annotations.SerializedName;

public class Merchant{

	@SerializedName("merchantId")
	private String merchantId;

	@SerializedName("merchantName")
	private String merchantName;

	public void setMerchantId(String merchantId){
		this.merchantId = merchantId;
	}

	public String getMerchantId(){
		return merchantId;
	}

	public void setMerchantName(String merchantName){
		this.merchantName = merchantName;
	}

	public String getMerchantName(){
		return merchantName;
	}

	@Override
 	public String toString(){
		return 
			"Merchant{" + 
			"merchantId = '" + merchantId + '\'' + 
			",merchantName = '" + merchantName + '\'' + 
			"}";
		}
}