package com.acbelter.myexplist;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import com.acbelter.myexplist.R.layout;
import com.acbelter.myexplist.database.AsyncDbTask;
import com.acbelter.myexplist.database.MyDatabaseAdapter;

import java.util.List;

public class MainActivity extends Activity {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private MyDatabaseAdapter mDbAdapter;
    private ExpandableListView mListView;
    private MyExpandableListAdapter mListAdapter;
    private static List<MyItem> sListData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        mListView = (ExpandableListView) findViewById(R.id.my_list);
        mListView.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        mDbAdapter = MyDatabaseAdapter.getInstance(this);
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

        if (sListData == null) {
            sListData = mDbAdapter.getList();
        }

        mListAdapter = new MyExpandableListAdapter(this, sListData);
        mListView.setAdapter(mListAdapter);
        mDbAdapter.runModificationThread();

        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int pos = parent.getFlatListPosition(ExpandableListView
                        .getPackedPositionForGroup(groupPosition));
                if (!parent.isGroupExpanded(groupPosition) && !parent.isItemChecked(pos)) {
                    parent.expandGroup(groupPosition, true);
                    parent.setItemChecked(pos, true);
                } else if (parent.isGroupExpanded(groupPosition) && parent.isItemChecked(pos)) {
                    parent.collapseGroup(groupPosition);
                    parent.setItemChecked(pos, false);
                } else if (parent.isGroupExpanded(groupPosition) && !parent.isItemChecked(pos)) {
                    parent.setItemChecked(pos, true);
                }
                return true;
            }
        });

        mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                int pos = parent.getFlatListPosition(ExpandableListView
                        .getPackedPositionForChild(groupPosition, childPosition));
                if (parent.isItemChecked(pos)) {
                    parent.setItemChecked(pos, false);
                } else {
                    parent.setItemChecked(pos, true);
                }
                return true;
            }
        });
    }

    public void onClickInsert(View view) {
        int pos = mListView.getCheckedItemPosition();
        long packedPos = mListView.getExpandableListPosition(pos);
        if (ExpandableListView.getPackedPositionType(packedPos) ==
                ExpandableListView.PACKED_POSITION_TYPE_NULL) {
            // Add new group
            MyItem newGroupItem = MyItem.newGroupItem("Group");
            sListData.add(newGroupItem);

            mListAdapter.notifyDataSetChanged();
            MyDatabaseAdapter.getInstance(this).addModificationTask(
                    new AsyncDbTask(AsyncDbTask.TYPE_INSERT, newGroupItem));
        } else if (ExpandableListView.getPackedPositionType(packedPos) ==
                ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            // Add new child
            int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
            MyItem parent = (MyItem) mListAdapter.getGroup(groupPos);
            MyItem newChildItem = MyItem.newChildItem(parent, "Child");
            parent.children.add(newChildItem);
            if (parent.checked) {
                parent.checked = false;
            }

            mListAdapter.notifyDataSetChanged();
            MyDatabaseAdapter.getInstance(this).addModificationTask(
                    new AsyncDbTask(AsyncDbTask.TYPE_INSERT, newChildItem));
        }
    }

    public void onClickDelete(View view) {
        int pos = mListView.getCheckedItemPosition();
        long packedPos = mListView.getExpandableListPosition(pos);
        if (ExpandableListView.getPackedPositionType(packedPos) ==
                ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
            int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
            // Next line prevents IndexOutOfBoundException when deleting the last element!
            mListView.setItemChecked(mListView.getFlatListPosition(packedPos), false);

            MyItem removedItem = sListData.remove(groupPos);
            mListAdapter.notifyDataSetChanged();
            for (MyItem child : removedItem.children) {
                MyDatabaseAdapter.getInstance(this).addModificationTask(
                        new AsyncDbTask(AsyncDbTask.TYPE_DELETE, child));
            }
            MyDatabaseAdapter.getInstance(this).addModificationTask(
                    new AsyncDbTask(AsyncDbTask.TYPE_DELETE, removedItem));

        } else if (ExpandableListView.getPackedPositionType(packedPos) ==
                ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            int groupPos = ExpandableListView.getPackedPositionGroup(packedPos);
            int childPos = ExpandableListView.getPackedPositionChild(packedPos);
            // Next line prevents IndexOutOfBoundException when deleting the last element!
            mListView.setItemChecked(mListView.getFlatListPosition(packedPos), false);

            MyItem removedItem = sListData.get(groupPos).children.remove(childPos);
            mListAdapter.notifyDataSetChanged();
            MyDatabaseAdapter.getInstance(this).addModificationTask(
                    new AsyncDbTask(AsyncDbTask.TYPE_DELETE, removedItem));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbAdapter.close();
    }
}
