package com.otc.sdk.pos.flows.sources.server.models.request.retrieve;

public class Paging{
	private int pageNumber;
	private int pageSize;

	public void setPageNumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

	public int getPageNumber(){
		return pageNumber;
	}

	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}

	public int getPageSize(){
		return pageSize;
	}

	@Override
 	public String toString(){
		return 
			"Paging{" + 
			"pageNumber = '" + pageNumber + '\'' + 
			",pageSize = '" + pageSize + '\'' + 
			"}";
		}
}
