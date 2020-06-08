package com.otc.sdk.pos.flows.sources.server.models.request;

public class DeviceAuth {
	private boolean unattended;
	private String captureType;
	private String terminalId;

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

	@Override
 	public String toString(){
		return 
			"DeviceAuth{" +
			"unattended = '" + unattended + '\'' + 
			",captureType = '" + captureType + '\'' + 
			",terminalId = '" + terminalId + '\'' + 
			"}";
		}
}
