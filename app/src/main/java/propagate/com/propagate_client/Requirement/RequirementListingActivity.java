package propagate.com.propagate_client.Requirement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.RequirementModule;

/**
 * Created by kaustubh on 19/3/15.
 */
public class RequirementListingActivity extends ActionBarActivity {

  ListView propertyListView;
  ImageView imgAddButton;
  RequirementListAdapter requirementListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    imgAddButton = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadAddRequirementActivity();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<RequirementModule> requirementList = RequirementModule.getInstance().getRequirementInfo(this, 0);

    requirementListAdapter = new RequirementListAdapter(this, R.layout.custom_property_view,requirementList);

    propertyListView = (ListView) findViewById(R.id.listingListView);
    propertyListView.setAdapter(requirementListAdapter);

  }

  private void loadAddRequirementActivity(){
    Intent intent = new Intent(getApplicationContext(),AddRequirementActivity.class);
    startActivity(intent);
    finish();
  }

}
