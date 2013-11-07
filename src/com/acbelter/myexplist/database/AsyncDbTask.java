package com.acbelter.myexplist.database;

import com.acbelter.myexplist.MyItem;

public class AsyncDbTask {
    public static final int UPDATE_TYPE = 0;
    public static final int INSERT_TYPE = 1;
    public static final int DELETE_TYPE = 2;

    private int mType = -1;
    private MyItem mContentItem;

    public AsyncDbTask(int type, MyItem contentItem) {
        if (type != UPDATE_TYPE && type != INSERT_TYPE && type != DELETE_TYPE) {
            throw new IllegalArgumentException("Incorrect value of type");
        }

        if (contentItem == null) {
            throw new IllegalArgumentException("Second argument must be not null");
        }

        mType = type;
        mContentItem = contentItem;
    }

    public int getType() {
        return mType;
    }
}
