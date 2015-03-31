package propagate.com.propagate_client.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by kaustubh on 31/3/15.
 */
public class RegisterModule {

  private SQLiteDatabase db;
  private ContentValues contentValues;
  private static RegisterModule mInstance;

  private long id;
  private String name;
  private String email;
  private String number;
  private String role;
  private String expr;
  private String summary;

  public static RegisterModule getInstance(){
    if(mInstance == null)
    {
      mInstance = new RegisterModule();
    }
    return mInstance;
  }

  public RegisterModule(){}

  public RegisterModule(String name, String email, String number) {
    this.name = name;
    this.email = email;
    this.number = number;
  }

  public RegisterModule(long id, String name, String email, String number, String role,String expr,String summary) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.number = number;
    this.role = role;
    this.expr = expr;
    this.summary = summary;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getExpr() {
    return expr;
  }

  public void setExpr(String expr) {
    this.expr = expr;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public long addRegisterUser(Context context, RegisterModule registerModule){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    contentValues = new ContentValues();
    contentValues.put(databaseHelper.KEY_user_name, registerModule.getName());
    contentValues.put(databaseHelper.KEY_user_phone, registerModule.getNumber());
    contentValues.put(databaseHelper.KEY_user_email, registerModule.getEmail());

    long lastId = db.insert(databaseHelper.TABLE_REGISTER_USER,null,contentValues);
    databaseHelper.close();

    Log.i("Database", "Inserted Register User Details");
    return lastId;
  }

  /*Update user details Expr and Summary*/
  public void updateUserDetails(Context context,long reg_id,String role,String expr,String summary){
    DatabaseHelper databaseHelper = new DatabaseHelper(context);
    db = databaseHelper.open();

    ContentValues values = new ContentValues();
    values.put(databaseHelper.KEY_user_role, role);
    values.put(databaseHelper.KEY_user_expr, expr);
    values.put(databaseHelper.KEY_user_summary, summary);
    db.update(databaseHelper.TABLE_DIST_DETAIL, values, databaseHelper.KEY_id+"="+reg_id, null);
    databaseHelper.close();
    Log.i("Database", "Updated User Details");
  }
}
