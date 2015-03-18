package propagate.com.propagate_client.volleyRequest;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.distributionList.CreateDistListActivity;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.SessionManager;

public class AppController extends Application {

	public static final String TAG = AppController.class.getSimpleName();
  private SessionManager sessionManager;
  private long group_id;
  private String isoCode;

	private RequestQueue mRequestQueue;

	private static AppController mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
    isoCode = CommonFunctions.getIsoCode(getApplicationContext());
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

  /*
  * Retrieve session token for authentication
  * */
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


  /*
  * Register device id for notification
  * */
  public void registerDeviceID(String deviceId){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.registerDeviceUrl,
        getGroupParams(deviceId),
        registerDeviceRequestListener,
        registerDeviceRequestErrorListener,
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

  Response.Listener<String> registerDeviceRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String token) {
      Log.e("Register Device", token);
    }
  };

  Response.ErrorListener registerDeviceRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Register Error",error.toString());
    }
  };


  /*
  * Post Created Distribution List
  * */
  public void postCreateDistList(long g_id){
    group_id = g_id;
    ArrayList<DistListModule> distInfo = new ArrayList<DistListModule>();
    distInfo = DistListModule.getInstance().getDistLists(getApplicationContext(),group_id);
    String distListName = "";
    if(distInfo.size() != 0)
      distListName = distInfo.get(0).getDist_name();

    ArrayList<DistListModule> distMemberInfo = new ArrayList<DistListModule>();
    distMemberInfo = DistListModule.getInstance().getDistListMembers(getApplicationContext(), group_id);
    JSONArray contacts = new JSONArray();

    if(distMemberInfo != null) {
      for (DistListModule member : distMemberInfo) {
        String phoneNumber = CommonFunctions.getContactPhoneNoById(this, member.getContact_id());
        if(phoneNumber != null) {
          String internationalNum = CommonFunctions.getInternationalFormatNumber(phoneNumber, isoCode);
          if (internationalNum != null)
            contacts.put(internationalNum);
        }
      }
    }

    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.testUrl,
        getGroupParams(contacts,distListName),
        distListRequestListener,
        distListRequestErrorListener,
        getApplicationContext()
    );
    postRequest.setRetryPolicy(new DefaultRetryPolicy(
        (int) TimeUnit.SECONDS.toMillis(30),
        0,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public Map<String,String> getGroupParams(JSONArray contacts,String distListName){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("name", distListName);
    jsonParams.put("createdBy", "1");
    jsonParams.put("members", contacts.toString());

    return jsonParams;
  }

  Response.Listener<String> distListRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String g_id) {
      Log.i("dist list response",g_id);
      DistListModule.getInstance().updateGroupStatus(getApplicationContext(), group_id);
    }
  };

  Response.ErrorListener distListRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Error Response",error.toString());
    }
  };

  /*
  * Post Created Property
  * */

  public void postCreateProperty(long id){
    ArrayList<PropertyModule> propertyList = new ArrayList<PropertyModule>();
    propertyList = PropertyModule.getInstance().getPropertyInfo(getApplicationContext(),id);
    PropertyModule propertyInfo = null;
    if(propertyList.size() != 0)
      propertyInfo = propertyList.get(0);

    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.createPropertyUrl,
        getGroupParams(propertyInfo),
        createPropertyRequestListener,
        createPropertyRequestErrorListener,
        getApplicationContext()
    );
    AppController.getInstance().addToRequestQueue(postRequest);
  }

  public Map<String,String> getGroupParams(PropertyModule propertyInfo){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("agentId", Long.toString(propertyInfo.getAgent_id()));
    jsonParams.put("title", propertyInfo.getTitle());
    jsonParams.put("description", propertyInfo.getDesc());
    jsonParams.put("clientEmail", propertyInfo.getClient_email());
    jsonParams.put("location", propertyInfo.getLocation());
    jsonParams.put("address", propertyInfo.getAddress());
    jsonParams.put("area", propertyInfo.getArea());
    jsonParams.put("price", propertyInfo.getPrice());
    jsonParams.put("type", propertyInfo.getType());

    return jsonParams;
  }



  Response.Listener<String> createPropertyRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String property_id) {
      Log.i("add property response",property_id);
    }
  };

  Response.ErrorListener createPropertyRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Error Response",error.toString());
    }
  };

 }
