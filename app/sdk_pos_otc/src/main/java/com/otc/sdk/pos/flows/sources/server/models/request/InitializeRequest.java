package com.otc.sdk.pos.flows.sources.server.models.request;

public class InitializeRequest{
	private Header header;
	private DeviceIni device;

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setDevice(DeviceIni device){
		this.device = device;
	}

	public DeviceIni getDevice(){
		return device;
	}

	@Override
 	public String toString(){
		return 
			"InitializeRequest{" + 
			"header = '" + header + '\'' + 
			",device = '" + device + '\'' +
			"}";
		}
}
