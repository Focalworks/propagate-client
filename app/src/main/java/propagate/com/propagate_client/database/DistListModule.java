package propagate.com.propagate_client.database;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

import propagate.com.propagate_client.utils.CommonFunctions;

/**
 * Created by kaustubh on 3/2/15.
 */
public class DistListModule implements Serializable {

  private ContentValues contentValues;
  private SQLiteDatabase db;
  private static DistListModule mInstance;

  private long dist_id;
  private String dist_name;
  private String created_by;
  private int count;
  private String status;
  private String created;

  private String contact_id;
  private String member_name;
  private String photo_uri;

  public static DistListModule getInstance(){
    if(mInstance == null)
    {
      mInstance = new DistListModule();
    }
    return mInstance;
  }

  public DistListModule(){}

  public DistListModule(String dist_name,String created_by,int count){
    this.dist_name = dist_name;
    this.created_by = created_by;
    this.count = count;
  }

  public DistListModule(long dist_id,String member_name,String contact_id,String photo_uri){
    this.dist_id = dist_id;
    this.member_name = member_name;
    this.contact_id = contact_id;
    this.photo_uri = photo_uri;
  }

  public DistListModule(long dist_id, String dist_name, String createdBy,int count, String created_timestamp, String status) {
    this.dist_id = dist_id;
    this.dist_name = dist_name;
    this.created_by = createdBy;
    this.count = count;
    this.created = created_timestamp;
    this.status = status;
  }

  public long getDist_id() {
    return dist_id;
  }

  public void setDist_id(long dist_id) {
    this.dist_id = dist_id;
  }

  public String getDist_name() {
    return dist_name;
  }

  public void setDist_name(String dist_name) {
    this.dist_name = dist_name;
  }

  public String getCreated_by() {
    return created_by;
  }

  public void setCreated_by(String created_by) {
    this.created_by = created_by;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMember_name() {
    return member_name;
  }

  public void setMember_name(String member_name) {
    this.member_name = member_name;
  }

  public String getContact_id() {
    return contact_id;
  }

  public void setContact_id(String contact_id) {
    this.contact_id = contact_id;
  }

  public String getPhoto_uri() {
    return photo_uri;
  }

  public void setPhoto_uri(String photo_uri) {
    this.photo_uri = photo_uri;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public long addDistList(Context context, DistListModule distListModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    contentValues = new ContentValues();
    contentValues.put(databaseHelper.KEY_dist_name, distListModule.getDist_name());
    contentValues.put(databaseHelper.KEY_created_by, distListModule.getCreated_by());
    contentValues.put(databaseHelper.KEY_count, distListModule.getCount());
    contentValues.put(databaseHelper.KEY_created, CommonFunctions.getCreatedDate());
    contentValues.put(databaseHelper.KEY_status, 0);

    long lastId = db.insert(databaseHelper.TABLE_DIST_DETAIL,null,contentValues);
    databaseHelper.close();

    Log.i("Database", "Inserted Distribution List");
    return lastId;
  }

  public long addDistListMembers(Context context, DistListModule distListModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    contentValues = new ContentValues();
    contentValues.put(databaseHelper.KEY_dist_ID, distListModule.getDist_id());
    contentValues.put(databaseHelper.KEY_contact_id, distListModule.getContact_id());
    contentValues.put(databaseHelper.KEY_member_name, distListModule.getMember_name());
    contentValues.put(databaseHelper.KEY_photo_uri, distListModule.getPhoto_uri());

    long lastId = db.insert(databaseHelper.TABLE_DIST_MEMBERS,null,contentValues);
    databaseHelper.close();

    Log.i("Database", "Inserted List Members "+getMember_name());
    return lastId;
  }

  public ArrayList<DistListModule> getDistLists(Context context,long groupId){
    ArrayList<DistListModule> distList = new ArrayList<DistListModule>();
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();
    Cursor cursor;
    if(groupId == 0) {
      cursor = db.rawQuery("select * from " + databaseHelper.TABLE_DIST_DETAIL + " ORDER BY " + databaseHelper.KEY_created + " DESC", null);
    }else {
      cursor = db.query(databaseHelper.TABLE_DIST_DETAIL, new String[]{databaseHelper.KEY_id,databaseHelper.KEY_dist_name,
              databaseHelper.KEY_created_by, databaseHelper.KEY_count, databaseHelper.KEY_created, databaseHelper.KEY_status}, databaseHelper.KEY_id + "=?",
          new String[]{String.valueOf(groupId)}, null, null, null, null);
    }

    int idIndex = cursor.getColumnIndex(databaseHelper.KEY_id);
    int nameIndex = cursor.getColumnIndex(databaseHelper.KEY_dist_name);
    int createdByIndex = cursor.getColumnIndex(databaseHelper.KEY_created_by);
    int countIndex = cursor.getColumnIndex(databaseHelper.KEY_count);
    int createdIndex = cursor.getColumnIndex(databaseHelper.KEY_created);
    int statusIndex = cursor.getColumnIndex(databaseHelper.KEY_status);
    if (cursor .moveToFirst()) {
      while (cursor.isAfterLast() == false) {
        long id = cursor.getLong(idIndex);
        String name = cursor.getString(nameIndex);
        String createdBy = cursor.getString(createdByIndex);
        int count = cursor.getInt(countIndex);
        String created_timestamp = cursor.getString(createdIndex);
        String status = cursor.getString(statusIndex);

        distList.add(new DistListModule(id,name,createdBy,count,created_timestamp,status));
        cursor.moveToNext();
      }
    }

    cursor.close();
    databaseHelper.close();

    return distList;
  }

  public ArrayList<DistListModule> getDistListMembers(Context context,long distListId){
    ArrayList<DistListModule> distList = new ArrayList<DistListModule>();
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();
    Cursor cursor;
    cursor = db.query(databaseHelper.TABLE_DIST_MEMBERS, new String[]{databaseHelper.KEY_id,
            databaseHelper.KEY_member_name, databaseHelper.KEY_contact_id, databaseHelper.KEY_photo_uri}, databaseHelper.KEY_dist_ID + "=?",
        new String[]{String.valueOf(distListId)}, null, null, null, null);

    int idIndex = cursor.getColumnIndex(databaseHelper.KEY_id);
    int nameIndex = cursor.getColumnIndex(databaseHelper.KEY_member_name);
    int contact_idIndex = cursor.getColumnIndex(databaseHelper.KEY_contact_id);
    int photoIndex = cursor.getColumnIndex(databaseHelper.KEY_photo_uri);
    if (cursor .moveToFirst()) {
      while (cursor.isAfterLast() == false) {

        long id = cursor.getLong(idIndex);
        String name = cursor.getString(nameIndex);
        String contact_id = cursor.getString(contact_idIndex);
        String photoUri = cursor.getString(photoIndex);

        distList.add(new DistListModule(id,name,contact_id,photoUri));
        cursor.moveToNext();
      }
    }

    cursor.close();
    databaseHelper.close();

    return distList;
  }

  /*Update distribution list status*/
  public void updateGroupStatus(Context context,long gid){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_status, 1);
    db.update(databaseHelper.TABLE_DIST_DETAIL, values, databaseHelper.KEY_id+"="+gid, null);
    Log.i("Database", "Updated Dist List Status");
  }

}
