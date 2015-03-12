package propagate.com.propagate_client.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;

import fw.com.photoapp.R;
import fw.com.photoapp.dashboard.UserDashboard;
import fw.com.photoapp.database.ReceiveImageModule;
import fw.com.photoapp.database.ReceiveSetModule;
import fw.com.photoapp.userRegistration.RegistrationActivity;
import fw.com.photoapp.utils.CommonFunctions;
import fw.com.photoapp.utils.Constants;

/**
 * Created by kaustubh on 2/2/15.
 */
public class GCMIntentService extends IntentService {

  private static final int NOTIFICATION_ID = 1;
  private NotificationManager mNotificationManager;
  NotificationCompat.Builder builder;
  int numMessages = 0;

  public GCMIntentService() {
    super("GCMIntentService");
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
    // The getMessageType() intent parameter must be the intent you received
    // in your BroadcastReceiver.
    String messageType = gcm.getMessageType(intent);

    if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
      if (GoogleCloudMessaging.
          MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//        sendNotification(extras);
      } else if (GoogleCloudMessaging.
          MESSAGE_TYPE_DELETED.equals(messageType)) {
//        sendNotification(extras);
        // If it's a regular GCM message, do some work.
      } else if (GoogleCloudMessaging.
          MESSAGE_TYPE_MESSAGE.equals(messageType)) {
        Log.i("PhotoApp GCM", "Completed work @ " + SystemClock.elapsedRealtime());
        // Post notification of received message.
        sendNotification(extras);
        Log.i("PhotoApp GCM", "Received: " + extras.toString());
      }
    }
    // Release the wake lock provided by the WakefulBroadcastReceiver.
    GcmBroadcastReceiver.completeWakefulIntent(intent);
  }

  // Put the message into a notification and post it.
  // This is just one simple example of what you might choose to do with
  // a GCM message.
  private void sendNotification(Bundle extras) {
    Intent myIntent = null;
    Constants.notificationPreference = getSharedPreferences("notification",Context.MODE_PRIVATE);
    int count = Constants.notificationPreference.getInt("count", 0);
    Log.i("count",count+"");
    Bundle bundle = extras;
    String title = bundle.getString("title");
    String message = bundle.getString("message");
    String notificationType = bundle.getString("type");

    if(notificationType.equals(Constants.notificationReceivePhotoSet)){
      saveReceivePhotoSetData(bundle);
      myIntent = new Intent(this, UserDashboard.class);
      myIntent.putExtra("tab_index",1);
    }

    mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, RegistrationActivity.class), 0);

    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    if (count != 0)
      inboxStyle.setSummaryText(CommonFunctions.getPlurals(count,"Message"));

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.ic_action_done)
            .setTicker("PhotoApp Notification")
            .setContentTitle(title)
            .setContentText(message);
    if(myIntent != null) {
      myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, Intent.FILL_IN_ACTION);
      mBuilder.setContentIntent(pendingIntent);
    }

//  mBuilder.setStyle(inboxStyle);
    mBuilder.setContentIntent(contentIntent);
    mBuilder.setAutoCancel(true);

    Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.textnotification);
    mBuilder.setSound(sound);

    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    Constants.editorNotificationPreference = Constants.notificationPreference.edit();
    Constants.editorNotificationPreference.putInt("count", count);
    Constants.editorNotificationPreference.commit();
  }

  public void saveReceivePhotoSetData(Bundle bundle){
    String receive_title = bundle.getString("photoSetTitle");
    String receive_created_by = bundle.getString("createdByNumber");
    String receive_img_url = bundle.getString("urls");
    String receive_created = bundle.getString("created");

    try {
      JSONArray urlJsonArray = new JSONArray(receive_img_url);
      ReceiveSetModule receiveSetModule = new ReceiveSetModule();
      long lastInsertedId =receiveSetModule.addReceivePhotoSetData(this, new ReceiveSetModule(receive_title,receive_created_by,urlJsonArray.length(),receive_created));

      ReceiveImageModule receiveImageModule = new ReceiveImageModule();

      for (int i = 0; i < urlJsonArray.length(); i++)
        receiveImageModule.addReceiveImageData(this,new ReceiveImageModule(Long.toString(lastInsertedId),urlJsonArray.getString(i)));
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
}
