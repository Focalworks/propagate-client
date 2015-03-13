package propagate.com.propagate_client.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.util.Log;

import java.util.HashMap;

import propagate.com.propagate_client.authentication.LoginSelectionActivity;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 12/3/15.
 */
public class SessionManager {
  // Shared Preferences
  SharedPreferences pref;

  // Editor for Shared preferences
  SharedPreferences.Editor editor;

  // Context
  Context context;

  // Shared pref mode
  int PRIVATE_MODE = 0;

  // SharedPreference file name
  private static final String PREF_NAME = "SessionPreference";

  // All Shared Preferences Keys
  private static final String IS_LOGIN = "IsLoggedIn";
  public static final String KEY_USER_ID = "user_id";
  public static final String KEY_NAME = "name";
  public static final String KEY_EMAIL = "email";
  public static final String KEY_LOGIN_TYPE = "login_type";
  public static final String KEY_SESSION_TOKEN = "session_token";
  public static final String KEY_XCSRF_TOKEN = "xcsrf_token";
  public static final String KEY_XAUTH_TOKEN = "xauth_token";

  // Constructor
  public SessionManager(Context context){
    this.context = context;
    pref = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    editor = pref.edit();
  }

  /**
   * Create login session
   * */
  public void createLoginSession(String userId,String name, String email, String login_type){

    editor.putBoolean(IS_LOGIN, true);
    editor.putString(KEY_USER_ID, userId);
    editor.putString(KEY_NAME, name);
    editor.putString(KEY_EMAIL, email);
    editor.putString(KEY_LOGIN_TYPE, login_type);

    // commit changes
    editor.commit();
  }

  /**
   * Create login session
   * */
  public void createLoginSessionToken(String xcsrf_token){
    editor.putString(KEY_XCSRF_TOKEN,xcsrf_token);

    // commit changes
    editor.commit();
  }

  /**
   * Create login session
   * */
  public void createXAuthToken(String xauth_token){
    editor.putString(KEY_XAUTH_TOKEN,xauth_token);

    // commit changes
    editor.commit();
  }


  /**
   * Check login method wil check user login status
   * If false it will redirect user to login page
   * Else won't do anything
   * */
  public void checkLogin(){
    // Check login status
    if(!this.isLoggedIn()){
      Intent i = new Intent(context, LoginSelectionActivity.class);
      i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(i);
    }

  }

  /**
   * Get stored session data
   * */
  public HashMap<String, String> getUserDetails(){
    HashMap<String, String> user = new HashMap<String, String>();

    user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
    user.put(KEY_NAME, pref.getString(KEY_NAME, null));
    user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
    user.put(KEY_LOGIN_TYPE, pref.getString(KEY_LOGIN_TYPE, null));

    // return user
    return user;
  }

  /**
   * Get stored session tokens
   * */
  public HashMap<String, String> getSessionTokens(){
    HashMap<String, String> user = new HashMap<String, String>();

    user.put(KEY_SESSION_TOKEN, pref.getString(KEY_SESSION_TOKEN, null));
    user.put(KEY_XCSRF_TOKEN, pref.getString(KEY_XCSRF_TOKEN, null));
    user.put(KEY_XAUTH_TOKEN, pref.getString(KEY_XAUTH_TOKEN, null));
    // return user
    return user;
  }

  /**
   * Clear session details
   * */
  public void logoutUser(){
    // Clearing all data from Shared Preferences
    editor.clear();
    editor.commit();

    // After logout redirect user to Logging Activity
    Intent i = new Intent(context, LoginSelectionActivity.class);
    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(i);
  }

  /**
   * Quick check for login
   * **/
  // Get Login State
  public boolean isLoggedIn(){
    return pref.getBoolean(IS_LOGIN, false);
  }

}
