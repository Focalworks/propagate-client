package propagate.com.propagate_client.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by kaustubh on 26/3/15.
 */
public class LoginSessionManager {

  // Shared Preferences reference
  SharedPreferences pref;

  // Editor reference for Shared preferences
  SharedPreferences.Editor editor;

  // Context
  Context context;

  // Shared pref mode
  int PRIVATE_MODE = 0;

  // Shared pref file name
  private static final String PREFER_NAME = "LoginUserPref";

  // All Shared Preferences Keys
  private static final String IS_USER_LOGIN = "IsUserLoggedIn";

  public static final String KEY_EMAIL = "email";
  public static final String KEY_ACCESS_TOKEN = "access_token";
  public static final String KEY_REFRESH_TOKEN = "refresh_token";
  public static final String KEY_DEVICE_REGISTERED = "device_registered";

  // Constructor
  public LoginSessionManager(Context context){
    this.context = context;
    pref = context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  //Create login session
  public void createUserLoginSession(String email, String access_token, String refresh_token){
    Log.e("User Session",access_token+" "+refresh_token+" "+email);
    editor.putBoolean(IS_USER_LOGIN, true);
    editor.putString(KEY_EMAIL, email);
    editor.putString(KEY_ACCESS_TOKEN, access_token);
    editor.putString(KEY_REFRESH_TOKEN, refresh_token);

    // commit changes
    editor.commit();
  }

  /**
   * Get stored session data
   * */
  public HashMap<String, String> getUserDetails(){

    //Use hashmap to store user credentials
    HashMap<String, String> user = new HashMap<String, String>();

    user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
    user.put(KEY_ACCESS_TOKEN, pref.getString(KEY_ACCESS_TOKEN, null));
    user.put(KEY_REFRESH_TOKEN, pref.getString(KEY_REFRESH_TOKEN, null));

    // return user
    return user;
  }

  /**
   * Clear session details
   * */
  public void logoutUser(){

    // Clearing all user data from Shared Preferences
    editor.clear();
    editor.commit();

    // After logout redirect user to Login Activity
    Intent i = new Intent(context, LoginActivity.class);

    // Closing all the Activities
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    // Add new Flag to start new Activity
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    // Staring Login Activity
    context.startActivity(i);
  }

  // Check for login
  public boolean isUserLoggedIn(){
    return pref.getBoolean(IS_USER_LOGIN, false);
  }

  //Check for DeviceRegistration
  public boolean isDeviceRegistered(){
    return pref.getBoolean(KEY_DEVICE_REGISTERED, false);
  }

  public void resetAccessToken(String access_token,String refresh_token){
    editor.putString(KEY_ACCESS_TOKEN, access_token);
    editor.putString(KEY_REFRESH_TOKEN, refresh_token);

    // commit changes
    editor.commit();
  }

  public void resetDeviceRegistered(Boolean val){
    editor.putBoolean(KEY_DEVICE_REGISTERED, val);

    // commit changes
    editor.commit();
  }
}
