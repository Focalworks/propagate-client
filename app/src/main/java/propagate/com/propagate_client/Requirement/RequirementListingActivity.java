package propagate.com.propagate_client.Requirement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.database.PropertyModule;
import propagate.com.propagate_client.database.RequirementModule;
import propagate.com.propagate_client.distributionList.DistListingActivity;
import propagate.com.propagate_client.property.PropertyListingActivity;

/**
 * Created by kaustubh on 19/3/15.
 */
public class RequirementListingActivity extends Activity {

  ListView requirementListView;
  ImageView imgAddReq;
  RequirementListAdapter requirementListAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    imgAddReq = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddReq.setVisibility(View.VISIBLE);
    imgAddReq.setOnClickListener(new View.OnClickListener() {
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

    requirementListView = (ListView) findViewById(R.id.listingListView);
    requirementListView.setAdapter(requirementListAdapter);

    requirementListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RequirementModule requirementModule = requirementListAdapter.getItem(position);
        Intent intent = new Intent(getApplicationContext(),RequirementDetailActivity.class);
        intent.putExtra("requirementModule",requirementModule);
        startActivity(intent);
        finish();
      }
    });

    requirementListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        RequirementModule requirementModule = requirementListAdapter.getItem(position);
        showRemovePopup(requirementModule);
        return false;
      }
    });
  }

  private void loadAddRequirementActivity(){
    Intent intent = new Intent(getApplicationContext(),AddRequirementActivity.class);
    startActivity(intent);
    finish();
  }

  private void showRemovePopup(final RequirementModule requirementModule){
    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

    // set dialog message
    alertDialogBuilder
        .setMessage("Remove requirement "+requirementModule.getTitle())
        .setCancelable(false)
        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog,int id) {
            RequirementModule.getInstance().deleteProperty(getApplicationContext(),requirementModule.getR_id());
            requirementListAdapter.remove(requirementModule);
            requirementListAdapter.notifyDataSetChanged();
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

    MenuItem item = menu.findItem(R.id.action_requirement);
    item.setVisible(false);

    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()){
      case R.id.action_dist_list:
        Intent intent = new Intent(this, DistListingActivity.class);
        startActivity(intent);
        finish();
        return true;

      case R.id.action_property:
        Intent i = new Intent(this, PropertyListingActivity.class);
        startActivity(i);
        finish();
        return true;
    }

    return false;
  }

}
