package propagate.com.propagate_client.volleyRequest;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.SessionManager;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
  private SessionManager sessionManager;

	private RequestQueue mRequestQueue;

	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
    sessionManager = new SessionManager(getApplicationContext());
	}

	public static synchronized AppController getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

  public void getSessionToken(){
    VolleyStringRequest getSessionTokenRequest = new VolleyStringRequest(
        Request.Method.GET,
        Constants.getSessionTokenUrl,
        null,
        requestSessionListener,
        requestSessionErrorListener,
        getApplicationContext()
    );
    addToRequestQueue(getSessionTokenRequest);
  }

  Response.Listener<String> requestSessionListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String token) {
      Log.e("Response token", token);
      sessionManager.createLoginSessionToken(token);
    }
  };

  Response.ErrorListener requestSessionErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Error Response",error.toString());
    }
  };

  public void registerDeviceID(String deviceId){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.registerDeviceUrl,
        getGroupParams(deviceId),
        requestListener,
        requestErrorListener,
        getApplicationContext()
    );

    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public Map<String,String> getGroupParams(String registrationId){
    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("deviceId", CommonFunctions.getDeviceId(this));
    jsonParams.put("registrationId", registrationId);

    return jsonParams;
  }

  Response.Listener<String> requestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String token) {
      Log.e("Register Device", token);
    }
  };

  Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Register Error",error.toString());
    }
  };


  public void loginUser(){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.loginUrl,
        getLoginParams(),
        loginRequestListener,
        loginRequestErrorListener,
        getApplicationContext()
    );

    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public Map<String,String> getLoginParams(){
    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("email", "kaustubh@gmail.com");
    jsonParams.put("password", "123456");

    return jsonParams;
  }

  Response.Listener<String> loginRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String token) {
      Log.e("Login token", token);
    }
  };

  Response.ErrorListener loginRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Login Error Response",error.toString());
    }
  };

}
