package com.acbelter.myexplist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
    private static BlockingQueue<AsyncDbTask> mDbTasksQueue;
    private Thread mModifThread;
    private volatile boolean mModifThreadRun;

    private static volatile MyDatabaseAdapter sInstance;

    private static long sMaxId;

    // FIXME Test this realization of singleton
    public static MyDatabaseAdapter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyDatabaseAdapter(context.getApplicationContext());
        }
        return sInstance;
    }

    private MyDatabaseAdapter(Context context) {
        mContext = context;
    }

    public void runModificationThread() {
        if (mModifThreadRun) {
            return;
        }

        mDbTasksQueue = new LinkedBlockingQueue<AsyncDbTask>();
        mModifThreadRun = true;
        mModifThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mModifThreadRun) {
                    try {
                        AsyncDbTask dbTask = mDbTasksQueue.take();
                        switch (dbTask.getType()) {
                            case AsyncDbTask.TYPE_UPDATE: {
                                mDatabase.beginTransaction();
                                try {
                                    ContentValues cv = newContentValues(dbTask.getContent());
                                    int count = mDatabase.update("my_table", cv,
                                            "_id=?", new String[]{String.valueOf(dbTask
                                                    .getContent().id)});
                                    assert (count == 1);
                                    mDatabase.setTransactionSuccessful();
                                } finally {
                                    mDatabase.endTransaction();
                                }
                                break;
                            }
                            case AsyncDbTask.TYPE_INSERT: {
                                mDatabase.beginTransaction();
                                try {
                                    ContentValues cv = newContentValues(dbTask.getContent());
                                    long id = mDatabase.insert("my_table", null, cv);
                                    assert (id == dbTask.getContent().id);
                                    mDatabase.setTransactionSuccessful();
                                } finally {
                                    mDatabase.endTransaction();
                                }
                                break;
                            }
                            case AsyncDbTask.TYPE_DELETE: {
                                mDatabase.beginTransaction();
                                try {
                                    int count = mDatabase.delete("my_table",
                                            "_id=?", new String[]{String.valueOf(dbTask
                                            .getContent().id)});
                                    assert (count == 1);
                                    mDatabase.setTransactionSuccessful();
                                } finally {
                                    mDatabase.endTransaction();
                                }
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
        if (mDbTasksQueue == null || !mModifThreadRun) {
            throw new IllegalStateException("Modification thread isn't running");
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
            stopModificationThread();
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

    private ContentValues newContentValues(MyItem item) {
        ContentValues values = new ContentValues();
        values.put("parent_id", item.parentId);
        values.put("text", item.text);
        values.put("checked", item.checked);
        return values;
    }

    public List<MyItem> getList() {
        List<MyItem> groups = new ArrayList<MyItem>();
        List<MyItem> children = new ArrayList<MyItem>();
        Cursor c = fetchAll();

        //Log.d(DEBUG_TAG, DatabaseUtils.dumpCursorToString(c));

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
