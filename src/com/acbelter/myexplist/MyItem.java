package com.acbelter.myexplist;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class MyItem {
    public long id;
    public long parentId;
    public String text;
    public boolean checked;
    public List<MyItem> children;

    public MyItem(Cursor cursor) {
        id = cursor.getLong(MyDatabaseHelper.INDEX_ID);
        parentId = cursor.getLong(MyDatabaseHelper.INDEX_PARENT_ID);
        text = cursor.getString(MyDatabaseHelper.INDEX_TEXT);

        int checkedInt = cursor.getInt(MyDatabaseHelper.INDEX_CHECKED);
        if (checkedInt == 0) {
            checked = false;
        } else if (checkedInt == 1) {
            checked = true;
        } else {
            throw new IllegalArgumentException("Incorrect checked state");
        }

        if (parentId == -1) {
            children = new ArrayList<MyItem>();
        }
    }
}
