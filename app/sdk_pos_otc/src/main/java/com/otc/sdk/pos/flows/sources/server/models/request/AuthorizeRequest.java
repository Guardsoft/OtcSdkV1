package com.otc.sdk.pos.flows.sources.server.models.request;

public class AuthorizeRequest{
	private Header header;
	private Cryptography cryptography;
	private Merchant merchant;
	private DeviceAuth device;
	private Card card;
	private Order order;

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setCryptography(Cryptography cryptography){
		this.cryptography = cryptography;
	}

	public Cryptography getCryptography(){
		return cryptography;
	}

	public void setMerchant(Merchant merchant){
		this.merchant = merchant;
	}

	public Merchant getMerchant(){
		return merchant;
	}

	public void setDevice(DeviceAuth device){
		this.device = device;
	}

	public DeviceAuth getDevice(){
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
			"AuthorizeRequest{" + 
			"header = '" + header + '\'' + 
			",cryptography = '" + cryptography + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",deviceAuth = '" + device + '\'' +
			",card = '" + card + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}
}
