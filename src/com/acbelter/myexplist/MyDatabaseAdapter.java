package com.acbelter.myexplist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseAdapter {
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public MyDatabaseAdapter(Context context) {
        mContext = context;
    }

    public void open() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext);
        mDatabase = dbHelper.getWritableDatabase();
    }

    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    public Cursor fetchAll() {
        return mDatabase.query("my_table", null, null, null, null, null, null);
    }

    public long insert(long parentId, String text) {
        return mDatabase.insert("my_table", null, newContentValues(parentId, text));
    }

    private ContentValues newContentValues(long parentId, String text) {
        ContentValues values = new ContentValues();
        values.put("parent_id", parentId);
        values.put("text", text);
        values.put("checked", 0);
        return values;
    }

    public List<MyItem> getList() {
        List<MyItem> groups = new ArrayList<MyItem>();
        List<MyItem> children = new ArrayList<MyItem>();
        Cursor c = fetchAll();
        while (c.moveToNext()) {
            MyItem item = new MyItem(c);
            if (item.parentId == -1) {
                groups.add(item);
            } else {
                children.add(item);
            }
        }

        for (MyItem group : groups) {
            for (MyItem child : children) {
                if (child.parentId == group.id) {
                    child.parent = group;
                    group.children.add(child);
                }
            }
        }

        return groups;
    }
}
