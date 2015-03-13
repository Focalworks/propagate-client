package propagate.com.propagate_client.utils;

/**
 * Created by kaustubh on 12/3/15.
 */
public class Constants {
  /* GCM Notification */
  public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  public static final String PROPERTY_REG_ID = "registration_id";
  public static final String PROPERTY_APP_VERSION = "appVersion";
  public static final String SENDER_ID= "617187157247";

  /*
  * Urls
  * */
//   private static String baseUrl = "http://192.168.7.205/RND/laravel_rnd/propaget/public/";
   private static String baseUrl = "http://192.168.7.102/propagate/public/";

   public static String getSessionTokenUrl = baseUrl + "get-token";
   public static String registerDeviceUrl = baseUrl + "register-device";
   public static String loginUrl = baseUrl + "mobile/login";
   public static String testingAuthUrl = baseUrl + "testingAuth";
}
