package propagate.com.propagate_client.login;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Arrays;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.gcm.RegisterDeviceTask;


public class LoginSelectionActivity extends Activity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,RegisterDeviceTask.OnTaskExecutionFinished {

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
  private GoogleApiClient mGoogleApiClient;

  /**
   * A flag indicating that a PendingIntent is in progress and prevents us
   * from starting further intents.
   */
  private boolean mIntentInProgress;
  private static final int RC_SIGN_IN = 0;
  private LoginSessionManager loginSessionManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_selection);

    loginSessionManager = new LoginSessionManager(this);

    if(loginSessionManager.isUserLoggedIn()){
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
    btnFBSignIn.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
      @Override
      public void onUserInfoFetched(GraphUser user) {
        if (user != null) {
          Toast.makeText(getApplicationContext(),"You are currently logged in as " + user.getName()+" "+user.getProperty("email")+" "+user.getId(),Toast.LENGTH_SHORT).show();
        }
      }
    });

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        .build();

    registerDeviceForGCM();

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

      case R.id.loginSelectionSignIn:
        Intent i = new Intent(getApplicationContext(),RegisterUserActivity.class);
        startActivity(i);
        finish();
        break;

      case R.id.loginSelectionSignUP:
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
    Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    getProfileInformation();
  }

  /**
   * Fetching user's information name, email, profile pic
   * */
  private void getProfileInformation() {
    try {
      if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
        Person currentPerson = Plus.PeopleApi
            .getCurrentPerson(mGoogleApiClient);
        currentPerson.getId();
        String personName = currentPerson.getDisplayName();
        String personPhotoUrl = currentPerson.getImage().getUrl();
        String personGooglePlusProfile = currentPerson.getUrl();
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        Log.e("", "Name: " + personName + ", plusProfile: "
            + personGooglePlusProfile + ", email: " + email
            + ", Image: " + personPhotoUrl);
      } else {
        Toast.makeText(getApplicationContext(),
            "Person information is null", Toast.LENGTH_LONG).show();
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

}