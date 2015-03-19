package propagate.com.propagate_client.property;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 17/3/15.
 */
public class AddPropertyActivity extends ActionBarActivity {

  EditText etTitle,etDescription,etClientEmail,etLocation,etAddress,etArea,etPrice;
  Spinner spType;
  Button btnSubmit;

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
    btnSubmit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        long id = PropertyModule.getInstance().addProperty(getApplicationContext(),new PropertyModule(etTitle.getText().toString(),
            etDescription.getText().toString(),1,etClientEmail.getText().toString(),etLocation.getText().toString(),
            etAddress.getText().toString(),etArea.getText().toString(),etPrice.getText().toString(),spType.getSelectedItem().toString()));

        AppController.getInstance().postCreateProperty(id);
        loadPropertyListingActivity();
      }
    });
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
}
