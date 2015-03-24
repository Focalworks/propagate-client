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
 * Created by kaustubh on 19/3/15.
 */
public class RequirementModule implements Serializable{

  private SQLiteDatabase db;
  private ContentValues contentValues;

  private long r_id;
  private String title;
  private String description;
  private String client_email;
  private String location;
  private String area;
  private String range;
  private String price;
  private String price_range;
  private String type;
  private String created;
  private int status;

  private static RequirementModule mInstance;

  public static RequirementModule getInstance(){
    if(mInstance == null)
    {
      mInstance = new RequirementModule();
    }
    return mInstance;
  }

  public RequirementModule(){}

  public RequirementModule(String title, String description, String client_email, String location, String area, String range, String price, String price_range, String type) {
    this.title = title;
    this.description = description;
    this.client_email = client_email;
    this.location = location;
    this.area = area;
    this.range = range;
    this.price = price;
    this.price_range = price_range;
    this.type = type;
  }

  public RequirementModule(long r_id, String title, String description, String client_email, String location, String area, String range, String price, String price_range, String type) {
    this.r_id = r_id;
    this.title = title;
    this.description = description;
    this.client_email = client_email;
    this.location = location;
    this.area = area;
    this.range = range;
    this.price = price;
    this.price_range = price_range;
    this.type = type;
   }

  public RequirementModule(long r_id, String title, String description, String client_email, String location, String area, String range, String price, String price_range, String type, String created, int status) {
    this.r_id = r_id;
    this.title = title;
    this.description = description;
    this.client_email = client_email;
    this.location = location;
    this.area = area;
    this.range = range;
    this.price = price;
    this.price_range = price_range;
    this.type = type;
    this.created = created;
    this.status = status;
  }

  public long getR_id() {
    return r_id;
  }

  public void setR_id(long r_id) {
    this.r_id = r_id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getClient_email() {
    return client_email;
  }

  public void setClient_email(String client_email) {
    this.client_email = client_email;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getPrice_range() {
    return price_range;
  }

  public void setPrice_range(String price_range) {
    this.price_range = price_range;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public long addRequirement(Context context, RequirementModule requirementModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    contentValues = new ContentValues();
    contentValues.put(databaseHelper.KEY_title, requirementModule.getTitle());
    contentValues.put(databaseHelper.KEY_description, requirementModule.getDescription());
    contentValues.put(databaseHelper.KEY_client_email, requirementModule.getClient_email());
    contentValues.put(databaseHelper.KEY_location, requirementModule.getLocation());
    contentValues.put(databaseHelper.KEY_area, requirementModule.getArea());
    contentValues.put(databaseHelper.KEY_range, requirementModule.getRange());
    contentValues.put(databaseHelper.KEY_price, requirementModule.getPrice());
    contentValues.put(databaseHelper.KEY_price_range, requirementModule.getPrice_range());
    contentValues.put(databaseHelper.KEY_type, requirementModule.getType());
    contentValues.put(databaseHelper.KEY_created, CommonFunctions.getCreatedDate());
    contentValues.put(databaseHelper.KEY_status, 0);

    long lastId = db.insert(databaseHelper.TABLE_REQUIREMENT,null,contentValues);
    databaseHelper.close();

    Log.i("Database", "Inserted requirement");
    return lastId;
  }

  public ArrayList<RequirementModule> getRequirementInfo(Context context,long propertyId){
    ArrayList<RequirementModule> requirementList = new ArrayList<RequirementModule>();
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();
    Cursor cursor;
    if(propertyId == 0) {
      cursor = db.rawQuery("select * from " + databaseHelper.TABLE_REQUIREMENT + " ORDER BY " + databaseHelper.KEY_created + " DESC", null);
    }else {
      cursor = db.query(databaseHelper.TABLE_REQUIREMENT, new String[]{databaseHelper.KEY_id,databaseHelper.KEY_title,
              databaseHelper.KEY_description, databaseHelper.KEY_client_email, databaseHelper.KEY_location, databaseHelper.KEY_area,
              databaseHelper.KEY_range,databaseHelper.KEY_price,databaseHelper.KEY_price_range,databaseHelper.KEY_type,
              databaseHelper.KEY_created,databaseHelper.KEY_status}, databaseHelper.KEY_id + "=?",
          new String[]{String.valueOf(propertyId)}, null, null, null, null);
    }

    if (cursor .moveToFirst()) {
      while (cursor.isAfterLast() == false) {

        long id = cursor.getLong(cursor.getColumnIndex(databaseHelper.KEY_id));
        String title = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_title));
        String description = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_description));
        String client_email = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_client_email));
        String location = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_location));
        String area = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_area));
        String range = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_range));
        String price = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_price));
        String price_range = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_price_range));
        String type = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_type));
        String created = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_created));
        int status = cursor.getInt(cursor.getColumnIndex(databaseHelper.KEY_status));

        requirementList.add(new RequirementModule(id,title,description,client_email,location,area,range,price,price_range,type,created,status));
        cursor.moveToNext();
      }
    }

    cursor.close();
    databaseHelper.close();

    return requirementList;
  }

  /*Update  requirement*/
  public void updateRequirement(Context context,RequirementModule requirementModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_title, requirementModule.getTitle());
    values.put(databaseHelper.KEY_description, requirementModule.getDescription());
    values.put(databaseHelper.KEY_client_email, requirementModule.getClient_email());
    values.put(databaseHelper.KEY_location, requirementModule.getLocation());
    values.put(databaseHelper.KEY_area, requirementModule.getArea());
    values.put(databaseHelper.KEY_range, requirementModule.getRange());
    values.put(databaseHelper.KEY_price, requirementModule.getPrice());
    values.put(databaseHelper.KEY_price_range, requirementModule.getPrice_range());
    values.put(databaseHelper.KEY_type, requirementModule.getType());
    values.put(databaseHelper.KEY_status, 0);
    db.update(databaseHelper.TABLE_REQUIREMENT, values, databaseHelper.KEY_id+"="+requirementModule.getR_id(), null);
    databaseHelper.close();
    Log.i("Database", "Updated Requirement");
  }

  /*Update  status*/
  public void updateRequirementStatus(Context context,long rid,long req_id){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_server_req_id, req_id);
    values.put(databaseHelper.KEY_status, 1);
    db.update(databaseHelper.TABLE_REQUIREMENT, values, databaseHelper.KEY_id+"="+rid, null);
    databaseHelper.close();
    Log.i("Database", "Updated Requirement Status");
  }

  /*delete requirement*/
  public void deleteProperty(Context context, long rid){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();
    db.delete(databaseHelper.TABLE_REQUIREMENT, databaseHelper.KEY_id + "=" + rid, null);
    databaseHelper.close();
    Log.i("Database", "Deleted Requirement");
  }
}
