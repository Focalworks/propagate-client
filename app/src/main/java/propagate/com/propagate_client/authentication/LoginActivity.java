package propagate.com.propagate_client.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.gcm.GCMUtils;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.APIHandler;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;
import propagate.com.propagate_client.volleyRequest.VolleyStringRequest;

/**
 * Created by kaustubh on 11/3/15.
 */
public class LoginActivity extends Activity implements APIHandlerInterface{

  LoginSessionManager loginSessionManager;
  EditText etEmail,etPassword;
  Button btnLogin;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.login_screen);

    loginSessionManager = new LoginSessionManager(this);

    etEmail = (EditText) findViewById(R.id.loginScreenEtEmailId);
    etPassword = (EditText) findViewById(R.id.loginScreenEtPassword);

    btnLogin = (Button) findViewById(R.id.loginScreenBtnLogin);
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(checkValidation())
          loginOauthToken();
      }
    });
  }

  private void loginOauthToken(){
    APIHandler.getInstance(this).restAPIRequest(
        Request.Method.POST,
        Constants.loginOauthUrl,
        getOauthParams(),
        null
    );
  }

  public Map<String,String> getOauthParams(){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("username", etEmail.getText().toString());
    jsonParams.put("password", etPassword.getText().toString());
    jsonParams.put("password", etPassword.getText().toString());
    jsonParams.put("client_id", "testclient");
    jsonParams.put("client_secret", "testpass");
    jsonParams.put("grant_type", "password");
    return jsonParams;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    /*if (id == R.id.action_settings) {
      return true;
    }*/

    return super.onOptionsItemSelected(item);
  }

  private  boolean checkValidation(){
    boolean val = true;

    if(TextUtils.isEmpty(etEmail.getText())){
      etEmail.setError("Field can not be empty.");
      val = false;
    }
    if(TextUtils.isEmpty(etPassword.getText())){
      etPassword.setError("Field can not be empty.");
      val = false;
    }

    return val;
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

  @Override
  public void OnRequestResponse(String response) {
    Log.i("Response", response);
    try {
      JSONObject jsonObj = new JSONObject(response);
      loginSessionManager.createUserLoginSession(etEmail.getText().toString(),jsonObj.getString("access_token"),jsonObj.getString("refresh_token"));
      registerDeviceID();
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
    Log.i("Error Response", error.toString());
  }
}
