package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import com.google.gson.annotations.SerializedName;

public class AuthorizeResponse {

	@SerializedName("customFields")
	private CustomFields customFields;

	@SerializedName("header")
	private Header header;

	@SerializedName("merchant")
	private Merchant merchant;

	@SerializedName("device")
	private Device device;

	@SerializedName("order")
	private Order order;

	public void setCustomFields(CustomFields customFields){
		this.customFields = customFields;
	}

	public CustomFields getCustomFields(){
		return customFields;
	}

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setMerchant(Merchant merchant){
		this.merchant = merchant;
	}

	public Merchant getMerchant(){
		return merchant;
	}

	public void setDevice(Device device){
		this.device = device;
	}

	public Device getDevice(){
		return device;
	}

	public void setOrder(Order order){
		this.order = order;
	}

	public Order getOrder(){
		return order;
	}

	@Override
 	public String toString(){
		return 
			"AuthorizeResponse{" +
			"customFields = '" + customFields + '\'' + 
			",header = '" + header + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",device = '" + device + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}
}