package propagate.com.propagate_client.login;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.gcm.RegisterDeviceTask;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.APIHandler;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;


public class LoginSelectionActivity extends Activity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,RegisterDeviceTask.OnTaskExecutionFinished,APIHandlerInterface {

  private LinearLayout linearSignOptions,linearSignIN;
  private Button btnDefaultLogin,btnSignIn,btnSignUp;
  private SignInButton btnGPlusLogin;
  private LoginButton btnFBSignIn;
  private UiLifecycleHelper uiHelper;
  private String loginType = "facebook";
  private Animation animUP,animGoUP;

  /* Track whether the sign-in button has been clicked so that we know to resolve
  all issues preventing sign-in without waiting.
 */
  private boolean mSignInClicked;

  /* Store the connection result from onConnectionFailed callbacks so that we can
 * resolve them when the user clicks sign-in.
 */
  private ConnectionResult mConnectionResult;

  // Google client to interact with Google API
  public static GoogleApiClient mGoogleApiClient;

  /**
   * A flag indicating that a PendingIntent is in progress and prevents us
   * from starting further intents.
   */
  private boolean mIntentInProgress;
  private static final int RC_SIGN_IN = 0;
  private LoginSessionManager loginSessionManager;
  private Animation animScale;
  private String account;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_selection);

    loginSessionManager = new LoginSessionManager(this);

    if(loginSessionManager.isUserLoggedIn()){
      if(!loginSessionManager.isDeviceRegistered())
        AppController.getInstance().registerDeviceID();

      Intent intent = new Intent(this, DistListingActivity.class);
      startActivity(intent);
      finish();
    }

    uiHelper = new UiLifecycleHelper(this, statusCallback);
    uiHelper.onCreate(savedInstanceState);

    linearSignOptions = (LinearLayout) findViewById(R.id.linearLoginOptions);
    linearSignIN = (LinearLayout) findViewById(R.id.linearSignUp);

    btnDefaultLogin = (Button) findViewById(R.id.loginSelectionBtnDefaultLogin);
    btnDefaultLogin.setOnClickListener(this);

    btnSignIn = (Button) findViewById(R.id.loginSelectionSignIn);
    btnSignIn.setOnClickListener(this);

    btnSignUp = (Button) findViewById(R.id.loginSelectionSignUP);
    btnSignUp.setOnClickListener(this);

    btnGPlusLogin = (SignInButton) findViewById(R.id.loginSelectionBtnGPlusSignIn);
    btnGPlusLogin.setOnClickListener(this);

    btnFBSignIn = (LoginButton) findViewById(R.id.loginSelectionBtnFBSignIn);
    btnFBSignIn.setOnClickListener(this);
    btnFBSignIn.setReadPermissions(Arrays.asList("email"));

    if(!loginSessionManager.isUserLoggedIn()) {
      btnFBSignIn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
        @Override
        public void onUserInfoFetched(GraphUser user) {
          Session session = Session.getActiveSession();
          if (user != null) {
            account = user.getProperty("email").toString();
            Log.e("Facebook", "You are currently logged in as " + user.getName());
            Log.e("Facebook Access Token", session.getAccessToken());
            fbOauthToken(session.getAccessToken());
          }
        }
      });

      mGoogleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(Plus.API)
          .addScope(Plus.SCOPE_PLUS_LOGIN)
          .addScope(new Scope("email"))
          .build();

      registerDeviceForGCM();
    }

    animScale = AnimationUtils.loadAnimation(this,R.anim.scale_down);
    animUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
    animGoUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_go_up);
    animGoUP.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {}

      @Override
      public void onAnimationEnd(Animation animation) {
        linearSignOptions.setVisibility(View.GONE);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {}
    });
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.loginSelectionBtnDefaultLogin:
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
        break;

      case R.id.loginSelectionBtnGPlusSignIn:
        loginType = "google";
        if (!mGoogleApiClient.isConnecting()) {
          mSignInClicked = true;
          resolveSignInError();
        }
        break;

      case R.id.loginSelectionSignUP:
        Intent i = new Intent(getApplicationContext(),RegisterUserActivity.class);
        startActivity(i);
        finish();
        break;

      case R.id.loginSelectionSignIn:
        linearSignOptions.startAnimation(animGoUP);
        linearSignIN.setVisibility(View.VISIBLE);
        linearSignIN.startAnimation(animUP);
        break;
    }
  }

  /*
  * Handles Device Registration for GCM Notification
  * */
  private void registerDeviceForGCM(){
    if (GCMUtils.checkPlayServices(this)) {
      String regId = GCMUtils.getRegistrationId(this);

      if (regId.isEmpty()) {
        new RegisterDeviceTask(this).execute();
      }
    } else {
      Log.i("Registration GCM", "No valid Google Play Services APK found.");
    }
  }

  /*
  * Returns registration id from AsyncTask class
  * */
  @Override
  public void onTaskFinishedEvent(String result){
    Log.e("Reg Id",result);
  }


  /*
  * Code for Google+ Login
  * */

  @Override
  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
  }

  /* A helper method to resolve the current ConnectionResult error. */
  private void resolveSignInError() {
    if (mConnectionResult.hasResolution()) {
      try {
        mIntentInProgress = true;
        mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
      } catch (IntentSender.SendIntentException e) {
        mIntentInProgress = false;
        mGoogleApiClient.connect();
      }
    }
  }

  @Override
  public void onConnectionFailed(ConnectionResult result) {
    if (!result.hasResolution()) {
      GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
      return;
    }

    if (!mIntentInProgress) {

      mConnectionResult = result;

      if (mSignInClicked) {

        resolveSignInError();
      }
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
  }

  @Override
  public void onConnected(Bundle connectionHint) {
    mSignInClicked = false;
    getProfileInformation();
//    Toast.makeText(getApplicationContext(),"User Connected...!!!", Toast.LENGTH_LONG).show();
  }

  /**
   * Fetching user's information name, email, profile pic
   * */
  private void getProfileInformation() {
    try {
      if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        new GPlusAccessTokenTask().execute();
      } else {
        Toast.makeText(getApplicationContext(),"Person information is null", Toast.LENGTH_LONG).show();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  /*
  * Code for Facebook Login
  * */
  private Session.StatusCallback statusCallback = new Session.StatusCallback() {
    @Override
    public void call(Session session, SessionState state,
                     Exception exception) {
      if (state.isOpened()) {
        Log.d("MainActivity", "Facebook session opened.");
      } else if (state.isClosed()) {
        Log.d("MainActivity", "Facebook session closed.");
      }
    }
  };

  private void fbOauthToken(String access_token){
    Map<String, String> params = new HashMap<String, String>();
    params.put("code", access_token);
    params.put("client", "android");
    params.put("grant_type", "facebook");
    params.put("client_id", "testclient");
    params.put("client_secret", "testpass");

    APIHandler.getInstance(this).restAPIRequest(
        Request.Method.POST,
        Constants.fbTokenUrl,
        params,
        null
    );
  }

  @Override
  public void onResume() {
    super.onResume();
    uiHelper.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    uiHelper.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    uiHelper.onDestroy();
  }

  @Override
  public void onSaveInstanceState(Bundle savedState) {
    super.onSaveInstanceState(savedState);
    uiHelper.onSaveInstanceState(savedState);
  }


  /*
  * Handle result return from Google+ and Facebook LoginActivity
  * */
  @Override
  protected void onActivityResult(int requestCode, int responseCode, Intent intent) {

    if (requestCode == RC_SIGN_IN && loginType == "google") {
      if (responseCode != RESULT_OK) {
        mSignInClicked = false;
      }

      mIntentInProgress = false;

      if (!mGoogleApiClient.isConnecting()) {
        mGoogleApiClient.connect();
      }
    }else if(loginType == "facebook"){
      uiHelper.onActivityResult(requestCode, responseCode, intent);
    }
  }

  private class GPlusAccessTokenTask extends AsyncTask<String,Void,String>{

    @Override
    protected String doInBackground(String... params) {
      String accessToken = "";

      account = Plus.AccountApi.getAccountName(mGoogleApiClient);
      try {

        accessToken = GoogleAuthUtil.getToken(
            LoginSelectionActivity.this,
            account,
            "oauth2:"+Scopes.PLUS_LOGIN + " " + Scopes.PLUS_ME + " https://www.googleapis.com/auth/userinfo.email"
        );

      } catch (Exception e) {
        e.printStackTrace();
        Log.e("GPlus access " + account, e.toString());
      }

      return accessToken;
    }

    @Override

    protected void onPostExecute(String accessToken) {
      super.onPostExecute(accessToken);
      Log.e("GPlus access token", accessToken);
      postGoogleToken(accessToken);
    }
  }

  private void postGoogleToken(String accessToken){
    Map<String, String> params = new HashMap<String, String>();
    params.put("code", accessToken);
    params.put("client", "android");
    params.put("grant_type", "google");
    params.put("client_id", "testclient");
    params.put("client_secret", "testpass");

    APIHandler.getInstance(this).restAPIRequest(
        Request.Method.POST,
        Constants.googleTokenUrl,
        params,
        null
    );
  }

  @Override
  public void OnRequestResponse(String response) {
    Log.e("GToken response",response);
    try {
      JSONObject jsonObj = new JSONObject(response);
      loginSessionManager.createUserLoginSession(account,jsonObj.getString("access_token"),jsonObj.getString("refresh_token"));

      if(!loginSessionManager.isDeviceRegistered())
        AppController.getInstance().registerDeviceID();

      Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_LONG).show();
      Intent intent = new Intent(getApplicationContext(), DistListingActivity.class);
      startActivity(intent);
      finish();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    Log.e("GToken Error response",error.toString());
  }

}
