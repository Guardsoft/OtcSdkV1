package com.otc.sdk.pos.flows.sources.server.models.response.initialize;

public class AcquirerParams {
	private String p1;
	private String p2;

	public void setP1(String p1){
		this.p1 = p1;
	}

	public String getP1(){
		return p1;
	}

	public void setP2(String p2){
		this.p2 = p2;
	}

	public String getP2(){
		return p2;
	}

	@Override
 	public String toString(){
		return 
			"AcquirerParams{" + 
			"p1 = '" + p1 + '\'' + 
			",p2 = '" + p2 + '\'' + 
			"}";
		}
}
