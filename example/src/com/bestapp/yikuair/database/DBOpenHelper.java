package com.bestapp.yikuair.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

public class DBOpenHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "yikuair_contacts";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_NAME = "contactsTable";
	public static final String ID = "_id"; // ID
	public static final String USERID = "userid"; // userID
	public static final String NAME = "name"; // realname
	public static final String PHONE = "phone"; // phone
	public static final String MOBILE = "mobile";// mobile_phone
	public static final String EMAIL = "email"; // email
	public static final String DUTY = "duty"; // duty
	public static final String DEPARTMENT = "department"; // department
	public static final String HEADURL = "headURL"; // headUrl
	public static final String SIGNATURE = "signature"; // signature
	public static final String COMPANYID = "companyid"; // companyID
	public static final String ALPHA = "alpha"; // alpha
	public static final String TEAM = "team"; // team
	public static final String SEX = "sex"; // sex
	public static final String DBID = "dbid"; // dbid
	public static final String PINYIN = "pinyin";
	public static final String SEARCHIND = "searchindex";

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if( /*enableWAL &&*/ Build.VERSION.SDK_INT >= 11){  
            getWritableDatabase().enableWriteAheadLogging();  
        }  
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE_NAME + " (" + ID
				+ " integer primary key autoincrement," + USERID + " varchar,"
				+ NAME + " varchar," + PHONE + " varchar," + MOBILE
				+ " varchar," + EMAIL + " varchar," + DUTY + " varchar,"
				+ DEPARTMENT + " varchar," + HEADURL + " varchar," + SIGNATURE
				+ " vachar," + COMPANYID + " vachar," + ALPHA + " vachar,"
				+ TEAM + " vachar," + SEX + " vachar," + DBID + " vachar,"
				+ PINYIN + " vachar," + SEARCHIND + " vachar)";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		String sql = "drop table if exists " + TABLE_NAME;
		db.execSQL(sql);
		onCreate(db);
	}

	public Cursor select(int type, String groupNa) {
		SQLiteDatabase db = this.getReadableDatabase();
		String[] selectInfo = null;
		String group = null;
		String selection = null;
		String[] selectionArgs = null;

		if (type == 1) {
			selectInfo = new String[] { "COUNT(*)", DEPARTMENT };
			group = DEPARTMENT;
		} else if (type == 2) {
			selection = DEPARTMENT + " like ?";
			selectionArgs = new String[] { "%" + groupNa + "%" };
		}

		Cursor cursor = db.query(TABLE_NAME, selectInfo, selection,
				selectionArgs, group, null, null);
		return cursor;
	}

	public long insert(String[] strArray) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(USERID, strArray[0]);
		cv.put(NAME, strArray[1]);
		cv.put(PHONE, strArray[2]);
		cv.put(MOBILE, strArray[3]);
		cv.put(EMAIL, strArray[4]);
		cv.put(DUTY, strArray[5]);
		cv.put(DEPARTMENT, strArray[6]);
		cv.put(HEADURL, strArray[7]);
		cv.put(SIGNATURE, strArray[8]);
		cv.put(COMPANYID, strArray[9]);
		cv.put(ALPHA, strArray[10]);
		cv.put(TEAM, strArray[11]);
		cv.put(SEX, strArray[12]);
		cv.put(DBID, strArray[13]);
		cv.put(PINYIN, strArray[14]);
		cv.put(SEARCHIND, strArray[15]);
		return db.insert(TABLE_NAME, null, cv);
	}

	public void delete(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = ID + "=?";
		String[] whereValues = { id };
		db.delete(TABLE_NAME, where, whereValues);
	}

	public void delete(String id, String no) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = DBID + "=?";
		String[] whereValues = { id };
		db.delete(TABLE_NAME, where, whereValues);
	}

	public int update(String id, String[] strArray) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = ID + "=?";
		String[] whereValues = { id };
		ContentValues cv = new ContentValues();
		cv.put(USERID, strArray[0]);
		cv.put(NAME, strArray[1]);
		cv.put(PHONE, strArray[2]);
		cv.put(MOBILE, strArray[3]);
		cv.put(EMAIL, strArray[4]);
		cv.put(DUTY, strArray[5]);
		cv.put(DEPARTMENT, strArray[6]);
		cv.put(HEADURL, strArray[7]);
		cv.put(SIGNATURE, strArray[8]);
		cv.put(COMPANYID, strArray[9]);
		cv.put(ALPHA, strArray[10]);
		cv.put(TEAM, strArray[11]);
		cv.put(SEX, strArray[12]);
		cv.put(DBID, strArray[13]);
		cv.put(PINYIN, strArray[14]);
		cv.put(SEARCHIND, strArray[15]);
		return db.update(TABLE_NAME, cv, where, whereValues);
	}

	public Cursor select(String selection, String[] selectionArgs) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, selection, selectionArgs,
				null, null, null);
		return cursor;
	}

	public Cursor get(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String where = DBID + "=?";
		String[] whereValues = { id };
		Cursor cursor = db.query(TABLE_NAME, null, where, whereValues, null,
				null, null);
		return cursor;
	}

	public boolean deleteDatabase(Context context) {
		return context.deleteDatabase(DATABASE_NAME);
	}
}
