package com.senception.contextualmanager.inference;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Senception on 06/03/2018.
 */

public class MyObject implements Parcelable{

    private int test;

    public MyObject(int i){
        test = i;
    }

    protected MyObject(Parcel in) {
        test = in.readInt();
    }

    public static final Creator<MyObject> CREATOR = new Creator<MyObject>() {
        @Override
        public MyObject createFromParcel(Parcel in) {
            return new MyObject(in);
        }

        @Override
        public MyObject[] newArray(int size) {
            return new MyObject[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(test);
    }
}
