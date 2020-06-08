package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
	private boolean reloadParams;
	private boolean unattended;
	private String serialNumber;
	private AdditionalParams additionalParams;
	private AcquirerParams acquirerParams;
	private String terminalId;
	private boolean reloadKeys;

	protected Device(Parcel in) {
		reloadParams = in.readByte() != 0;
		unattended = in.readByte() != 0;
		serialNumber = in.readString();
		terminalId = in.readString();
		reloadKeys = in.readByte() != 0;
	}

	public static final Creator<Device> CREATOR = new Creator<Device>() {
		@Override
		public Device createFromParcel(Parcel in) {
			return new Device(in);
		}

		@Override
		public Device[] newArray(int size) {
			return new Device[size];
		}
	};

	public void setReloadParams(boolean reloadParams){
		this.reloadParams = reloadParams;
	}

	public boolean isReloadParams(){
		return reloadParams;
	}

	public void setUnattended(boolean unattended){
		this.unattended = unattended;
	}

	public boolean isUnattended(){
		return unattended;
	}

	public void setSerialNumber(String serialNumber){
		this.serialNumber = serialNumber;
	}

	public String getSerialNumber(){
		return serialNumber;
	}

	public void setAdditionalParams(AdditionalParams additionalParams){
		this.additionalParams = additionalParams;
	}

	public AdditionalParams getAdditionalParams(){
		return additionalParams;
	}

	public void setAcquirerParams(AcquirerParams acquirerParams){
		this.acquirerParams = acquirerParams;
	}

	public AcquirerParams getAcquirerParams(){
		return acquirerParams;
	}

	public void setTerminalId(String terminalId){
		this.terminalId = terminalId;
	}

	public String getTerminalId(){
		return terminalId;
	}

	public void setReloadKeys(boolean reloadKeys){
		this.reloadKeys = reloadKeys;
	}

	public boolean isReloadKeys(){
		return reloadKeys;
	}

	@Override
 	public String toString(){
		return 
			"Device{" + 
			"reloadParams = '" + reloadParams + '\'' + 
			",unattended = '" + unattended + '\'' + 
			",serialNumber = '" + serialNumber + '\'' + 
			",additionalParams = '" + additionalParams + '\'' + 
			",acquirerParams = '" + acquirerParams + '\'' + 
			",terminalId = '" + terminalId + '\'' + 
			",reloadKeys = '" + reloadKeys + '\'' + 
			"}";
		}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (reloadParams ? 1 : 0));
		dest.writeByte((byte) (unattended ? 1 : 0));
		dest.writeString(serialNumber);
		dest.writeString(terminalId);
		dest.writeByte((byte) (reloadKeys ? 1 : 0));
	}
}
