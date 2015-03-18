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

  //Common Columns
  public static final String KEY_id = "id";
  public static final String KEY_created = "created_time";


  // Distribution List Details Table Column names
  public static final String KEY_dist_ID = "dist_id";
  public static final String KEY_dist_name = "name";
  public static final String KEY_created_by = "created_by";
  public static final String KEY_count = "count";
  public static final String KEY_status = "status";

  //Distribution Members Table Column names
  public static final String KEY_contact_id = "contact_id";
  public static final String KEY_member_name = "name";
  public static final String KEY_photo_uri = "photo_uri";

  //Property Table Column Names
  public static final String KEY_property_title = "title";
  public static final String KEY_property_description = "description";
  public static final String KEY_property_agent_id = "agent_id";
  public static final String KEY_property_client_email = "client_email";
  public static final String KEY_property_location = "location";
  public static final String KEY_property_address = "address";
  public static final String KEY_property_area = "area";
  public static final String KEY_property_price = "price";
  public static final String KEY_property_type = "type";

  public DatabaseHelper(Context context){
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    String CREATE_DIST_DETAIL = "CREATE TABLE " + TABLE_DIST_DETAIL + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_dist_name + " TEXT,"+KEY_created_by+ " TEXT,"+KEY_created+ " TEXT,"
        +KEY_count+" INTEGER,"+KEY_status+" INTEGER)";
    db.execSQL(CREATE_DIST_DETAIL);

    String CREATE_DIST_MEMBERS = "CREATE TABLE " + TABLE_DIST_MEMBERS + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_dist_ID + " INTEGER,"+KEY_contact_id+ " TEXT,"+KEY_member_name+ " TEXT,"
        +KEY_photo_uri+" TEXT)";
    db.execSQL(CREATE_DIST_MEMBERS);

    String CREATE_PROPERTY = "CREATE TABLE " + TABLE_PROPERTY + "("
        + KEY_id + " INTEGER PRIMARY KEY,"+ KEY_property_title + " TEXT,"+KEY_property_description+ " TEXT,"+KEY_property_agent_id+ " INTEGER,"+KEY_property_client_email+ " TEXT,"
        +KEY_property_location+" TEXT,"+KEY_property_address+" TEXT,"+KEY_property_area+" TEXT,"+KEY_property_price+" TEXT,"+KEY_property_type+" TEXT,"+KEY_created+ " TEXT,"+KEY_status+" INTEGER)";
    db.execSQL(CREATE_PROPERTY);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIST_DETAIL);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIST_MEMBERS);
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY);
  }

  public SQLiteDatabase open(){
    db = this.getWritableDatabase();
    return db;
  }

  public void closeDatabase(){
    db.close();
  }
}
