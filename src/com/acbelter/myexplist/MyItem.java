package com.acbelter.myexplist;

import android.database.Cursor;
import com.acbelter.myexplist.database.AsyncDbTask;
import com.acbelter.myexplist.database.MyDatabaseAdapter;
import com.acbelter.myexplist.database.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MyItem {
    // TODO Encapsulate fields
    public long id;
    public long parentId;
    public String text;
    public boolean checked;
    public List<MyItem> children;
    public MyItem parent;

    public static MyItem newGroupItem(String text) {
        MyItem item = new MyItem();
        item.id = MyDatabaseAdapter.getNextId();
        item.parentId = -1;
        item.text = text;
        item.checked = false;
        item.children = new ArrayList<MyItem>(5);
        item.parent = null;
        return item;
    }

    public static MyItem newChildItem(MyItem parent, String text) {
        MyItem item = new MyItem();
        item.id = MyDatabaseAdapter.getNextId();
        item.parentId = parent.id;
        item.text = text;
        item.checked = false;
        item.children = null;
        item.parent = parent;
        return item;
    }

    public MyItem() {}

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
            throw new IllegalArgumentException("Incorrect checked state in the database");
        }

        if (parentId == -1) {
            children = new ArrayList<MyItem>(5);
        }
    }

    public boolean isChild() {
        return children == null;
    }

    public boolean isChildrenChecked() {
        if (isChild()) {
            throw new UnsupportedOperationException("This item must be group");
        }

        for (MyItem child : children) {
            if (!child.checked) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id=");
        builder.append(id);
        builder.append(" ");
        builder.append("parentId=");
        builder.append(parentId);
        builder.append(" ");
        builder.append("text=");
        builder.append(text);
        builder.append(" ");
        builder.append("checked=");
        builder.append(checked);
        builder.append(" ");
        if (!isChild()) {
            builder.append("childrenSize=");
            builder.append(children.size());
            builder.append("\n");
        }
        return builder.toString();
    }
}
