package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.List;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.property.PropertyListingActivity;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 18/3/15.
 */
public class AddRequirementActivity extends Activity {

  EditText etTitle,etDescription,etEmail,etLocation,etArea,etRange,etPrice,etPriceRange;
  Spinner spType;
  Button btnSubmit;
  RequirementModule requirementModule;
  long requirement_id;

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
        if(btnSubmit.getText().equals("Submit")) {
          long id = RequirementModule.getInstance().addRequirement(getApplicationContext(), new RequirementModule(etTitle.getText().toString(),
              etDescription.getText().toString(), etEmail.getText().toString(), etLocation.getText().toString(), etArea.getText().toString(),
              etRange.getText().toString(), etPrice.getText().toString(), etPriceRange.getText().toString(), spType.getSelectedItem().toString()));

          AppController.getInstance().postCreateRequirement(id);
        }else{
          RequirementModule.getInstance().updateRequirement(getApplicationContext(),new RequirementModule(requirement_id,etTitle.getText().toString(),
              etDescription.getText().toString(), etEmail.getText().toString(), etLocation.getText().toString(), etArea.getText().toString(),
              etRange.getText().toString(), etPrice.getText().toString(), etPriceRange.getText().toString(), spType.getSelectedItem().toString()));
        }
        loadRequirementListingActivity();
      }
    });
  }

  private void loadRequirementListingActivity(){
    Intent intent = new Intent(getApplicationContext(),RequirementListingActivity.class);
    startActivity(intent);
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    loadRequirementListingActivity();
  }
}
