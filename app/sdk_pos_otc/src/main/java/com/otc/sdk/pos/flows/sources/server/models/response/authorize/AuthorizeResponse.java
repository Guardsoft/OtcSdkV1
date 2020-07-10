package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import android.os.Parcel;
import android.os.Parcelable;

public class AuthorizeResponse implements Parcelable{

	//private CustomFields customFields;
	private Header header;
	private Merchant merchant;
	private Device device;
	private Order order;

	public AuthorizeResponse() {
	}

	protected AuthorizeResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		merchant = in.readParcelable(Merchant.class.getClassLoader());
		device = in.readParcelable(Device.class.getClassLoader());
		order = in.readParcelable(Order.class.getClassLoader());
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(header, flags);
		dest.writeParcelable(merchant, flags);
		dest.writeParcelable(device, flags);
		dest.writeParcelable(order, flags);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<AuthorizeResponse> CREATOR = new Creator<AuthorizeResponse>() {
		@Override
		public AuthorizeResponse createFromParcel(Parcel in) {
			return new AuthorizeResponse(in);
		}

		@Override
		public AuthorizeResponse[] newArray(int size) {
			return new AuthorizeResponse[size];
		}
	};

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
			",header = '" + header + '\'' + 
			",merchant = '" + merchant + '\'' + 
			",device = '" + device + '\'' + 
			",order = '" + order + '\'' + 
			"}";
		}

}