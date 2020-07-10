package com.otc.sdk.pos.flows.sources.server.models.response.retrieve;

import android.os.Parcel;
import android.os.Parcelable;

public class Paging implements Parcelable {
	private int pageNumber;
	private int totalPages;
	private int pageSize;

	protected Paging(Parcel in) {
		pageNumber = in.readInt();
		totalPages = in.readInt();
		pageSize = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(pageNumber);
		dest.writeInt(totalPages);
		dest.writeInt(pageSize);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<Paging> CREATOR = new Creator<Paging>() {
		@Override
		public Paging createFromParcel(Parcel in) {
			return new Paging(in);
		}

		@Override
		public Paging[] newArray(int size) {
			return new Paging[size];
		}
	};

	public void setPageNumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

	public int getPageNumber(){
		return pageNumber;
	}

	public void setTotalPages(int totalPages){
		this.totalPages = totalPages;
	}

	public int getTotalPages(){
		return totalPages;
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
			",totalPages = '" + totalPages + '\'' + 
			",pageSize = '" + pageSize + '\'' + 
			"}";
		}
}
