package com.otc.sdk.pos.flows.sources.config;

import android.os.Parcel;
import android.os.Parcelable;

public class CustomError implements Parcelable {

    private final int statusCode;
    private final String message;

    public CustomError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    protected CustomError(Parcel in) {
        statusCode = in.readInt();
        message = in.readString();
    }

    public static final Creator<CustomError> CREATOR = new Creator<CustomError>() {
        @Override
        public CustomError createFromParcel(Parcel in) {
            return new CustomError(in);
        }

        @Override
        public CustomError[] newArray(int size) {
            return new CustomError[size];
        }
    };

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusCode);
        dest.writeString(message);
    }

    @Override
    public String toString() {
        return "CustomError{" +
                "statusCode=" + statusCode +
                ", message='" + message + '\'' +
                '}';
    }
}
