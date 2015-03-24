package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.Requirement.RequirementListingActivity;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.property.PropertyListingActivity;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistListingActivity extends Activity {

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
    imgAddGroup.setVisibility(View.VISIBLE);
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
    ArrayList<DistListModule> distArrayList = DistListModule.getInstance().getDistLists(this, 0);

    distListAdapter = new DistListAdapter(this, R.layout.custom_dist_view,distArrayList);

    groupListView = (ListView) findViewById(R.id.listingListView);
    groupListView.setAdapter(distListAdapter);

    groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DistListModule distList = distListAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(),DistListDetailActivity.class);
        intent.putExtra("distListId",distList.getDist_id());
        startActivity(intent);
      }
    });

    groupListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        DistListModule distList = distListAdapter.getItem(position);
        showRemovePopup(distList);
        return false;
      }
    });

  }

  private void showRemovePopup(final DistListModule distListModule){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    // set dialog message
    alertDialogBuilder
        .setMessage("Remove Distribution List "+distListModule.getDist_name())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {

            DistListModule.getInstance().deleteDistList(getApplicationContext(),distListModule.getDist_id());

            distListAdapter.remove(distListModule);
            distListAdapter.notifyDataSetChanged();
            dialog.dismiss();
          }
        })
        .setNegativeButton("No",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            dialog.dismiss();
          }
        });

    // create alert dialog
    AlertDialog alertDialog = alertDialogBuilder.create();

    // show it
    alertDialog.show();
  }

  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_main, menu);

    MenuItem item = menu.findItem(R.id.action_dist_list);
    item.setVisible(false);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.action_property:
        Intent intent = new Intent(this, PropertyListingActivity.class);
        startActivity(intent);
        finish();
        return true;

      case R.id.action_requirement:
        Intent i = new Intent(this, RequirementListingActivity.class);
        startActivity(i);
        finish();
        return true;
    }

    return false;
  }

}
