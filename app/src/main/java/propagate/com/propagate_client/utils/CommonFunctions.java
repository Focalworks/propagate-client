package propagate.com.propagate_client.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  /*
  * Return Country ISO Code
  * */
  public static String getIsoCode(Context context){
    String isoCode;
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    isoCode = tm.getSimCountryIso();

    return isoCode;
  }

  /*
  *  Hide KeyBoard Programmatically
  * */
  public static void hideKeyboard(Activity activity) {
    // Check if no view has focus:
    View view = activity.getCurrentFocus();
    if (view != null) {
      InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
  }

  public static String getPlurals(int num,String s){
    String str = "";

    if(num == 1){
      str = (num)+" "+s;
    }else{
      str = (num)+" "+s+"s";
    }

    return str;
  }

  public static String getInternationalFormatNumber(String phoneNumber,String countryCode){
    String internationalFormat = "";
    if(phoneNumber.startsWith("00")) {
      internationalFormat = phoneNumber.replaceFirst("00", "+");
      return internationalFormat;
    }

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    PhoneNumber numberProto = null;

    try {
      numberProto = phoneUtil.parse(phoneNumber, countryCode.toUpperCase());
    } catch (NumberParseException e) {
      // if there’s any error
      Log.e("Parse Error", e.toString()+" "+numberProto);
    }

    // check if the number is valid
    boolean isValid = phoneUtil.isValidNumber(numberProto);

    if (isValid) {
      // get the valid number’s international format
      internationalFormat = phoneUtil.format(numberProto,PhoneNumberFormat.INTERNATIONAL);
    } else {
      if(phoneNumber.startsWith("+")) {
        internationalFormat = phoneNumber;
      }
      else {
        internationalFormat = null;
      }
    }

    return internationalFormat;
  }

  public static String getCreatedDate() {
    long timestamp = System.currentTimeMillis();
    Calendar c = Calendar.getInstance();
    c.setTimeInMillis(timestamp);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Log.i("created date",sdf.format(d));
    return sdf.format(d);
  }

  public static String getContactPhoneNoById(Context context,String contactID){
    String contactNumber = null;
    Cursor cursorPhone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
        new String[]{contactID},
        null);

    if(cursorPhone.moveToFirst()) {
      contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
    }

    cursorPhone.close();

    return contactNumber;
  }

  public static ArrayList<HashMap<String, String>> getContactList(Context context){
    ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
    ContentResolver cr = context.getContentResolver();
    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, Constants.PROJECTION, null, null, null);

    if (cursor != null) {
      try {
        final int contactIdIndex = cursor.getColumnIndex(ContactsContract.Contacts._ID);
        final int displayNameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        final int imgIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI);
        long number = 0;
        String displayName, profile_Img, contactId;

        while (cursor.moveToNext()) {
          if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
            contactId = cursor.getString(contactIdIndex);
            displayName = cursor.getString(displayNameIndex);
            profile_Img = cursor.getString(imgIndex);

            HashMap<String, String> map = new HashMap<String, String>();
            map.put("contact_id", contactId);
            map.put("display_name", displayName);
            map.put("profile_pic", profile_Img);
            map.put("isSelected", "false");
            contactList.add(map);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        cursor.close();
      }
    }

    return contactList;
  }

  public static ArrayList<HashMap<String, String>> getAllContacts(Context context){

    SortedSet<HashMap<String, String>> lists = new TreeSet<HashMap<String, String>>(new Comparator<HashMap<String, String>>() {
      @Override
      public int compare(HashMap<String, String> arg0, HashMap<String, String> arg1) {
        return arg0.get("phone_number").compareTo(arg1.get("phone_number"));
      }
    });

    String isoCode = CommonFunctions.getIsoCode(context);

    ContentResolver cr = context.getContentResolver();
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    Cursor phones = cr.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
        null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            + " ASC");
    while (phones.moveToNext()) {
      String contact_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
      String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
      String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
      String photo = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

      String internationalFormat = CommonFunctions.getInternationalFormatNumber(phoneNumber, isoCode);
      if (internationalFormat !=null ) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("contact_id", contact_id);
        map.put("display_name", name);
        map.put("profile_pic", photo);
        map.put("isSelected", "false");
        list.add(map);
      }
    }
    phones.close();

    Iterator<HashMap<String, String>> iterator = list.iterator();
    while(iterator.hasNext()) {
      lists.add(iterator.next());
    }
    list.clear();
    list.addAll(lists);


    return  list;
  }

  public static int getVersion(Context context) {
    try {
      PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      return pInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      return 0;
    }
  }

  // validating email id
  public static boolean isValidEmail(String emailAddress) {
    String pttn = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Pattern p = Pattern.compile(pttn);
    Matcher m = p.matcher(emailAddress);

    if (m.matches()) {
      return true;
    }
    return false;
  }

  public static String trimMessage(String json, String key){
    String trimmedString = null;

    try{
      JSONObject jsonObj = new JSONObject(json);
      trimmedString = jsonObj.getString(key);
    } catch(JSONException e){
      e.printStackTrace();
      return null;
    }

    return trimmedString;
  }

  public static void errorResponseHandler(Context context,VolleyError error){
    String json = null;
    NetworkResponse response = error.networkResponse;
    if(response != null && response.data != null){
      switch(response.statusCode){
        case 422:
          json = new String(response.data);
          json = CommonFunctions.trimMessage(json, "message");
          if(json != null) Toast.makeText(context, "" + json, Toast.LENGTH_SHORT).show();
          break;

        case 500:
          json = new String(response.data);
          json = CommonFunctions.trimMessage(json, "message");
          if(json != null) Toast.makeText(context,""+json,Toast.LENGTH_SHORT).show();
          break;
      }
    }
  }

}
