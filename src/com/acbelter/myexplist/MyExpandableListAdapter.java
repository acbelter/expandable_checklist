package com.acbelter.myexplist;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.acbelter.myexplist.R.id;
import com.acbelter.myexplist.R.layout;

import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private Context mContext;
    private List<MyItem> mListData;

    public MyExpandableListAdapter(Context context, List<MyItem> listData) {
        mContext = context;
        mListData = listData;
    }

    @Override
    public int getGroupCount() {
        return mListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mListData.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mListData.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((MyItem) getGroup(groupPosition)).id;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((MyItem) getChild(groupPosition, childPosition)).id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout.item_group, parent, false);
        }

        final MyItem item = (MyItem) getGroup(groupPosition);
        TextView textView = (TextView) convertView.findViewById(id.text);
        CheckBox checkBox = (CheckBox) convertView.findViewById(id.check_box_group);

        textView.setText(item.text);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.checked);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout.item_child, parent, false);
        }

        final MyItem item = (MyItem) getChild(groupPosition, childPosition);
        TextView textView = (TextView) convertView.findViewById(id.text);
        CheckBox checkBox = (CheckBox) convertView.findViewById(id.check_box_child);

        textView.setText(item.text);
        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(item.checked);
        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void printData() {
        for (MyItem item : mListData) {
            Log.d(DEBUG_TAG, item.toString());
        }
    }
}
