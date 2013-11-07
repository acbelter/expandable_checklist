package com.acbelter.myexplist.database;

import com.acbelter.myexplist.MyItem;

public class AsyncDbTask {
    public static final int TYPE_UPDATE = 0;
    public static final int TYPE_INSERT = 1;
    public static final int TYPE_DELETE = 2;

    private int mType = -1;
    private MyItem mContentItem;

    public AsyncDbTask(int type, final MyItem contentItem) {
        if (type != TYPE_UPDATE && type != TYPE_INSERT && type != TYPE_DELETE) {
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

    public MyItem getContent() {
        return mContentItem;
    }
}
