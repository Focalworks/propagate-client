package propagate.com.propagate_client.volleyRequest;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import propagate.com.propagate_client.authentication.LoginSessionManager;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;

public class AppController extends Application implements APIHandlerInterface{

  public static final String TAG = AppController.class.getSimpleName();
  private long id;
  private String isoCode;
  private RequestQueue mRequestQueue;
  private static AppController mInstance;
  private static LoginSessionManager loginSessionManager;

  @Override
  public void onCreate() {
    super.onCreate();
    mInstance = this;
    loginSessionManager = new LoginSessionManager(this);
    isoCode = CommonFunctions.getIsoCode(getApplicationContext());
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
  public void postCreateDistList(Activity activity,long g_id){
    id = g_id;
    ArrayList<DistListModule> distInfo = new ArrayList<DistListModule>();
    distInfo = DistListModule.getInstance().getDistLists(getApplicationContext(),id);
    String distListName = "";
    if(distInfo.size() != 0)
      distListName = distInfo.get(0).getDist_name();

    ArrayList<DistListModule> distMemberInfo = new ArrayList<DistListModule>();
    distMemberInfo = DistListModule.getInstance().getDistListMembers(getApplicationContext(), id);
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

    HashMap<String,String> userDetails = loginSessionManager.getUserDetails();
    String access_token = userDetails.get(loginSessionManager.KEY_ACCESS_TOKEN);

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.postDistListUrl+"?access_token="+access_token,
        getGroupParams(contacts,distListName),
        null
    );
  }

  public Map<String,String> getGroupParams(JSONArray contacts,String distListName){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("name", distListName);
    jsonParams.put("members", contacts.toString());

    return jsonParams;
  }

  Response.Listener<String> distListRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String response) {

    }
  };

  Response.ErrorListener distListRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Error Response",error.toString());
    }
  };

  /*
  * Post Delete Distribution List
  * */
  public void postDeleteDistList(Activity activity,long dist_id){
    HashMap<String,String> userDetails = loginSessionManager.getUserDetails();
    String access_token = userDetails.get(loginSessionManager.KEY_ACCESS_TOKEN);

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.DELETE,
        Constants.postDistListUrl+"/"+dist_id+"?access_token="+access_token,
        null,
        null
    );
  }

  /*
  * Post Created Property
  * */

  public void postCreateProperty(Activity activity,long prop_id){
    id = prop_id;
    ArrayList<PropertyModule> propertyList = new ArrayList<PropertyModule>();
    propertyList = PropertyModule.getInstance().getPropertyInfo(getApplicationContext(),id);
    PropertyModule propertyInfo = null;
    if(propertyList.size() != 0)
      propertyInfo = propertyList.get(0);

    HashMap<String,String> userDetails = loginSessionManager.getUserDetails();
    String access_token = userDetails.get(loginSessionManager.KEY_ACCESS_TOKEN);

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.createPropertyUrl+"?access_token="+access_token,
        getPropertyParams(propertyInfo),
        null
    );
  }

  public Map<String,String> getPropertyParams(PropertyModule propertyInfo){

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

  /*
  * Post Created Requirement
  * */

  public void postCreateRequirement(Activity activity,long id){
    ArrayList<RequirementModule> requirementList = new ArrayList<RequirementModule>();
    requirementList = RequirementModule.getInstance().getRequirementInfo(getApplicationContext(), id);
    RequirementModule requirementInfo = null;
    if(requirementList.size() != 0)
      requirementInfo = requirementList.get(0);

    HashMap<String,String> userDetails = loginSessionManager.getUserDetails();
    String access_token = userDetails.get(loginSessionManager.KEY_ACCESS_TOKEN);

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.createRequirementUrl+"?access_token="+access_token,
        getRequirementParams(requirementInfo),
        null
    );
  }

  public Map<String,String> getRequirementParams(RequirementModule requirementInfo){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("title", requirementInfo.getTitle());
    jsonParams.put("description", requirementInfo.getDescription());
    jsonParams.put("clientEmail", requirementInfo.getClient_email());
    jsonParams.put("location", requirementInfo.getLocation());
    jsonParams.put("area", requirementInfo.getArea());
    jsonParams.put("range", requirementInfo.getRange());
    jsonParams.put("price", requirementInfo.getPrice());
    jsonParams.put("priceRange", requirementInfo.getPrice_range());
    jsonParams.put("type", requirementInfo.getType());

    return jsonParams;
  }

  @Override
  public void OnRequestResponse(String response) {

  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {

  }
}
