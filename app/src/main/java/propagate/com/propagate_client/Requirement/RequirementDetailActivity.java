package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.property.AddPropertyActivity;
import propagate.com.propagate_client.property.PropertyListingActivity;

/**
 * Created by kaustubh on 24/3/15.
 */
public class RequirementDetailActivity extends Activity {

  TextView txtTitle,txtDesc,txtEmail,txtLocation,txtArea,txtRange,txtPrice,txtPriceRange,txtType;
  RequirementModule requirementModule;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_requirement);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null) {
      requirementModule = (RequirementModule) bundle.getSerializable("requirementModule");
    }

    txtTitle = (TextView) findViewById(R.id.view_requirement_title);
    txtDesc = (TextView) findViewById(R.id.view_requirement_description);
    txtEmail = (TextView) findViewById(R.id.view_requirement_client_email);
    txtLocation = (TextView) findViewById(R.id.view_requirement_location);
    txtArea = (TextView) findViewById(R.id.view_requirement_area);
    txtRange = (TextView) findViewById(R.id.view_requirement_range);
    txtPrice = (TextView) findViewById(R.id.view_requirement_price);
    txtPriceRange = (TextView) findViewById(R.id.view_requirement_price_range);
    txtType = (TextView) findViewById(R.id.view_requirement_type);

    if (requirementModule != null) {
      txtTitle.setText(requirementModule.getTitle());
      txtDesc.setText(requirementModule.getDescription());
      txtEmail.setText(requirementModule.getClient_email());
      txtLocation.setText(requirementModule.getLocation());
      txtArea.setText(requirementModule.getArea());
      txtRange.setText(requirementModule.getRange());
      txtPrice.setText(requirementModule.getPrice());
      txtPriceRange.setText(requirementModule.getPrice_range());
      txtType.setText(requirementModule.getType());
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
        Intent intent = new Intent(getApplicationContext(),AddRequirementActivity.class);
        intent.putExtra("requirementModule",requirementModule);
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
    Intent intent = new Intent(this,RequirementListingActivity.class);
    startActivity(intent);
  }
}
