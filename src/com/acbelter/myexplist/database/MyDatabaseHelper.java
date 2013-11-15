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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "my_database";
    private static final int DB_VERSION = 1;

    public static final int INDEX_ID = 0;
    public static final int INDEX_PARENT_ID = 1;
    public static final int INDEX_TEXT = 2;
    public static final int INDEX_CHECKED = 3;

    private static final String MY_TABLE_CREATE =
            "CREATE TABLE my_table (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "parent_id INTEGER NOT NULL, " +
                    "text TEXT NOT NULL, " +
                    "checked INTEGER NOT NULL);";

    private static final String MY_TABLE_DROP = "DROP TABLE IF EXISTS my_table";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MY_TABLE_DROP);
        onCreate(db);
    }
}
