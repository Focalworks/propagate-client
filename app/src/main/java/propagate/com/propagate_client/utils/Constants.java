package propagate.com.propagate_client.utils;

import android.provider.ContactsContract;

/**
 * Created by kaustubh on 12/3/15.
 */
public class Constants {
  /* GCM Notification */
  public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  public static final String PROPERTY_REG_ID = "registration_id";
  public static final String PROPERTY_APP_VERSION = "appVersion";
  public static final String SENDER_ID= "617187157247";

  public static final String[] PROJECTION = new String[] {
      ContactsContract.Contacts._ID,
      ContactsContract.Contacts.DISPLAY_NAME,
      ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
      ContactsContract.Contacts.HAS_PHONE_NUMBER
  };

  /*
  * Urls
  * */
//  private static String baseUrl = "http://192.168.7.150/propaget/prop_lravel5/propaget/public/";
//  private static String baseUrl = "http://192.168.7.205/RND/laravel_rnd/propaget/public/";
//  private static String baseUrl = "http://192.168.7.102/propaget/public/";
  private static String baseUrl = "http://192.168.0.104/propaget/public/";
  public static String getSessionTokenUrl = baseUrl + "get-token";
  public static String registerDeviceUrl = baseUrl + "register-device";
  public static String loginOauthUrl = baseUrl + "oauth/token";
  public static String postDistListUrl = baseUrl + "dist-list";
  public static String createPropertyUrl = baseUrl + "property";
  public static String createRequirementUrl = baseUrl + "req-list";

  public static String test = baseUrl + "test";
  public static String testBad = baseUrl + "test-bad";
  public static String testAuth = baseUrl + "test-auth";
}
