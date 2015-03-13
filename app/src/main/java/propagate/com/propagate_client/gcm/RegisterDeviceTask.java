package propagate.com.propagate_client.gcm;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import propagate.com.propagate_client.utils.Constants;

/**
 * Created by kaustubh on 12/3/15.
 */
public class RegisterDeviceTask extends AsyncTask<String,String,String> {

  private Context context;
  private OnTaskExecutionFinished taskExecutionFinished;
  private GoogleCloudMessaging gcm;

  public RegisterDeviceTask(Context context){
    this.context = context;
    this.taskExecutionFinished = (OnTaskExecutionFinished)context;

    gcm = GoogleCloudMessaging.getInstance(context);
  }

  public interface OnTaskExecutionFinished
  {
    public void onTaskFinishedEvent(String result);
  }

  @Override
  protected String doInBackground(String... params) {
    String regId="";
    String msg = "";
    try {
      if (gcm == null) {
        gcm = GoogleCloudMessaging.getInstance(context);
      }
      regId = gcm.register(Constants.SENDER_ID);
      msg = "Device registered, registration ID=" + regId;

      GCMUtils.storeRegistrationId(context, regId);
    } catch (IOException ex) {
      msg = "Error :" + ex.getMessage();
    }
    return regId;
  }

  @Override
  protected void onPostExecute(String msg) {
    Log.i("PhotoApp Reg_ID", msg);
    if(taskExecutionFinished != null)
      taskExecutionFinished.onTaskFinishedEvent(msg);
  }
}
