package propagate.com.propagate_client.volleyRequest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import propagate.com.propagate_client.authentication.LoginSessionManager;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;

/**
 * Created by kaustubh on 25/3/15.
 */
public class APIHandler{

  private static APIHandler mInstance;
  private static Context context;
  private static int appVersion;
  private APIHandlerInterface apiHandlerInterface;
  private static LoginSessionManager loginSessionManager;

  public static APIHandler getInstance(Context context){
    if(mInstance == null)
    {
      mInstance = new APIHandler();
    }
    mInstance.context = context;
    loginSessionManager = new LoginSessionManager(context);
    mInstance.apiHandlerInterface = (APIHandlerInterface)context;
    appVersion = CommonFunctions.getVersion(context);
    return mInstance;
  }

  public void restAPIRequest(int method,String url,Map<String, String> params,HashMap<String,String> headers){
    HashMap<String,String> userDetails = loginSessionManager.getUserDetails();
    String access_token = userDetails.get(loginSessionManager.KEY_ACCESS_TOKEN);

    HashMap<String,String> headerParams = headers();
    if(headers != null)
      headerParams.putAll(headers);

    VolleyStringRequest postRequest = new VolleyStringRequest(
        method,
        url+"?access_token="+access_token,
        params,
        requestListener,
        requestErrorListener,
        context,
        headerParams
    );
    postRequest.setRetryPolicy(new DefaultRetryPolicy(
        (int) TimeUnit.SECONDS.toMillis(30),
        0,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public HashMap<String,String> headers(){
    HashMap<String, String> appendHeader = new HashMap<String, String>();
    appendHeader.put("Content-Type","application/x-www-form-urlencoded");
    appendHeader.put("client-type","android");
    appendHeader.put("client-version", appVersion+"");

    return appendHeader;
  }

  Response.Listener<String> requestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {
      apiHandlerInterface.OnRequestResponse(response);
    }
  };

  Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      apiHandlerInterface.OnRequestErrorResponse(error);
    }
  };

}
