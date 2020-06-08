package com.otc.sdk.pos.flows.sources.server.models.response.authorize;

import com.google.gson.annotations.SerializedName;

public class Device{
	@SerializedName("reloadParams")
	private boolean reloadParams;

	@SerializedName("unattended")
	private boolean unattended;

	@SerializedName("captureType")
	private String captureType;

	@SerializedName("terminalId")
	private String terminalId;

	@SerializedName("serialNumber")
	private String serialNumber;

	@SerializedName("reloadKeys")
	private boolean reloadKeys;

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
}