package com.example.customersample.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    /*
     * This time we will not be using the hardcoded string values
     * Instead here we are defining all the Strings that is required for our database
     * for example databasename, table name and column names.
     * */
    private static final String DATABASE_NAME = "CustomerDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "customer";
    private static final String COLUMN_NO = "number";
    private static final String COLUMN_FNAME = "firstName";
    private static final String COLUMN_MNAME = "middleName";
    private static final String COLUMN_LNAME = "lastName";
    private static final String COLUMN_EMAIL = "emailId";
    private static final String COLUMN_PHONE = "phone";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
         * The query to create our table
         * It is same as we had in the previous post
         * The only difference here is we have changed the
         * hardcoded string values with String Variables
         * */

        String sql = "CREATE TABLE " + TABLE_NAME + " (\n" +
                "    " + COLUMN_NO + " INTEGER  PRIMARY KEY,\n" +
                "    " + COLUMN_FNAME + " varchar(200) NOT NULL,\n" +
                "    " + COLUMN_MNAME + " varchar(200) NOT NULL,\n" +
                "    " + COLUMN_LNAME + " varchar(200) NOT NULL,\n" +
                "    " + COLUMN_EMAIL + " varchar(200) NOT NULL,\n" +
                "    " + COLUMN_PHONE + " varchar(200) NOT NULL\n" +");";

        /*
         * Executing the string to create the table
         * */
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /*
         * We are doing nothing here
         * Just dropping and creating the table
         * */
        String sql = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }
    //For Add Customer
    public boolean addCustomer(String fname, String mname, String lname, String email, String phone) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(fname, fname);
        contentValues.put(fname, mname);
        contentValues.put(fname, lname);
        contentValues.put(fname, email);
        contentValues.put(fname, phone);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }

    //Get All Customer List
    public Cursor getAllCustomer() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    //Delete Customer Record
   public boolean deleteCustomer(String number) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_NO + "=?", new String[]{String.valueOf(number)}) == 1;
    }

    public Cursor rawQuery(String count, Object o) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
