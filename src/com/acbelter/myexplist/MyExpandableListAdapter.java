/*
 * Copyright 2013 acbelter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import com.acbelter.myexplist.database.AsyncDbTask;
import com.acbelter.myexplist.database.MyDatabaseAdapter;

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
                setChecked(item, isChecked);
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
                setChecked(item, isChecked);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private void setChecked(MyItem item, boolean isChecked) {
        item.checked = isChecked;
        if (item.isChild()) {
            if (!isChecked && item.parent.checked) {
                item.parent.checked = false;
                MyDatabaseAdapter.getInstance(mContext).addModificationTask(
                        new AsyncDbTask(AsyncDbTask.TYPE_UPDATE, item.parent));
            } else if (isChecked && item.parent.isChildrenChecked()) {
                item.parent.checked = true;
                MyDatabaseAdapter.getInstance(mContext).addModificationTask(
                        new AsyncDbTask(AsyncDbTask.TYPE_UPDATE, item.parent));
            }
        } else {
            for (MyItem child : item.children) {
                child.checked = isChecked;
                MyDatabaseAdapter.getInstance(mContext).addModificationTask(
                        new AsyncDbTask(AsyncDbTask.TYPE_UPDATE, child));
            }
        }
    }

    public void printData() {
        for (MyItem item : mListData) {
            Log.d(DEBUG_TAG, item.toString());
        }
    }
}
