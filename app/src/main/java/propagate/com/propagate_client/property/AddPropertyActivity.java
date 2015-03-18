package propagate.com.propagate_client.property;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.utils.Constants;
import propagate.com.propagate_client.volleyRequest.AppController;
import propagate.com.propagate_client.volleyRequest.VolleyStringRequest;

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
      }
    });
  }

}
