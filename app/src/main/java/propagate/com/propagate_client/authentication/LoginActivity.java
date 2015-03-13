package propagate.com.propagate_client.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.gcm.RegisterDeviceTask;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.utils.SessionManager;
import propagate.com.propagate_client.volleyRequest.AppController;
import propagate.com.propagate_client.volleyRequest.VolleyStringRequest;

/**
 * Created by kaustubh on 11/3/15.
 */
public class LoginActivity extends ActionBarActivity{

  GoogleCloudMessaging gcm;
  String regId;
  EditText etEmail,etPassword;
  Button btnLogin,btnLogOut;
  SessionManager sessionManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_screen);

    registerDeviceForGCM();

    sessionManager = new SessionManager(getApplicationContext());

    etEmail = (EditText) findViewById(R.id.etEmailId);
    etPassword = (EditText) findViewById(R.id.etPassword);

    btnLogin = (Button) findViewById(R.id.btnLogin);
    btnLogOut = (Button) findViewById(R.id.btnLogOut);
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loginUser();
      }
    });
    btnLogOut.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        testing();
      }
    });
  }

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
      sessionManager.createXAuthToken(token);
    }
  };

  Response.ErrorListener loginRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Login Error Response",error.toString());
    }
  };

  public void testing(){
    VolleyStringRequest postRequest = new VolleyStringRequest(
        Request.Method.POST,
        Constants.testingAuthUrl,
        null,
        testRequestListener,
        testRequestErrorListener,
        getApplicationContext()
    );

    AppController.getInstance().addToRequestQueue(postRequest);
  }

  Response.Listener<String> testRequestListener = new Response.Listener<String>() {
    @Override
    public void onResponse(String token) {
      Log.e("test token", token);
    }
  };

  Response.ErrorListener testRequestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) { Log.e("test Error Response",error.networkResponse.headers+"");  }
  };

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

  Response.ErrorListener requestErrorListener = new Response.ErrorListener() {
    @Override
    public void onErrorResponse(VolleyError error) {
      Log.e("Register Error",error.toString());
    }
  };

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
}
