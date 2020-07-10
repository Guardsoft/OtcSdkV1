package com.otc.sdk.pos.flows.sources.server.models.request.retrieve;

public class Card {

	private String track2;
	private String sequenceNumber;

	public void setTrack2(String track2){
		this.track2 = track2;
	}

	public String getTrack2(){
		return track2;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@Override
	public String toString() {
		return "Card{" +
				"track2='" + track2 + '\'' +
				", sequenceNumber='" + sequenceNumber + '\'' +
				'}';
	}
}
