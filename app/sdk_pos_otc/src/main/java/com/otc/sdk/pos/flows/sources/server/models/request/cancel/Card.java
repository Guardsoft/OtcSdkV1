package com.otc.sdk.pos.flows.sources.server.models.request.cancel;

public class Card{
	private String track2;

	public void setTrack2(String track2){
		this.track2 = track2;
	}

	public String getTrack2(){
		return track2;
	}

	@Override
 	public String toString(){
		return 
			"Card{" + 
			"track2 = '" + track2 + '\'' + 
			"}";
		}
}
