package propagate.com.propagate_client.property;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 17/3/15.
 */
public class AddPropertyActivity extends Activity implements APIHandlerInterface{

  EditText etTitle,etDescription,etClientEmail,etLocation,etAddress,etArea,etPrice;
  Spinner spType;
  Button btnSubmit;
  PropertyModule propertyModule;
  long property_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_property);

    etTitle = (EditText) findViewById(R.id.addPropertyEtTitle);
    etDescription = (EditText) findViewById(R.id.addPropertyEtDescription);
    etClientEmail = (EditText) findViewById(R.id.addPropertyEtEmail);
    etLocation = (EditText) findViewById(R.id.addPropertyEtLocation);
    etAddress = (EditText) findViewById(R.id.addPropertyEtAddress);
    etArea = (EditText) findViewById(R.id.addPropertyEtArea);
    etPrice = (EditText) findViewById(R.id.addPropertyEtPrice);

    spType = (Spinner) findViewById(R.id.addPropertySpType);

    btnSubmit = (Button) findViewById(R.id.addPropertyBtnSubmit);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      propertyModule = (PropertyModule) bundle.getSerializable("propertyModule");

      property_id = propertyModule.getP_id();
      etTitle.setText(propertyModule.getTitle());
      etDescription.setText(propertyModule.getDesc());
      etClientEmail.setText(propertyModule.getClient_email());
      etLocation.setText(propertyModule.getLocation());
      etAddress.setText(propertyModule.getAddress());
      etArea.setText(propertyModule.getArea());
      etPrice.setText(propertyModule.getPrice());

      List<String> arrProperty = Arrays.asList(getResources().getStringArray(R.array.array_property_type));

      spType.setSelection(arrProperty.indexOf(propertyModule.getType()));

      btnSubmit.setText("Update");
    }

    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (checkValidation()) {
          if (btnSubmit.getText().equals("Submit")) {
            property_id = PropertyModule.getInstance().addProperty(getApplicationContext(), new PropertyModule(etTitle.getText().toString(),
                etDescription.getText().toString(), 1, etClientEmail.getText().toString(), etLocation.getText().toString(),
                etAddress.getText().toString(), etArea.getText().toString(), etPrice.getText().toString(), spType.getSelectedItem().toString()));

            AppController.getInstance().postCreateProperty(AddPropertyActivity.this,property_id);
          } else {
            PropertyModule.getInstance().updateProperty(getApplicationContext(), new PropertyModule(property_id, etTitle.getText().toString(),
                etDescription.getText().toString(), 1, etClientEmail.getText().toString(), etLocation.getText().toString(),
                etAddress.getText().toString(), etArea.getText().toString(), etPrice.getText().toString(), spType.getSelectedItem().toString()));
          }
        }
      }
    });
  }

  private boolean checkValidation(){
    boolean val = true;
    if(TextUtils.isEmpty(etTitle.getText().toString())){
      etTitle.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etDescription.getText().toString())){
      etDescription.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etClientEmail.getText().toString())){
      etClientEmail.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etLocation.getText().toString())){
      etLocation.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etAddress.getText().toString())){
      etAddress.setError("Field can not be blank.");
      return false;
    }
    if(TextUtils.isEmpty(etArea.getText().toString())){
      etArea.setError("Field can not be blank.");
      return false;
    }
    if(TextUtils.isEmpty(etPrice.getText().toString())){
      etPrice.setError("Field can not be blank.");
      return false;
    }

    return val;
  }

  private void loadPropertyListingActivity(){
    Intent intent = new Intent(getApplicationContext(),PropertyListingActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadPropertyListingActivity();
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
          Log.i("data",data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type){
            case "save":
              JSONObject list = new JSONObject(jsonObj.getString("prop"));
              long server_prop_id = list.getLong("id");
              PropertyModule.getInstance().updatePropertyStatus(getApplicationContext(), property_id, server_prop_id);
              loadPropertyListingActivity();
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
