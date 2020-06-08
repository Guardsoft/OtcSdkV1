package com.otc.sdk.pos.flows.sources.server.models.request;

import java.util.List;

public class ScopesItem{
	private List<String> elements;
	private String keyId;
	private String keyType;

	public void setElements(List<String> elements){
		this.elements = elements;
	}

	public List<String> getElements(){
		return elements;
	}

	public void setKeyId(String keyId){
		this.keyId = keyId;
	}

	public String getKeyId(){
		return keyId;
	}

	public void setKeyType(String keyType){
		this.keyType = keyType;
	}

	public String getKeyType(){
		return keyType;
	}

	@Override
 	public String toString(){
		return 
			"ScopesItem{" + 
			"elements = '" + elements + '\'' + 
			",keyId = '" + keyId + '\'' + 
			",keyType = '" + keyType + '\'' + 
			"}";
		}
}