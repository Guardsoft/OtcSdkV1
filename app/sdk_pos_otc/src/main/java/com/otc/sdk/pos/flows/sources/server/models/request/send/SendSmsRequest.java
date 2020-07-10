package com.otc.sdk.pos.flows.sources.server.models.request.send;

public class SendSmsRequest{
	private Voucher voucher;
	private Header header;
	private Merchant merchant;
	private Device device;
	private Order order;

	public void setVoucher(Voucher voucher){
		this.voucher = voucher;
	}

	public Voucher getVoucher(){
		return voucher;
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
			"SendSmsRequest{" + 
			"voucher = '" + voucher + '\'' + 
			",header = '" + header + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",device = '" + device + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}
}
