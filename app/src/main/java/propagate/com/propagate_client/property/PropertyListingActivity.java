package propagate.com.propagate_client.property;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;

/**
 * Created by kaustubh on 19/3/15.
 */
public class PropertyListingActivity extends ActionBarActivity {

  ListView propertyListView;
  ImageView imgAddButton;
  PropertyListAdapter propertyListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    imgAddButton = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadAddPropertyActivity();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<PropertyModule> propertyList = PropertyModule.getInstance().getPropertyInfo(this, 0);

    propertyListAdapter = new PropertyListAdapter(this, R.layout.custom_property_view,propertyList);

    propertyListView = (ListView) findViewById(R.id.listingListView);
    propertyListView.setAdapter(propertyListAdapter);

  }

  private void loadAddPropertyActivity(){
    Intent intent = new Intent(getApplicationContext(),AddPropertyActivity.class);
    startActivity(intent);
    finish();
  }

}
