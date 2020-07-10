package com.otc.sdk.pos.flows.sources.server.models.request.authorize;

public class Card{
	private String sequenceNumber;
	private String track2;
	private String pinBlock;
	private String emv;

	public void setSequenceNumber(String sequenceNumber){
		this.sequenceNumber = sequenceNumber;
	}

	public String getSequenceNumber(){
		return sequenceNumber;
	}

	public String getPinBlock() {
		return pinBlock;
	}

	public void setPinBlock(String pinBlock) {
		this.pinBlock = pinBlock;
	}

	public void setTrack2(String track2){
		this.track2 = track2;
	}

	public String getTrack2(){
		return track2;
	}

	public void setEmv(String emv){
		this.emv = emv;
	}

	public String getEmv(){
		return emv;
	}

	@Override
	public String toString() {
		return "Card{" +
				"sequenceNumber='" + sequenceNumber + '\'' +
				", track2='" + track2 + '\'' +
				", pinBlock='" + pinBlock + '\'' +
				", emv='" + emv + '\'' +
				'}';
	}
}
