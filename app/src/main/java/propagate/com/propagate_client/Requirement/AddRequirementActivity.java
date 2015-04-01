package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.app.ProgressDialog;
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
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 18/3/15.
 */
public class AddRequirementActivity extends Activity implements APIHandlerInterface{

  EditText etTitle,etDescription,etEmail,etLocation,etArea,etRange,etPrice,etPriceRange;
  Spinner spType;
  Button btnSubmit;
  RequirementModule requirementModule;
  long requirement_id;
  private ProgressDialog ringProgressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.add_requirement);

    etTitle = (EditText) findViewById(R.id.addRequirementEtTitle);
    etDescription = (EditText) findViewById(R.id.addRequirementEtDescription);
    etEmail = (EditText) findViewById(R.id.addRequirementEtEmail);
    etLocation = (EditText) findViewById(R.id.addRequirementEtLocation);
    etArea = (EditText) findViewById(R.id.addRequirementEtArea);
    etRange = (EditText) findViewById(R.id.addRequirementEtRange);
    etPrice = (EditText) findViewById(R.id.addRequirementEtPrice);
    etPriceRange = (EditText) findViewById(R.id.addRequirementEtPriceRange);

    spType = (Spinner) findViewById(R.id.addRequirementSpType);

    btnSubmit = (Button) findViewById(R.id.addRequirementBtnSubmit);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      requirementModule = (RequirementModule) bundle.getSerializable("requirementModule");

      requirement_id = requirementModule.getR_id();
      etTitle.setText(requirementModule.getTitle());
      etDescription.setText(requirementModule.getDescription());
      etEmail.setText(requirementModule.getClient_email());
      etLocation.setText(requirementModule.getLocation());
      etArea.setText(requirementModule.getArea());
      etRange.setText(requirementModule.getRange());
      etPrice.setText(requirementModule.getPrice());
      etPriceRange.setText(requirementModule.getPrice_range());

      List<String> arrProperty = Arrays.asList(getResources().getStringArray(R.array.array_property_type));

      spType.setSelection(arrProperty.indexOf(requirementModule.getType()));

      btnSubmit.setText("Update");
    }

    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (checkValidation()) {
          if (btnSubmit.getText().equals("Submit")) {
            requirement_id = RequirementModule.getInstance().addRequirement(getApplicationContext(), new RequirementModule(etTitle.getText().toString(),
                etDescription.getText().toString(), etEmail.getText().toString(), etLocation.getText().toString(), etArea.getText().toString(),
                etRange.getText().toString(), etPrice.getText().toString(), etPriceRange.getText().toString(), spType.getSelectedItem().toString()));

            AppController.getInstance().postCreateRequirement(AddRequirementActivity.this,requirement_id);
            launchProgressDialog();
          } else {
            RequirementModule.getInstance().updateRequirement(getApplicationContext(), new RequirementModule(requirement_id, etTitle.getText().toString(),
                etDescription.getText().toString(), etEmail.getText().toString(), etLocation.getText().toString(), etArea.getText().toString(),
                etRange.getText().toString(), etPrice.getText().toString(), etPriceRange.getText().toString(), spType.getSelectedItem().toString()));
          }
        }
      }
    });
  }

  private void loadRequirementListingActivity(){
    Intent intent = new Intent(getApplicationContext(),RequirementListingActivity.class);
    startActivity(intent);
    finish();
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
    if(TextUtils.isEmpty(etEmail.getText().toString())){
      etEmail.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etLocation.getText().toString())){
      etLocation.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etArea.getText().toString())){
      etArea.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etRange.getText().toString())){
      etRange.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etPrice.getText().toString())){
      etPrice.setError("Field can not be blank.");
      val=false;
    }
    if(TextUtils.isEmpty(etPriceRange.getText().toString())){
      etPriceRange.setError("Field can not be blank.");
      val=false;
    }

    return val;
  }

  public void launchProgressDialog() {
    ringProgressDialog = ProgressDialog.show(AddRequirementActivity.this, "Please wait ...", "Creating Requirement...", true);
    ringProgressDialog.setCancelable(false);
  }

  public void dismissProgressDialog() {
    ringProgressDialog.dismiss();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadRequirementListingActivity();
  }

  @Override
  public void OnRequestResponse(String response) {
    if(response != null){
      String message = CommonFunctions.trimMessage(response, "message");
      Log.i("message", message);
      if(message != null)
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

      String data = CommonFunctions.trimMessage(response, "data");
      if(data != null) {
        try {
          Log.i("data", data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type) {
            case "save":
              JSONObject list = new JSONObject(jsonObj.getString("req"));
              long server_req_id = list.getLong("id");
              RequirementModule.getInstance().updateRequirementStatus(getApplicationContext(), requirement_id, server_req_id);
              loadRequirementListingActivity();
              break;
            case "delete":
              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      dismissProgressDialog();
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Toast.makeText(getApplicationContext(),"No Connection Error",Toast.LENGTH_SHORT).show();
    else if(error.networkResponse != null){
      CommonFunctions.errorResponseHandler(getApplicationContext(),error);
    }
    dismissProgressDialog();
    loadRequirementListingActivity();
  }
}
