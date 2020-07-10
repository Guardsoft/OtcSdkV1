package com.otc.sdk.pos.flows.sources.server.models.response.authorize;


import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable {
	private boolean reloadParams;
	private boolean unattended;
	private String captureType;
	private String terminalId;
	private String serialNumber;
	private boolean reloadKeys;

	protected Device(Parcel in) {
		reloadParams = in.readByte() != 0;
		unattended = in.readByte() != 0;
		captureType = in.readString();
		terminalId = in.readString();
		serialNumber = in.readString();
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

	public void setCaptureType(String captureType){
		this.captureType = captureType;
	}

	public String getCaptureType(){
		return captureType;
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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString() {
		return "Device{" +
				"reloadParams=" + reloadParams +
				", unattended=" + unattended +
				", captureType='" + captureType + '\'' +
				", terminalId='" + terminalId + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				", reloadKeys=" + reloadKeys +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (reloadParams ? 1 : 0));
		dest.writeByte((byte) (unattended ? 1 : 0));
		dest.writeString(captureType);
		dest.writeString(terminalId);
		dest.writeString(serialNumber);
		dest.writeByte((byte) (reloadKeys ? 1 : 0));
	}
}