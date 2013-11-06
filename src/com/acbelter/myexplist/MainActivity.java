package com.acbelter.myexplist;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ExpandableListView;
import com.acbelter.myexplist.R.layout;

import java.util.List;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private MyDatabaseAdapter mDbAdapter;
    private ExpandableListView mListView;
    private MyExpandableListAdapter mListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        mListView = (ExpandableListView) findViewById(R.id.my_list);
        mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        mDbAdapter = new MyDatabaseAdapter(this);
        mDbAdapter.open();

        // Init if need
        if (mDbAdapter.fetchAll().getCount() == 0) {
            long parentId = mDbAdapter.insert(-1, "Item 1");
            mDbAdapter.insert(parentId, "Item 2");
            mDbAdapter.insert(parentId, "Item 3");
            parentId = mDbAdapter.insert(-1, "Item 4");
            mDbAdapter.insert(parentId, "Item 5");
            mDbAdapter.insert(parentId, "Item 6");
            parentId = mDbAdapter.insert(-1, "Item 7");
            mDbAdapter.insert(parentId, "Item 8");
            mDbAdapter.insert(parentId, "Item 9");
        }

        List<MyItem> listData = mDbAdapter.getList();
        mListAdapter = new MyExpandableListAdapter(this, listData);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbAdapter.close();
    }
}
