package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

import android.os.Parcel;
import android.os.Parcelable;


public class InitializeResponse implements Parcelable {

	private Keys keys;
	private Header header;
	private Merchant merchant;
	private Device device;

	protected InitializeResponse(Parcel in) {
		keys = in.readParcelable(Keys.class.getClassLoader());
		header = in.readParcelable(Header.class.getClassLoader());
		merchant = in.readParcelable(Merchant.class.getClassLoader());
		device = in.readParcelable(Device.class.getClassLoader());
	}

	public static final Creator<InitializeResponse> CREATOR = new Creator<InitializeResponse>() {
		@Override
		public InitializeResponse createFromParcel(Parcel in) {
			return new InitializeResponse(in);
		}

		@Override
		public InitializeResponse[] newArray(int size) {
			return new InitializeResponse[size];
		}
	};

	public Keys getKeys() {
		return keys;
	}

	public void setKeys(Keys keys) {
		this.keys = keys;
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(keys, flags);
		dest.writeParcelable(header, flags);
		dest.writeParcelable(merchant, flags);
		dest.writeParcelable(device, flags);
	}

	@Override
	public String toString() {
		return "InitializeResponse{" +
				"keys=" + keys +
				", header=" + header +
				", merchant=" + merchant +
				", device=" + device +
				'}';
	}
}
