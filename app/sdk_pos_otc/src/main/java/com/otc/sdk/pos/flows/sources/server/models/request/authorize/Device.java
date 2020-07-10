package com.otc.sdk.pos.flows.sources.server.models.request.authorize;

public class Device{
	private boolean unattended;
	private String captureType;
	private String terminalId;
	private String serialNumber;

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

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Override
	public String toString() {
		return "Device{" +
				"unattended=" + unattended +
				", captureType='" + captureType + '\'' +
				", terminalId='" + terminalId + '\'' +
				", serialNumber='" + serialNumber + '\'' +
				'}';
	}
}
