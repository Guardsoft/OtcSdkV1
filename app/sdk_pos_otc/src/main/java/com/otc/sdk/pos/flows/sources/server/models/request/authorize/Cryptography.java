package com.otc.sdk.pos.flows.sources.server.models.request.authorize;

import java.util.List;

public class Cryptography{
	private String owner;
	private String mode;
	private List<ScopesItem> scopes;

	public void setOwner(String owner){
		this.owner = owner;
	}

	public String getOwner(){
		return owner;
	}

	public void setMode(String mode){
		this.mode = mode;
	}

	public String getMode(){
		return mode;
	}

	public void setScopes(List<ScopesItem> scopes){
		this.scopes = scopes;
	}

	public List<ScopesItem> getScopes(){
		return scopes;
	}

	@Override
 	public String toString(){
		return 
			"Cryptography{" + 
			"owner = '" + owner + '\'' + 
			",mode = '" + mode + '\'' + 
			",scopes = '" + scopes + '\'' + 
			"}";
		}
}