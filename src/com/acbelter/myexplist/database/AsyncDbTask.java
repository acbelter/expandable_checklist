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
