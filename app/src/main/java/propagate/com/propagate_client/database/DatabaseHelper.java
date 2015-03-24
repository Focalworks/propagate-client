package propagate.com.propagate_client.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kaustubh on 16/3/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

  private static SQLiteDatabase db;
  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "Propagate";

  // table name
  public static final String TABLE_DIST_DETAIL = "dist_lists";
  public static final String TABLE_DIST_MEMBERS = "dist_list_members";
  public static final String TABLE_PROPERTY = "property";
  public static final String TABLE_REQUIREMENT = "requirement";

  //Common Columns
  public static final String KEY_id = "id";
  public static final String KEY_created = "created_time";
  public static final String KEY_status = "status";

  // Distribution List Details Table Column names
  public static final String KEY_dist_ID = "dist_id";
  public static final String KEY_dist_name = "name";
  public static final String KEY_created_by = "created_by";
  public static final String KEY_count = "count";
  public static final String KEY_server_dist_id = "server_dist_id";

  //Distribution Members Table Column names
  public static final String KEY_contact_id = "contact_id";
  public static final String KEY_member_name = "name";
  public static final String KEY_photo_uri = "photo_uri";

  //Property and Requirement Table Column Names
  public static final String KEY_title = "title";
  public static final String KEY_description = "description";
  public static final String KEY_agent_id = "agent_id";
  public static final String KEY_client_email = "client_email";
  public static final String KEY_location = "location";
  public static final String KEY_address = "address";
  public static final String KEY_area = "area";
  public static final String KEY_range = "range";
  public static final String KEY_price = "price";
  public static final String KEY_price_range = "price_range";
  public static final String KEY_type = "type";
  public static final String KEY_server_prop_id = "server_prop_id";
  public static final String KEY_server_req_id = "server_req_id";

  public DatabaseHelper(Context context){
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_DIST_DETAIL = "CREATE TABLE " + TABLE_DIST_DETAIL + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_dist_name + " TEXT,"+KEY_created_by+ " TEXT,"+KEY_created+ " TEXT,"
        +KEY_count+" INTEGER,"+KEY_status+" INTEGER,"+KEY_server_dist_id+" TEXT)";
    db.execSQL(CREATE_DIST_DETAIL);

    String CREATE_DIST_MEMBERS = "CREATE TABLE " + TABLE_DIST_MEMBERS + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_dist_ID + " INTEGER,"+KEY_contact_id+ " TEXT,"+KEY_member_name+ " TEXT,"
        +KEY_photo_uri+" TEXT)";
    db.execSQL(CREATE_DIST_MEMBERS);

    String CREATE_PROPERTY = "CREATE TABLE " + TABLE_PROPERTY + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_title + " TEXT,"+KEY_description+ " TEXT,"+KEY_agent_id+ " INTEGER,"
        +KEY_client_email+ " TEXT,"+KEY_location+" TEXT,"+KEY_address+" TEXT,"+KEY_area+" TEXT,"+KEY_price+" TEXT,"
        +KEY_type+" TEXT,"+KEY_created+ " TEXT,"+KEY_status+" INTEGER,"+KEY_server_prop_id+" TEXT)";
    db.execSQL(CREATE_PROPERTY);

    String CREATE_REQUIREMENT = "CREATE TABLE " + TABLE_REQUIREMENT + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_title + " TEXT,"+KEY_description+ " TEXT,"+KEY_client_email+ " TEXT,"
        +KEY_location+" TEXT,"+KEY_area+" TEXT,"+KEY_range+" TEXT,"+KEY_price+" TEXT,"+KEY_price_range+" TEXT,"
        +KEY_type+" TEXT,"+KEY_created+ " TEXT,"+KEY_status+" INTEGER,"+KEY_server_req_id+" TEXT)";
    db.execSQL(CREATE_REQUIREMENT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIST_DETAIL);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIST_MEMBERS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUIREMENT);
  }

  public SQLiteDatabase open(){
    db = this.getWritableDatabase();
    return db;
  }

  public void closeDatabase(){
    db.close();
  }
}
