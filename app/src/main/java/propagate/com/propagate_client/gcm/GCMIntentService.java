package propagate.com.propagate_client.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.login.LoginSelectionActivity;

/**
 * Created by kaustubh on 12/3/15.
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

    Bundle bundle = extras;
    String title = bundle.getString("title");
    String message = bundle.getString("message");

    myIntent = new Intent(this, LoginSelectionActivity.class);
    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

    mNotificationManager = (NotificationManager)
        this.getSystemService(Context.NOTIFICATION_SERVICE);

    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        new Intent(this, LoginSelectionActivity.class), 0);

    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(this)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setTicker("Propagate Notification")
            .setContentTitle(title)
            .setContentText(message);
    
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, myIntent, Intent.FILL_IN_ACTION);
    mBuilder.setContentIntent(pendingIntent);

    mBuilder.setContentIntent(contentIntent);
    mBuilder.setAutoCancel(true);

    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

  }
}
