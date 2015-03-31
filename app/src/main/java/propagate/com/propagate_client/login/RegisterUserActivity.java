package propagate.com.propagate_client.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RegisterModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.APIHandler;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;

/**
 * Created by kaustubh on 31/3/15.
 */
public class RegisterUserActivity extends Activity implements APIHandlerInterface {

  private EditText etName,etEmail,etNumber,etPassword,etConfPassword;
  private Button btnRegister;
  private long id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.register_user);

    etName = (EditText) findViewById(R.id.register_user_name);
    etEmail = (EditText) findViewById(R.id.register_user_email);
    etNumber = (EditText) findViewById(R.id.register_user_phone_number);
    etPassword = (EditText) findViewById(R.id.register_user_pass);
    etConfPassword = (EditText) findViewById(R.id.register_user_confirm_pass);

    btnRegister = (Button) findViewById(R.id.register_user_btn_register);
    btnRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(checkValidation()) {
          id = RegisterModule.getInstance().addRegisterUser(getApplicationContext(), new RegisterModule(etName.getText().toString(), etEmail.getText().toString(), etNumber.getText().toString()));
          postRegisterUser();
        }
      }
    });
  }

  public void postRegisterUser(){
    APIHandler.getInstance(getApplicationContext()).restAPIRequest(
        Request.Method.POST,
        Constants.registerUserUrl,
        getUserParams(),
        null
    );
  }

  public Map<String,String> getUserParams(){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("name", etName.getText().toString());
    jsonParams.put("phoneNumber", etNumber.getText().toString());
    jsonParams.put("email", etEmail.getText().toString());
    jsonParams.put("password", etPassword.getText().toString());

    return jsonParams;
  }

  @Override
  public void OnRequestResponse(String response) {
    if(response != null){
      String message = CommonFunctions.trimMessage(response, "message");
      if(message != null)
        Toast.makeText(getApplicationContext(), "" + message, Toast.LENGTH_SHORT).show();

      String data = CommonFunctions.trimMessage(response, "data");
      if(data != null)
        try {
          Log.i("data", data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type){
            case "save":
              JSONObject register = new JSONObject(jsonObj.getString("reg"));
              Intent intent = new Intent(getApplicationContext(), RegisterUserDetailActivity.class);
              intent.putExtra("reg_id", id);
              startActivity(intent);
              finish();
              break;
            case "delete":
              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Toast.makeText(getApplicationContext(),"No Connection Error",Toast.LENGTH_SHORT).show();
    else if(error.networkResponse != null){
      CommonFunctions.errorResponseHandler(getApplicationContext(),error);
    }
  }

  private boolean checkValidation(){
    boolean val = true;

    if(TextUtils.isEmpty(etName.getText().toString()))
    {
      etName.setError("Field can not be blank.");
      val= false;
    }

    if(TextUtils.isEmpty(etEmail.getText().toString()))
    {
      etEmail.setError("Field can not be blank.");
      val= false;
    }

    if(TextUtils.isEmpty(etNumber.getText().toString()))
    {
      etNumber.setError("Field can not be blank.");
      val= false;
    }

    if(TextUtils.isEmpty(etPassword.getText().toString()))
    {
      etPassword.setError("Field can not be blank.");
      val= false;
    }

    if(TextUtils.isEmpty(etConfPassword.getText().toString()))
    {
      etConfPassword.setError("Field can not be blank.");
      val= false;
    }

    if (!etConfPassword.getText().toString().equals(etPassword.getText().toString())){
      etConfPassword.setError("Password doesn't match");
      val= false;
    }

    return val;
  }
}
