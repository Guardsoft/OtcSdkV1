package com.otc.sdk.pos.flows.sources.server.models.request.cancel;

public class VoidRequest {
	private Object customFields;
	private Header header;
	private Merchant merchant;
	private Cryptography cryptography;
	private Device device;
	private Card card;
	private Order order;

	public void setCustomFields(Object customFields){
		this.customFields = customFields;
	}

	public Object getCustomFields(){
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

	public void setCryptography(Cryptography cryptography){
		this.cryptography = cryptography;
	}

	public Cryptography getCryptography(){
		return cryptography;
	}

	public void setDevice(Device device){
		this.device = device;
	}

	public Device getDevice(){
		return device;
	}

	public void setCard(Card card){
		this.card = card;
	}

	public Card getCard(){
		return card;
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
			"VoidRequest{" +
			"customFields = '" + customFields + '\'' + 
			",header = '" + header + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",cryptography = '" + cryptography + '\'' + 
			",device = '" + device + '\'' + 
			",card = '" + card + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}
}
