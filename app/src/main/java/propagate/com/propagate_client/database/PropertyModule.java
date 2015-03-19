package propagate.com.propagate_client.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import propagate.com.propagate_client.utils.CommonFunctions;

/**
 * Created by kaustubh on 17/3/15.
 */
public class PropertyModule {

  private SQLiteDatabase db;
  private ContentValues contentValues;

  private long p_id;
  private String title;
  private String desc;
  private int agent_id;
  private String client_email;
  private String location;
  private String address;
  private String area;
  private String price;
  private String type;
  private int status;
  private String created;

  private static PropertyModule mInstance;

  public static PropertyModule getInstance(){
    if(mInstance == null)
    {
      mInstance = new PropertyModule();
    }
    return mInstance;
  }

  public PropertyModule() {
  }

  public PropertyModule(String title,String desc,int agent_id,String client_email,String location,String address,String area,String price,String type){
    this.title = title;
    this.desc = desc;
    this.agent_id = agent_id;
    this.client_email = client_email;
    this.location = location;
    this.address = address;
    this.area = area;
    this.price = price;
    this.type = type;
  }

  public PropertyModule(long id, String title, String description, int agent_id, String client_email, String location, String address, String area, String price, String type, String created, int status) {
    this.p_id = id;
    this.title = title;
    this.desc = description;
    this.agent_id = agent_id;
    this.client_email = client_email;
    this.location = location;
    this.address = address;
    this.area = area;
    this.price = price;
    this.type = type;
    this.created = created;
    this.status = status;
  }

  public long getP_id() {
    return p_id;
  }

  public void setP_id(long p_id) {
    this.p_id = p_id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public int getAgent_id() {
    return agent_id;
  }

  public void setAgent_id(int agent_id) {
    this.agent_id = agent_id;
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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public long addProperty(Context context, PropertyModule propertyModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    contentValues = new ContentValues();
    contentValues.put(databaseHelper.KEY_title, propertyModule.getTitle());
    contentValues.put(databaseHelper.KEY_description, propertyModule.getDesc());
    contentValues.put(databaseHelper.KEY_agent_id, propertyModule.getAgent_id());
    contentValues.put(databaseHelper.KEY_client_email, propertyModule.getClient_email());
    contentValues.put(databaseHelper.KEY_location, propertyModule.getLocation());
    contentValues.put(databaseHelper.KEY_address, propertyModule.getAddress());
    contentValues.put(databaseHelper.KEY_area, propertyModule.getArea());
    contentValues.put(databaseHelper.KEY_price, propertyModule.getPrice());
    contentValues.put(databaseHelper.KEY_type, propertyModule.getType());
    contentValues.put(databaseHelper.KEY_created, CommonFunctions.getCreatedDate());
    contentValues.put(databaseHelper.KEY_status, 0);

    long lastId = db.insert(databaseHelper.TABLE_PROPERTY,null,contentValues);
    databaseHelper.close();

    Log.i("Database", "Inserted property");
    return lastId;
  }

  public ArrayList<PropertyModule> getPropertyInfo(Context context,long propertyId){
    ArrayList<PropertyModule> propertyList = new ArrayList<PropertyModule>();
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();
    Cursor cursor;
    if(propertyId == 0) {
      cursor = db.rawQuery("select * from " + databaseHelper.TABLE_PROPERTY + " ORDER BY " + databaseHelper.KEY_created + " DESC", null);
    }else {
      cursor = db.query(databaseHelper.TABLE_PROPERTY, new String[]{databaseHelper.KEY_id,databaseHelper.KEY_title,
              databaseHelper.KEY_description, databaseHelper.KEY_agent_id, databaseHelper.KEY_client_email,
              databaseHelper.KEY_location, databaseHelper.KEY_address, databaseHelper.KEY_area,
              databaseHelper.KEY_price,databaseHelper.KEY_type,databaseHelper.KEY_created,databaseHelper.KEY_status}, databaseHelper.KEY_id + "=?",
          new String[]{String.valueOf(propertyId)}, null, null, null, null);
    }

    if (cursor .moveToFirst()) {
      while (cursor.isAfterLast() == false) {

        long id = cursor.getLong(cursor.getColumnIndex(databaseHelper.KEY_id));
        String title = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_title));
        String description = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_description));
        int agent_id = cursor.getInt(cursor.getColumnIndex(databaseHelper.KEY_agent_id));
        String client_email = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_client_email));
        String location = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_location));
        String address = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_address));
        String area = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_area));
        String price = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_price));
        String type = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_type));
        String created = cursor.getString(cursor.getColumnIndex(databaseHelper.KEY_created));
        int status = cursor.getInt(cursor.getColumnIndex(databaseHelper.KEY_status));

        propertyList.add(new PropertyModule(id,title,description,agent_id,client_email,location,address,area,price,type,created,status));
        cursor.moveToNext();
      }
    }

    cursor.close();
    databaseHelper.close();

    return propertyList;
  }

  /*Update  status*/
  public void updatePropertyStatus(Context context,long pid){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_status, 1);
    db.update(databaseHelper.TABLE_PROPERTY, values, databaseHelper.KEY_id+"="+pid, null);
    Log.i("Database", "Updated Property Status");
  }

}
