package com.otc.sdk.pos.flows.sources.server.models.response.retrieve;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RetrieveResponse implements Parcelable {
	private Header header;
	private Paging paging;
	private List<TransactionsItem> transactions;

	protected RetrieveResponse(Parcel in) {
		header = in.readParcelable(Header.class.getClassLoader());
		paging = in.readParcelable(Paging.class.getClassLoader());
		transactions = in.createTypedArrayList(TransactionsItem.CREATOR);
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(header, flags);
		dest.writeParcelable(paging, flags);
		dest.writeTypedList(transactions);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<RetrieveResponse> CREATOR = new Creator<RetrieveResponse>() {
		@Override
		public RetrieveResponse createFromParcel(Parcel in) {
			return new RetrieveResponse(in);
		}

		@Override
		public RetrieveResponse[] newArray(int size) {
			return new RetrieveResponse[size];
		}
	};

	public void setHeader(Header header){
		this.header = header;
	}

	public Header getHeader(){
		return header;
	}

	public void setPaging(Paging paging){
		this.paging = paging;
	}

	public Paging getPaging(){
		return paging;
	}

	public void setTransactions(List<TransactionsItem> transactions){
		this.transactions = transactions;
	}

	public List<TransactionsItem> getTransactions(){
		return transactions;
	}

	@Override
	public String toString() {
		return "RetrieveResponse{" +
				"header=" + header +
				", paging=" + paging +
				", transactions=" + transactions +
				'}';
	}
}