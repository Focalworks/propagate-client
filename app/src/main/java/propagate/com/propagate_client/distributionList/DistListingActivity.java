package propagate.com.propagate_client.distributionList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import propagate.com.propagate_client.R;
import propagate.com.propagate_client.Requirement.RequirementListingActivity;
import propagate.com.propagate_client.TestActivity;
import propagate.com.propagate_client.database.DistListModule;
import propagate.com.propagate_client.property.PropertyListingActivity;
import propagate.com.propagate_client.utils.CommonFunctions;
import propagate.com.propagate_client.utils.CustomAdapterInterface;
import propagate.com.propagate_client.volleyRequest.APIHandlerInterface;
import propagate.com.propagate_client.volleyRequest.AppController;

/**
 * Created by kaustubh on 4/2/15.
 */
public class DistListingActivity extends Activity implements APIHandlerInterface,CustomAdapterInterface {

  ListView groupListView;
  ImageView imgAddGroup;
  DistListAdapter distListAdapter;
  DistListModule listModule;
  final String[] groupItems = {
      "Group Info", "Delete Group"
  };
  private long group_id;
  private ProgressDialog ringProgressDialog;
  private Animation animUP;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_listing);

    animUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.plus_slide_up);

    imgAddGroup = (ImageView) findViewById(R.id.imgAddBtn);
    imgAddGroup.setVisibility(View.VISIBLE);
    imgAddGroup.startAnimation(animUP);
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
            dialog.dismiss();
            listModule = distListModule;
            AppController.getInstance().postDeleteDistList(DistListingActivity.this, distListModule.getServer_dist_id());
            launchProgressDialog("Deleting Distribution List...");
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

      case R.id.action_logout:
        Intent logout = new Intent(this, TestActivity.class);
        startActivity(logout);
        finish();
        return true;
    }

    return false;
  }

  public void launchProgressDialog(String msg) {
    ringProgressDialog = ProgressDialog.show(DistListingActivity.this, "Please wait ...", msg, true);
    ringProgressDialog.setCancelable(false);
  }

  public void dismissProgressDialog() {
    ringProgressDialog.dismiss();
  }

  @Override
  public void OnRequestResponse(String response) {
    if(response != null){
      String message = CommonFunctions.trimMessage(response, "message");
      Log.i("message", message);
      if(message != null)
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

      String data = CommonFunctions.trimMessage(response, "data");
      if(data != null) {
        try {
          Log.i("data", data);
          JSONObject jsonObj = new JSONObject(data);
          String type = jsonObj.getString("type");
          switch (type) {
            case "save":
              JSONObject list = new JSONObject(jsonObj.getString("list"));
              long server_group_id = list.getLong("id");
              DistListModule.getInstance().updateDistListStatus(getApplicationContext(), group_id, server_group_id);
              ArrayList<DistListModule> distArrayList = DistListModule.getInstance().getDistLists(this, 0);
              distListAdapter = new DistListAdapter(this, R.layout.custom_dist_view, distArrayList);
              groupListView.setAdapter(distListAdapter);
              break;
            case "delete":
              DistListModule.getInstance().deleteDistList(getApplicationContext(), listModule.getDist_id());
              distListAdapter.remove(listModule);
              distListAdapter.notifyDataSetChanged();
              break;
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      dismissProgressDialog();
    }
  }

  @Override
  public void OnRequestErrorResponse(VolleyError error) {
    if(error instanceof NoConnectionError)
      Toast.makeText(getApplicationContext(),"No Connection Error",Toast.LENGTH_SHORT).show();
    else if(error.networkResponse != null){
      CommonFunctions.errorResponseHandler(getApplicationContext(), error);
    }
    dismissProgressDialog();
  }

  @Override
  public void OnBtnClick(long id) {
    group_id = id;
    launchProgressDialog("Creating Distribution List...");
    AppController.getInstance().postCreateDistList(DistListingActivity.this,group_id);
  }
}
