package propagate.com.propagate_client.database;

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
  private long server_dist_id;
  private String dist_name;
  private String created_by;
  private int count;
  private String status;
  private String pending_status;
  private String created;

  private long member_id;
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

  /*dist list*/
  public DistListModule(String dist_name,String created_by,int count){
    this.dist_name = dist_name;
    this.created_by = created_by;
    this.count = count;
  }

  /*members*/
  public DistListModule(long dist_id,String member_name,String contact_id,String photo_uri){
    this.dist_id = dist_id;
    this.member_name = member_name;
    this.contact_id = contact_id;
    this.photo_uri = photo_uri;
  }

  /*members*/
  public DistListModule(long member_id,long dist_id,String member_name,String contact_id,String photo_uri){
    this.member_id = member_id;
    this.dist_id = dist_id;
    this.member_name = member_name;
    this.contact_id = contact_id;
    this.photo_uri = photo_uri;
  }

  /*dist list*/
  public DistListModule(long server_dist_id,long dist_id, String dist_name, String createdBy,int count, String created_timestamp, String status) {
    this.server_dist_id = server_dist_id;
    this.dist_id = dist_id;
    this.dist_name = dist_name;
    this.created_by = createdBy;
    this.count = count;
    this.created = created_timestamp;
    this.status = status;
  }

  public long getServer_dist_id() {
    return server_dist_id;
  }

  public void setServer_dist_id(long server_dist_id) {
    this.server_dist_id = server_dist_id;
  }

  public long getMember_id() {
    return member_id;
  }

  public void setMember_id(long member_id) {
    this.member_id = member_id;
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

  public String getPending_status() {
    return pending_status;
  }

  public void setPending_status(String pending_status) {
    this.pending_status = pending_status;
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
    contentValues.put(databaseHelper.KEY_pending_status, 0);

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

    Log.i("Database", "Inserted List Members "+distListModule.getDist_id());
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
              databaseHelper.KEY_created_by, databaseHelper.KEY_count, databaseHelper.KEY_created, databaseHelper.KEY_status,
              databaseHelper.KEY_pending_status,databaseHelper.KEY_server_dist_id}, databaseHelper.KEY_id + "=?",
          new String[]{String.valueOf(groupId)}, null, null, null, null);
    }

    int serverIdIndex = cursor.getColumnIndex(databaseHelper.KEY_server_dist_id);
    int idIndex = cursor.getColumnIndex(databaseHelper.KEY_id);
    int nameIndex = cursor.getColumnIndex(databaseHelper.KEY_dist_name);
    int createdByIndex = cursor.getColumnIndex(databaseHelper.KEY_created_by);
    int countIndex = cursor.getColumnIndex(databaseHelper.KEY_count);
    int createdIndex = cursor.getColumnIndex(databaseHelper.KEY_created);
    int statusIndex = cursor.getColumnIndex(databaseHelper.KEY_status);
    if (cursor .moveToFirst()) {
      while (cursor.isAfterLast() == false) {
        long id = cursor.getLong(idIndex);
        long server_id = cursor.getLong(serverIdIndex);
        String name = cursor.getString(nameIndex);
        String createdBy = cursor.getString(createdByIndex);
        int count = cursor.getInt(countIndex);
        String created_timestamp = cursor.getString(createdIndex);
        String status = cursor.getString(statusIndex);

        distList.add(new DistListModule(server_id,id,name,createdBy,count,created_timestamp,status));
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
        distList.add(new DistListModule(id,distListId,name,contact_id,photoUri));
        cursor.moveToNext();
      }
    }

    cursor.close();
    databaseHelper.close();

    return distList;
  }

  /*Update distribution list status*/
  public void updateDistListStatus(Context context,long dist_id,long server_dist_id){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_server_dist_id, server_dist_id);
    values.put(databaseHelper.KEY_status, 1);
    db.update(databaseHelper.TABLE_DIST_DETAIL, values, databaseHelper.KEY_id+"="+dist_id, null);
    databaseHelper.close();
    Log.i("Database", "Updated Dist List Status");
  }

  /*Update distribution list pending status*/
  public void updateDistListPendingStatus(Context context,long dist_id){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_pending_status, 1);
    db.update(databaseHelper.TABLE_DIST_DETAIL, values, databaseHelper.KEY_id+"="+dist_id, null);
    databaseHelper.close();
    Log.i("Database", "Updated Dist List Status");
  }

  /*Update distribution list count*/
  public void updateDistListCount(Context context,long dist_id,String type){
    int count = 0;
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    Cursor cursor = db.rawQuery("SELECT * FROM "+databaseHelper.TABLE_DIST_DETAIL+" where "+databaseHelper.KEY_id+"="+dist_id, null);
    //cc.moveToFirst();
    if(cursor.getCount()>=1) {
      cursor.moveToFirst();
      int countIndex = cursor.getColumnIndex(databaseHelper.KEY_count);
      count = cursor.getInt(countIndex);
    }
    if(type.equals("add"))
      count++;
    else
      count--;

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_count, count);
    db.update(databaseHelper.TABLE_DIST_DETAIL, values, databaseHelper.KEY_id+"="+dist_id, null);
    databaseHelper.close();
    Log.i("Database", "Updated Dist List Count");
  }

  public void deleteDistList(Context context,long dist_id){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    db.delete(databaseHelper.TABLE_DIST_DETAIL, databaseHelper.KEY_id + "=" + dist_id, null);
    db.delete(databaseHelper.TABLE_DIST_MEMBERS, databaseHelper.KEY_dist_ID + "=" + dist_id, null);
    databaseHelper.close();
    Log.i("Database", "Deleted dist list");
  }

  public void deleteMember(Context context,long dist_id,long member_id){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    db.delete(databaseHelper.TABLE_DIST_MEMBERS, databaseHelper.KEY_dist_ID + "=" + dist_id+" and "+databaseHelper.KEY_id + "=" + member_id, null);
    databaseHelper.close();
    Log.i("Database", "Deleted Member");
  }

}
