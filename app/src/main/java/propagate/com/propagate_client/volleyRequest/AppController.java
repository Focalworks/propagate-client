package propagate.com.propagate_client.volleyRequest;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.facebook.Session;
import com.google.android.gms.plus.Plus;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.login.LoginSelectionActivity;
import propagate.com.propagate_client.login.LoginSessionManager;
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


  public void logoutUser(){
    loginSessionManager.logoutUser();
    if (LoginSelectionActivity.mGoogleApiClient.isConnected()){
      Plus.AccountApi.clearDefaultAccount(LoginSelectionActivity.mGoogleApiClient);
      LoginSelectionActivity.mGoogleApiClient.disconnect();
      LoginSelectionActivity.mGoogleApiClient.connect();
    } else if (Session.getActiveSession() != null) {
      Session.getActiveSession().closeAndClearTokenInformation();
      Session.setActiveSession(null);
    }
  }

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

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.postDistListUrl,
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

  /*
  * Post Delete Distribution List
  * */
  public void postDeleteDistList(Activity activity,long dist_id){

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.DELETE,
        Constants.postDistListUrl+"/"+dist_id,
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

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.postPropertyUrl,
        getPropertyParams(propertyInfo),
        null
    );
  }

  public Map<String,String> getPropertyParams(PropertyModule propertyInfo){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("title", propertyInfo.getTitle());
    jsonParams.put("description", propertyInfo.getDesc());
    jsonParams.put("client_email", propertyInfo.getClient_email());
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

    APIHandler.getInstance(activity).restAPIRequest(
        Request.Method.POST,
        Constants.postRequirementUrl,
        getRequirementParams(requirementInfo),
        null
    );
  }

  public Map<String,String> getRequirementParams(RequirementModule requirementInfo){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("title", requirementInfo.getTitle());
    jsonParams.put("description", requirementInfo.getDescription());
    jsonParams.put("client_email", requirementInfo.getClient_email());
    jsonParams.put("location", requirementInfo.getLocation());
    jsonParams.put("area", requirementInfo.getArea());
    jsonParams.put("range", requirementInfo.getRange());
    jsonParams.put("price", requirementInfo.getPrice());
    jsonParams.put("price_range", requirementInfo.getPrice_range());
    jsonParams.put("type", requirementInfo.getType());

    return jsonParams;
  }

  /*
  * Register device id for notification
  * */
  public void registerDeviceID(){
    APIHandler.getInstance(this).restAPIRequest(
        Request.Method.POST,
        Constants.registerDeviceUrl,
        getGroupParams(),
        null
    );
  }

  public Map<String,String> getGroupParams(){
    String registrationId = GCMUtils.getRegistrationId(getApplicationContext());
    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("deviceId", CommonFunctions.getDeviceId(this));
    jsonParams.put("registrationId", registrationId);

    return jsonParams;
  }

  @Override
  public void OnRequestResponse(String response) {
    Log.e("Request response",response);
    loginSessionManager.resetDeviceRegistered(true);
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    Log.e("Request response error",error.toString());
    loginSessionManager.resetDeviceRegistered(false);
  }
}
