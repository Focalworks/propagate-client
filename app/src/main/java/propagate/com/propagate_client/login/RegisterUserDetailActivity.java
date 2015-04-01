package propagate.com.propagate_client.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.RegisterModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.APIHandler;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;

/**
 * Created by kaustubh on 31/3/15.
 */
public class RegisterUserDetailActivity extends Activity implements View.OnClickListener, APIHandlerInterface {

  EditText etExpr,etSummary;
  Button btnSkip,btnSubmit;
  private long reg_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.register_user_details);

    Bundle b = getIntent().getExtras();

    if(b != null){
      reg_id = b.getLong("reg_id");
    }

    etExpr = (EditText) findViewById(R.id.register_detail_expr);
    etSummary = (EditText) findViewById(R.id.register_detail_summary);

    btnSkip = (Button) findViewById(R.id.register_detail_btnSkip);
    btnSkip.setOnClickListener(this);
    btnSubmit = (Button) findViewById(R.id.register_detail_btnSubmit);
    btnSubmit.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.register_detail_btnSkip:
        loadDistListing();
        break;

      case R.id.register_detail_btnSubmit:
        RegisterModule.getInstance().updateUserDetails(getApplicationContext(), reg_id, etExpr.getText().toString(), etSummary.getText().toString());
        postRegisterUserDetail();
        break;

      default:
        break;
    }
  }

  public void loadDistListing(){
    Intent intent = new Intent(getApplicationContext(), DistListingActivity.class);
    startActivity(intent);
    finish();
  }

  public void postRegisterUserDetail(){
    APIHandler.getInstance(RegisterUserDetailActivity.this).restAPIRequest(
        Request.Method.PUT,
        Constants.registerUserUrl+"/0",
        getUserParams(),
        null
    );
  }

  public Map<String,String> getUserParams(){

    Map<String, String> jsonParams = new HashMap<String, String>();
    jsonParams.put("experience", etExpr.getText().toString());
    jsonParams.put("summary", etSummary.getText().toString());

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
              loadDistListing();
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
}
