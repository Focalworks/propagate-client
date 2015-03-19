package propagate.com.propagate_client.Requirement;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 18/3/15.
 */
public class AddRequirementActivity extends ActionBarActivity {

  EditText etTitle,etDescription,etEmail,etLocation,etArea,etRange,etPrice,etPriceRange;
  Spinner spType;
  Button btnSubmit;

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
    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        long id = RequirementModule.getInstance().addRequirement(getApplicationContext(),new RequirementModule(etTitle.getText().toString(),
            etDescription.getText().toString(),etEmail.getText().toString(),etLocation.getText().toString(),etArea.getText().toString(),
            etRange.getText().toString(),etPrice.getText().toString(),etPriceRange.getText().toString(),spType.getSelectedItem().toString()));

        AppController.getInstance().postCreateRequirement(id);
      }
    });
  }
}
