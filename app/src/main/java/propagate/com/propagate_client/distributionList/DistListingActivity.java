package propagate.com.propagate_client.distributionList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.DistListModule;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistListingActivity extends ActionBarActivity {

  ListView groupListView;
  ImageView imgAddGroup;
  DistListAdapter distListAdapter;
  final String[] groupItems = {
      "Group Info", "Delete Group"
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    imgAddGroup = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddGroup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),CreateDistListActivity.class);
        startActivity(intent);
        finish();
      }
    });

    /*
    * pass id:0 to get all records and pass individual id to get individual record
    * */
    ArrayList<DistListModule> groupList = DistListModule.getInstance().getDistLists(this, 0);

    distListAdapter = new DistListAdapter(this, R.layout.custom_dist_view,groupList);

    groupListView = (ListView) findViewById(R.id.listingListView);
    groupListView.setAdapter(distListAdapter);

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }

}
