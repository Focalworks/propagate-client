package propagate.com.propagate_client.authentication;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.CallableStatement;
import java.util.Arrays;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.gcm.RegisterDeviceTask;


public class LoginSelectionActivity extends ActionBarActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks,RegisterDeviceTask.OnTaskExecutionFinished {

  private Button btnDefaultLogin;
  private SignInButton btnGPlusLogin;
  private LoginButton btnFBSignIn;
  private UiLifecycleHelper uiHelper;
  private String loginType = "facebook";
  private String regId;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_selection);

    uiHelper = new UiLifecycleHelper(this, statusCallback);
    uiHelper.onCreate(savedInstanceState);


    btnDefaultLogin = (Button) findViewById(R.id.btnDefaultLogin);
    btnDefaultLogin.setOnClickListener(this);

    btnGPlusLogin = (SignInButton) findViewById(R.id.btnGPlusSignIn);
    btnGPlusLogin.setOnClickListener(this);

    btnFBSignIn = (LoginButton) findViewById(R.id.btnFBSignIn);
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

  }

  private void registerDeviceForGCM(){
    if (GCMUtils.checkPlayServices(this)) {
      regId = GCMUtils.getRegistrationId(this);

      if (regId.isEmpty()) {
        new RegisterDeviceTask(this).execute(regId);
      }
    } else {
      Log.i("Registration GCM", "No valid Google Play Services APK found.");
    }
  }

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

  protected void onStart() {
    super.onStart();
    mGoogleApiClient.connect();
  }

  protected void onStop() {
    super.onStop();
    if (mGoogleApiClient.isConnected()) {
      mGoogleApiClient.disconnect();
    }
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
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btnDefaultLogin:
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        break;

      case R.id.btnGPlusSignIn:
        loginType = "google";
        if (!mGoogleApiClient.isConnecting()) {
          mSignInClicked = true;
          resolveSignInError();
        }
        break;

      case R.id.btnFBSignIn:

        break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_login, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
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

  @Override
  public void onConnected(Bundle connectionHint) {
    mSignInClicked = false;
    Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
    getProfileInformation();
  }

  @Override
  public void onSaveInstanceState(Bundle savedState) {
    super.onSaveInstanceState(savedState);
    uiHelper.onSaveInstanceState(savedState);
  }

  @Override
  public void onConnectionSuspended(int i) {
    mGoogleApiClient.connect();
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

  @Override
  public void onTaskFinishedEvent(String result){
    regId = result;
  }
}
