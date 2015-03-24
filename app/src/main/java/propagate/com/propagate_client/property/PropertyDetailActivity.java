package propagate.com.propagate_client.property;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;

/**
 * Created by kaustubh on 23/3/15.
 */
public class PropertyDetailActivity extends Activity {

  TextView txtTitle,txtDesc,txtEmail,txtLocation,txtAddress,txtArea,txtPrice,txtType;
  PropertyModule propertyModule;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_property);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      propertyModule = (PropertyModule) bundle.getSerializable("propertyModule");
    }

    txtTitle = (TextView) findViewById(R.id.view_property_title);
    txtDesc = (TextView) findViewById(R.id.view_property_description);
    txtEmail = (TextView) findViewById(R.id.view_property_client_email);
    txtLocation = (TextView) findViewById(R.id.view_property_location);
    txtAddress = (TextView) findViewById(R.id.view_property_address);
    txtArea = (TextView) findViewById(R.id.view_property_area);
    txtPrice = (TextView) findViewById(R.id.view_property_price);
    txtType = (TextView) findViewById(R.id.view_property_type);

    if (propertyModule != null) {
      txtTitle.setText(propertyModule.getTitle());
      txtDesc.setText(propertyModule.getDesc());
      txtEmail.setText(propertyModule.getClient_email());
      txtLocation.setText(propertyModule.getLocation());
      txtAddress.setText(propertyModule.getAddress());
      txtArea.setText(propertyModule.getArea());
      txtPrice.setText(propertyModule.getPrice());
      txtType.setText(propertyModule.getType());
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_edit,menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case  R.id.action_edit:
        Intent intent = new Intent(getApplicationContext(),AddPropertyActivity.class);
        intent.putExtra("propertyModule",propertyModule);
        startActivity(intent);
        finish();
        break;

      default:
        break;
    }

    return false;
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    Intent intent = new Intent(this,PropertyListingActivity.class);
    startActivity(intent);
  }
}
