package com.otc.sdk.pos.flows.sources.server.models.request.send;

public class Device{
	private String terminalId;

	public void setTerminalId(String terminalId){
		this.terminalId = terminalId;
	}

	public String getTerminalId(){
		return terminalId;
	}

	@Override
 	public String toString(){
		return 
			"Device{" + 
			"terminalId = '" + terminalId + '\'' + 
			"}";
		}
}
