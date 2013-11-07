package com.acbelter.myexplist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.acbelter.myexplist.MyItem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyDatabaseAdapter {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private BlockingQueue<AsyncDbTask> mDbTasksQueue;
    private Thread mModifThread;
    private volatile boolean mModifThreadRun;

    private static long sMaxId;

    public MyDatabaseAdapter(Context context) {
        mContext = context;
    }

    public void runModificationThread() {
        mDbTasksQueue = new LinkedBlockingQueue<AsyncDbTask>();
        mModifThreadRun = true;
        mModifThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mModifThreadRun) {
                    try {
                        AsyncDbTask dbTask = mDbTasksQueue.take();
                        switch (dbTask.getType()) {
                            case AsyncDbTask.UPDATE_TYPE: {
                                // TODO
                                break;
                            }
                            case AsyncDbTask.INSERT_TYPE: {
                                // TODO
                                break;
                            }
                            case AsyncDbTask.DELETE_TYPE: {
                                // TODO
                                break;
                            }
                        }
                    } catch (InterruptedException e) {
                        mModifThreadRun = false;
                    }
                }
            }
        });

        mModifThread.start();
    }

    public void stopModificationThread() {
        mModifThreadRun = false;
    }

    public boolean addModificationTask(AsyncDbTask newTask) {
        if (mDbTasksQueue == null) {
            throw new IllegalStateException("Modification thread not running");
        }

        return mDbTasksQueue.offer(newTask);
    }

    public void open() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(mContext);
        mDatabase = dbHelper.getWritableDatabase();
        sMaxId = getMaxId();
    }

    public void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    private long getMaxId() {
        int id = 0;
        final String MY_QUERY = "SELECT MAX(_id) AS _id FROM my_table";
        Cursor mCursor = mDatabase.rawQuery(MY_QUERY, null);

        if (mCursor.getCount() > 0) {
            mCursor.moveToFirst();
            id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        }
        return id;
    }

    public static long getNextId() {
        return sMaxId++;
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
