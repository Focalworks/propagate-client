package propagate.com.propagate_client.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kaustubh on 12/3/15.
 */
public class CommonFunctions {

  // Add code to print out the key hash and add to facebook app
  private void printKeyHash(Context context){
    try {
      PackageInfo info = context.getPackageManager().getPackageInfo(
          "propagate.com.propagate_client",
          PackageManager.GET_SIGNATURES);
      for (Signature signature : info.signatures) {
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(signature.toByteArray());
        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
      }
    } catch (PackageManager.NameNotFoundException e) {
      Log.d("KeyHash:", e.toString());
    } catch (NoSuchAlgorithmException e) {
      Log.d("KeyHash:", e.toString());
    }
  }

  // Returns Device ID
  public static String getDeviceId(Context context) {
    String sim1DeviceId;
    TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

    sim1DeviceId = tm.getDeviceId();

    if(sim1DeviceId == null)
      sim1DeviceId = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

    return sim1DeviceId;
  }

}
