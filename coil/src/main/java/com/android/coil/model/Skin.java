package com.android.coil.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;

public class Skin implements Parcelable {
    @ColorInt
    private int borderColor;

    @ColorInt
    private int baseLineColor;

    @ColorInt
    private int threadColor;

    public Skin(int borderColor, int baseLineColor, int threadColor) {
        this.borderColor = borderColor;
        this.baseLineColor = baseLineColor;
        this.threadColor = threadColor;
    }

    protected Skin(Parcel in) {
        borderColor = in.readInt();
        baseLineColor = in.readInt();
        threadColor = in.readInt();
    }

    public static final Creator<Skin> CREATOR = new Creator<Skin>() {
        @Override
        public Skin createFromParcel(Parcel in) {
            return new Skin(in);
        }

        @Override
        public Skin[] newArray(int size) {
            return new Skin[size];
        }
    };

    public int getBorderColor() {
        return borderColor;
    }

    public int getBaseLineColor() {
        return baseLineColor;
    }

    public int getThreadColor() {
        return threadColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(borderColor);
        parcel.writeInt(baseLineColor);
        parcel.writeInt(threadColor);
    }
}
