package propagate.com.propagate_client.volleyRequest;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import propagate.com.propagate_client.login.LoginSessionManager;
import propagate.com.propagate_client.utils.CommonFunctions;

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

    if(access_token != null)
      url = url+"?access_token="+access_token;

    HashMap<String,String> headerParams = headers();
    if(headers != null)
      headerParams.putAll(headers);

    VolleyStringRequest postRequest = new VolleyStringRequest(
        method,
        url,
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
