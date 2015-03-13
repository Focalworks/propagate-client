package propagate.com.propagate_client.gcm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import propagate.com.propagate_client.authentication.LoginSelectionActivity;
import propagate.com.propagate_client.utils.Constants;

/**
 * Created by kaustubh on 12/3/15.
 */
public class GCMUtils {

  public static boolean checkPlayServices(Activity activity) {
    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
        GooglePlayServicesUtil.getErrorDialog(resultCode, activity, Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
      } else {
        Log.i("PhotoApp GCM", "This device is not supported.");
        activity.finish();
      }
      return false;
    }
    return true;
  }

  public static String getRegistrationId(Context context) {
    final SharedPreferences prefs = getGCMPreferences(context);
    String registrationId = prefs.getString(Constants.PROPERTY_REG_ID, "");
    if (registrationId.isEmpty()) {
      Log.i("PhotoApp GCM", "Registration not found.");
      return "";
    }
    // Check if app was updated; if so, it must clear the registration ID
    // since the existing regID is not guaranteed to work with the new
    // app version.
    int registeredVersion = prefs.getInt(Constants.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    int currentVersion = getAppVersion(context);
    if (registeredVersion != currentVersion) {
      Log.i("PhotoApp GCM", "App version changed.");
      return "";
    }
    return registrationId;
  }

  public static void storeRegistrationId(Context context, String regId) {
    final SharedPreferences prefs = getGCMPreferences(context);
    int appVersion = getAppVersion(context);
    Log.i("PhotoApp GCM", "Saving regId on app version " + appVersion);
    SharedPreferences.Editor editor = prefs.edit();
    editor.putString(Constants.PROPERTY_REG_ID, regId);
    editor.putInt(Constants.PROPERTY_APP_VERSION, appVersion);
    editor.commit();
  }

  private static SharedPreferences getGCMPreferences(Context context) {
    // This sample app persists the registration ID in shared preferences, but
    // how you store the regID in your app is up to you.
    return context.getSharedPreferences(LoginSelectionActivity.class.getSimpleName(),
        Context.MODE_PRIVATE);
  }

  private static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager()
          .getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      // should never happen
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

}
