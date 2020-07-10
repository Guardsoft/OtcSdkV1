package com.otc.sdk.pos.flows.sources.server.models.request.retrieve;

public class RetrieveRequest {
	private Object customFields;
	private Header header;
	private Merchant merchant;
	private Cryptography cryptography;
	private Paging paging;
	private Device device;
	private Card card;

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

	public void setPaging(Paging paging){
		this.paging = paging;
	}

	public Paging getPaging(){
		return paging;
	}

	public void setDevice(Device device){
		this.device = device;
	}

	public Device getDevice(){
		return device;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	@Override
	public String toString() {
		return "RetrieveRequest{" +
				"customFields=" + customFields +
				", header=" + header +
				", merchant=" + merchant +
				", cryptography=" + cryptography +
				", paging=" + paging +
				", device=" + device +
				", card=" + card +
				'}';
	}
}
